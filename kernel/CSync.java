package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.sync.CReqDataPacket;

import wallet.network.CPeer;
import wallet.network.packets.sync.CNetStatPacket;


public class CSync extends Thread 
{
    // Adr
    public String adr;
    
    // Ads
    public String ads;
    
    // Agents
    public String agents;
    
    // Agents
    public String agents_feeds;
    
    // Assets
    public String assets;
    
    // Assets owners
    public String assets_owners;
    
    // Assets markets
    public String assets_mkts;
    
    // Assets markets pos
    public String assets_mkts_pos;
    
    // Comments
    public String comments;
    
    // Delegates votes
    public String del_votes;
    
    // Delegates 
    public String delegates;
    
    // Domains
    public String domains;
    
    // Escrowed
    public String escrowed;
    
    // Feeds
    public String feeds;
    
    // Feeds branches
    public String feeds_branches;
    
    // Feeds bets
    public String feeds_bets;
    
    // Feeds bets pos
    public String feeds_bets_pos;
    
    // Profiles
    public String profiles;
    
    // Storage
    public String storage;
    
    // Tweets
    public String tweets;
    
    // Tweets follow
    public String tweets_follow;
    
    // Tweets likes
    public String votes;
    
    // Started
    long start=0;
   
    public CSync() throws Exception 
    {
       // Clear blocks pool
       UTILS.DB.executeUpdate("DELETE FROM blocks_pool");
       
       // Start
       this.start=UTILS.BASIC.tstamp();
       
        // Update sync start
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                   + "SET sync_start='"+UTILS.BASIC.tstamp()+"'");
    }
        
        public String getFreePeer() throws Exception
        {
            // Load peers
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM peers "
                                           + "ORDER BY last_seen DESC");
            
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                // Next
                rs.next();
                
                
                if (!this.isBusy(rs.getString("peer")))
                {
                    // Return
                    String peer=rs.getString("peer");
                      
                    // Return
                    return peer;
                }
            }
            else
            {
                // Return local
                //if (!this.isBusy("127.0.0.1"))
                //    return "127.0.0.1";
            }
            
            // Return
            return "0.0.0.0";
        }
        
        public void findPeers() throws Exception
        {
            // Blockchain
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
			                       + "FROM sync "
			  	              + "WHERE type='ID_GET_NETSTAT' "
                                               + "AND peer='' "
                                               + "AND status='ID_PENDING'");
                                            
            if (UTILS.DB.hasData(rs))
            {
                // Get free peer
                String peer=this.getFreePeer();
                
                // Found ?
                if (!peer.equals("0.0.0.0"))
                    UTILS.DB.executeUpdate("UPDATE sync "
                                            + "SET peer='"+peer+"' "
                                          + "WHERE peer='' "
                                            + "AND status='ID_PENDING' "
                                            + "AND type='ID_GET_NETSTAT'");
            }
            else
            {
                    // Blocks
                    rs=UTILS.DB.executeQuery("SELECT * "
			                     + "FROM sync "
			  	            + "WHERE type='ID_BLOCKS' "
                                              + "AND peer='' "
                                              + "AND status='ID_PENDING'");
                                            
                    if (UTILS.DB.hasData(rs))
                    {
                       // Get free peer
                       String peer=this.getFreePeer();
                
                       // Found ?
                       if (!peer.equals("0.0.0.0"))
                           UTILS.DB.executeUpdate("UPDATE sync "
                                                   + "SET peer='"+peer+"' "
                                                 + "WHERE peer='' "
                                                   + "AND status='ID_PENDING' "
                                                   + "AND type='ID_BLOCKS'");
                    }
                }
        }
               
           
        public boolean blockExist(String hash) throws Exception
        {
            // Check
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM blocks "
                                              + "WHERE hash='"+hash+"'");
            
            // Has data
            if (UTILS.DB.hasData(rs))
               return true;
           else
               return false;
        }
        
        public void loadNetstat(CNetStatPacket packet) throws Exception
        {
           // Update sync target
           UTILS.DB.executeUpdate("UPDATE net_stat "
                                   + "SET sync_target='"+packet.last_block+"'");
           
           // Processing
           UTILS.DB.executeUpdate("UPDATE sync "
                                   + "SET status='ID_PROCESSING', "
                                       + "tstamp='"+UTILS.BASIC.tstamp()+"' "
                                 + "WHERE type='ID_GET_NETSTAT'");
           
           // No more blocks ?
           if (UTILS.NET_STAT.last_block>=packet.last_block)
           {
               // Clean status table
               UTILS.DB.executeUpdate("DELETE FROM sync");
               
               // Set status
               UTILS.STATUS.setEngineStatus("ID_ONLINE");
               
               // Return
               return;
           }
           else
           {
               long c=0;
               
               // Start
               long start=UTILS.NET_STAT.last_block;
               if (start==0) start=1;
               
               long a=0;
               
               for (a=start; a<packet.last_block; a++)
               {
                  // Increase counter
                  c++;    
                  
                  if (c%1000==0)
                  {
                      // Insert
                      UTILS.DB.executeUpdate("INSERT INTO sync "
                                                + "SET status='ID_PENDING', "
                                                    + "peer='', "
                                                    + "type='ID_BLOCKS', "
                                                    + "tab='', "
                                                    + "start='"+start+"', "
                                                    + "end='"+a+"', "
                                                    + "tstamp='"+UTILS.BASIC.tstamp()+"'");
                      
                     // Start
                     start=a;
                  }
               }
               
               // Blocks remained 
               if (a>=start)
               UTILS.DB.executeUpdate("INSERT INTO sync "
                                                + "SET status='ID_PENDING', "
                                                    + "peer='', "
                                                    + "type='ID_BLOCKS', "
                                                    + "tab='', "
                                                    + "start='"+start+"', "
                                                    + "end='"+a+"', "
                                                    + "tstamp='"+UTILS.BASIC.tstamp()+"'");
                       
               
           }
           
            
            // Delete from sync
            UTILS.DB.executeUpdate("DELETE FROM sync WHERE type='ID_GET_NETSTAT'");
        }
	
	public void run()
	{
            try
            {
                 // Deletes sync
                 UTILS.DB.executeUpdate("DELETE FROM sync");
                 
                 // Sync status
                 UTILS.STATUS.setEngineStatus("ID_SYNC");
            }
            catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CSync.java", 57);
              }
		
	}
	
	
	public boolean isBusy(String peer) throws Exception
	{
           // Get the number of active jobs
	   ResultSet rs=UTILS.DB.executeQuery("SELECT * "
				              + "FROM sync "
				             + "WHERE peer='"+peer+"'");
	    
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
	       return true;
	    else
	       return false;
	}
	
	public void tick() throws Exception
	{
            // Synced ?
            if (UTILS.STATUS.engine_status.equals("ID_ONLINE") || 
                UTILS.NETWORK.peers.peers.size()<0 || 
                !UTILS.NETWORK.CONSENSUS.status.equals("ID_WAITING")) return;
            
            
            // Time ups ?
            if (UTILS.BASIC.tstamp()-this.start>3000)
            {
               System.out.println("Sync frozen. Exiting !!!");
               System.exit(0);
            }
            
            // Frozen on downloading
            UTILS.DB.executeUpdate("UPDATE sync "
                                    + "SET status='ID_PENDING' "
                                  + "WHERE status='ID_DOWNLOADING' "
                                    + "AND tstamp<"+(UTILS.BASIC.tstamp()-30));
            
                // Free peers
                this.findPeers();
            
	        
		// Get the number of jobs
		ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM sync");
	        
                
                // ------------------------- No records on sync stack ----------------------------------------------------
		if (UTILS.DB.hasData(rs)==false)
		{
		    // Get netstat 
                    UTILS.DB.executeUpdate("INSERT INTO sync "
                                                 + "SET type='ID_GET_NETSTAT', "
                                                      + "status='ID_PENDING', "
                                                      + "peer='', "
                                                      + "tstamp='"+UTILS.BASIC.tstamp()+"'");
                    
                }
                // ------------------------- Data on sync stack ----------------------------------------------------
		else
                {
                     // Net stat
		     rs=UTILS.DB.executeQuery("SELECT * "
			                         + "FROM sync "
			                        + "WHERE type='ID_GET_NETSTAT' "
                                                  + "AND status='ID_PENDING' "
                                                  + "AND peer<>'' "
                                             + "ORDER BY start ASC");
                     
                     
                     if (UTILS.DB.hasData(rs))
                     {
                        // Next
		        rs.next();
						  
		        // Request data packet
			CReqDataPacket packet=new CReqDataPacket("ID_NETSTAT"); 
			        
                        // Send packet
                        UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
		        // Update db
			UTILS.DB.executeUpdate("UPDATE sync "
					        + "SET status='ID_DOWNLOADING', "
                                                    + "tstamp='"+UTILS.BASIC.tstamp()+"' "
				              + "WHERE type='ID_GET_NETSTAT'");
                     }
                     else
                     {
                        // Downloading blocks?
                        rs=UTILS.DB.executeQuery("SELECT * "
			                         + "FROM sync "
			                        + "WHERE type='ID_BLOCKS' "
                                                  + "AND (status='ID_DOWNLOADING' "
                                                   + "OR status='ID_PROCESSING')");
                    
                        if (!UTILS.DB.hasData(rs))
                        {
		            // Blocks
		            rs=UTILS.DB.executeQuery("SELECT * "
			                             + "FROM sync "
			                            + "WHERE type='ID_BLOCKS' "
                                                      + "AND status='ID_PENDING' "
                                                      + "AND peer<>'' "
                                                 + "ORDER BY start ASC");
	   
	                    if (UTILS.DB.hasData(rs))
	                    {
                                // Next
		                rs.next();
                                
                                // Update db
			        UTILS.DB.executeUpdate("UPDATE sync "
					                + "SET status='ID_DOWNLOADING', "
                                                            + "tstamp='"+UTILS.BASIC.tstamp()+"' "
					              + "WHERE type='ID_BLOCKS' "
					                + "AND start='"+rs.getLong("start")+"'");
						  
		                // Request data packet
			        CReqDataPacket packet=new CReqDataPacket("ID_BLOCKS", 
					    		                 rs.getLong("start"), 
					    		                 rs.getLong("end")); 
			        
                                // Send packet
                                UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
		        }
                    }
                }
            }
        }
       
}
