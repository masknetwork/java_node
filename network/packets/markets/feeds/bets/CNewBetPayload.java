package wallet.network.packets.markets.feeds.bets;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewBetPayload extends CPayload
{
   // Feed symbol 
   String feed_symbol;
   
   // Feed component symbol
   String feed_component_symbol;
   
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
   long expires_block;
   
   // Currency
   String cur;
   
   // UID
   String UID;
   
   // Market bid
   double mkt_bid;
                                 
   public CNewBetPayload(String adr, 
                         String feed_symbol, 
                         String feed_component_symbol, 
                         String tip, 
                         double val_1, 
                         double val_2, 
                         double budget, 
                         double win_multiplier, 
                         long start_block, 
                         long end_block, 
                         long expires_block, 
                         String cur,
                         double mkt_bid)  
   {
       // Feed symbol 
       this.feed_symbol=feed_symbol;
   
       // Feed component symbol
       this.feed_component_symbol=feed_component_symbol;
   
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
       this.expires_block=expires_block;
       
       // Currency
       this.cur=cur;
   
       // Market bid
       this.mkt_bid=mkt_bid;
       
       // UID
       this.UID=UTILS.BASIC.randString(10);
       
       // Hash
       hash=UTILS.BASIC.hash(this.hashCode()+
                             this.feed_symbol+
                             this.feed_component_symbol+
                             this.tip+
                             this.UID+
                             String.valueOf(this.val_1)+
                             String.valueOf(this.val_2)+
                             String.valueOf(this.budget)+
                             String.valueOf(this.win_multiplier)+
                             String.valueOf(this.start_block)+
                             String.valueOf(this.end_block)+
                             String.valueOf(this.expires_block)+
                             this.cur+
                             String.valueOf(this.mkt_bid));
       
       // Sign
       this.sign();
   }
   
   public CResult check(CBlockPayload block)
    {
       // Feed exist and valid
       if (!UTILS.BASIC.feedExist(this.feed_symbol))
          return new CResult(false, "Invalid feed symbol", "CNewBetPayload.java", 74); 
       
       // Feed component symbol exist and valid
       if (!UTILS.BASIC.feedComponentExist(this.feed_symbol, this.feed_component_symbol))
          return new CResult(false, "Invalid feed symbol", "CNewBetPayload.java", 74); 
       
       // Tip
       if (tip.equals("ID_TOUCH") &&
           tip.equals("ID_NO_TOUCH") && 
           tip.equals("ID_ABOVE") &&
           tip.equals("ID_UNDER") && 
           tip.equals("ID_BETWEEN"))
       return new CResult(false, "Invalid type", "CNewBetPayload.java", 74); 
   
       // Budget
       if (this.budget<0.01)
          return new CResult(false, "Invalid budget", "CNewBetPayload.java", 74); 
       
       // Win multiplier
       if (this.win_multiplier<1.01)
          return new CResult(false, "Invalid win multiplier", "CNewBetPayload.java", 74); 
       
       // Currency
       if (!UTILS.BASIC.assetExist(this.cur))
          return new CResult(false, "Invalid currency", "CNewBetPayload.java", 74); 
       
       // Can buy start block
       if (this.start_block<UTILS.BASIC.block())
         return new CResult(false, "Invalid start block", "CNewBetPayload.java", 74); 
       
       // End block
       if (this.end_block<=this.start_block)
          return new CResult(false, "Invalid end block", "CNewBetPayload.java", 74); 
       
       // Expires block
       if (this.expires_block<=this.end_block)
          return new CResult(false, "Invalid expires block", "CNewBetPayload.java", 74); 
       
       // Market bid
       if (this.mkt_bid<0.0001)
          return new CResult(false, "Invalid market bid", "CNewBetPayload.java", 74); 
       
       // Funds
       double balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, this.cur);
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
                            this.block);
       
       // Check hash
       String h=UTILS.BASIC.hash(this.hashCode()+
                             this.feed_symbol+
                             this.feed_component_symbol+
                             this.tip+
                             this.UID+
                             String.valueOf(this.val_1)+
                             String.valueOf(this.val_2)+
                             String.valueOf(this.budget)+
                             String.valueOf(this.win_multiplier)+
                             String.valueOf(this.start_block)+
                             String.valueOf(this.end_block)+
                             String.valueOf(this.expires_block)+
                             this.cur+
                             String.valueOf(this.mkt_bid));
        
       if (!h.equals(this.hash))
           return new CResult(false, "Invalid hash", "CNewBetPayload.java", 74); 
       
       // Return
       return new CResult(true, "Ok", "CNewBetPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Do transfer
        if (this.cur.equals("MSK"))
          UTILS.BASIC.doTrans(this.target_adr, 
                              this.budget, 
                              this.hash);
        else
          UTILS.BASIC.doAssetTrans(this.target_adr, 
                                   this.budget, 
                                   this.cur,
                                   this.hash);
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_bets(uid, "
                                                    + "adr, "
                                                    + "feed_symbol, "
                                                    + "feed_component_symbol, "
                                                    + "tip, "
                                                    + "val_1, "
                                                    + "val_2, "
                                                    + "budget, "
                                                    + "win_multiplier, "
                                                    + "start_block, "
                                                    + "end_block, "
                                                    + "expires_block, "
                                                    + "cur, "
                                                    + "mkt_bid, "
                                                    + "block, "
                                                    + "rowhash) VALUES('"
                                                    +this.UID+"', '"
                                                    +this.target_adr+"', '"
                                                    +this.feed_symbol+"', '"
                                                    +this.feed_component_symbol+"', '"
                                                    +this.tip+"', '"
                                                    +this.val_1+"', '"
                                                    +this.val_2+"', '"
                                                    +this.budget+"', '"
                                                    +this.win_multiplier+"', '"
                                                    +this.start_block+"', '"
                                                    +this.end_block+"', '"
                                                    +this.expires_block+"', '"
                                                    +this.cur+"', '"
                                                    +this.mkt_bid+"', '"
                                                    +this.block+"')");
        
        // Rowhash
        UTILS.ROWHASH.updateLastID("feeds_bets");
        
        // Return
	return new CResult(true, "Ok", "CNewBetPayload", 67); 
    }        
}
