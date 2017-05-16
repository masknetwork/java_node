// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

import wallet.kernel.*;
import wallet.kernel.net_stat.consensus.CConsensus;
import wallet.network.packets.*;
import wallet.network.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.sync.CReqDataPacket;

public class CNetwork extends Thread
{
    // Peers
    public CPeers peers;
		
    // Station ID
    public String ID;
		
    // Peer
    public CPeer peer;
    
      // Transaction pool
    public static CTransPool TRANS_POOL;
	
    // Escrowed pool ?
    public static CEscrowedPool ESCROWED_POOL=null;
    
    // Multisig pool ?
    public static CMultisigPool MULTISIG_POOL=null; 
    
    // Consensus
    public  CConsensus CONSENSUS=null;
    
    // Last hash
    public String last_hash; 

    public CNetwork()  throws Exception
    {
        // Initialized
	System.out.println("Network initialized....");
		  
	// Conected peers
	this.peers=new CPeers(this); 
        
        // Transaction pooll
        this.TRANS_POOL=new CTransPool();
       
        // Consensus
        this.CONSENSUS=new CConsensus();
    }
	
	  public void init() throws Exception
	  {
		  
	  }
	  
	  public void run()
	  {
              try
              {
	          // Start the server
                  this.peers.serverStart(UTILS.SETTINGS.port); 	
              
              }
              catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CNetwork.java", 57);
              }
	  }
	  
	  
	  public boolean isConPacket(String tip) throws Exception
          {
            if (tip.equals("ID_PING_PACKET") || 
                tip.equals("ID_PONG_PACKET") || 
                tip.equals("ID_REQ_CON_PACKET") || 
                tip.equals("ID_REQ_CON_RESPONSE_PACKET"))
              return true;
            else
              return false;
          }
          
          public void seen(String adr) throws Exception
          {
              // Updates last seen
	      UTILS.DB.executeUpdate("UPDATE peers "
                                      + "SET last_seen='"+UTILS.BASIC.tstamp()+"' "
                                    + "WHERE peer='"+adr+"'");
          }
          
	  public synchronized void processRequest(CPacket packet, CPeer sender) throws Exception
	  {
             // Last hash
             if (packet.hash.equals(this.last_hash))
                 return;
             
             // Last hash
             this.last_hash=packet.hash;
              
	     try
	     { 
		  // Seen
                  seen(sender.adr);
                  
		  // Already processed
		  if (this.packetExist(packet, sender)) return;
                  
                  // Timestamp
                  if (Math.abs(packet.tstamp-UTILS.BASIC.tstamp())>60) 
                  {
                      System.out.println(packet.tstamp+", "+UTILS.BASIC.tstamp());
                      return;
                  }
                  
                  // Console
		  System.out.println("Received packet ("+String.valueOf(UTILS.BASIC.tstamp())+")..........."+packet.tip+" ("+packet.hash+")");
                 
                  
                  // Sync ?
                  if (UTILS.STATUS.engine_status.equals("ID_SYNC"))
                  {
                      if (packet.tip.equals("ID_DELIVER_BLOCKS_PACKET") ||
                          packet.tip.equals("ID_REQ_CON_RESPONSE_PACKET") || 
                          packet.tip.equals("ID_REQ_DATA_PACKET") || 
                          packet.tip.equals("ID_PUT_BLOCK_PACKET") || 
                          packet.tip.equals("ID_NETSTAT_PACKET") ||
                          packet.tip.equals("ID_PING_PACKET"))
                      packet.check(sender);
                      
                  }
                  else
                  {
                        // Broadcast packet
                        if (packet instanceof CBroadcastPacket)
			{ 
                            if (!packet.tip.equals("ID_BLOCK"))
                            {
                                // Check
                                packet.check((CBlockPayload)null);
                                
                                // Add to current block
                                UTILS.CBLOCK.addPacket((CBroadcastPacket)packet);
                            
                                // Broadcast packet
                                if (peers.peers.size()>0) 
                                    this.broadcast(packet);
                            }
                        }
                        else if (packet.tip.equals("ID_BLOCK"))
                        {
                            // Deserialize block
                            CBlockPacket block=(CBlockPacket) packet;
                                
                            // Load block
                            this.CONSENSUS.blockReceived(block);
                        }
                        else packet.check(sender);
                    }
                  
                  
                  }
		  catch (Exception ex)
		  {
                      System.out.println(ex.getMessage()+" CNetwork.java - 165");
		  }
	  }
	  
	  public void broadcast(CPacket packet) throws Exception
	  {
              //UTILS.CONSOLE.write("Broadcasting packet ("+packet.tip+")...");
              
              if (peers.peers.size()==0)
              {
                  this.processRequest(packet, new CPeer());
              }
              else
              {
		   // Broadcast packet
		  for (int a=0; a<=peers.peers.size()-1; a++)
		  {
			  CPeer p=(CPeer)peers.peers.get(a);
			  p.writePacket(packet);
		  }
              }
	  }
	  
	  public boolean packetExist(CPacket packet, CPeer sender) throws Exception
	  {
              if (!UTILS.BASIC.isHash(packet.hash)) return true;
              
              ResultSet rs=UTILS.DB.executeQuery("SELECT * "
		      		                 + "FROM rec_packets "
		      		                + "WHERE hash='"+packet.hash+"'");
		      
		      if (UTILS.DB.hasData(rs))
                      {
                          // Return
		    	  return true;
                      }
		      else
                      {
                           // Log packet
                           this.logPacket(packet, sender);
                          
                          // Return
		    	  return false;
                      }
             
	  }
	  
	  public void logPacket(CPacket packet, CPeer peer) throws Exception
	   {
               String adr;
               
               if (peer==null)
                  adr="";
               else
                  adr=peer.adr;
                   
	       UTILS.DB.executeUpdate("INSERT INTO rec_packets(tip, "
                                                    + "fromIP, "
                                                    + "tstamp, "
                                                    + "hash) "
		  		     + "VALUES('"+packet.tip+"', "+ "'"
                                                 +adr+"', "
		  		                 + "'"+String.valueOf(UTILS.BASIC.tstamp())+"', "
		  		                 + "'"+packet.hash+"')");
	   }
	  
	  public void sendToPeer(String peer, CPacket packet) throws Exception
	  {
             if (peers.peers.size()==0)
              {
                  this.processRequest(packet, new CPeer());
              }
              else
              {
		  for (int a=0; a<=peers.peers.size()-1; a++)
		  {
			  CPeer p=(CPeer)peers.peers.get(a);
			  if (p.adr.equals(peer)) 
			  {
				  p.writePacket(packet);
				  
			  }
		  } 
              }
	  }
     
	 public void connectTo(String adr, int port) throws Exception
         {
             this.peers.conect(adr, port);
         }
         
         public void removePeer(String adr)
         {
             this.peers.removePeer(adr);
         }
         
        
}