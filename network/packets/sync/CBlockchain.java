package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.kernel.*;
import wallet.network.CResult;

public class CBlockchain 
{
   // Blocks
   public ArrayList<CChainBlock> blocks=new ArrayList<CChainBlock>();
           
   public CBlockchain(long start) throws Exception
   {
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load data
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM blocks "
                                  + "WHERE block>="+start);
       
       // Add block
       while (rs.next())
       this.blocks.add(new CChainBlock(rs.getString("prev_hash"),
                                       rs.getString("hash"),
                                       rs.getLong("block"), 
                                       rs.getString("signer"), 
                                       rs.getLong("signer_balance"),
                                       rs.getLong("tstamp"), 
                                       rs.getLong("nonce"), 
                                       rs.getString("payload_hash"),
                                       rs.getLong("net_dif")));
       
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
               if (!cur_block.prev_hash.equals(prev_block.block_hash))
                   return new CResult(false, "Invalid previous hash (block "+cur_block.block+")", "CNewFeedPayload", 67);
               
               // Check curent block
               CResult res=cur_block.check();
               if (!res.passed) 
                  return new CResult(false, "Invalid block (block "+cur_block.block+")", "CNewFeedPayload", 67);
           }
       }      
               
       return new CResult(true, "Ok", "CNewFeedPayload", 67);
   }
}
