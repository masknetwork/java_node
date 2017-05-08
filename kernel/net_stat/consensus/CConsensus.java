package wallet.kernel.net_stat.consensus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import wallet.kernel.CCrons;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.sync.CGetBlockPacket;

public class CConsensus 
{
   // Status
   public String status;
   
   // Timer
   Timer timer;
   
   // Chain
   ArrayList <String> chain;
   
   // Actual block hash
   public String block_hash;
   
   // Actual block number
   public long block_number;
   
   // Refresh ?
   public boolean refresh;
   
   public CConsensus() throws Exception
   {
      UTILS.DB.executeUpdate("DELETE FROM blocks_pool");
      
       // Status
       setStatus("ID_WAITING");
       
        // Timer
       timer = new Timer();
       RemindTask task=new RemindTask();
       timer.schedule(task, 0, 1000);
   }
   
   
   public synchronized void blockReceived(CBlockPacket block) throws Exception
   {
       // Refresh
       refresh=false;
       
       // Processing ?
       if (!this.status.equals("ID_WAITING"))
       {
             // Message
             System.out.println("Status is not WAITING...");
             
             // Add to pool
             this.addToPool(block);
             
             // Store
             this.store(block);
             
             // Return
             return;
       }
          
        // Status
        this.setStatus("ID_PROCESSING");
       
        // Block exist ?
        if (this.blockExist(block.hash))
        {
           // Print
           System.out.println("Block already in blockchain....");
           
           // Remove from pool
           this.removeFromPool(block.hash);
           
           // Waiting
           this.setStatus("ID_WAITING");
           
           // Return
           return;
        }
        
       try
       {
          // Begin
          UTILS.DB.begin();
          
          // POW
          if (!block.preCheck()) 
              throw new Exception ("Block precheck failed");
       
           // Store
          this.store(block);
       
          // Broadcast
          if (!UTILS.STATUS.engine_status.equals("ID_SYNC")) 
               UTILS.NETWORK.broadcast(block);
          
          
          // Unknown parent
          if (!this.blockExist(block.prev_hash)) 
          {
              System.out.println("No prev hash found...");
              
              if (!UTILS.STATUS.engine_status.equals("ID_SYNC"))
              {
                 // Add to pool
                 this.addToPool(block);
               
                 // Request missing block
                 CGetBlockPacket packet=new CGetBlockPacket(block.prev_hash);
               
                 // Broadcast
                 UTILS.NETWORK.broadcast(packet);
              }
              else throw new Exception("Prev block not found");
          }
          else
          {
             // Block number
             if (block.block==UTILS.NET_STAT.last_block+1)
             {
                if (UTILS.NET_STAT.last_block_hash.equals(block.prev_hash))
                {
                   // Commit
                   commitBlock(block);
                }
                else 
                {
                   // Status
                   this.setStatus("ID_REORGANIZING");
              
                   // Reorganize blockchain
                   this.reorganize(block.prev_hash);
                
                   // Add to chain
                   this.commitBlock(block);
                }
            }  
            else if (block.block>UTILS.NET_STAT.last_block+1)
            {
                System.out.println("Block number bigger than (last_block+1)...");
                
                if (!UTILS.STATUS.engine_status.equals("ID_SYNC"))
                   this.addToPool(block);
                else
                   throw new Exception("Invalid block");
            }
            else 
            {
                System.out.println("Block number behind or equal to last block...");
                
                if (this.blockExist(block.block))
                   this.addBlock(block);
                else
                   this.commitBlock(block);
            }
        }
          
        // Commit
        UTILS.DB.commit();
        
         // Refresh
         if (refresh)
            UTILS.NET_STAT.refreshTables(block.block, block.hash);
         
        // Status
        this.setStatus("ID_WAITING");
        
       }
       catch (Exception ex)
       {
           // Rollback
           System.out.println("Rolling back block " + block.block + " - " + ex.getMessage());
           
           // Rollback
           UTILS.DB.rollback();
           
           // Remove from pool
           this.removeFromPool(block.hash);
          
           // New status
           this.setStatus("ID_WAITING");
          
          // Throws exception
          throw new Exception(ex);
       }
   }
   
   public void commitBlock(CBlockPacket block) throws Exception
   {
        // Block hash
        this.block_hash=block.hash;
                  
        // Block number
        this.block_number=block.block;
            
        // Check
        block.check();
        
        // Add to chain
        this.addBlock(block);
        
        // Commit
        block.commit();
                  
        // Commited
        this.commited(block.block, block.hash);
        
        // Refresh
        refresh=true;
   }
   
   public void commited(long block, String hash) throws Exception
   {
        // Reset commited
        UTILS.DB.executeUpdate("UPDATE blocks "
                                + "SET commited=0 "
                              + "WHERE block='"+block+"'");
                
        // Commited
        UTILS.DB.executeUpdate("UPDATE blocks "
                                + "SET commited='"+UTILS.BASIC.tstamp()+"' "
                              + "WHERE hash='"+hash+"'");
   }
   
