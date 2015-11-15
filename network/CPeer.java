package wallet.network;

import java.net.*;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

import wallet.kernel.*;
import wallet.network.packets.*;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;


public class CPeer extends Thread 
{
	// Client
	private Socket client;
	
	// Input stream
	private ObjectInputStream in;
	
	// Output stream
    private ObjectOutputStream out;
    
    // Peers
    private CPeers peers;
    
    // Address
    public String adr;
    
    // Port
    public int port;
     
    // Input stream count
    public CountingInputStream in_count;
    
    // Output stream 
    public CountingOutputStream out_count;
    
    // Last seen
    long last_seen=0;
    
    // Task
    RemindTask task;
    
    // Timer
    Timer timer;
    
    // Created
    long created=0;
    
    // Active
    boolean active=false;
    
    // Mode
    String mode="";
    
    // Schedule delete in 2 seconds
    boolean schedule_delete=false;
   
	CPeer(CPeers peers, String adr, int port)
	{
           UTILS.CONSOLE.write("New peer created to "+adr);
           
           // Address
	   this.adr=adr;
		
	   // Peers
	   this.peers=peers;
                
           // Port
           this.port=port;
           
           // Mode
           this.mode="ID_MODE_TO";
           
           // Already connected
           if (peers.conectedTo(this.adr))
           {
             this.schedule_delete=true;
             UTILS.CONSOLE.write(this.adr+" will be removed");
           }
           
           try
           {
              // Client
              client=new Socket(InetAddress.getByName(adr), port);
           }
	   catch (IOException ex) 
	   { 
	       UTILS.LOG.log("IOException", ex.getMessage(), "CPeer.java", 67); 
               this.couldNotConnect();
	   }
	}
	
   CPeer(CPeers peers, Socket client) throws SocketException
   {   
          // Adr
	  this.adr=client.getInetAddress().getHostAddress();
          
          // Write
          UTILS.CONSOLE.write("New peer created by client "+this.adr);
          
          // Already connected
          if (peers.conectedTo(this.adr))
          {
              this.schedule_delete=true;
              UTILS.CONSOLE.write(this.adr+" will be removed");
          }
          
          // Port
          this.port=client.getPort();
	  
	  // Client
	  this.client=client;
	  
  	  // Peers
          this.peers=peers;
   }
   
   public void couldNotConnect()
   {
       UTILS.DB.executeUpdate("UPDATE peers_pool "
                         + "SET con_att_no=con_att_no+1, "
                             + "con_att_last='"+UTILS.BASIC.tstamp()
                             +"', accept_con='ID_NO' "
                             + "WHERE peer='"+this.adr+"'");
   }
   
   public void close()
   {
       try
       {
         if (client!=null) client.close();
         
         if (this.in!=null) in.close();
         if (this.out!=null) this.out.close();
         
         if (timer!=null)
         {
           timer.cancel();
           timer.purge();
         }
       }
       catch (IOException ex)
       {
           UTILS.LOG.log("IOException", ex.getMessage(), "CPeer.java", 131);
       }
   }
   
   public void conAtt(String peer)
   {
	   UTILS.DB.executeUpdate("UPDATE peers "
	   		             + "SET last_con_att='"+UTILS.BASIC.tstamp()+"' "
	   		           + "WHERE peer='"+peer+"'");
   }
   
   
   public void startMonitor()
   {
 	 
   }
  
   
   class RemindTask extends TimerTask 
   {  
       public CPeer parent;
               
       @Override
       public void run() 
       {  
          // Record traffic
           if (parent.in_count!=null && parent.out_count!=null)
           UTILS.DB.executeUpdate("UPDATE peers "
                            + "SET in_traffic='"+parent.in_count.getByteCount()+"', "
                                + "out_traffic='"+parent.out_count.getByteCount()+"' "
                          + "WHERE peer='"+parent.adr+"'");
           
         
     	  // If peer is active but no connection has been established in 2 sec 
           if (UTILS.NETWORK.peers.conectedTo(parent.adr)==false || parent.schedule_delete==true)
           {
               if (UTILS.BASIC.tstamp()-parent.created>2)
               {
                   UTILS.CONSOLE.write("Removed for inactivity "+parent.adr);
                   parent.close();
               }
           }
       }
   }
   
   public void run()
   {	   
	   try
	   {
              // Created
              this.created=UTILS.BASIC.tstamp();
              
              // Timer
              timer = new Timer();
              task=new RemindTask();
              task.parent=this;
              timer.schedule(task, 0, 1000); 
              
              // Output stream
              out_count=new CountingOutputStream(client.getOutputStream());
	      out=new ObjectOutputStream(out_count);
              
              // Input stream
              in_count=new CountingInputStream(client.getInputStream());
	      in=new ObjectInputStream(in_count);
       
	      // Mode
              if (this.mode.equals("ID_MODE_TO"))
                 UTILS.DB.executeUpdate("UPDATE peers_pool "
                                + "SET accept_con='ID_YES', "
                                + "con_att_no=0, "
                                + "con_att_last=0, "
                                + "last_seen='"+UTILS.BASIC.tstamp()+"'"
                              + "WHERE peer='"+this.adr+"'");
              
               // Wait for data
	       while (true)
               {
                 CPacket packet=(CPacket)in.readObject();
		 UTILS.NETWORK.processRequest(packet, this); 
               }
	   }
	   catch (EOFException ex) 
	   { 
	       this.peers.removePeer(this); 
	   }
	   catch (IOException ex) 
	   { 
		   UTILS.LOG.log("IOException", ex.getMessage(), "CPeer.java", 93);
	   }
	   catch (ClassNotFoundException ex) 
	   { 
		   UTILS.LOG.log("ClassNotFoundException", ex.getMessage(), "CPeer.java", 93);
	   }
	   catch (Exception ex) 
	   { 
		   UTILS.LOG.log("Exception", ex.getMessage(), "CPeer.java", 93);
	   }
   }
   
   public void writePacket(CPacket packet)
   {   
	   try
	   {
		   out.writeObject(packet);
                   out.flush();
                   UTILS.CONSOLE.write(" Written "+packet.tip+" to "+this.adr);
                   packet=null;
	   }
	   catch (IOException e) 
	   { 
		   UTILS.LOG.log("IOException", e.getMessage(), "CPeer.java", 252); 
	   }
   }
}