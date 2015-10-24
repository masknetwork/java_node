package wallet.network;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.*;
import wallet.network.packets.blocks.*;

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
	

    public CNetwork() 
    {
        // Initialized
	UTILS.CONSOLE.write("Network initialized....");
		  
	// Conected peers
	this.peers=new CPeers(this); 
        
        // Transaction pooll
        this.TRANS_POOL=new CTransPool();
       
    }
	
	  public void init()
	  {
		  
	  }
	  
	  public void run()
	  {
	      // Start the server
              this.peers.serverStart(UTILS.SETTINGS.port); 	
              
              // Boot
              this.bootstrap();
	  }
	  
	  public void bootstrap()
	  {
	      // Load bootstrap nodes
	      for (int a=1; a<=100; a++)
              {
		   if (UTILS.SETTINGS.settings.containsKey("add_boot_"+String.valueOf(a)))
		   {
		    	String p=UTILS.SETTINGS.settings.getProperty("add_boot_"+String.valueOf(a));
		    	String v[]=p.split("\\:");
		        
                        // Connect to boot node
                        this.connectTo(v[0], Integer.parseInt(v[1]));
                   }
	      }	  
	  }
	  
	  public boolean isConPacket(String tip)
          {
            if (tip.equals("ID_PING_PACKET") || 
                tip.equals("ID_PONG_PACKET") || 
                tip.equals("ID_REQ_CON_PACKET") || 
                tip.equals("ID_REQ_CON_RESPONSE_PACKET"))
              return true;
            else
              return false;
          }
          
          public void seen(String adr)
          {
              // Updates last seen
	      UTILS.DB.executeUpdate("UPDATE peers "
                                      + "SET last_seen='"+UTILS.BASIC.tstamp()+"' "
                                    + "WHERE peer='"+adr+"'");
          }
          
	  public void processRequest(CPacket packet, CPeer sender)
	  {
	     try
	     {
		  // Last seen
                  if (sender!=null) seen(sender.adr);
		  
                   // Console
		  UTILS.CONSOLE.write("Received packet........... : "+packet.tip+" ("+packet.hash+")");
                  
		  // Already processed
		  if (this.packetExist(packet, sender)) return;
                  
                  // If relay mode on, return any packet without processing
		  if (UTILS.SETTINGS.relay==true) this.sendToPeer(sender.adr, packet);
                  
		  // Process packet
                  if (UTILS.SETTINGS.relay==false)
		  {
		        // Check packet
                        CResult res;  
                        if (packet.tip.equals("ID_REQ_CON_PACKET") || 
                            packet.tip.equals("ID_REQ_CON_RESPONSE_PACKET") || 
                            packet.tip.equals("ID_GET_PEERS_PACKET"))
                              res=packet.checkWithPeer(sender);
                        else 
                            if (!packet.tip.equals("ID_BLOCK"))
                        {
			      res=packet.check(null);
                              
                               if (res.passed==false) 
			       {
			         res.report();
			         return;
			        }
                        }
                              
			// Broadcast packet
                        if (packet instanceof CBroadcastPacket)
			{ 
                            if (!packet.tip.equals("ID_BLOCK"))
                            {
                              // Add to current block
                              UTILS.CBLOCK.addPacket(packet);
                            
                              // Broadcast packet
                              if (peers.peers.size()>0) 
                                  this.broadcast(packet);
                            }
                        }
                        
                        if (packet.tip.equals("ID_BLOCK"))
                        {
                                CBlockPacket block=(CBlockPacket) packet;
                                res=block.check();
                                
                                if (res.passed) 
                                {
                                    res=block.commit();
                                    if (peers.peers.size()>0) 
                                        this.broadcast(packet);
                                }
                                else res.report();
                            }
			}
		  }
		  catch (Exception ex)
		  {
			  UTILS.LOG.log("Exception", ex.getMessage(), "CNetwork.java", 220);
		  }
	  }
	  
	  public void broadcast(CPacket packet)
	  {
              UTILS.CONSOLE.write("Broadcasting packet ("+packet.tip+")...");
               
              if (peers.peers.size()==0)
              {
                  this.processRequest(packet, null);
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
	  
	  public boolean packetExist(CPacket packet, CPeer sender)
	  {
              try
              {
                  Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		  ResultSet rs=s.executeQuery("SELECT * "
		      		                             + "FROM packets "
		      		                            + "WHERE hash='"+packet.hash+"'");
		      
		      if (UTILS.DB.hasData(rs))
                      {
                          // Close
                          if (s!=null) s.close();
                          
                          // Return
		    	  return true;
                      }
		      else
                      {
                           // Log packet
                           this.logPacket(packet, sender);
                   
                          // Close
                          if (s!=null) s.close();
                          
                          // Return
		    	  return false;
                      }
              }
              catch (SQLException ex)
              {
                  // Log
                  UTILS.LOG.log("SQLException", ex.getMessage(), "CNetwork.java", 181);
                  
                  // Return
                  return false;
              }
	  }
	  
	  public void logPacket(CPacket packet, CPeer peer)
	   {
               String adr;
               
               if (peer==null)
                  adr="";
               else
                  adr=peer.adr;
                   
	       UTILS.DB.executeUpdate("INSERT INTO packets(tip, "
                                                    + "fromIP, "
                                                    + "tstamp, "
                                                    + "hash) "
		  		     + "VALUES('"+packet.tip+"', "+ "'"
                                                 +adr+"', "
		  		                 + "'"+String.valueOf(UTILS.BASIC.tstamp())+"', "
		  		                 + "'"+packet.hash+"')");
	   }
	  
	  public void sendToPeer(String peer, CPacket packet)
	  {
		  for (int a=0; a<=peers.peers.size()-1; a++)
		  {
			  CPeer p=(CPeer)peers.peers.get(a);
			  if (p.adr.equals(peer)) 
			  {
				  p.writePacket(packet);
				  UTILS.CONSOLE.write(System.currentTimeMillis() +": Dedicated message to "+p.adr);
			  }
		  } 
	  }
     
	 public void connectTo(String adr, int port)
         {
             this.peers.conect(adr, port);
         }
         
         public void removePeer(String adr)
         {
             this.peers.removePeer(adr);
         }
}