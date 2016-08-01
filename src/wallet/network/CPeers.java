// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.packets.peers.*;

public class CPeers
{
	private static final SocketAddress SocketAddress = null;

	// Port
	public long port;
	
	// Server
	private ServerSocket server;
	
	// Station
	public CNetwork network;
	
	// Peers
	public ArrayList peers=new ArrayList();
	
	// Peers number
	public int no=0;
	
        // Tick number
        long tick=0;
        
        // Timer
        Timer timer;
        
        // Task
        RemindTask task;
       
        
   CPeers(CNetwork station) throws Exception
   {   
	   this.network=station;
           
           // Timer
             timer = new Timer();
              task=new RemindTask();
              task.parent=this;
             timer.schedule(task, 0, 1000); 
   }
   
   public void lastSeen(String peer) throws Exception
   {
	   for (int a=0; a<=this.peers.size()-1; a++)
	   {
		  CPeer p=(CPeer) this.peers.get(a);   
		  if (p.adr.equals(peer)) p.last_seen=UTILS.BASIC.tstamp();
	   }
   }
   
   public void addPeer(CPeer peer, int port) throws Exception
   {
       try
       {
            // Return if connected
            if (this.conectedTo(peer.adr)) return;
       
            // Add peer	   
            peers.add(peer);
       
            // Add peer
            UTILS.DB.executeUpdate("INSERT INTO peers (peer, "
                                                    + "port, "
                                                    + "in_traffic, "
                                                    + "out_traffic, "
                                                    + "last_seen,"
                                                    + "tstamp) "
                                        + "VALUES('"+peer.adr+"', '"
                                                    +port+"', "
                                                    +"'0', "
                                                    +"'0', '"
                                                    +UTILS.BASIC.tstamp()+"', '"
                                                    +UTILS.BASIC.tstamp()+"')");
       
           // Peer recorded
           
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM peers_pool "
                                    + "WHERE peer='"+peer.adr+"'");
       
           if (UTILS.DB.hasData(rs)==false)
            UTILS.DB.executeUpdate("INSERT INTO peers_pool (peer, "
                                         + "port, "
                                         + "con_att_no, "
                                         + "con_att_last, "
                                         + "accept_con, "
                                         + "banned, "
                                         + "last_seen) "
	   		      + "VALUES ('"+peer.adr+"', '"
                                           +port+"', "
                                           + "'0', "
                                           + "'0', "
                                           + "'ID_YES', "
                                           + "'0', '"
                                           +UTILS.BASIC.tstamp()+"')");
           else
           UTILS.DB.executeUpdate("UPDATE peers "
	   		 + "SET last_seen='"+UTILS.BASIC.tstamp()+"' "
	   	       + "WHERE peer='"+peer.adr+"'");
       
           // Close
          
       
           // Console
           System.out.println("Peer Added "+peer.adr);
       }
       catch (SQLException ex)
       {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CPeers.java", 120);
       }
       catch (Exception ex)
       {
           UTILS.LOG.log("Exception", ex.getMessage(), "CPeers.java", 120);
       }
   }
   
   public void removePeer(CPeer peer) throws Exception
   {
       // Console
       if (this.conectedTo(peer.adr)) 
           System.out.println("Connected peer removed "+peer.adr+" !!!");
       else
           System.out.println("Peer removed "+peer.adr+" !!!");
       
       // Close peer
       peer.close();
       
       // Removes from list
       this.peers.remove(peer);
       
       // Update db
       UTILS.DB.executeUpdate("DELETE FROM peers "
                            + "WHERE peer='"+peer.adr+"'");
   }
   
   public void removePeer(String peer) 
   {
       
       try
       {
       //Remove from peers list
       for (int a=0; a<=this.peers.size()-1; a++)
       {
           CPeer p=(CPeer) this.peers.get(a);   
	   if (p.adr.equals(peer)) this.removePeer(p);
       } 
       
      for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
         System.out.println(ste);
      }
       
       // Delete from db
       UTILS.DB.executeUpdate("DELETE FROM peers WHERE peer='"+peer+"'"); 
       
       // Peer removed
       System.out.println("Peer removed "+peer+" !!!");
       }
       catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
   }
   
   public CPeer conect(String adr, int port)
   {
       try
       {
          CPeer peer=new CPeer(this, adr, port);
          
          if (peer.client!=null)
          {
             // Start peer
             peer.start(); 
             Thread.sleep(1000);
		   
             // Initiate connection
             CReqConnectionPacket packet=new CReqConnectionPacket();
             peer.writePacket(packet);
      
             // Return
             return peer;
          }
       }
       catch (InterruptedException ex)
       {
              UTILS.LOG.log("InterruptedException", ex.getMessage(), "CPeers.java", 161);
       }
       catch (Exception ex)
       {
              UTILS.LOG.log("InterruptedException", ex.getMessage(), "CPeers.java", 161);
       }
       
     return null;
   }
   
   
   
   public void getNewPeers() throws Exception
   {
      if (this.peers.size()>0)
      {
         ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS no FROM peers_pool");
      
         if (UTILS.DB.hasData(rs))
           if (rs.getLong("no")<250)
           {
              CGetPeersPacket packet=new CGetPeersPacket();
              this.broadcast(packet);
           }
      }
   }
   
   public void checkPendingPeers() throws Exception
   {
      if (this.peers.size()<3)
          {
             // Select pending peers
             
             ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                      + "FROM peers_pool "
                                  + "ORDER BY rand() "
                                     + "LIMIT 0,1");
       
     
         
             if (UTILS.DB.hasData(rs))
             {
               // Next
               rs.next();
            
               // Connect
               if (this.conectedTo(rs.getString("peer"))==false) 
               {
                  System.out.println("Connecting to "+rs.getString("peer")+"+..");
                  UTILS.NETWORK.connectTo(rs.getString("peer"), rs.getInt("port"));
               }
             }
          
         
          
          }
       
   }
   
   public void removeInactives() throws Exception
   {
       try
       {
           
           
           // Load data
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM peers "
                                      + "WHERE last_seen<"+String.valueOf(UTILS.BASIC.tstamp()-600));
           
           // Remove peers
           while (rs.next()) 
               this.removePeer(rs.getString("peer"));
           
           // Close connection
         
       }
       catch (SQLException ex)
       {
           UTILS.LOG.log("SQLexception", ex.getMessage(), "CPeers.java", 209);
       }
       catch (Exception ex)
       {
           UTILS.LOG.log("Exception", ex.getMessage(), "CPeers.java", 284);
       }
       
   }
   
   class RemindTask extends TimerTask 
   {  
       public CPeers parent;
               
       @Override
       public void run() 
       {  
           try
           {  
              // Tick
              tick++;
        
              // Try to connect to pending peers
              if (tick % 10==0) 
                 checkPendingPeers();
           
               // Removes inactive peers
               removeInactives();
               
               // Check peers pool connectivity
               if (tick % 60==0) 
                   checkPeersConn();
           }
           catch (Exception ex) 
       	   {  
       		UTILS.LOG.log("Exception", ex.getMessage(), "CBuyDomainPayload.java", 57);
           }
       }
   }
  
   public void connFailed(String peer) throws Exception
   {
       UTILS.DB.executeUpdate("UPDATE peers_pool "
                              + "SET accept_con='ID_PENDING', "
                                  + "con_att_no=con_att_no+1, "
                                  + "con_att_last='"+UTILS.BASIC.tstamp()+"' "
                            + "WHERE peer='"+peer+"'");
       
       // Dead peers
       UTILS.DB.executeUpdate("DELETE FROM peers_pool WHERE con_att_no>5");
   }
   
   public void portOpen(String ip, int port)
   {
       try
       {
          // Cerate socket
          Socket socket = new Socket();
          
          // Open socket
          socket.connect(new InetSocketAddress(ip, port), 1000);
          
          UTILS.DB.executeUpdate("UPDATE peers_pool "
                                  + "SET con_att_no=0, "
                                      + "accept_con='ID_YES', "
                                      + "con_att_last='"+UTILS.BASIC.tstamp()+"' "
                                + "WHERE peer='"+ip+"'");
          
          // Close socket
          socket.close();
        } 
       catch (Exception ex) 
       {
          try
          {
              this.connFailed(ip);
          }
          catch (Exception e)
          {
              
          }
       }
   }
   
   
   public void checkPeersConn() throws Exception
   {
       if (this.peers.size()>0)
       {
          // Load pending peers
          ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                             + "FROM peers_pool "
                                            + "WHERE (accept_con='ID_PENDING' AND con_att_last<"+(UTILS.BASIC.tstamp()-600)+") "
                                               + "OR (accept_con='ID_YES' AND con_att_last<"+(UTILS.BASIC.tstamp()-3600)+")");
       
          while (rs.next())
          {
              if (!InetAddress.getByName(rs.getString("peer")).isReachable(1000))
                   UTILS.DB.executeUpdate("DELETE FROM peers_pool "
                                              + "WHERE peer='"+rs.getString("peer")+"'");
              else
                   this.portOpen(rs.getString("peer"), rs.getInt("port"));
          }
       }
   }
   
   public void broadcast(CPacket packet)  throws Exception
   {
        // Broadcast packet
        for (int a=0; a<=this.peers.size()-1; a++)
        {
	    CPeer p=(CPeer)peers.get(a);
	    p.writePacket(packet);
	}
		  
        System.out.println("Broadcasted packet...");
    }
   
   public boolean conectedTo(String adr) throws Exception
   {
	   for (int a=0; a<=this.peers.size()-1; a++)
	   {
	      CPeer peer=(CPeer) this.peers.get(a);
	      if (peer.adr.equals(adr)) return true;
	   }
	   
	   return false;
   }
   
   public boolean checkCon(Socket so) throws Exception
   {
       try
       {
       // Address
       String IP=so.getInetAddress().getHostAddress();
       
       // Port
       int port=so.getPort();
       
       // Self
       if (IP.equals("127.0.0.1"))
           return false;
       
       // Already connected
       if (this.conectedTo(IP))
           return false;
       
       // Search for the same IP
       
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM con_log "
                                    + "WHERE IP='"+IP+"' "
                                      + "AND tstamp>"+(UTILS.BASIC.tstamp()-5));
       if (UTILS.DB.hasData(rs)==true)
           return false;
       
       // Close
      
       
       // Record connection
       UTILS.DB.executeUpdate("INSERT INTO con_log (IP, "
                                           + "port, "
                                           + "tstamp) "
                           + "VALUES('"+IP+"', '"
                                       +port+"', '"
                                       +UTILS.BASIC.tstamp()+"')");
       }
       catch (SQLException ex)
       {
           // Log
           UTILS.LOG.log("SQLException", ex.getMessage(), "CPeers.java", 390);
           
           // Return
           return false;
       }
        
       return true;
   }
   
   public void serverStart(int port) throws Exception
   {
	   this.port=port;	
	   
	   try
	   {
	     server=new ServerSocket(port);
	     
	     System.out.println("Server started on port "+port+"+");  
	     
	     while (true) 
	     {
	    	// Creates the socket 
                Socket s=server.accept();
	        
                // Check remote adr
                if (this.checkCon(s)==true)
                {
	    	  CPeer p=new CPeer(this, s);
	    	  p.start();
                }
               
	     }
	   }
	   catch (SocketTimeoutException ex) 
	   { 
	       UTILS.LOG.log("SocketTimeoutException", ex.getMessage(), "CPeers.java", 88);
               UTILS.LOG.log("Exiting application", "EXIT", "CPeers.java", 471);
               System.exit(0); 
	   }
	   catch (SocketException ex) 
	   { 
	       UTILS.LOG.log("SocketException", ex.getMessage(), "CPeers.java", 88);
               UTILS.LOG.log("Exiting application", "EXIT", "CPeers.java", 478);
               System.exit(0); 
	   }
	   catch (IOException ex) 
	   { 
	       UTILS.LOG.log("IOException", ex.getMessage(), "CPeers.java", 88);
               UTILS.LOG.log("Exiting application", "EXIT", "CPeers.java", 485);
               System.exit(0); 
	   }
   }
   
}