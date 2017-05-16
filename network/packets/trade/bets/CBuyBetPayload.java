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
import wallet.network.packets.trans.CTransPayload;

public class CBuyBetPayload extends CPayload
{
   // Bet UID
   long betID;
   
   // Amount
   double amount;
   
   public CBuyBetPayload(String adr, 
                         long betID, 
                         double amount) throws Exception
   {
      // Address
      super(adr);
      
      // Bet UID
      this.betID=betID;
      
      // Amount
      this.amount=amount;
      
      // Hash
      hash=UTILS.BASIC.hash(this.getHash()+
                            this.betID+
                            String.valueOf(this.amount));
         
      // Sign
      this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
    {
        // Super class
   	  super.check(block);
          
        // Check bet uid
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_bets "
                                          + "WHERE betID='"+this.betID+"'"
                                            +" AND end_block>"+(this.block+1)
                                            +" AND accept_block>"+(this.block+1));
            
        // Has data ?
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid UID - CBuyBetPayload.java"); 
         
        // Next
        rs.next();
            
        // Funds
        double balance=UTILS.ACC.getBalance(this.target_adr, rs.getString("cur"), block);
       
        // Check balance
        if (balance<this.amount)
            throw new Exception("Innsufficient funds - CBuyBetPayload.java"); 
            
        // Bet budget
        double left_budget=rs.getDouble("budget")/(1.0+rs.getDouble("win_multiplier")/100.0);
            
        // Valid bet
        if ((left_budget-rs.getDouble("invested"))<this.amount)
            throw new Exception("Insufficient budget - CBuyBetPayload.java"); 
            
        // Amount
        if (this.amount<0.0001)
           throw new Exception("Invalid amount - CBuyBetPayload.java"); 
        
        // Bets closed ?
        if (this.block>rs.getLong("accept_block"))
            throw new Exception("Betting ended - CBuyBetPayload.java"); 
            
        // Block the funds
        UTILS.ACC.newTrans(this.target_adr, 
                           "",
                           -Double.parseDouble(UTILS.FORMAT_4.format(this.amount)),
                           true,
                           rs.getString("cur"), 
                           "You have invested in a binary option / bet ("+String.valueOf(this.betID)+")", 
                           "", 
                           this.hash, 
                           this.block,
                           block,
                           0);
            
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.betID+
                                  String.valueOf(this.amount));
            
        if (!this.hash.equals(h))
           throw new Exception("Invalid hash - CBuyBetPayload.java");    
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
           // Super
           super.commit(block);
           
           // Commit
           UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        
           // Load data
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM feeds_bets_pos "
                                             + "WHERE adr='"+this.target_adr+"' "
                                               + "AND betID='"+this.betID+"'");
        
           // Insert position
           if (!UTILS.DB.hasData(rs))
           {
               // Insert position
               UTILS.DB.executeUpdate("INSERT INTO feeds_bets_pos "
                                            + "SET adr='"+this.target_adr+"', "
                                                               + "betID='"+this.betID+"', "
                                                               + "amount='"+UTILS.FORMAT_4.format(this.amount)+"', "
                                                               + "block='"+this.block+"'");
               
               // Increase bets amount
               UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                       + "SET bets=bets+1 "
                                     + "WHERE betID='"+this.betID+"'");
           }
           else
           {
               // Update position
               UTILS.DB.executeUpdate("UPDATE feeds_bets_pos "
                                       + "SET amount=amount+"+UTILS.FORMAT_4.format(this.amount)+", "
                                           + "block='"+this.block+"' "
                                     + "WHERE adr='"+this.target_adr+"' "
                                       + "AND betID='"+this.betID+"'");
           }
           
           // Increase invested value
           UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                   + "SET invested=invested+"+UTILS.FORMAT_4.format(this.amount)+" "
                                 + "WHERE betID='"+this.betID+"'");
           
           // Vote bet
           UTILS.BASIC.voteTarget(this.target_adr, "ID_BET", betID, block.block);
           
           // Load bet details
           rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM feeds_bets "
                                   + "WHERE betID='"+betID+"'");
        
            // Feed ID
            long feedID=UTILS.BASIC.getFeedID(rs.getString("feed"));
        
            // Vote feed
            UTILS.BASIC.voteTarget(this.target_adr, "ID_FEED", feedID, block.block);
       
    }        
}
