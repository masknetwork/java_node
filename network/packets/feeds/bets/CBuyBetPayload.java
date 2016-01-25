package wallet.network.packets.feeds.bets;

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
   long bet_uid;
   
   // Amount
   double amount;
   
   public CBuyBetPayload(String adr, 
                         long bet_uid, 
                         double amount)
   {
      // Address
      super(adr);
      
      // Bet UID
      this.bet_uid=bet_uid;
      
      // Amount
      this.amount=amount;
      
      // Hash
      hash=UTILS.BASIC.hash(this.getHash()+
                            this.bet_uid+
                            String.valueOf(this.amount));
         
      // Sign
      this.sign();
   }
   
   public CResult check(CBlockPayload block)
    {
        try
        {
            // Statement
            Statement s=UTILS.DB.getStatement();
          
            // Check bet uid
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM feeds_bets "
                                       + "WHERE uid='"+this.bet_uid+"'"
                                         +" AND end_block>"+UTILS.BASIC.block()
                                         +" AND accept_block>"+UTILS.BASIC.block());
            
            // Has data ?
            if (!UTILS.DB.hasData(rs))
               return new CResult(false, "Invalid UID", "CNewBetPayload.java", 74); 
         
            // Next
            rs.next();
            
            // Funds
            if (UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, rs.getString("cur"))<this.amount)
                 return new CResult(false, "Insufficient funds to execute the operation", "CNewBetPayload.java", 74); 
            
            // Bet budget
            double left_budget=rs.getDouble("budget")/(1.0+rs.getDouble("win_multiplier")/100.0);
            
            if (left_budget<this.amount)
                return new CResult(false, "Insufficient bet budget to execute the operation", "CNewBetPayload.java", 74); 
            
            // Amount
            if (this.amount<0.0001)
               return new CResult(false, "Insufficient amount", "CNewBetPayload.java", 74); 
            
            // Block the funds
            UTILS.BASIC.newTrans(this.target_adr, 
                                 "",
                                 -this.amount,
                                 true,
                                 rs.getString("cur"), 
                                 "You have invested in a binary option / bet ("+String.valueOf(this.bet_uid)+")", 
                                 "", 
                                 this.hash, 
                                 this.block);
            
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
                                      this.bet_uid+
                                      String.valueOf(this.amount));
            
            if (!this.hash.equals(h))
                return new CResult(false, "Invalid hash", "CNewBetPayload.java", 74); 
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        try
        {
           // Super
           CResult res=super.commit(block);
           if (!res.passed) return res;
        
           // Check
           res=this.check(block);
           if (!res.passed) return res;
        
           // Statement
           Statement s=UTILS.DB.getStatement();
          
           // Load data
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_bets_pos "
                                      + "WHERE adr='"+this.target_adr+"' "
                                        + "AND bet_uid='"+this.bet_uid+"'");
        
           // Insert position
           if (!UTILS.DB.hasData(rs))
           {
               // Insert position
               UTILS.DB.executeUpdate("INSERT INTO feeds_bets_pos(adr, "
                                                               + "bet_uid, "
                                                               + "amount, "
                                                               + "block) VALUES('"
                                                               +this.target_adr+"', '"
                                                               +this.bet_uid+"', '"
                                                               +this.amount+"', '"
                                                               +this.block+"')");
               
               // Increase bets amount
               UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                       + "SET bets=bets+1 "
                                     + "WHERE uid='"+this.bet_uid+"'");
           }
           else
           {
               // Update position
               UTILS.DB.executeUpdate("UPDATE feeds_bets_pos "
                                       + "SET amount=amount+"+this.amount+", "
                                           + "block='"+this.block+"' "
                                     + "WHERE adr='"+this.target_adr+"' "
                                       + "AND bet_uid='"+this.bet_uid+"'");
           }
           
           // Increase invested value
           UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                   + "SET invested=invested+"+this.amount+" "
                                 + "WHERE uid='"+this.bet_uid+"'");
                   
           // Commit
           UTILS.BASIC.clearTrans(hash, "ID_ALL");
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}