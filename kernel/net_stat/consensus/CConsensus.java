package wallet.kernel.net_stat.consensus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
               this.addToPool(block);
               this.setStatus("ID_WAITING");
               return;
       }
       
       // Block number
       if (block.block==UTILS.NET_STAT.last_block+1)
       {
          if (UTILS.NET_STAT.last_block_hash.equals(block.prev_hash))
          {
              // Check
              CResult res=block.check();
              
              if (res.passed)
              {
                  // Block hash
                  this.block_hash=block.hash;
                  
                  /// Block number
                  this.block_number=block.block;
                  
                  // Add to chain
                  if (this.addBlock(block))
                  {
                     // Commit
                     block.commit();
                  
                     // Commited
                     this.commited(block.block, block.hash);
                  
                     // Reorginizing ?
                     if (this.status.equals("ID_REORGANIZING"))
                         this.setStatus("ID_WAITING");
                  }
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
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       while (!found)
       {
           ResultSet rs=s.executeQuery("SELECT * "
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
       
       // Close
       s.close();
   }
   
   public void reorganize(long block) throws Exception
   {
         System.out.println("From block "+block);
        
         
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load
       ResultSet rs=s.executeQuery("SELECT * "
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
       UTILS.NET_STAT.table_ads.loadCheckpoint(rs.getString("hash"), 
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
       
       // Close
       s.close();
       
   }
   
   public String getTableCRC(String hash, String table) throws Exception
   {
       // CRC
       String crc="";
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM blocks "
                                  + "WHERE hash='"+hash+"'");
       
       // Next
       rs.next();
       
       // Return
       switch (table)
       {
           case "adr" : crc=rs.getString("tab_1"); break;
           case "ads" : crc=rs.getString("tab_2"); break;
           case "domains" : crc=rs.getString("tab_3"); break;
       }
       
       // Close
       s.close();
       
       // Return
       return crc;
   }
   
   public boolean isCheckPoint(String hash) throws Exception
   {
       // Statement
       Statement s=UTILS.DB.getStatement();
       
        ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM checkpoints "
                                      + "WHERE hash='"+hash+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
        {
           // Close
           rs.close();
            
           return true;
        }
        else
        {
            // Close
            rs.close();
            
           return false;
        }
   }
   
  
   public void addToPool(CBlockPacket block) throws Exception
   {
       System.out.println("Added to pool - "+block.hash);
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM blocks_pool "
                                  + "WHERE hash='"+block.hash+"'");
       
        // Has data
       if (UTILS.DB.hasData(rs))
       {
            // Close
            s.close();
       
           return;
       }
       
       // Close
       s.close();

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
       // Already exist ?
        Statement s=UTILS.DB.getStatement();
        
        // Load
        ResultSet rs=s.executeQuery("SELECT * FROM blocks WHERE hash='"+hash+"'");
        
        // Exist ?
        if (UTILS.DB.hasData(rs))
        {
            // Close
            s.close();
            
            // Return
            return true;
        }
        else
        {
            // Close
            s.close();
            
            // Return
            return false;
        }
        
        
   }
   
   public boolean addBlock(CBlockPacket block) throws Exception
   {
       if (this.blockExist(status)) return false;
        
       // Deserialize payload
       CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(block.payload);
       
       // Check dif
       long new_dif=UTILS.CBLOCK.getNewDif(block.prev_hash);
       
       if (block.net_dif!=new_dif)
       {
           System.out.println("Invalid difficulty "+block.net_dif+", "+new_dif);       
           return false;
       }
                  
              
       // Insert
       UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
                                                        + "block, "
        		                                + "prev_hash, "
                                                        + "signer, "
                                                        + "packets, "
                                                        + "tstamp, "
                                                        + "nonce, "
                                                        + "net_dif, "
                                                        + "signer_balance, "
                                                        + "tab_1, "
                                                        + "tab_2, "
                                                        + "tab_3, "
                                                        + "tab_4, "
                                                        + "tab_5, "
                                                        + "tab_6, "
                                                        + "tab_7, "
                                                        + "tab_8, "
                                                        + "tab_9, "
                                                        + "tab_10, "
                                                        + "payload_hash, "
                                                        + "size) "
        		                + "VALUES('"+block.hash+"', '"+
                                                     String.valueOf(block.block)+"', '"+
        		                             block.prev_hash+"', '"+
                                                     block.signer+"', '"+
                                                     block_payload.packets.size()+"', '"+
                                                     block.tstamp+"', '"+
                                                     block.nonce+"', '"+
                                                     block.net_dif+"', '"+
                                                     block.signer_balance+"', '"+
                                                     block.tab_1+"', '"+
                                                     block.tab_2+"', '"+
                                                     block.tab_3+"', '"+
                                                     block.tab_4+"', '"+
                                                     block.tab_5+"', '"+
                                                     block.tab_6+"', '"+
                                                     block.tab_7+"', '"+
                                                     block.tab_8+"', '"+
                                                     block.tab_9+"', '"+
                                                     block.tab_10+"', '"+
                                                     block.payload_hash+"', '"+
        		                             block.payload.length+"')");
       
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
               Statement s=UTILS.DB.getStatement();
               
               // Check pool
               ResultSet rs=s.executeQuery("SELECT * FROM blocks_pool");
               
               // Has data
               if (UTILS.DB.hasData(rs))
               {
                   // Next
                   while (rs.next())
                      blockReceived(loadBlock(rs.getString("hash")));
               }
               
               // Close
               s.close();
           }
           catch (Exception ex)
           {
               System.out.println(ex.getMessage());
           }
       }
}
}
