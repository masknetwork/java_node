package wallet.network.packets.markets.feeds.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CClosePosPayload extends CPayload
{
   // Pos uid
   String uid;
   
   public CClosePosPayload(String adr, String pos_uid)
   {
       // Constructor
       super(adr);
       
       // UID
       this.uid=pos_uid;
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             String.valueOf(pos_uid));
       
       // Sign
       this.sign();
   }
   
   public CResult check(CBlockPayload block)
    {
        try
       {
          // Statement
          Statement s=UTILS.DB.getStatement();
          
          // Load position data
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM feed_markets_pos"
                                     + "WHERE uid='"+this.uid+"' "
                                       + "AND adr='"+this.target_adr+"'"
                                       + "AND status='ID_MARKET'");
          
          // Valid ?
          if (!UTILS.DB.hasData(rs))
               return new CResult(false, "Invalid order", "CBuyDomainPacket", 42);
          
          // Next
          rs.next();
          
          // Market symbol
          String mkt_symbol=rs.getString("mkt_symbol");
          
          // Order address
          String order_adr=rs.getString("adr");
          
          // Load market data
          rs=s.executeQuery("SELECT * "
                            + "FROM feeds_markets "
                           + "WHERE mkt_symbol='"+mkt_symbol+"'");
          
          // Next
          rs.next();
          
          // Market address
          String mkt_adr=rs.getString("mkt_adr");
          
          // Market currency
          String mkt_cur=rs.getString("mkt_cur");
          
          // Market balance
          double mkt_balance=UTILS.BASIC.getBalance(mkt_adr, mkt_cur);
          
          // To pay
          double pay=rs.getDouble("pl")+rs.getDouble("margin");
          
          // Market balance lower than payment owned ?
          if (mkt_balance<pay) pay=mkt_balance;
          
          // Debit market address
          UTILS.BASIC.newTrans(mkt_adr, 
                               order_adr,
                               -pay,
                               true,
                               mkt_cur, 
                               "Order "+this.uid+" has been closed and the profit / losss payed", 
                               "", 
                               this.hash, 
                               this.block);
         
          // Credit owner address
          UTILS.BASIC.newTrans(order_adr, 
                               mkt_adr,
                               pay,
                               true,
                               mkt_cur, 
                               "Order "+this.uid+" has been closed and the profit / losss payed", 
                               "", 
                               this.hash, 
                               this.block);
          
           // Close
           s.close();
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
        
        // Commit payment
        UTILS.BASIC.clearTrans(hash, "ID_ALL");
         
        // Update order
        UTILS.DB.executeUpdate("UPDATE feed_spec_mkts_pos "
                                + "SET status='ID_CLOSED', "
                                    + "close_reason='ID_MANUAL' "
                              + "WHERE uid='"+this.uid+"'");
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}
