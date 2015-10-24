package wallet.network;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.sync.*;

public class CBlocks  extends CTicker
{
	// Current block
	public CBlockPacket myCurrentBlock=null;
	public CBlockPayload myCurrentBlockPayload=null;
	
	// Previous block
	public CBlockPacket myPrevBlock=null;
	public CBlockPayload myPrevBlockPayload=null;
	
	// Last Block
	public CBlockPacket lBlock;
	
	// Broadcasters
	public CBroadcasters broadcasters;
	
	// Rceived blocks
	ArrayList received=new ArrayList();
	
	// Best block
	CBlockPacket bestBlock=null;
	
	// Best block balance
	double bestBalance=0;
	
	// Last broadcast
	long last_broadcast_block=0;
	
	// Last packet hash
	public String last_packet=null;
	
	// Last block hash
	public String last_block=null;
	
	// Block signer versions
	public CBlockSignersVersions blocksigners=new CBlockSignersVersions();
	
	// Checkpoint versions
	public CCheckPointVersions checkpoints=new CCheckPointVersions();
	
	// Wait for block
	boolean wait_for_block=false;
	
	// Wait for block hash
	String wait_for_block_hash="";
	
	// Last accuracy
	public int accuracy=0;
	
	// Signers
	ArrayList bSigners=new ArrayList();
	
	// Last new block sequence
	long last_new_block=0;
	
	public CBlocks() 
	{
		 File dir = new File("blocks");
		  if (!dir.exists()) 
		  {
		     try
		     {
		        dir.mkdir();   
		     } 
		     catch(SecurityException ex)
		     {
		        UTILS.LOG.log("SecurityException", ex.getMessage(), "CBlocks", 46);
		     }        
		     
		  }
		  
			// Broadcast blocks ?
			if ((UTILS.SETTINGS.settings.getProperty("broadcast_blocks")==null) || 
			    (UTILS.SETTINGS.settings.getProperty("broadcast_blocks")!=null &&
			    UTILS.SETTINGS.settings.getProperty("broadcast_blocks").equals("true")))
			{
			   
			    UTILS.CONSOLE.write("Blocks broadcasting is ON...");
			     
			    // New block
				this.newBlock();
			}
			else
				UTILS.CONSOLE.write("Blocks broadcasting is OFF...");
		  
	      // Current payload
		  myCurrentBlockPayload=new CBlockPayload();
			   
		  
		  // Broadcasters
		  broadcasters=new CBroadcasters();
	}
	
	public void tick()
	{
		// Syncronizing ?
		if (!UTILS.STATUS.engine_status.equals("ID_ONLINE"))
			return;
			
		try
		{
		   if (UTILS.STATUS.netstat.equals("ID_NEW_BLOCK")) 
	          this.newBlock();
		   
		   if (UTILS.STATUS.netstat.equals("ID_COMMIT_BLOCK")) 
		      if (this.bestBlock!=null && this.blocksigners!=null)  
		      {
		    	  if (this.blocksigners.versions.size()>0)
		    	  {
		    	    String highest_hash=this.blocksigners.getBestVersion().block_hash;
		    	  
		    	    // Network highets block is the best received block
		    	    if (this.bestBlock.hash.equals(highest_hash))
		    	    {
		    		  CResult res=this.bestBlock.commit();
		              if (res.passed==false) res.report();
		        		 
		              this.bestBalance=0;
		              this.bestBlock=null;
		              this.received.clear();
		    	    }
		    	    else
		    	    {
		    		  this.wait_for_block=true;
		    		  this.wait_for_block_hash=highest_hash;
		    	    }
		    	  }
		      }
		   
		   // --------------------------------------- Broadcast blocks ------------------------
		   if (this.broadcasters.hasItems==true)
		   {
			   if (UTILS.STATUS.netstat.indexOf("SEND_BLOCK")>0)
		           {
		    	      Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			      ResultSet rs=s.executeQuery("SELECT * "
			   		                                  + "FROM broadcasters "
			   		                                 + "WHERE section='"+UTILS.STATUS.netstat+"' "
			   		                              + "ORDER BY second ASC");
			   
			      
			      if (UTILS.DB.hasData(rs)==true)
			      {
			    	  rs.next();
			    
			    	  if (this.last_broadcast_block!=UTILS.BASIC.block() &&
			    	  this.myPrevBlockPayload!=null &&
			    	  rs.getDouble("balance")>this.bestBalance)
			          {
			    	    // Last broadcast block
						this.last_broadcast_block=UTILS.BASIC.block();
						
				        // Signer
				        String signer=rs.getString("adr");
				     
				        // Broadcaster transaction
				        UTILS.CONSOLE.write("Prev payload has "+this.myPrevBlockPayload.packets.size());
				     
				 	    // Sign payload
				        this.myPrevBlockPayload.sign(signer);
				  
				        // Build the payload
				        //this.myPrevBlock=new CBlockPacket();
				        //this.myPrevBlock.payload=UTILS.SERIAL.serialize(this.myPrevBlockPayload);
					
				         // Hash
				        // this.myPrevBlock.sign();
					
					    // Broadcast the packet
					    UTILS.NETWORK.broadcast(this.myPrevBlock);
			         }
                                  
                                  // Close
                                  if (s!=null) s.close();
		       }
		      }
		   }
		   
		   
		   // --------------------------------------- Broadcast confirms ------------------------
		     if (UTILS.STATUS.netstat.indexOf("CONFIRM")>0 && this.bestBlock!=null)
		      {
			      for (int a=0; a<=UTILS.WALLET.addresses.size()-1; a++)
			      {
			    	  CAddress adr=(CAddress)UTILS.WALLET.addresses.get(a);
			    	  
			    	  if (UTILS.STATUS.getStage(adr.balance).equals(UTILS.STATUS.netstat.replace("CONFIRM", "SEND_BLOCK")))
			    		  if (this.findSigner(adr.getPublic())==false)
			    		  {
			                  CBlockSignPacket packet=new CBlockSignPacket(adr.getPublic(), 
			    			                                               this.bestBlock.hash, 
			    			                                               this.accuracy);
			    	          UTILS.NETWORK.broadcast(packet);
			    	          
			    	          // Add signer
			    	          this.bSigners.add(adr.getPublic());
			              }
		          }
		      }
		
		   
	
		}
		catch (SQLException ex)
		{
			UTILS.LOG.log("CBlocks.java", ex.getMessage(), "CBlocks.java", 89);
		}
		
	}
	
