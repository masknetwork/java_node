// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.bets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewBetPayload extends CPayload
{
   // Feed symbol 
   String feed_symbol_1;
   
   // Feed component symbol
   String feed_component_symbol_1;
   
   // Feed symbol 
   String feed_symbol_2;
   
   // Feed component symbol
   String feed_component_symbol_2;
   
   // Feed symbol 
   String feed_symbol_3;
   
   // Feed component symbol
   String feed_component_symbol_3;
   
   // Tip
   String tip;
   
   // Value 1
   double val_1;
   
   // Value 2
   double val_2;
   
   // Budget
   double budget;
   
   // Win multiplier
   double win_multiplier;
   
   // Can buy start block
   long start_block;
   
   // End block
   long end_block;
   
   // Expires block
   long accept_block;
   
   // Currency
   String cur;
   
   // UID
   long UID;
   
   // Title
   String title;
   
   // Description
   String description;
   
   public CNewBetPayload(String adr, 
                         String feed_symbol_1, 
                         String feed_component_symbol_1, 
                         String feed_symbol_2, 
                         String feed_component_symbol_2, 
                         String feed_symbol_3, 
                         String feed_component_symbol_3, 
                         String tip, 
                         double val_1, 
                         double val_2, 
                         String title,
                         String description,
                         double budget, 
                         double win_multiplier, 
                         long start_block, 
                         long end_block, 
                         long accept_block, 
                         String cur)   throws Exception
   {
       // Constructor
       super(adr);
       
       // Feed symbol 
       this.feed_symbol_1=feed_symbol_1;
   
       // Feed component symbol
       this.feed_component_symbol_1=feed_component_symbol_1;
       
       // Feed symbol 
       this.feed_symbol_2=feed_symbol_2;
   
       // Feed component symbol
       this.feed_component_symbol_2=feed_component_symbol_2;
       
       // Feed symbol 
       this.feed_symbol_3=feed_symbol_3;
   
       // Feed component symbol
       this.feed_component_symbol_3=feed_component_symbol_3;
   
       // Tip
       this.tip=tip;
   
       // Value 1
       this.val_1=val_1;
   
       // Value 2
       this.val_2=val_2;
   
       // Budget
       this.budget=budget;
   
       // Win multiplier
       this.win_multiplier=win_multiplier;
   
       // Can buy start block
       this.start_block=start_block;
   
       // End block
       this.end_block=end_block;
   
       // Expires block
       this.accept_block=accept_block;
       
       // Currency
       this.cur=cur;
   
       // Title
       this.title=title;
       
       // Description
       this.description=description;
       
       // UID
       this.UID=Math.round(Math.random()*1000000000000L+UTILS.BASIC.block());
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.feed_symbol_1+
                             this.feed_component_symbol_1+
                             this.feed_symbol_2+
                             this.feed_component_symbol_2+
                             this.feed_symbol_3+
                             this.feed_component_symbol_3+
                             this.tip+
                             this.UID+
                             String.valueOf(this.val_1)+
                             String.valueOf(this.val_2)+
                             this.title+
                             this.description+
                             String.valueOf(this.budget)+
                             String.valueOf(this.win_multiplier)+
                             String.valueOf(this.start_block)+
                             String.valueOf(this.end_block)+
                             String.valueOf(this.accept_block)+
                             this.cur);
       
       // Sign
       this.sign();
   }
   
   public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Feed 1
           if (!UTILS.BASIC.feedValid(this.feed_symbol_1, this.feed_component_symbol_1))
              return new CResult(false, "Invalid feed", "CNewBetPayload.java", 74); 
           
           // Feed 2
           if (!feed_symbol_2.equals(""))
             if (!UTILS.BASIC.feedValid(this.feed_symbol_2, this.feed_component_symbol_2))
               return new CResult(false, "Invalid feed", "CNewBetPayload.java", 74); 
           
           // Feed 3
           if (!this.feed_symbol_3.equals(""))
             if (!UTILS.BASIC.feedValid(this.feed_symbol_3, this.feed_component_symbol_3))
               return new CResult(false, "Invalid feed", "CNewBetPayload.java", 74); 
           
           // Tip
           if (!tip.equals("ID_TOUCH_UP") &&
               !tip.equals("ID_TOUCH_DOWN") &&
               !tip.equals("ID_NOT_TOUCH_UP") &&
               !tip.equals("ID_NOT_TOUCH_DOWN") &&
               !tip.equals("ID_CLOSE_ABOVE") &&
               !tip.equals("ID_CLOSE_BELOW") && 
               !tip.equals("ID_CLOSE_BETWEEN") && 
               !tip.equals("ID_NOT_CLOSE_BETWEEN") && 
               !tip.equals("ID_CLOSE_EXACT_VALUE"))
           return new CResult(false, "Invalid type", "CNewBetPayload.java", 74); 
   
           // Budget
           if (this.budget<0.01)
              return new CResult(false, "Invalid budget", "CNewBetPayload.java", 74); 
       
           // Win multiplier
           if (this.win_multiplier<1.01)
              return new CResult(false, "Invalid win multiplier", "CNewBetPayload.java", 74); 
       
           // Currency
           if (!this.cur.equals("MSK"))
              if (!UTILS.BASIC.isAsset(this.cur))
                 return new CResult(false, "Invalid currency", "CNewBetPayload.java", 74); 
       
           // End block
           if (this.end_block<=this.start_block)
              return new CResult(false, "Invalid end block", "CNewBetPayload.java", 74); 
       
           // Expires block
           if (this.accept_block>this.end_block)
              return new CResult(false, "Invalid expires block", "CNewBetPayload.java", 74); 
       
           // Funds
           double balance=0;
           
           if (block==null)
              balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, this.cur);
           else
             balance=UTILS.BASIC.getBalance(this.target_adr, this.cur);
           
           if (balance<this.budget)
               return new CResult(false, "Insufficient funds", "CNewBetPayload.java", 74); 
       
           // Put hold on coins
           UTILS.BASIC.newTrans(this.target_adr, 
                                "none",
                                -this.budget, 
                                true,
                                this.cur, 
                                 "Budget for bet "+this.UID, 
                                "", 
                                this.hash, 
                                this.block,
                                block,
                                0);
       
            // Check hash
            String h=UTILS.BASIC.hash(this.getHash()+
                                      this.feed_symbol_1+
                                      this.feed_component_symbol_1+
                                      this.feed_symbol_2+
                                      this.feed_component_symbol_2+
                                      this.feed_symbol_3+
                                      this.feed_component_symbol_3+
                                      this.tip+
                                      this.UID+
                                      String.valueOf(this.val_1)+
                                      String.valueOf(this.val_2)+
                                      this.title+
                                      this.description+
                                      String.valueOf(this.budget)+
                                      String.valueOf(this.win_multiplier)+
                                      String.valueOf(this.start_block)+
                                      String.valueOf(this.end_block)+
                                      String.valueOf(this.accept_block)+
                                      this.cur);
        
           if (!h.equals(this.hash))
           return new CResult(false, "Invalid hash", "CNewBetPayload.java", 74); 
       }
         catch (Exception ex) 
       	 {  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CNewFeedMarketPayload.java", 57);
         }
        
       // Return
       return new CResult(true, "Ok", "CNewBetPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_bets(mktID, "
                                                    + "adr, "
                                                    + "feed_1, "
                                                    + "branch_1, "
                                                    + "feed_2, "
                                                    + "branch_2, "
                                                    + "feed_3, "
                                                    + "branch_3, "
                                                    + "tip, "
                                                    + "val_1, "
                                                    + "val_2, "
                                                    + "title, "
                                                    + "description, "
                                                    + "budget, "
                                                    + "win_multiplier, "
                                                    + "start_block, "
                                                    + "end_block, "
                                                    + "accept_block, "
                                                    + "cur, "
                                                    + "status, "
                                                    + "block) VALUES('"
                                                    +this.UID+"', '"
                                                    +this.target_adr+"', '"
                                                    +this.feed_symbol_1+"', '"
                                                    +this.feed_component_symbol_1+"', '"
                                                    +this.feed_symbol_2+"', '"
                                                    +this.feed_component_symbol_2+"', '"
                                                    +this.feed_symbol_3+"', '"
                                                    +this.feed_component_symbol_3+"', '"
                                                    +this.tip+"', '"
                                                    +this.val_1+"', '"
                                                    +this.val_2+"', '"
                                                    +this.title+"', '"
                                                    +this.description+"', '"
                                                    +this.budget+"', '"
                                                    +this.win_multiplier+"', '"
                                                    +this.start_block+"', '"
                                                    +this.end_block+"', '"
                                                    +this.accept_block+"', '"
                                                    +this.cur+"', 'ID_PENDING', '"
                                                    +this.block+"')");
        
        // Do transfer
        UTILS.BASIC.clearTrans(hash, "ID_ALL");
        
        // Return
	return new CResult(true, "Ok", "CNewBetPayload", 67); 
    }     
    
    
}
