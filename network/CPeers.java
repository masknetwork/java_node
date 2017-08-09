// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.packets.peers.*;
import wallet.network.packets.sync.CPing;

public class CPeers extends Thread
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
   }
   
   @Override
   public void run()
   {
       try
       {
           // Timer
           timer = new Timer();
           task=new RemindTask();
           task.parent=this;
           timer.schedule(task, 0, 1000); 
        
           // Server start
           this.serverStart(UTILS.SETTINGS.port); 	
       }
       catch (Exception ex)
       {
           System.out.println(ex.getMessage());
       }
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
        // Return if connected
        if (this.conectedTo(peer.adr)) return;
       
        // Add peer	   
        peers.add(peer);
        
        // Address ?
       if (!UTILS.BASIC.isIP(peer.adr))
           throw new Exception("Invalid peer - CPeers.java, 76");
       
        // Add peer
        UTILS.DB.executeUpdate("INSERT INTO peers "
                                     + "SET peer='"+peer.adr+"', "
                                         + "port='"+port+"', "
                                         + "in_traffic='0', "
                                         + "out_traffic='0', "
                                         + "last_seen='"+UTILS.BASIC.tstamp()+"',"
                                         + "tstamp='"+UTILS.BASIC.tstamp()+"'");
       
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM peers_pool "
                                          + "WHERE peer='"+peer.adr+"'");
       
           if (UTILS.DB.hasData(rs)==false)
            UTILS.DB.executeUpdate("INSERT INTO peers_pool "
                                         + "SET peer='"+peer.adr+"', "
                                             + "port='"+port+"', "
                                             + "con_att_no='0', "
                                             + "con_att_last='0', "
                                             + "accept_con='ID_YES', "
                                             + "banned='0', "
                                             + "last_seen='"+UTILS.BASIC.tstamp()+"'");
           else
           UTILS.DB.executeUpdate("UPDATE peers "
	   		           + "SET last_seen='"+UTILS.BASIC.tstamp()+"' "
	   	                 + "WHERE peer='"+peer.adr+"'");
       
           
           // Console
           System.out.println("Peer Added "+peer.adr);
      
   }
   
   public void removePeer(CPeer peer) throws Exception
   {
       // Console
       System.out.println("Peer removed "+peer.adr+" !!!");
       
       // Close peer
       peer.close();
       
       // Removes from list
       this.peers.remove(peer);
       
       // address
       if (!UTILS.BASIC.isIP(peer.adr))
           throw new Exception("Invalid peer - CPeers.java, 124");
       
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
      
       // Is adr
       if (!UTILS.BASIC.isIP(peer))
           throw new Exception("Invalid peer - CPeers.java, 145");
       
       // Delete from db
       UTILS.DB.executeUpdate("DELETE FROM peers "
                                  + "WHERE peer='"+peer+"'"); 
       
       // Peer removed
       System.out.println("Peer removed "+peer+" !!!");
       }
       catch (Exception ex) 
       {  
            System.out.println(ex.getMessage() + " - CPeers.java, 146");
       }
   }
   
   public CPeer conect(String adr, int port) throws Exception
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
       catch (Exception ex)
       {
          System.out.println(ex.getMessage() + " - CPeers.java, 172");
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
      if (this.peers.size()<UTILS.SETTINGS.min_peers)
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
                  System.out.println("Connecting to "+rs.getString("peer")+"...");
                  UTILS.NETWORK.connectTo(rs.getString("peer"), rs.getInt("port"));
               }
             }
     }    
   }
   
   public void removeInactives() throws Exception
   {
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM peers "
                                          + "WHERE last_seen<"+String.valueOf(UTILS.BASIC.tstamp()-300));
           
        // Remove peers
        while (rs.next()) 
          this.removePeer(rs.getString("peer"));
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
               
               // Ping
               if (tick % 60==0) 
               {
                  CPing ping=new CPing();
                  UTILS.NETWORK.broadcast(ping);
               }
           }
           catch (Exception ex) 
       	   {  
               System.out.println(ex.getMessage() + " - CPeers.java, 267");
           }
       }
   }
  
   public void connFailed(String peer) throws Exception
   {
       if (!UTILS.BASIC.isIP(peer))
           throw new Exception("Invalid peer - CPeers.java, 285");
       
       UTILS.DB.executeUpdate("UPDATE peers_pool "
                              + "SET accept_con='ID_PENDING', "
                                  + "con_att_no=con_att_no+1, "
                                  + "con_att_last='"+UTILS.BASIC.tstamp()+"' "
                            + "WHERE peer='"+peer+"'");
       
       // Dead peers
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM peers_pool "
                                         + "WHERE con_att_no>5");
       if (UTILS.DB.hasData(rs))
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
          
          if (!UTILS.BASIC.isIP(ip))
           throw new Exception("Invalid peer - CPeers.java, 308");
          
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
       // Sync ?
       if (UTILS.STATUS.engine_status.equals("ID_SYNC"))
           return;
           
       // Load pending peers
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM peers_pool "
                                         + "WHERE (accept_con='ID_PENDING' AND con_att_last<"+(UTILS.BASIC.tstamp()-600)+") "
                                            + "OR (accept_con='ID_YES' AND con_att_last<"+(UTILS.BASIC.tstamp()-3600)+")");
       
          while (rs.next())
          {
              if (!this.conectedTo(rs.getString("peer")))
              {
                 if (!InetAddress.getByName(rs.getString("peer")).isReachable(1000))
                   this.connFailed(rs.getString("peer"));
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
              
	      if (peer.adr.equals(adr)) 
              {
                  if (!UTILS.BASIC.isIP(adr))
                        throw new Exception("Invalid peer - CPeers.java, 376");
                  
                  // Peer exist ?
                  ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM peers "
                                                    + "WHERE peer='"+adr+"'");
                  
                  // Has data ?
                  if (!UTILS.DB.hasData(rs))
                  {
                      // Remove peer
                      this.removePeer(peer);
                      
                      // Return
                      return false;
                  }
                  
                  // Return
                  return true;
              }
	   }
	   
	   return false;
   }
   
   public boolean isWhiteListed(String IP) throws Exception
   {
       // Valid IP
       if (!UTILS.BASIC.isIP(IP))
           throw new Exception("Invalid IP - CPeers.java, 405");
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM ips "
                                         + "WHERE IP='"+IP+"' "
                                           + "AND status='ID_WHITELIST'");
       
       // Has data ?
       if (UTILS.DB.hasData(rs))
           return true;
       else
           return false;
   }
   
   public boolean isBlackListed(String IP) throws Exception
   {
       // Valid IP
       if (!UTILS.BASIC.isIP(IP))
           throw new Exception("Invalid IP - CPeers.java, 405");
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM ips "
                                         + "WHERE IP='"+IP+"' "
                                           + "AND status='ID_BLACKLIST'");
       
       // Has data ?
       if (UTILS.DB.hasData(rs))
           return true;
       else
           return false;
   }
   
   public boolean whitelistExist() throws Exception
   {
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM ips "
                                         + "WHERE status='ID_WHITELIST'");
       
       // Has data ?
       if (UTILS.DB.hasData(rs))
           return true;
       else
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
           
           // Sync ?
           if (UTILS.STATUS.engine_status.equals("ID_SYNC"))
           {
               System.out.println("Refunsng connection of "+IP+" - we are syncing");
               return false;
           }
           
            // Already connected
           if (this.conectedTo(IP))
           {
               System.out.println("Refunsng connection of "+IP+" - already connected");
               return false;
           }
           
           if (!UTILS.BASIC.isIP(IP))
               throw new Exception("Invalid peer - CPeers.java, 426");
           
           // Blacklisted ?
           if (this.isBlackListed(IP))
               throw new Exception("Blacklisted IP - CPeers.java, 426");
           
           // Whitelisted ?
           if (this.whitelistExist())
             if (this.isWhiteListed(IP))
               throw new Exception("IP is not whitelisted - CPeers.java, 426");
           
           // Record connection
           UTILS.DB.executeUpdate("INSERT INTO con_log "
                                        + "SET IP='"+IP+"', "
                                           + "port='"+port+"', "
                                           + "tstamp='"+UTILS.BASIC.tstamp()+"'");
       }
       catch (SQLException ex)
       {
           // Log
           System.out.println(ex.getMessage() + " - CPeers.java, 416");
           
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
	     
	     System.out.println("Server started on port "+port);  
	     
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
                else 
                {
                    s.close();
                }
               
	     }
	   }
	   catch (Exception ex) 
	   { 
	       System.out.println(ex.getMessage() + " - CPeers.java, 455");
           }
	   
   }
   
}