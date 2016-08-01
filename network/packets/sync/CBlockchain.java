package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.kernel.*;
import wallet.network.CResult;

public class CBlockchain  implements java.io.Serializable
{
   // Serial
   private static final long serialVersionUID = 100L;
   
   // Blocks
   public ArrayList<CChainBlock> blocks=new ArrayList<CChainBlock>();
   
   // Last block
   public long last_block;
   
   // Consensus status
   public String consensus;
           
   public CBlockchain(long start) throws Exception
   {
       // Statement
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
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
                                            rs.getString("net_dif"),
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
                                            rs.getString("tab_10"),
                                            rs.getString("tab_11"),
                                            rs.getString("tab_12"),
                                            rs.getString("tab_13"),
                                            rs.getString("tab_14")));
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
                   // Adr
                   UTILS.SYNC.adr=cur_block.tab_1;
                   
                   // Ads
                   UTILS.SYNC.ads=cur_block.tab_2;
                   
                   // Domains
                   UTILS.SYNC.domains=cur_block.tab_3;
                   
                   // Agents
                   UTILS.SYNC.agents=cur_block.tab_4;
                   
                   // Assets
                   UTILS.SYNC.assets=cur_block.tab_5;
                   
                   // Assets owners
                   UTILS.SYNC.assets_owners=cur_block.tab_6;
                   
                   // Assets markets
                   UTILS.SYNC.assets_mkts=cur_block.tab_7;
                   
                   // Assets markets pos
                   UTILS.SYNC.assets_mkts_pos=cur_block.tab_8;
                   
                   // Escrowed
                   UTILS.SYNC.escrowed=cur_block.tab_9;
                   
                   // Profiles
                   UTILS.SYNC.profiles=cur_block.tab_10;
                   
                   // Tweets
                   UTILS.SYNC.tweets=cur_block.tab_11;
                   
                   // Tweets likes
                   UTILS.SYNC.upvotes=cur_block.tab_12;
                   
                   // Tweets comments
                   UTILS.SYNC.comments=cur_block.tab_13;
                   
                   // Tweets follow
                   UTILS.SYNC.tweets_follow=cur_block.tab_14;
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
