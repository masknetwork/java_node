// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.bets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewBetPayload extends CPayload
{
   // Feed symbol 
   String feed;
   
   // Feed component symbol
   String branch;
   
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
                         String feed, 
                         String branch, 
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
       this.feed=feed;
   
       // Feed component symbol
       this.branch=branch;
       
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
                             this.feed+
                             this.branch+
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
        if (!UTILS.BASIC.isBranch(this.feed, this.branch))
              throw new Exception("Invalid feed - CNewBetPayload.java"); 
        
        // Load last price
        double last_price=UTILS.BASIC.getFeedPrice(this.feed, this.branch);
           
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
        
        // Checks price vs bet type
        switch (tip)
        {
            // Touch up
            case "ID_TOUCH_UP" : if (last_price>=this.val_1)
                                    throw new Exception("Invalid feed price - CNewBetPayload.java"); 
                                 break;
                                 
            // Touch up
            case "ID_TOUCH_DOWN" : if (last_price<=this.val_1)
                                    throw new Exception("Invalid feed price - CNewBetPayload.java"); 
                                   break;
                                   
            // Touch up
            case "ID_NOT_TOUCH_UP" : if (last_price>=this.val_1)
                                         throw new Exception("Invalid feed price - CNewBetPayload.java"); 
                                     break;
                                     
            // Touch up
            case "ID_NOT_TOUCH_DOWN" : if (last_price<=this.val_1)
                                         throw new Exception("Invalid feed price - CNewBetPayload.java"); 
                                       break;
        }
        
        // Val 1
        if (this.val_1<0)
            throw new Exception("Invalid value 1 - CNewBetPayload.java"); 
        
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
        if (this.win_multiplier<1.01)
              throw new Exception("Invalid win multiplier - CNewBetPayload.java"); 
       
        // Currency
        if (!this.cur.equals("MSK"))
        {
            // Valid currency ?
            if (!UTILS.BASIC.isAsset(this.cur))
               throw new Exception("Invalid currency - CNewBetPayload.java"); 
            
            // Asset expire block
            if (UTILS.BASIC.getAssetExpireBlock(this.cur)<this.end_block)
                throw new Exception("Invalid end block - CNewBetPayload.java"); 
        }
        
        // Feed expiration date
        if (UTILS.BASIC.getFeedExpireBlock(feed, branch)<this.end_block)
            throw new Exception("Invalid end block - CNewBetPayload.java"); 
        
        // End / accept block
        if (this.end_block<=this.block+1 || 
            this.accept_block<=this.block+1)
        throw new Exception("Invalid end / accept block - CNewBetPayload.java"); 
       
        // Expires block
        if (this.end_block<=this.accept_block)
              throw new Exception("Invalid accept block - CNewBetPayload.java"); 
        
        // Can spend ?
        if (!UTILS.BASIC.canSpend(this.target_adr))
             throw new Exception("Target address can't spend funds - CNewBetPayload.java"); 
        
        // Funds
        double balance=UTILS.ACC.getBalance(this.target_adr, this.cur, block);
        if (balance<this.budget)
               throw new Exception("Innsuficient funds - CNewBetPayload.java"); 
        
        // Bet ID
        if (UTILS.BASIC.isID(betID))
           throw new Exception("Invalid betID - CNewBetPayload.java"); 
        
        // Put hold on coins
        UTILS.ACC.newTrans(this.target_adr, 
                           "",
                           -this.budget, 
                           this.cur, 
                           "Budget for bet "+this.betID, 
                           "", 
                           this.hash, 
                           this.block);
       
        // Check hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.feed+
                                  this.branch+
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
        
        // Do transfer
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        
        // Load feed
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+this.feed+"' "
                                            + "AND symbol='"+this.branch+"'");
        
        // Next
        rs.next();
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_bets "
                                     + "SET betID='"+this.betID+"', "
                                         + "adr='"+this.target_adr+"', "
                                         + "feed='"+this.feed+"', "
                                         + "branch='"+this.branch+"', "
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
                                         + "last_price='"+rs.getDouble("val")+"', "
                                         + "cur='"+this.cur+"', "
                                         + "status='ID_PENDING', "
                                         + "block='"+this.block+"'");
        
        // Vote feed
        UTILS.BASIC.voteTarget(this.target_adr, 
                               "ID_FEED", 
                               UTILS.BASIC.getFeedID(this.feed), 
                               block.block);
    }     
}