   public void reorganize(String hash) throws Exception
   {
       // Init chain
       this.chain=new ArrayList();
       
       // Add block
       this.chain.add(hash);
       
       // Find a chain up to the last checkpoint
       boolean found=false;
       
       
       while (!found)
       {
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM blocks "
                                             + "WHERE hash='"+hash+"'");
           
           // Next 
           rs.next();
           
           // Parent
           hash=rs.getString("prev_hash");
           
           // Is checkpoint ?
           if (!this.isCheckPoint(hash))
           {
               this.chain.add(hash);
               if (this.chain.size()>110)
               {
                  System.out.println("Chain too long...");
                  System.exit(0);
               }
           }
           else
           {
               found=true;
               this.reorganize(rs.getLong("block")-1);
           }
       }
       
   }
   
   public void reorganize(long block) throws Exception
   {
       System.out.println("From block "+block);
       
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE block='"+block+"'");
       
       // Next
       rs.next();
       
       // Update net stat
       UTILS.DB.executeUpdate("UPDATE net_stat "
                               + "SET last_block='"+rs.getLong("block")+"', "
                                    + "last_block_hash='"+rs.getString("hash")+"'");
       
       UTILS.NET_STAT.last_block=rs.getLong("block");
       UTILS.NET_STAT.last_block_hash=rs.getString("hash");
       
       // Reload addresses
       System.out.println("Loading adr...");
       UTILS.NET_STAT.table_adr.loadCheckpoint(rs.getString("hash"));
       
       // Reload ads
       System.out.println("Loading ads...");
       UTILS.NET_STAT.table_ads.loadCheckpoint(rs.getString("hash"));
       
       // Reload assets
       System.out.println("Loading assets...");
       UTILS.NET_STAT.table_assets.loadCheckpoint(rs.getString("hash"));
       
       // Reload assets owners
       System.out.println("Loading assets_owners...");
       UTILS.NET_STAT.table_assets_owners.loadCheckpoint(rs.getString("hash"));
       
       // Assets markets
       System.out.println("Loading assets_mkts...");
       UTILS.NET_STAT.table_assets_mkts.loadCheckpoint(rs.getString("hash"));
       
       // Assets markets pos
       System.out.println("Loading assets_mkts_pos...");
       UTILS.NET_STAT.table_assets_mkts_pos.loadCheckpoint(rs.getString("hash"));
       
       // Comments
       System.out.println("Loading comments...");
       UTILS.NET_STAT.table_com.loadCheckpoint(rs.getString("hash"));
       
       // Feeds
       System.out.println("Loading feeds...");
       UTILS.NET_STAT.table_feeds.loadCheckpoint(rs.getString("hash"));
       
       // Feeds branches
       System.out.println("Loading feeds_branches...");
       UTILS.NET_STAT.table_feeds_branches.loadCheckpoint(rs.getString("hash"));
       
       // Feeds bets
       System.out.println("Loading feeds_bets...");
       UTILS.NET_STAT.table_feeds_bets.loadCheckpoint(rs.getString("hash"));
       
       // Feeds bets pos
       System.out.println("Loading feeds_bets_pos...");
       UTILS.NET_STAT.table_feeds_bets_pos.loadCheckpoint(rs.getString("hash"));
       
       // Feeds mkts
       System.out.println("Loading feeds_spec_mkts...");
       UTILS.NET_STAT.table_feeds_spec_mkts.loadCheckpoint(rs.getString("hash"));
       
       // Feeds mkts pos
       System.out.println("Loading feeds_spec_mkts_pos...");
       UTILS.NET_STAT.table_feeds_spec_mkts_pos.loadCheckpoint(rs.getString("hash"));
       
       // Tweets
       System.out.println("Loading tweets...");
       UTILS.NET_STAT.table_tweets.loadCheckpoint(rs.getString("hash"));
       
       // Tweets follow
       System.out.println("Loading teets_follow...");
       UTILS.NET_STAT.table_tweets_follow.loadCheckpoint(rs.getString("hash"));
       
       // Votes
       System.out.println("Loading assets_owners...");
       UTILS.NET_STAT.table_assets_owners.loadCheckpoint(rs.getString("hash"));
       
       // Reload del_votes
       System.out.println("Loading del_votes...");
       UTILS.NET_STAT.table_del_votes.loadCheckpoint(rs.getString("hash"));
       
       // Reload delegates
       System.out.println("Loading delegates...");
       UTILS.NET_STAT.table_delegates.loadCheckpoint(rs.getString("hash"));
       
       // Reload domains
       System.out.println("Loading domains...");
       UTILS.NET_STAT.table_domains.loadCheckpoint(rs.getString("hash"));
       
       // Reload escrowed
       System.out.println("Loading escrowed...");
       UTILS.NET_STAT.table_escrowed.loadCheckpoint(rs.getString("hash"));
       
       // Reload profiles
       System.out.println("Loading profiles...");
       UTILS.NET_STAT.table_profiles.loadCheckpoint(rs.getString("hash"));
       
       // Delete blocks
       UTILS.DB.executeUpdate("DELETE FROM blocks "
                                  + "WHERE block>"+block);
       
       // Load blocks from chain
       for (int a=this.chain.size()-1; a>=0; a--)
       {
           // Load block
           CBlockPacket b=this.loadBlock(this.chain.get(a));
           
           // Commit
           this.commitBlock(b);
       }
       
       System.out.println("Done.");
   }
   
   public boolean isCheckPoint(String hash) throws Exception
   {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM checkpoints "
                                          + "WHERE hash='"+hash+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
            return true;
        else
            return false;
   }
   
  
   public void addToPool(CBlockPacket block) throws Exception
   {
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks_pool "
                                         + "WHERE hash='"+block.hash+"'");
       
        // Has data
       if (UTILS.DB.hasData(rs))
           return;
       
       // Insert
       UTILS.DB.executeUpdate("INSERT INTO blocks_pool "
                                    + "SET hash='"+block.hash+"', "
                                        + "block='"+block.block+"', "
                                        + "tstamp='"+UTILS.BASIC.tstamp()+"'");
       
       // Debug
       System.out.println("Added to pool - "+block.hash);
   }
   
   public boolean blockExist(String hash) throws Exception
   {
       // Found
       boolean found=false;
       
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE hash='"+hash+"'");
        
       // Exist ?
       if (UTILS.DB.hasData(rs))
            found=true;
       
       // Return
       return found;
    }
   
   public boolean blockExist(long block) throws Exception
   {
       // Found
       boolean found=false;
       
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE block='"+block+"'");
        
       // Exist ?
       if (UTILS.DB.hasData(rs))
            found=true;
       
       // Return
       return found;
    }
   
   public void addBlock(CBlockPacket block) throws Exception
   {
        // Deserialize payload
        CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(block.payload);
       
        // Insert
        UTILS.DB.executeUpdate("INSERT INTO blocks "
                                    + "SET hash='"+block.hash+"', "
                                        + "block='"+block.block+"', "
        		                + "prev_hash='"+block.prev_hash+"', "
                                        + "signer='"+block.signer+"', "
                                        + "packets='"+block_payload.packets.size()+"', "
                                        + "tstamp='"+block.tstamp+"', "
                                        + "nonce='"+block.nonce+"', "
                                        + "net_dif='"+block.net_dif+"', "
                                        + "payload_hash='"+block.payload_hash+"', "
                                        + "size='"+block.payload.length+"'");
  
        // Remove
        this.removeFromPool(block.hash);
       
        // Debug
        System.out.println("Added to blockchain - "+block.hash);
   }
   
   public void removeFromPool(String hash) throws Exception
   {
      // Out
       System.out.println("Removing from pool "+hash);
       
      // Remove from pool
       UTILS.DB.executeUpdate("DELETE FROM blocks_pool WHERE hash='"+hash+"'"); 
   }
   
   public void store(CBlockPacket block) throws Exception
   {
        FileOutputStream fout = new FileOutputStream(new File(UTILS.WRITEDIR+"blocks/"+block.hash+".block"));
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(block);
   }
   
   public CBlockPacket loadBlock(String hash) throws Exception
   {
       // Finds the block
       File f = new File(UTILS.WRITEDIR+"blocks/"+hash+".block");
	
       if (f.exists())
       {
            // Read image from disk
	    FileInputStream f_in = new FileInputStream(UTILS.WRITEDIR+"blocks/"+hash+".block");

	    // Read object using ObjectInputStream
	    ObjectInputStream obj_in = new ObjectInputStream (f_in);

	    // Read an object
	    CBlockPacket obj = (CBlockPacket)obj_in.readObject();
				     
	    // Add block
	    return obj;
	}
       else
       {
           // Message
           System.out.println("Could not find block on disk. Removing from pool.");
           
           // Remove
           this.removeFromPool(hash);
           
           // Return
           return null;
       }
   }
   
   public void setStatus(String status)
   {
       // Processing ?
       if (status.equals("ID_PROCESSING"))
           System.out.println("");
       
       // Debug
       System.out.println("-------------------------------- "+status+" -----------------------------------------");
       
       // Waitng ?
       if (status.equals("ID_WAITING"))
           System.out.println("");
       
       // Status
       this.status=status;
   }
   
   class RemindTask extends TimerTask 
     {  
       @Override
       public void run()
       {  
           try
           {
              
               // Delete from pool expired blocks
               UTILS.DB.executeUpdate("DELETE FROM blocks_pool "
                                          + "WHERE tstamp<"+(UTILS.BASIC.tstamp()-600));
               
               
               // Check pool
               ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                  + "FROM blocks_pool "
                                              + "ORDER BY block ASC");
               
               // Has data
               if (UTILS.DB.hasData(rs))
               {
                   // Next
                   while (rs.next())
                   {
                      // Block
                      CBlockPacket b=loadBlock(rs.getString("hash"));
                      
                      // Not null ?
                      if (b!=null)
                         blockReceived(loadBlock(rs.getString("hash")));
                   }
               }
           }
           catch (Exception ex)
           {
               System.out.println(ex.getMessage());
           }
       }
}
}
