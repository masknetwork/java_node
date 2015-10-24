package wallet.network.packets.markets.feeds.regular;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewFeedMarketOrderPayload extends CPayload
{
    // Market
    String mkt_symbol;
    
    // Tip
    String tip;
    
    // Qty
    double qty;
    
    // Transaction
    CTransPayload trans;
    
    public CNewFeedMarketOrderPayload(String adr,
                                      String mkt_symbol, 
                                      String tip, 
                                      double qty)
    {
        // Constructor
        super(adr);
        
        try
        {
           // Market
           this.mkt_symbol=mkt_symbol;
    
           // Tip
           this.tip=tip;
    
           // Qty
           this.qty=qty;
        
        
            // Statement
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // Load data
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM feeds_markets "
                                       + "WHERE mkt_symbol='"+mkt_symbol+"'");
           
            // Next
            rs.next();
            
            // Market asset
            String mkt_asset=rs.getString("asset");
            
            // Market currency
            String mkt_cur=rs.getString("cur");
            
            // Market address
            String mkt_adr=rs.getString("mkt_adr");
            
            // Ask
            double ask=rs.getDouble("price_ask");
            
            // Bid
            double bid=rs.getDouble("price_bid");
            
            // Transaction
            if (tip.equals("ID_BUY"))
                this.trans=new CTransPayload(adr, mkt_adr, qty*ask, mkt_cur, "", "", "");
            else
                this.trans=new CTransPayload(adr, mkt_adr, qty*bid, mkt_asset, "", "", "");
            
            // Close
            s.close();
         }
         catch (SQLException ex) 
       	 {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedMarketOrderPayload.java", 57);
         }
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash()+
                                   adr+
                                   mkt_symbol+
                                   tip+
                                   String.valueOf(qty)+
                                   this.trans.hash);
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {
        try
        {
           // Market symbol
           if (!UTILS.BASIC.symbolValid(this.mkt_symbol))
              return new CResult(false, "Invalid market symbol", "CNewFeedMarketOrderPayload.java", 74);
        
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); 
           
           // Market exist ?
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_markets "
                                      + "WHERE mkt_symbol='"+this.mkt_symbol+"' "
                                        + "AND tip='ID_REGULAR' ");
           
           // Data ?
           if (!UTILS.DB.hasData(rs))
              return new CResult(false, "Invalid market symbol", "CNewFeedMarketOrderPayload.java", 74); 
           
           // Next 
           rs.next();
           
            // Market asset
            String mkt_asset=rs.getString("asset");
            
            // Market currency
            String mkt_cur=rs.getString("cur");
            
            // Market address
            String mkt_adr=rs.getString("mkt_adr");
            
            // Ask
            double ask=rs.getDouble("price_ask");
            
            // Bid
            double bid=rs.getDouble("price_bid");
           
            // Tip
            if (!tip.equals("ID_BUY") && !tip.equals("ID_SELL"))
                return new CResult(false, "Invalid operation", "CNewFeedMarketOrderPayload.java", 74); 
    
            // Qty
            if (this.qty<0.00000001)
              return new CResult(false, "Invalid qty", "CNewFeedMarketOrderPayload.java", 74); 
            
            // Check transaction
            CResult res=this.trans.check(block);
            if (!res.passed) return res;
            
            // Transaction recipient
            if (!trans.dest.equals(mkt_adr))
                return new CResult(false, "Invalid transaction recipient", "CNewFeedMarketOrderPayload.java", 74); 
            
            // Funds ?
            if (tip.equals("ID_BUY"))
            {
                // Check balance
                if (UTILS.BASIC.getBalance(this.target_adr, mkt_cur)<ask*this.qty)
                    return new CResult(false, "Insufficient funds to execute this operation", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Transaction currency
                if (!this.trans.cur.equals(mkt_cur))
                   return new CResult(false, "Invalid transaction currency", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Transaction amount
                if (this.trans.amount<ask*this.qty)
                   return new CResult(false, "Invalid transaction amount", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Credit assets
                UTILS.BASIC.newTrans(this.target_adr, 
                                     this.qty, 
                                     false,
                                     mkt_asset, 
                                     "", 
                                     "", 
                                     hash, 
                                     this.block);
                
                // Debit assets
                UTILS.BASIC.newTrans(mkt_adr, 
                                     -this.qty, 
                                     false,
                                     mkt_asset, 
                                     "", 
                                     "", 
                                     hash, 
                                     this.block);
            }
            else
            {
                 // Check balance
                 if (UTILS.BASIC.getBalance(this.target_adr, mkt_asset)<this.qty)
                    return new CResult(false, "Insufficient funds to execute this operation", "CNewFeedMarketOrderPayload.java", 74);
                 
                 // Transaction currency
                 if (!this.trans.cur.equals(mkt_asset))
                   return new CResult(false, "Invalid transaction currency", "CNewFeedMarketOrderPayload.java", 74); 
                
                 // Transaction amount
                 if (this.trans.amount!=this.qty)
                   return new CResult(false, "Invalid transaction amount", "CNewFeedMarketOrderPayload.java", 74); 
                
                 // Credit currency
                 UTILS.BASIC.newTrans(this.target_adr, 
                                      bid*qty, 
                                      false,
                                      mkt_cur, 
                                      "", 
                                      "", 
                                      hash, 
                                      this.block);
                 
                 // Debit currency
                 UTILS.BASIC.newTrans(mkt_adr, 
                                      -bid*qty, 
                                      false,
                                      mkt_cur, 
                                      "", 
                                      "", 
                                      hash, 
                                      this.block);
            }
            
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
                                       this.target_adr+
                                       mkt_symbol+
                                       tip+
                                       String.valueOf(qty)+
                                       this.trans.hash);
            
            if (!h.equals(this.hash))
              return new CResult(false, "Invalid hash", "CNewFeedMarketOrderPayload.java", 74); 
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedMarketOrderPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CNewFeedMarketOrderPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Commit transactions
        UTILS.BASIC.clearTrans(hash, "ID_ALL");
        
        // Clear transaction 
        this.trans.commit(block);
        
        // Price
        double price=0;
        
        try
        {
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); 
           
           // Market exist ?
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_markets "
                                      + "WHERE mkt_symbol='"+this.mkt_symbol+"' "
                                        + "AND tip='ID_REGULAR' ");
           
           // Data ?
           if (!UTILS.DB.hasData(rs))
              return new CResult(false, "Invalid market symbol", "CNewFeedMarketOrderPayload.java", 74); 
           
           // Next
           rs.next();
           
           // Price
           if (this.tip.equals("ID_BUY"))
              price=rs.getDouble("price_ask");
           else
              price=rs.getDouble("price_bid");
           
           // Insert transaction
           UTILS.DB.executeUpdate("INSERT INTO feed_reg_mkts_trans(mkt_symbol, "
                                                                  + "trader, "
                                                                  + "order_type, "
                                                                  + "qty, "
                                                                  + "price, "
                                                                  + "block) VALUES('"+
                                                                  this.mkt_symbol+"', '"+
                                                                  this.target_adr+"', '"+
                                                                  this.tip+"', '"+
                                                                  this.qty+"', '"+
                                                                  price+"', '"+
                                                                  UTILS.BASIC.block()+"')");
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedMarketOrderPayload.java", 57);
        }
          
        
        
        // Return
	return new CResult(true, "Ok", "CNewFeedMarketOrderPayload", 67); 
    }  
}
    