	public boolean findSigner(String signer)
	{
		for (int a=0; a<=this.bSigners.size()-1; a++)
			if (this.bSigners.get(a).equals(signer))
				return true;
		
		return false;
	}
	
	public void newBlock()
	{
	    if (myCurrentBlockPayload!=null && (UTILS.BASIC.tstamp()-this.last_new_block)>30)
	    {
		   // Delete from trans pool
		   UTILS.DB.executeUpdate("DELETE FROM trans_pool "
				                    + "WHERE block='"+this.myCurrentBlockPayload.block+"'");
	       
		   // Best balance
	       this.bestBalance=0;
		   
	       // Best block
	       this.bestBlock=null;
		 
		   // Old block
	       myPrevBlockPayload=this.myCurrentBlockPayload;
				
		   // New current block
		   myCurrentBlockPayload=new CBlockPayload();	
				
		   // Clear signers versions
		   this.blocksigners=new CBlockSignersVersions();
				
		   // Clear checkpoint versions
		   this.checkpoints=new CCheckPointVersions();
	    
	       // Reset signers
		   this.bSigners.clear();
		   
		   // Last new block sequence
		   this.last_new_block=UTILS.BASIC.tstamp();
	    }
	}
	
	public void addPacket(CPacket packet)
	{
		if (myCurrentBlockPayload!=null)
		  if (!packet.tip.equals("ID_BLOCK") && !(packet instanceof CBlockSignPacket)) 
			myCurrentBlockPayload.addPacket(packet);
		
	}
	
	
	// Broadcast current block
    public void broadcast()
	{	
    	 //this.myCurrentBlock=new CBlockPacket();
    	 
    	// Build the payload
	    this.myCurrentBlock.payload=UTILS.SERIAL.serialize(this.myCurrentBlockPayload);
		
	    // Hash
	   // this.myCurrentBlock.sign();
		
		// Return the packet
		UTILS.NETWORK.broadcast(this.myCurrentBlock);
	}
	
    // Commits the last block
	public CResult commit()
	{
		// Commits the block
		CBlockPayload p=(CBlockPayload)this.received.get(0);
		CResult res=p.commit();
		if (res.passed==false) res.report();
		
		// Refresh interface
		if (UTILS.SETTINGS.settings.getProperty("headless").equals("false"))
		{
			UTILS.WALLET.refresh();
			//UTILS.INTERFATA.t_panel.list.refresh(false);
		}
		
		// Result
		if (res.passed==false)
			return res;
		else
			return new CResult(true, "Ok", "CBlocks", 81);
	}
	
	public double getSignerBalance(String signer)
	{
		try
		{
                    Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    ResultSet rs=s.executeQuery("SELECT * FROM adr WHERE adr='"+signer+"'");
			
			if (UTILS.DB.hasData(rs)==false)
			{
                            // Close
                            if (s!=null) s.close();
		            
                            // Return
                            return 0;
			}
			else
			{
			    // Next
                            rs.next();
			    
                            // Balance
                            double balance=rs.getDouble("balance");
                            
                            // Close
                            if (s!=null) s.close();
                            
                            // Return
                            return balance;
			}
				
		}
		catch (SQLException ex)
		{
			UTILS.LOG.log("SQLException", ex.getMessage(), "CBlocks.java", 206);
		}
		
		return 0;
	}
	
	public void received(CBlockPacket block)
	{
		CBlockPayload pay=(CBlockPayload)UTILS.SERIAL.deserialize(block.payload);
		
		
		if (UTILS.SETTINGS.commit_any_block==true)
		{
		   CResult res=block.commit();
		   if (res.passed==false) res.report();
		   
		   UTILS.NETWORK.broadcast(block);
		}
		else
		{
		   double sBalance=this.getSignerBalance(pay.signer);
		   UTILS.CONSOLE.write("Signer balance "+sBalance+"("+this.bestBalance+")");
		   
		   if (sBalance>this.bestBalance)
		   {
			 this.bestBalance=sBalance;
			 this.bestBlock=block;
			 this.received.add(block);
			 
			 // Broadcast
			 UTILS.NETWORK.broadcast(block);
			
			 // Gets accuracy
			 this.accuracy=this.compareBlocks(this.myCurrentBlockPayload, pay);
			
			 //UTILS.FOOTER.write("New block received ("+pay.packets.size()+" packets).");
		   }
		}
	}
	
	public int compareBlocks(CBlockPayload pay1, CBlockPayload pay2)
	{
		int dif=0;
		
	
		
		return 100;
	}

}
