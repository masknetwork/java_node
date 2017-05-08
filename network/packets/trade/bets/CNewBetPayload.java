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
   
   // End block
   long end_block;
   
   // Expires block
   long accept_block;
   
   // Currency
   String cur;
   
   // BetID
   long betID;
   
   // Title
   String title;
   
   // Description
   String description;
   
   public CNewBetPayload(String adr, 
                         String feed_symbol, 
                         String feed_component_symbol, 
                         String tip, 
                         double val_1, 
                         double val_2, 
                         String title,
                         String description,
                         double budget, 
                         double win_multiplier, 
                         long end_block, 
                         long accept_block, 
                         String cur)   throws Exception
   {
       // Constructor
       super(adr);
       
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
       
       // betID
       this.betID=UTILS.BASIC.getID();
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.feed_symbol+
                             this.feed_component_symbol+
                             this.tip+
                             this.betID+
                             String.valueOf(this.val_1)+
                             String.valueOf(this.val_2)+
                             this.title+
                             this.description+
                             String.valueOf(this.budget)+
                             String.valueOf(this.win_multiplier)+
                             String.valueOf(this.end_block)+
                             String.valueOf(this.accept_block)+
                             this.cur);
       
       // Sign
       this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
   	  super.check(block);
          
        // Feed 1
        if (!UTILS.BASIC.isBranch(this.feed_symbol, this.feed_component_symbol))
              throw new Exception("Invalid feed - CNewBetPayload.java"); 
           
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
        throw new Exception("Invalid type - CNewBetPayload.java"); 
        
        
        // Values
        if (tip.equals("ID_CLOSE_BETWEEN") || 
            tip.equals("ID_NOT_CLOSE_BETWEEN"))
            if (this.val_2<=this.val_1)
               throw new Exception("Invalid values - CNewBetPayload.java"); 
        
        // Title
        if (!UTILS.BASIC.isTitle(this.title))
           throw new Exception("Invalid title - CNewBetPayload.java"); 
        
        // Description
        if (!UTILS.BASIC.isDesc(this.description))
           throw new Exception("Invalid description - CNewBetPayload.java"); 
   
        // Budget
        if (this.budget<0.01)
           throw new Exception("Invalid budget - CNewBetPayload.java"); 
       
        // Win multiplier
        if (this.win_multiplier<1)
              throw new Exception("Invalid win multiplier - CNewBetPayload.java"); 
       
        // Currency
        if (!this.cur.equals("MSK"))
              if (!UTILS.BASIC.isAsset(this.cur))
                 throw new Exception("Invalid currency - CNewBetPayload.java"); 
       
        // End block
        if (this.end_block<=this.block+1)
              throw new Exception("Invalid end block - CNewBetPayload.java"); 
       
        // Expires block
        if (this.accept_block>this.end_block)
              throw new Exception("Invalid accept block - CNewBetPayload.java"); 
       
        // Funds
        double balance=UTILS.ACC.getBalance(this.target_adr, this.cur, block);
           
        if (balance<this.budget)
               throw new Exception("Innsuficient funds - CNewBetPayload.java"); 
       
        // Put hold on coins
        UTILS.ACC.newTrans(this.target_adr, 
                           "none",
                           -this.budget, 
                           true,
                           this.cur, 
                           "Budget for bet "+this.betID, 
                           "", 
                           this.hash, 
                           this.block,
                           block,
                           0);
       
        // Check hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.feed_symbol+
                                  this.feed_component_symbol+
                                  this.tip+
                                  this.betID+
                                  String.valueOf(this.val_1)+
                                  String.valueOf(this.val_2)+
                                  this.title+
                                  this.description+
                                  String.valueOf(this.budget)+
                                  String.valueOf(this.win_multiplier)+
                                  String.valueOf(this.end_block)+
                                  String.valueOf(this.accept_block)+
                                  this.cur);
        
        if (!h.equals(this.hash))
           throw new Exception("Invalid hash - CNewBetPayload.java"); 
       
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_bets "
                                     + "SET betID='"+this.betID+"', "
                                         + "adr='"+this.target_adr+"', "
                                         + "feed='"+this.feed_symbol+"', "
                                         + "branch='"+this.feed_component_symbol+"', "
                                         + "tip='"+this.tip+"', "
                                         + "val_1='"+this.val_1+"', "
                                         + "val_2='"+this.val_2+"', "
                                         + "title='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
                                         + "budget='"+this.budget+"', "
                                         + "win_multiplier='"+this.win_multiplier+"', "
                                         + "start_block='"+this.block+"', "
                                         + "end_block='"+this.end_block+"', "
                                         + "accept_block='"+this.accept_block+"', "
                                         + "cur='"+this.cur+"', "
                                         + "status='ID_PENDING', "
                                         + "block='"+this.block+"'");
        
        // Do transfer
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        
        // Vote bet
        UTILS.BASIC.voteTarget(this.target_adr, "ID_BET", betID, block.block);
        
        // Feed ID
        long feedID=UTILS.BASIC.getFeedID(this.feed_symbol);
        
        // Vote feed
        UTILS.BASIC.voteTarget(this.target_adr, "ID_FEED", feedID, block.block);
    }     
}
