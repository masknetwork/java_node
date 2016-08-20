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

public class CConsensus extends Thread
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
   
   public CConsensus()
   {
       
   }
   
   public void run()
   {
       // Status
       this.setStatus("ID_WAITING");
       
        // Timer
       timer = new Timer();
       RemindTask task=new RemindTask();
       timer.schedule(task, 0, 1000);
   }
   
   public void blockReceived(CBlockPacket block) throws Exception
   {
       try
       {
          // POW
          if (!block.preCheck()) return;
       
           // Store
          this.store(block);
       
          // Broadcast
          UTILS.NETWORK.broadcast(block);
       
          // Processing ?
          if (!this.status.equals("ID_WAITING"))
         {
             this.addToPool(block);
             return;
         }
       
          // Status
          this.setStatus("ID_PROCESSING");
       
          // Unknown parent
          if (!this.blockExist(block.prev_hash)) 
          {
               // Add to pool
               this.addToPool(block);
               
               // Request missing block
               CGetBlockPacket packet=new CGetBlockPacket(block.prev_hash);
               
               // Broadcast
               UTILS.NETWORK.broadcast(packet);
               
               // Status
               this.setStatus("ID_WAITING");
               
               // Return
               return;
          }
       
          // Block number
          if (block.block==UTILS.NET_STAT.last_block+1)
          {
             if (UTILS.NET_STAT.last_block_hash.equals(block.prev_hash))
             {
                 // Check
                 block.check();
              
                 // Block hash
                 this.block_hash=block.hash;
                  
                 // Block number
                 this.block_number=block.block;
                  
                 // Add to chain
                 if (this.addBlock(block))
                 {
                    // Commit
                    block.commit();
                  
                    // Commited
                    this.commited(block.block, block.hash);
                  
                    // Reorganizing ?
                    if (this.status.equals("ID_REORGANIZING"))
                      this.setStatus("ID_WAITING");
                }
             }
             else 
             {
                 // Status
                 this.setStatus("ID_REORGANIZING");
              
                  // Add to chain
                  this.addBlock(block);
               
                  // Reorganize blockchain
                  this.reorganize(block.hash);
             }
          }  
          else if (block.block>UTILS.NET_STAT.last_block+1)
          {
              // Add to pool
              this.addToPool(block);
          }
          else 
          {
              this.addBlock(block);
          }
     
          // Status
          this.setStatus("ID_WAITING");
       }
       catch (Exception ex)
       {
          if (this.status.equals("ID_PROCESSING"))
              this.setStatus("ID_WAITING");
          
          System.out.println(ex.getMessage());
       }
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
       
       // Reload addresses
       UTILS.NET_STAT.table_adr.loadCheckpoint(rs.getString("hash"), 
                                               this.getTableCRC(this.chain.get(this.chain.size()-1), "adr"));
       
       // Reload ads
       UTILS.NET_STAT.table_ads.loadCheckpoint(rs.getString("hash"), 
                                               this.getTableCRC(this.chain.get(this.chain.size()-1), "ads"));
       
       // Reload domains
       UTILS.NET_STAT.table_domains.loadCheckpoint(rs.getString("hash"), 
                                               this.getTableCRC(this.chain.get(this.chain.size()-1), "domains"));
       
       // Load blocks from chain
       for (int a=this.chain.size()-1; a>=0; a--)
       {
           // Load block
           CBlockPacket b=this.loadBlock(this.chain.get(a));
           
           // Commit
           b.commit();
           
           // Commited
           this.commited(b.block, b.hash);
       }
       
   }
   
   public String getTableCRC(String hash, String table) throws Exception
   {
       // CRC
       String crc="";
       
       
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE hash='"+hash+"'");
       
       // Next
       rs.next();
       
       // Return
       switch (table)
       {
           // Addresses
           case "adr" : crc=rs.getString("tab_1"); break;
           
           // Ads
           case "ads" : crc=rs.getString("tab_2"); break;
           
           // Agents
           case "agents" : crc=rs.getString("tab_3"); break;
           
           // Agents feeds
           case "agents_domains" : crc=rs.getString("tab_4"); break;
           
           // Assets
           case "assets" : crc=rs.getString("tab_5"); break;
           
           // Assets owners
           case "assets_owners" : crc=rs.getString("tab_6"); break;
           
           // Assets markets
           case "assets_mkts" : crc=rs.getString("tab_7"); break;
           
           // Assets markets pos
           case "assets_mkts_po" : crc=rs.getString("tab_8"); break;
           
           // Comments
           case "comments" : crc=rs.getString("tab_9"); break;
           
           // Del Votes
           case "del_votes" : crc=rs.getString("tab_10"); break;
           
           // Domains
           case "domains" : crc=rs.getString("tab_11"); break;
           
           // Escrowed
           case "escrowed" : crc=rs.getString("tab_12"); break;
           
           // Feeds
           case "feeds" : crc=rs.getString("tab_13"); break;
           
           // Feeds branches
           case "feeds_branches" : crc=rs.getString("tab_14"); break;
           
           // Feeds bets
           case "feeds_bets" : crc=rs.getString("tab_15"); break;
           
           // Feeds bets pos
           case "feeds_bets_pos" : crc=rs.getString("tab_16"); break;
           
           // Profiles
           case "profiles" : crc=rs.getString("tab_17"); break;
           
           // Storage
           case "storage" : crc=rs.getString("tab_18"); break;
           
           // Tweets
           case "tweets" : crc=rs.getString("tab_19"); break;
           
           // Tweets follow
           case "tweets_follow" : crc=rs.getString("tab_20"); break;
           
           // Votes
           case "votes" : crc=rs.getString("tab_21"); break;
       }
       
        
       // Return
       return crc;
   }
   
   public boolean isCheckPoint(String hash) throws Exception
   {
       // Statement
       
       
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
       
       // Debug
       System.out.println("Added to pool - "+block.hash);
       
       // Deserialize payload
       CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(block.payload);
       
       // Insert
       UTILS.DB.executeUpdate("INSERT INTO blocks_pool(hash, "
                                                        + "block, "
        		                                + "tstamp) "
        		                + "VALUES('"+block.hash+"', '"+
                                                     String.valueOf(block.block)+"', '"+
        		                             UTILS.BASIC.tstamp()+"')");
   }
   
   public boolean blockExist(String hash) throws Exception
   {
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE hash='"+hash+"'");
        
       // Exist ?
       if (UTILS.DB.hasData(rs))
            return true;
       else
            return false;
    }
   
   public boolean addBlock(CBlockPacket block) throws Exception
   {
       if (this.blockExist(status)) return false;
        
       // Deserialize payload
       CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(block.payload);
       
       // Check dif
       String new_dif=UTILS.BASIC.formatDif(UTILS.CBLOCK.getNewDif(block.prev_hash).toString(16));
       
       if (!block.net_dif.equals(new_dif))
       {
           System.out.println("Invalid difficulty "+block.net_dif+", "+new_dif);       
           return false;
       }
             
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
                                        + "signer_balance='"+block.signer_balance+"', "
                                        + "tab_1='"+block.tab_1+"', "
                                        + "tab_2='"+block.tab_2+"', "
                                        + "tab_3='"+block.tab_3+"', "
                                        + "tab_4='"+block.tab_4+"', "
                                        + "tab_5='"+block.tab_5+"', "
                                        + "tab_6='"+block.tab_6+"', "
                                        + "tab_7='"+block.tab_7+"', "
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
                                        + "tab_30='"+block.tab_30+"', "
                                        + "payload_hash='"+block.payload_hash+"', "
                                        + "size='"+block.payload.length+"'");
  
       // Remove from pool
       UTILS.DB.executeUpdate("DELETE FROM blocks_pool WHERE hash='"+block.hash+"'");
       
       // Debug
       System.out.println("Added to blockchain - "+block.hash);
       
       // Return
       return true;
      
   }
   
   public void newBlock()
   {
       
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
       else return null;
   }
   
   public void setStatus(String status)
   {
       // Debug
       System.out.println("New consensus status : "+status);
       
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
               UTILS.DB.executeUpdate("DELETE FROM blocks_pool WHERE tstamp<"+(UTILS.BASIC.tstamp()-600));
               
               // Statement
               
               
               // Check pool
               ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM blocks_pool");
               
               // Has data
               if (UTILS.DB.hasData(rs))
               {
                   // Next
                   while (rs.next())
                      blockReceived(loadBlock(rs.getString("hash")));
               }
               
               // Close
               
           }
           catch (Exception ex)
           {
               System.out.println(ex.getMessage());
           }
       }
}
}
