// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.bets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.agents.CAgent;
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
   
   // BetID
   long betID;
   
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
       
       // betID
       this.betID=UTILS.BASIC.getID();
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.feed_symbol_1+
                             this.feed_component_symbol_1+
                             this.feed_symbol_2+
                             this.feed_component_symbol_2+
                             this.feed_symbol_3+
                             this.feed_component_symbol_3+
                             this.tip+
                             this.betID+
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
   
   public void check(CBlockPayload block) throws Exception
   {
        // Feed 1
        if (!UTILS.BASIC.isBranch(this.feed_symbol_1, this.feed_component_symbol_1))
              throw new Exception("Invalid feed - CNewBetPayload.java"); 
           
        // Feed 2
        if (!feed_symbol_2.equals(""))
           if (!UTILS.BASIC.isBranch(this.feed_symbol_2, this.feed_component_symbol_2))
               throw new Exception("Invalid feed - CNewBetPayload.java"); 
           
        // Feed 3
        if (!this.feed_symbol_3.equals(""))
           if (!UTILS.BASIC.isBranch(this.feed_symbol_3, this.feed_component_symbol_3))
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
        
        // Bet ID
        if (UTILS.BASIC.validID(this.betID))
            throw new Exception("Invalid bet ID - CNewBetPayload.java"); 
        
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
        if (this.end_block<=this.start_block)
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
                                  this.feed_symbol_1+
                                  this.feed_component_symbol_1+
                                  this.feed_symbol_2+
                                  this.feed_component_symbol_2+
                                  this.feed_symbol_3+
                                  this.feed_component_symbol_3+
                                  this.tip+
                                  this.betID+
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
           throw new Exception("Invalid hash - CNewBetPayload.java"); 
       
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        if (!this.cur.equals("MSK"))
        {
           // Asset has contract attached ?
           long asset_aID=UTILS.BASIC.getAssetContract(this.cur);
        
           // Has contract ?
           if (asset_aID>0)
           {
               // Asset has contract attached ?
               long cur_aID=UTILS.BASIC.getAssetContract(this.cur);
        
               // Has contract ?
               if (asset_aID>0)
               {
                   // Load message
                   CAgent AGENT=new CAgent(cur_aID, false, this.block);
                    
                   // Set Message 
                   AGENT.VM.SYS.EVENT.loadBetOpen(this.betID,
                                                  this.feed_symbol_1,
                                                  this.feed_component_symbol_1,
                                                  this.feed_symbol_2,
                                                  this.feed_component_symbol_2,
                                                  this.feed_symbol_3,
                                                  this.feed_component_symbol_3,
                                                  this.tip,
                                                  this.val_1,
                                                  this.val_2,
                                                  this.title,
                                                  this.description,
                                                  this.budget,
                                                  this.win_multiplier,
                                                  this.start_block,
                                                  this.end_block,
                                                  this.accept_block,
                                                  this.cur,
                                                  this.hash,
                                                  this.block);
                            
                   // Execute
                   AGENT.execute("#new_asset_market#", false, this.block);
            
                   // Aproved ?
                   if (!AGENT.VM.SYS.EVENT.OPEN_MARKET.APPROVED)
                       throw new Exception("rejected by application - CNewRegMarketPayload.java");
            }
          }
        }
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_bets "
                                     + "SET betID='"+this.betID+"', "
                                         + "adr='"+this.target_adr+"', "
                                         + "feed_1='"+this.feed_symbol_1+"', "
                                         + "branch_1='"+this.feed_component_symbol_1+"', "
                                         + "feed_2='"+this.feed_symbol_2+"', "
                                         + "branch_2='"+this.feed_component_symbol_2+"', "
                                         + "feed_3='"+this.feed_symbol_3+"', "
                                         + "branch_3='"+this.feed_component_symbol_3+"', "
                                         + "tip='"+this.tip+"', "
                                         + "val_1='"+this.val_1+"', "
                                         + "val_2='"+this.val_2+"', "
                                         + "title='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
                                         + "budget='"+this.budget+"', "
                                         + "win_multiplier='"+this.win_multiplier+"', "
                                         + "start_block='"+this.start_block+"', "
                                         + "end_block='"+this.end_block+"', "
                                         + "accept_block='"+this.accept_block+"', "
                                         + "cur='"+this.cur+"', "
                                         + "status='ID_PENDING', "
                                         + "block='"+this.block+"'");
        
        // Do transfer
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
    }     
}
