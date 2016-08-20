package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.sync.CReqDataPacket;

import wallet.network.CPeer;
import wallet.network.packets.sync.CBlockchain;
import wallet.network.packets.sync.CChainBlock;

public class CSync extends Thread 
{
    // Blockchain
    public CBlockchain blockchain;
    
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
   
    public CSync() 
    {
		
    }
        
        public String getFreePeer() throws Exception
        {
            // Load peers
            ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM peers");
            
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                while (rs.next())
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
                if (!this.isBusy("127.0.0.1"))
                    return "127.0.0.1";
            }
            
            // Return
            return "0.0.0.0";
        }
        
        public void findPeers() throws Exception
        {
            // Statement
            
            
            // Blockchain
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
			              + "FROM sync "
			  	     + "WHERE type='ID_GET_BLOCKCHAIN' "
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
                                            + "AND type='ID_GET_BLOCKCHAIN'");
            }
            else
            {
                // Tables
                rs=UTILS.DB.executeQuery("SELECT * "
			          + "FROM sync "
			         + "WHERE type='ID_GET_TABLE' "
                                   + "AND peer='' "
                                   + "AND status='ID_PENDING'");
                                            
                if (UTILS.DB.hasData(rs))
                {
                    // Next
                    rs.next();
                    
                    // Get free peer
                    String peer=this.getFreePeer();
                
                    // Found ?
                    if (!peer.equals("0.0.0.0"))
                        UTILS.DB.executeUpdate("UPDATE sync "
                                                + "SET peer='"+peer+"' "
                                              + "WHERE peer='' "
                                                + "AND status='ID_PENDING' "
                                                + "AND type='ID_GET_TABLE' "
                                                + "AND tab='"+rs.getString("tab")+"'"); 
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
            
            // Close
            
        }
                
       public void insertTable(String table) throws Exception
       {
           // Deletes from table
           UTILS.DB.executeUpdate("DELETE FROM "+table);
        
           // Insert table into sync stack
           UTILS.DB.executeUpdate("INSERT INTO sync "
                                        + "SET status='ID_PENDING', "
                                            + "peer='', "
                                            + "type='ID_GET_TABLE', "
                                            + "tab='"+table+"', "
                                            + "start='0', "
                                            + "end='0', "
                                            + "tstamp='0'");
       }
        
        public void flushBlockchain(long max_block) throws Exception
        {
            CChainBlock block=null;
            
            for (int a=0; a<=blockchain.blocks.size()-1; a++)
            {
                // Load block
                block=blockchain.blocks.get(a);
                
                // Max block
                if (block.block<=max_block && !this.blockExist(block.block_hash))
                    UTILS.DB.executeUpdate("INSERT INTO blocks "
                                             + "SET hash='"+block.block_hash+"', "
                                                 + "block='"+block.block+"', "
                                                 + "prev_hash='"+block.prev_hash+"', "
                                                 + "signer='"+block.signer+"', "
                                                 + "tstamp='"+block.tstamp+"', "
                                                 + "nonce='"+block.nonce+"', "
                                                 + "net_dif='"+block.net_dif+"', "
                                                 + "payload_hash='"+block.payload_hash+"', "
                                                 + "signer_balance='"+block.signer_balance+"', "
                                                 + "commited='"+block.commited+"', "
                                                 + "tab_1='"+block.tab_1+"', "
                                                 + "tab_2='"+block.tab_2+"', "
                                                 + "tab_3='"+block.tab_3+"', "
                                                 + "tab_4='"+block.tab_4+"', "
                                                 + "tab_5='"+block.tab_5+"', "
                                                 + "tab_6='"+block.tab_6+"', "
                                                 + "tab_7='"+block.tab_7+"',"
                                                 + "tab_8='"+block.tab_8+"', "
                                                 + "tab_9='"+block.tab_9+"', "
                                                 + "tab_10='"+block.tab_10+"', "
                                                 + "tab_11='"+block.tab_11+"', "
                                                 + "tab_12='"+block.tab_12+"', "
                                                 + "tab_13='"+block.tab_13+"', "
                                                 + "tab_14='"+block.tab_14+"', "
                                                 + "tab_15='"+block.tab_15+"', "
                                                 + "tab_16='"+block.tab_16+"', "
                                                 + "tab_17='"+block.tab_17+"', "
                                                 + "tab_18='"+block.tab_18+"', "
                                                 + "tab_19='"+block.tab_19+"', "
                                                 + "tab_20='"+block.tab_20+"', "
                                                 + "tab_21='"+block.tab_21+"', "
                                                 + "tab_22='"+block.tab_22+"', "
                                                 + "tab_23='"+block.tab_23+"', "
                                                 + "tab_24='"+block.tab_24+"', "
                                                 + "tab_25='"+block.tab_25+"', "
                                                 + "tab_26='"+block.tab_26+"', "
                                                 + "tab_27='"+block.tab_27+"', "
                                                 + "tab_28='"+block.tab_28+"', "
                                                 + "tab_29='"+block.tab_29+"', "
                                                 + "tab_30='"+block.tab_30+"'");
            }
            
            if (block!=null && block.commited>0)
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET last_block='"+block.block+"', "
                                        + "last_block_hash='"+block.block_hash+"', "
                                        + "net_dif='"+block.net_dif+"'");
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
        
        public void loadBlockchain(CBlockchain blockchain) throws Exception
        {
           // Load
           this.blockchain=blockchain; 
           
           // No more blocks ?
           if (this.blockchain.blocks.size()==0)
           {
               UTILS.STATUS.setEngineStatus("ID_ONLINE");
               
               // Delete from sync
               UTILS.DB.executeUpdate("DELETE FROM sync "
                                          + "WHERE type='ID_GET_BLOCKCHAIN'");
               
               return;
           }
           
            // Full resync ?
            if (this.blockchain.last_block>(UTILS.NET_STAT.last_block+5))
            {
                // Adr
                this.insertTable("adr");
                
                // Ads
                this.insertTable("ads");
                
                // Agents
                this.insertTable("agents");
                
                // Assets
                this.insertTable("assets");
                
                // Assets owners
                this.insertTable("assets_owners");
                
                // Comments
                this.insertTable("comments");
                
                // Del votes
                this.insertTable("del_votes");
                
                // Domains
                this.insertTable("domains");
                
                // Escrowed
                this.insertTable("escrowed");
                
                // Profiles
                this.insertTable("profiles");
                
                // Storage
                this.insertTable("storage");
                
                // Tweets
                this.insertTable("tweets");
                
                // Tweets follow
                this.insertTable("tweets_follow");
                
                // Votes
                this.insertTable("votes");
                
                // Start
                long start=Math.round(Math.floor(this.blockchain.last_block/UTILS.SETTINGS.chk_blocks))*UTILS.SETTINGS.chk_blocks;
                
                // Flush blockchain
                this.flushBlockchain(start-1);
                
                // Blocks
                if (this.blockchain.last_block>=start)
                   UTILS.DB.executeUpdate("INSERT INTO sync "
                                                + "SET status='ID_PENDING', "
                                                    + "peer='', "
                                                    + "type='ID_BLOCKS', "
                                                    + "tab='', "
                                                    + "start='"+start+"', "
                                                    + "end='"+this.blockchain.last_block+"', "
                                                    + "tstamp='0'");
            }
            else
            {
                UTILS.DB.executeUpdate("INSERT INTO sync "
                                             + "SET status='ID_PENDING', "
                                                 + "peer='', "
                                                 + "type='ID_BLOCKS', "
                                                 + "tab='', "
                                                 + "start='"+(UTILS.NET_STAT.last_block+1)+"', "
                                                 + "end='"+this.blockchain.last_block+"', "
                                                 + "tstamp='0'");
            }
            
            // Delete from sync
            UTILS.DB.executeUpdate("DELETE FROM sync WHERE type='ID_GET_BLOCKCHAIN'");
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
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
		
	}
	
	
	public boolean isBusy(String peer) throws Exception
	{
            // Statement
            
            
	   // Get the number of active jobs
	   ResultSet rs=UTILS.DB.executeQuery("SELECT * "
				       + "FROM sync "
				      + "WHERE peer='"+peer+"'");
	    
           boolean hasData=UTILS.DB.hasData(rs);
           
           
           
           
            if (hasData==true) 
	       return true;
	    else
	       return false;
	}
	
	public void tick() throws Exception
	{
            // Synced ?
            if (UTILS.STATUS.engine_status.equals("ID_ONLINE") || UTILS.NETWORK.peers.peers.size()<0) return;
                
                // Free peers
                this.findPeers();
            
	        // Statement
                
                
		// Get the number of jobs
		ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM sync");
	        
                
                // ------------------------- No records on sync stack ----------------------------------------------------
		if (UTILS.DB.hasData(rs)==false)
		{
		    // Inserts blockchain 
                    UTILS.DB.executeUpdate("INSERT INTO sync(type, "
                                                        + "status, "
                                                        + "peer, "
                                                        + "start, "
                                                        + "tstamp) "
                                            + "VALUES('ID_GET_BLOCKCHAIN', "
                                                    + "'ID_PENDING', "
                                                    + "'', "
                                                    + "'"+UTILS.NET_STAT.last_block+"', "
                                                    + "'"+UTILS.BASIC.tstamp()+"')");
                    
                }
                
                // ------------------------- Data on sync stack ----------------------------------------------------
		else
                {
                    // Blockchain
                    rs=UTILS.DB.executeQuery("SELECT * "
			              + "FROM sync "
			  	     + "WHERE type='ID_GET_BLOCKCHAIN' "
                                       + "AND peer<>'' "
                                       + "AND status='ID_PENDING'");
                                            
                    if (UTILS.DB.hasData(rs))
                    {
                       // Next
                       rs.next();
                                                
                       // Request data packet
		       CReqDataPacket packet=new CReqDataPacket("ID_BLOCKCHAIN", 
					    		     rs.getLong("start"),
                                                             0); 
		                
                       // Send to peer
                       UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
	               // Update db
		       UTILS.DB.executeUpdate("UPDATE sync "
					       + "SET status='ID_DOWNLOADING' "
					     + "WHERE type='ID_GET_BLOCKCHAIN'");
                       
                      
                    }
                    
                    // ------------------------- No blockchain to download ----------------------------------------------------
                    else
                    {
                        // Tables
                        rs=UTILS.DB.executeQuery("SELECT * "
			                  + "FROM sync "
			                 + "WHERE type='ID_GET_TABLE' "
                                           + "AND peer<>'' "
                                           + "AND (status='ID_PENDING' "
                                                + "OR status='ID_DOWNLOADING')");
                        
                        // ------------------------- Tables to download / downloading ----------------------------------------------------
                        if (UTILS.DB.hasData(rs))
                        {
                            rs=UTILS.DB.executeQuery("SELECT * "
			                      + "FROM sync "
			                     + "WHERE type='ID_GET_TABLE' "
                                               + "AND peer<>''"
                                               + "AND (status='ID_PENDING')");
                            
                             if (UTILS.DB.hasData(rs))
                             {
                                    // Next
                                    rs.next();
                                                
                                    // Request data packet
                                    long block_no=Math.round(Math.floor(this.blockchain.last_block/UTILS.SETTINGS.chk_blocks))*UTILS.SETTINGS.chk_blocks-1;
                                    String bhash=this.getBlockHash(block_no);
		                    CReqDataPacket packet=new CReqDataPacket("ID_GET_TABLE", 
					    		                     rs.getString("tab"),
                                                                             bhash); 
		                
                                     // Send to peer
                                     UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
	                             // Update db
	                             UTILS.DB.executeUpdate("UPDATE sync "
	                                                     + "SET status='ID_DOWNLOADING' "
					                   + "WHERE type='ID_GET_TABLE' "
                                                             + "AND tab='"+rs.getString("tab")+"'");
                             }
                        }
                        
                        // ------------------------- No tables to download ----------------------------------------------------
                        else
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
						  
		                // Request data packet
			        CReqDataPacket packet=new CReqDataPacket("ID_BLOCKS", 
					    		                 rs.getLong("start"), 
					    		                 rs.getLong("end")); 
			        
                                // Send packet
                                UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
		                // Update db
			        UTILS.DB.executeUpdate("UPDATE sync "
					                + "SET status='ID_DOWNLOADING' "
					              + "WHERE type='ID_BLOCKS' "
					                + "AND start='"+rs.getLong("start")+"'");
	                }
                    }
                }
        }
                                           
                // Close
                
	    
        }
		
        public String getBlockHash(long block)
        {
            for (int a=0; a<=this.blockchain.blocks.size()-1; a++)
            {
                CChainBlock b=this.blockchain.blocks.get(a);
                if (b.block==block) return b.block_hash;
            }
            
            return "";
        }
        
	public void removeTable(String table) throws Exception
        {
           UTILS.DB.executeUpdate("DELETE FROM sync WHERE tab='"+table+"'");
        }
        
        public String getTableCRC(String table)
        {
            // Addresses
            if (table.equals("adr")) return this.adr;
            
            // Ads
            if (table.equals("ads")) return this.ads;
            
            // Agents
            if (table.equals("agents")) return this.agents;
  
            // Agents feeds
            if (table.equals("agents_feeds")) return this.agents_feeds;
            
            // Assets
            if (table.equals("assets")) return this.assets;
            
            // Assets owners
            if (table.equals("assets_owners")) return this.assets_owners;
            
            // Assets markets
            if (table.equals("assets_mkts")) return this.assets_mkts;
            
            // Assets markets pos
            if (table.equals("assets_mkts_pos")) return this.assets_mkts_pos;
            
            // Comments
            if (table.equals("comments")) return this.comments;
            
            // Delegates votes
            if (table.equals("del_votes")) return this.del_votes;
            
            // Assets
            if (table.equals("domains")) return this.domains;
            
            // Escrowed
            if (table.equals("escrowed")) return this.escrowed;
            
            // Feeds
            if (table.equals("feeds")) return this.feeds;
            
            // Feeds branches
            if (table.equals("feeds_branches")) return this.feeds_branches;
            
            // Feeds bets
            if (table.equals("feeds_bets")) return this.feeds_bets;
            
            // Feeds bets pos
            if (table.equals("feeds_bets_pos")) return this.feeds_bets_pos;
            
            // Profiles
            if (table.equals("profiles")) return this.profiles;
            
            // Storage
            if (table.equals("storage")) return this.storage;
            
            // Tweets
            if (table.equals("tweets")) return this.tweets;
            
            // Tweets follow
            if (table.equals("tweets_follow")) return this.tweets_follow;
            
            // Votes
            if (table.equals("votes")) return this.votes;
            
            return "";
        }

}
