package wallet.network.packets.markets.feeds.bets;

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
   String bet_uid;
   
   // Amount
   double amount;
   
   // Transaction
   CTransPayload trans;
   
   public CBuyBetPayload(String adr, 
                         String bet_uid, 
                         double amount)
   {
      // Address
      super(adr);
      
      try
      {
         // Bet UID
         this.bet_uid=bet_uid;
      
         // Amount
         this.amount=amount;
      
         // Statement
         Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      
         // Load bet details
         ResultSet rs=s.executeQuery("SELECT * FROM feeds_bets WHERE uid='"+this.bet_uid+"'");
         
         // Transaction
         this.trans=new CTransPayload(this.target_adr, 
                                      rs.getString("adr"), 
                                      this.amount, 
                                      rs.getString("cur"), 
                                      "", "", "");
         
         // Hash
         hash=UTILS.BASIC.hash(this.hashCode()+
                               this.bet_uid+
                               String.valueOf(this.amount));
         
         // Close
         s.close();
      }
      catch (SQLException ex) 
      {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
      }
      
      // Sign
      this.sign();
   }
   
   public CResult check(CBlockPayload block)
    {
        try
        {
          // Check bet uid
          if (!UTILS.BASIC.UIDValid(this.bet_uid))
            return new CResult(false, "Invalid UID", "CNewBetPayload.java", 74); 
          
          // Statement
          Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
          
          // Bet uid exist
          ResultSet rs=s.executeQuery("SELECT * FROM feeds_bets WHERE uid='"+this.bet_uid+"'");
          if (!UTILS.DB.hasData(rs))
            return new CResult(false, "Invalid UID", "CNewBetPayload.java", 74); 
         
          // Check trans
          CResult res=this.trans.check(block);
          if (!res.passed) return res;
          
          // Transaction recipient
          if (!this.trans.dest.equals(rs.getString("adr")))
             return new CResult(false, "Invalid transaction recipient", "CNewBetPayload.java", 74); 
          
          // Transaction currency
          if (!this.trans.cur.equals(rs.getString("cur")))
             return new CResult(false, "Invalid transaction currency", "CNewBetPayload.java", 74); 
          
          // Transaction amount
          if (this.trans.amount<this.amount)
             return new CResult(false, "Invalid transaction amount", "CNewBetPayload.java", 74); 
          
          // Escrower
          if (!this.trans.escrower.equals(""))
             return new CResult(false, "Invalid escrower", "CNewBetPayload.java", 74); 
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
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_bets_pos(adr, "
                                                        + "bet_uid, "
                                                        + "amount, "
                                                        + "block) VALUES('"
                                                        +this.target_adr+"', '"
                                                        +this.bet_uid+"', '"
                                                        +this.amount+"', '"
                                                        +this.block+"')");
        
        // Rowhash
        UTILS.ROWHASH.updateLastID("feeds_bets");
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}
