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
        
   CPeers(CNetwork station)
   {   
	   this.network=station;
           
           // Timer
             timer = new Timer();
              task=new RemindTask();
              task.parent=this;
             timer.schedule(task, 0, 1000); 
   }
   
   public void lastSeen(String peer)
   {
	   for (int a=0; a<=this.peers.size()-1; a++)
	   {
		  CPeer p=(CPeer) this.peers.get(a);   
		  if (p.adr.equals(peer)) p.last_seen=UTILS.BASIC.tstamp();
	   }
   }
   
   public void addPeer(CPeer peer, int port)
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
       Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       ResultSet rs=s.executeQuery("SELECT * "
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
       if (s!=null) s.close();
       
       // Console
       UTILS.CONSOLE.write("Peer Added "+peer.adr);
       }
       catch (SQLException ex)
       {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CPeers.java", 120);
       }
   }
   
   public void removePeer(CPeer peer)
   {
       // Console
       if (this.conectedTo(peer.adr)) 
           UTILS.CONSOLE.write("Connected peer removed "+peer.adr+" !!!");
       else
           UTILS.CONSOLE.write("Peer removed "+peer.adr+" !!!");
       
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
       //Remove from peers list
       for (int a=0; a<=this.peers.size()-1; a++)
       {
           CPeer p=(CPeer) this.peers.get(a);   
	   if (p.adr.equals(peer)) this.removePeer(p);
       } 
       
       // Delete from db
       UTILS.DB.executeUpdate("DELETE FROM peers WHERE peer='"+peer+"'"); 
       
       // Peer removed
       UTILS.CONSOLE.write("Peer removed "+peer+" !!!");
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
       
     return null;
   }
   
   // Maintain the minimum peers number
   public void maintainPeers()
   {
      if (!UTILS.STATUS.engine_status.equals("ID_CONNECTING")) return;
	   	   
	   for (int a=0; a<=this.peers.size()-1; a++)
	   {
		  CPeer p=(CPeer) this.peers.get(a);   
		  System.out.println("Connected to "+p.adr+" ("+p.last_seen+")");
		//  if (p.last_seen<(UTILS.BASIC.tstamp()-100))
		//	    this.removePeer(p);
	   }
      
     	 if (peers.size()<UTILS.SETTINGS.min_peers)
     	 {
     		 try
     		 {
     		    int dif=UTILS.SETTINGS.min_peers-peers.size();
     		 
     		    // Loads peers
                    Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
 		        ResultSet rs=s.executeQuery("SELECT * "
 		  		                               + "FROM peers "
 		  		                              + "WHERE connected=0 "
 		  		                                + "AND last_con_att<"+(UTILS.BASIC.tstamp()-1000)
 		  		                         + " ORDER BY RAND() "
 		  		                             + "LIMIT 0, "+dif);
 		    
 		       if (UTILS.DB.hasData(rs))
 		       { 
 		          while (rs.next())
 		          {
 		        	    UTILS.CONSOLE.write("Low peer numbers. Connecting to "+rs.getString("peer"));
 			        	conect(rs.getString("peer"), rs.getInt("port"));
 		          }
 		       }
                       
                       // Close
                       if (s!=null) s.close();
 		       
     		 }
     		 catch (SQLException ex)
     		 {
     			 UTILS.LOG.log("SQLException", ex.getMessage(), "CPeers.java", 110);
     		 }
     	 }    
   }
   
   public void getNewPeers()
   {
      if (this.peers.size()>0)
       {
           CGetPeersPacket packet=new CGetPeersPacket();
           this.broadcast(packet);
       }
   }
   
   public void checkPendingPeers()
   {
       try
       {
           
       // Delete inactive peers having over 3 connection attempts
       UTILS.DB.executeUpdate("DELETE FROM peers_pool WHERE con_att_no>3");
       
       // Set to pending peers that are active and last seen more than an hour ago
       Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       ResultSet rs=s.executeQuery("SELECT * "
                                     + "FROM peers_pool "
                                     + "WHERE last_seen<"+String.valueOf(UTILS.BASIC.tstamp()-36));
        
       if (UTILS.DB.hasData(rs))
        {
          while (rs.next())
          {
            String peer=rs.getString("peer");
            if (this.conectedTo(peer)==false) 
               UTILS.DB.executeUpdate("UPDATE peers_pool "
                                 + "SET accept_con='ID_PENDING' "
                               + "WHERE peer='"+rs.getString("peer")+"'");
          }
        }
       
        // Close
        if (s!=null) s.close();
        
       // Select pending peers
       s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       rs=s.executeQuery("SELECT * "
                                     + "FROM peers_pool "
                                    + "WHERE accept_con='ID_PENDING' "
                                 + "ORDER BY RAND() "
                                    + "LIMIT 0, 10");
       
     
         
          if (UTILS.DB.hasData(rs))
          {
            while (rs.next())
            {
              if (this.conectedTo(rs.getString("peer"))==false) 
              {
                CPeer temp=new CPeer(UTILS.NETWORK.peers, rs.getString("peer"), rs.getInt("port"));
                temp.start();
              }
            }
          }
          
          // Close
          s.close();
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
             
         // Tick
         // tick++;
        
          // Finds new peers
          //if (tick % 3600==0)  
          //      parent.getNewPeers();
           
           // Try to connect to pending peers
           //if (tick % 1800==0) 
           //      parent.checkPendingPeers();
       }
   }
 
   
     // List all threads and recursively list all subgroup
     void listThreads(ThreadGroup group, String indent) {
        System.out.println(indent + "Group[" + group.getName() + 
        		":" + group.getClass()+"]");
        int nt = group.activeCount();
        Thread[] threads = new Thread[nt*2 + 10]; //nt is not accurate
        nt = group.enumerate(threads, false);

        // List every thread in the group
        for (int i=0; i<nt; i++) {
            Thread t = threads[i];
            System.out.println(indent + "  Thread[" + t.getName() 
            		+ ":" + t.getClass() + "]");
        }

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng*2 + 10];
        ng = group.enumerate(groups, false);

        for (int i=0; i<ng; i++) {
            listThreads(groups[i], indent + "  ");
        }
    }
   
   public void broadcast(CPacket packet) 
   {
        // Broadcast packet
        for (int a=0; a<=this.peers.size()-1; a++)
        {
	    CPeer p=(CPeer)peers.get(a);
	    p.writePacket(packet);
	}
		  
        UTILS.CONSOLE.write("Broadcasted packet...");
    }
   
   public boolean conectedTo(String adr)
   {
	   for (int a=0; a<=this.peers.size()-1; a++)
	   {
	      CPeer peer=(CPeer) this.peers.get(a);
	      if (peer.adr.equals(adr)) return true;
	   }
	   
	   return false;
   }
   
   public boolean checkCon(Socket so)
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
       Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       ResultSet rs=s.executeQuery("SELECT * "
                                     + "FROM con_log "
                                    + "WHERE IP='"+IP+"' "
                                      + "AND tstamp>"+(UTILS.BASIC.tstamp()-5));
       if (UTILS.DB.hasData(rs)==true)
           return false;
       
       // Close
       if (s!=null) s.close();
       
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
   
   public void serverStart(int port)
   {
	   this.port=port;	
	   
	   try
	   {
	     server=new ServerSocket(port);
	     
	     UTILS.CONSOLE.write("Server started on port "+port+".");  
	     
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
                else s.close();
	     }
	   }
	   catch (SocketTimeoutException ex) 
	   { 
	       UTILS.LOG.log("SocketTimeoutException", ex.getMessage(), "CPeers.java", 88);
	   }
	   catch (SocketException ex) 
	   { 
	       UTILS.LOG.log("SocketException", ex.getMessage(), "CPeers.java", 88);
	   }
	   catch (IOException ex) 
	   { 
	       UTILS.LOG.log("IOException", ex.getMessage(), "CPeers.java", 88);
	   }
   }
   
}