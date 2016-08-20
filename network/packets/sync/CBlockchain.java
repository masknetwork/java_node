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
                                            rs.getString("tab_14"),
                                            rs.getString("tab_15"),
                                            rs.getString("tab_16"),
                                            rs.getString("tab_17"),
                                            rs.getString("tab_18"),
                                            rs.getString("tab_19"),
                                            rs.getString("tab_20"),
                                            rs.getString("tab_21"),
                                            rs.getString("tab_22"),
                                            rs.getString("tab_23"),
                                            rs.getString("tab_24"),
                                            rs.getString("tab_25"),
                                            rs.getString("tab_26"),
                                            rs.getString("tab_27"),
                                            rs.getString("tab_28"),
                                            rs.getString("tab_29"),
                                            rs.getString("tab_30")));
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
                   
                   // Agents
                   UTILS.SYNC.agents=cur_block.tab_3;
                   
                   // Agents feeds
                   UTILS.SYNC.agents_feeds=cur_block.tab_4;
                   
                   // Assets
                   UTILS.SYNC.assets=cur_block.tab_5;
                   
                   // Assets owners
                   UTILS.SYNC.assets_owners=cur_block.tab_6;
                   
                   // Assets markets
                   UTILS.SYNC.assets_mkts=cur_block.tab_7;
                   
                   // Assets markets pos
                   UTILS.SYNC.assets_mkts_pos=cur_block.tab_8;
                   
                   // Comments
                   UTILS.SYNC.comments=cur_block.tab_9;
                   
                   // Delegates votes
                   UTILS.SYNC.del_votes=cur_block.tab_10;
                   
                   // Domains
                   UTILS.SYNC.domains=cur_block.tab_11;
                   
                   // Escrowed
                   UTILS.SYNC.escrowed=cur_block.tab_12;
                   
                   // Feeds
                   UTILS.SYNC.feeds=cur_block.tab_13;
                   
                   // Feeds branches
                   UTILS.SYNC.feeds_branches=cur_block.tab_14;
                   
                   // Feeds bets
                   UTILS.SYNC.feeds_bets=cur_block.tab_15;
                   
                   // Feeds bets pos
                   UTILS.SYNC.feeds_bets_pos=cur_block.tab_16;
                   
                   // Profiles
                   UTILS.SYNC.profiles=cur_block.tab_17;
                   
                   // Storage
                   UTILS.SYNC.storage=cur_block.tab_18;
                   
                   // Tweeets
                   UTILS.SYNC.tweets=cur_block.tab_19;
                   
                   // Tweets follow
                   UTILS.SYNC.tweets_follow=cur_block.tab_20;
                   
                   // Votes
                   UTILS.SYNC.votes=cur_block.tab_21;
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
