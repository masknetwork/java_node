package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.kernel.*;
import wallet.network.CResult;

public class CBlockchain  implements java.io.Serializable
{
   // Blocks
   public ArrayList<CChainBlock> blocks=new ArrayList<CChainBlock>();
   
   // Last block
   public long last_block;
   
   // Consensus status
   public String consensus;
           
   public CBlockchain(long start) throws Exception
   {
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load data
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM blocks "
                                  + "WHERE block>"+start
                               +" ORDER BY block ASC");
       
       // Add block
       while (rs.next())
       {
            // Last block
            this.last_block=rs.getLong("block");
                    
            // Load
            this.blocks.add(new CChainBlock(rs.getString("prev_hash"),
                                            rs.getString("hash"),
                                            rs.getLong("block"), 
                                            rs.getString("signer"), 
                                            rs.getLong("signer_balance"),
                                            rs.getLong("tstamp"), 
                                            rs.getLong("nonce"), 
                                            rs.getString("payload_hash"),
                                            rs.getLong("net_dif"),
                                            rs.getLong("commited"),
                                            rs.getString("tab_1"),
                                            rs.getString("tab_2"),
                                            rs.getString("tab_3"),
                                            rs.getString("tab_4"),
                                            rs.getString("tab_5"),
                                            rs.getString("tab_6"),
                                            rs.getString("tab_7"),
                                            rs.getString("tab_8"),
                                            rs.getString("tab_9"),
                                            rs.getString("tab_10")));
       }
       
       
       
   }
   
   public CResult check() throws Exception
   {
       // Previous block
       CChainBlock prev_block;
       
       // Curent block
       CChainBlock cur_block;
       
       for (int a=0; a<=this.blocks.size()-1; a++)
       {
           if (a>0)
           {
               // Prev block
               prev_block=this.blocks.get(a-1);
               
               // Curent block
               cur_block=this.blocks.get(a);
               
               // Previous block match
               if (!this.parentExist(cur_block.prev_hash, a))
                   return new CResult(false, "Invalid previous hash (block "+cur_block.block+")", "CNewFeedPayload", 67);
               
               // Check curent block
               CResult res=cur_block.check();
               if (!res.passed) 
                  return new CResult(false, "Invalid block (block "+cur_block.block+")", "CNewFeedPayload", 67);
               
               // Hash
               if (cur_block.block%UTILS.SETTINGS.chk_blocks==0)
               {
                   // Set adr hash
                   UTILS.SYNC.adr=cur_block.tab_1;
               }
           }
       }      
               
       return new CResult(true, "Ok", "CNewFeedPayload", 67);
   }
   
   public boolean parentExist(String hash, int pos)
   {
       for (int a=pos; a>=0; a--)
       {
           CChainBlock b=this.blocks.get(a);
           if (b.block_hash.equals(hash))
               return true;
       }
       
       // Not found
       return false;
   }
   
   public boolean commited(String hash)
    {
        for (int a=0; a<=this.blocks.size()-1; a++)
        {
            CChainBlock b=this.blocks.get(a);
            if (b.commited>0) return true;
        }
        
        // Not to be commited
        return false;
    }
}
