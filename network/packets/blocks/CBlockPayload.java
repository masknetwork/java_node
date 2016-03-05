// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trade.feeds.CFeedPayload;


public class CBlockPayload extends CPayload
{		
	// Packets list
	public ArrayList packets=new ArrayList();
	
	// Hash
	public String hash;
	
	// Sign
	public String sign;
	
	// Block
	public long block;
        
        
		
	public CBlockPayload(String adr, long prev_block_no)  throws Exception
	{
            // Constructor
            super(adr);
            
	    // ID
	    this.block=prev_block_no+1;
            
            // Hash
            this.hash();
	}
	
	public boolean exist(String hash)
	{
		for (int a=0; a<=this.packets.size()-1; a++)
			if (((CBroadcastPacket)this.packets.get(a)).hash.equals(hash))
				return true;
		
		return false;
	}
	
	public void addPacket(CPacket packet)
	{
            try
            {
		// Packet exist ?
		if (this.exist(packet.hash)) return;
		
                // Data feed packet ?
                if (packet.tip.equals("ID_FEED_PACKET"))
                {
                   // Deserialize payload
                   CFeedPayload dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(packet.payload);
                   
                   // Feed symbol
                   String feed=dec_payload.feed_symbol;
                           
                   // Already in block
                   for (int a=0; a<=this.packets.size()-1; a++)
                   {
                       // Load packet data
                       CPacket p=(CPacket)this.packets.get(a);
                       
                       // Feed packet ?
                       if (p.tip.equals("ID_FEED_PACKET"))
                       {
                           // Deserialize
                           dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(p.payload);
                           
                           // Remove if same feed ?
                           if (dec_payload.feed_symbol.equals(feed))
                               this.packets.remove(a);
                       }
                   }
                }
                
		// Adds packet
                if (packet.tip.equals("ID_FEED_PACKET"))
		   this.packets.add(packet); 
		else
                   this.packets.add(0, packet); 
                
                // Console
		//UTILS.CONSOLE.write("Packet "+pack.hash+" added to block "+this.hash+"+("+this.packets.size()+" packets)");
                
                // Recalculate hash
                this.hash();
            }
            catch (Exception ex) 
       	    {  
       		UTILS.LOG.log("Exception", ex.getMessage(), "SQLException.java", 57);
            }
            
	}
	
	// Hash
	public void hash() throws Exception
	{
	   String hash=UTILS.BASIC.hash(this.target_adr+
                                        String.valueOf(this.block)+
                                        UTILS.SERIAL.serialize(this.packets));
           
           // Return
           this.hash=hash;
	}
	
	// Sign payload
	public void sign(String signer) throws Exception
	{
		// Signer
		this.target_adr=signer;
		
		// Hash
		this.hash();
		
		// Get address
		CAddress adr=UTILS.WALLET.getAddress(signer);
		if (adr!=null) this.sign=adr.sign(this.hash);
	}
	
        // Deletes unconfirmed trans
        public void delTrans(long block) throws Exception
        {
            UTILS.DB.executeUpdate("DELETE FROM trans "
                                       + "WHERE status='ID_UNCONFIRMED'");
                    
            UTILS.DB.executeUpdate("DELETE FROM my_trans "
                                       + "WHERE status='ID_UNCONFIRMED'");
        }
        
	// Commits all transactions
	public CResult commit()  throws Exception
	{
            try
            {
            // Start logging
                UTILS.LOG_QUERIES=true;
                
                // Delete unconfirmed trans
                this.delTrans(this.block);
               
		for (int a=0; a<=this.packets.size()-1; a++)
	        {
                   // Packet
		   CPacket p=((CPacket)this.packets.get(a));
                   
                   // Commit
                   CResult res=p.commit(this);
		   
                   // Report
                   if (res.passed==false) res.report();
	        }
		
                // Pay block reward
                this.payReward(this.target_adr);
                 
                // Delete expired items
                this.delExpired();
                
                // Check options
                UTILS.CRONS.runCrons(block, this);
                
                // Refresh tables
                UTILS.NET_STAT.refreshTables(block);
            }
             catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("Exception", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
                
                // Trans pool
		UTILS.NETWORK.TRANS_POOL.newBlock(this.block);
                
		// Ok
		return new CResult(true, "Ok", "CBlock", 68);

        }
	
        public void payReward(String adr) throws Exception
        {
            UTILS.BASIC.newTransfer("default", 
                                    adr,
                                    0.25, 
                                    false,
                                    "MSK", 
                                    "Block reward", 
                                    "", 
                                    hash, 
                                    this.block,
                                    this,
                                    0);
            
            UTILS.BASIC.clearTrans(hash, "ID_ALL");
        }
        
        public void delExpired() throws Exception
        {
            // Expired address options
            UTILS.DB.executeUpdate("DELETE FROM adr_options WHERE expires<"+this.block);
            
            // Expired ads
            UTILS.DB.executeUpdate("DELETE FROM ads WHERE expires<"+this.block);
            
            // Expired domains
            UTILS.DB.executeUpdate("DELETE FROM domains WHERE expires<"+this.block);
            
            // Expired domains listings
            UTILS.DB.executeUpdate("UPDATE domains SET market_expires=0, market_bid=0 WHERE expires<"+this.block);
        }
        
	// Compare two blocks and returns the accuracy
	public int compare(CBlockPayload block)
	{
		return 90;
	}
	
	// Checks the block integrity
	public CResult check() throws Exception
	{
            long check_index=0;
            
            // Delete inconfirmed
            this.delTrans(this.block);
            
            // Signer valid
            if (!UTILS.BASIC.adressValid(this.target_adr))
                 return new CResult(false, "Invalid signer", "CBlockPayload.java", 239);
           
            // Check pow
            for (int a=0; a<=this.packets.size()-1; a++)
	        {   
		    CBroadcastPacket packet=((CBroadcastPacket)this.packets.get(a));
		    CResult res=packet.check(this);
		   
		    if (res.passed==false) return res;
		    check_index++;
		}
           
		
            // Ok
	    return new CResult(true, "Ok", "CBlock", 68);
	}

}