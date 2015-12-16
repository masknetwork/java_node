package wallet.network.packets.markets.feeds.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewSpecMarketPosPayload extends CPayload
{
   // Market symbol
   String mkt_symbol;
   
   // Tip
   String tip;
   
   // Price
   double open;
   
   // Stop loss
   double sl;
   
   // Take profit
   double tp;
   
   // Trailing stop
   double ts;
   
   // Buy qty
   long qty;
   
   // Leverage
   long leverage;
   
   // UID
   String uid;
   
   // Transaction
   CTransPayload trans;
    
   public CNewSpecMarketPosPayload(String adr, 
                                   String mkt_symbol, 
                                   String tip, 
                                   double open,
                                   double sl,
                                   double tp,
                                   double ts,
                                   double qty,
                                   long leverage)
   {
       // Constructor
       super(adr);
       
       try
       {
          // Market symbol
          this.mkt_symbol=mkt_symbol;
   
          // Tip
          this.tip=tip;
   
          // Price
          this.open=open;
   
          // Stop loss
          this.sl=sl;
          
          // Trailing stop
          this.ts=ts;
   
          // Buy qty
          this.tp=tp;
   
          // Leverage
          this.leverage=leverage;
       
          // Statement
          Statement s=UTILS.DB.getStatement();
           
          // Load Market
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM feed_markets "
                                     + "WHERE symbol='"+this.mkt_symbol+"'");
          
          // Next
          rs.next();
          
          // Calculate margin
          double max_loss=0;
          double margin;
          
          // Maximum loss
          if (this.tip.equals("ID_BUY"))
          {
              margin=(this.qty*rs.getDouble("price_ask"))/this.leverage;    
              max_loss=(rs.getDouble("price_ask")-this.sl)*this.qty;
          }
          else
          {
              margin=(this.qty*rs.getDouble("price_bid"))/this.leverage;  
              max_loss=(this.tp-rs.getDouble("price_bid"))*this.qty;
          }
          
          // Margin
          if (margin<max_loss) margin=max_loss;
          
          // Transaction
          this.trans=new CTransPayload(adr, 
                                       rs.getString("adr"), 
                                       margin, 
                                       rs.getString("cur"), 
                                       "", "", "", "", "",
                "", 
                "", 
                "", 
                "", 
                0);
          
          // UID
          this.uid=UTILS.BASIC.randString(10);
          
          // Hash
          hash=UTILS.BASIC.hash(this.getHash()+
                                this.mkt_symbol+
                                this.tip+
                                this.open+
                                this.sl+
                                this.tp+
                                this.ts+
                                this.qty+
                                this.leverage+
                                this.uid);
           
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
           // Market symbol
           if (!UTILS.BASIC.symbolValid(this.mkt_symbol))
              return new CResult(false, "Invalid market symbol", "CFeedMarketgMarketPayload.java", 74); 
        
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Market
           ResultSet rs=s.executeQuery("SELECT * FROM feed_markets WHERE symbol='"+this.mkt_symbol+"'");
           if (UTILS.DB.hasData(rs))
              return new CResult(false, "Invalid market symbol", "CFeedMarketgMarketPayload.java", 74); 
           
           // Leverage
           if (this.leverage>rs.getLong("max_leverage"))
               return new CResult(false, "Invalid leverage", "CFeedMarketgMarketPayload.java", 74); 
           
            // Tip
            if (!this.tip.equals("ID_BUY") && !this.tip.equals("ID_SELL"))
               return new CResult(false, "Invalid operation type", "CFeedMarketgMarketPayload.java", 74); 
         
            // Price
            if (this.tip.equals("ID_BUY"))
            {  
                if (rs.getDouble("price_ask")!=this.open)
                  return new CResult(false, "Invalid price", "CFeedMarketgMarketPayload.java", 74); 
            }
            else
            {
                 if (rs.getDouble("price_bid")!=this.open)
                  return new CResult(false, "Invalid price", "CFeedMarketgMarketPayload.java", 74);
            }
            
            // Calculate margin
          double max_loss=0;
          double margin;
          
          // Maximum loss
          if (this.tip.equals("ID_BUY"))
          {
              margin=(this.qty*rs.getDouble("price_ask"))/this.leverage;    
              max_loss=(rs.getDouble("price_ask")-this.sl)*this.qty;
          }
          else
          {
              margin=(this.qty*rs.getDouble("price_bid"))/this.leverage;  
              max_loss=(this.tp-rs.getDouble("price_bid"))*this.qty;
          }
          
          // Margin
          if (margin<max_loss) margin=max_loss;
            
          if (this.trans.amount<margin)
               return new CResult(false, "Invalid margin amount", "CFeedMarketgMarketPayload.java", 74); 
           
            // Buy qty
            if (this.qty<0.0001)
               return new CResult(false, "Invalid buy qty", "CFeedMarketgMarketPayload.java", 74);
            
            // Leverage
            if (this.leverage>rs.getLong("leverage"))
               return new CResult(false, "Invalid leverage", "CFeedMarketgMarketPayload.java", 74);
            
            // Transaction currency
            if (rs.getString("cur").equals(this.trans.cur))
                return new CResult(false, "Invalid transaction currency", "CFeedMarketgMarketPayload.java", 74);
                
            // Transaction recipient
            if (!this.trans.dest.equals(rs.getString("adr")))
                return new CResult(false, "Invalid transaction recipient", "CFeedMarketgMarketPayload.java", 74);
            
            // Check transaction
            CResult res=this.trans.check(block);
            if (!res.passed) return res;
            
            // UID
            if (!UTILS.BASIC.UIDValid(this.uid))
                return new CResult(false, "Invalid uid", "CFeedMarketgMarketPayload.java", 74);
            
            // UID already exist
            rs=s.executeQuery("SELECT * FROM feeds_mkts_pos WHERE uid='"+this.uid+"'");
            if (UTILS.DB.hasData(rs))
               return new CResult(false, "UID already exist", "CFeedMarketgMarketPayload.java", 74);
            
             String h=UTILS.BASIC.hash(this.getHash()+
                                       this.mkt_symbol+
                                       this.tip+
                                       this.open+
                                       this.sl+
                                       this.tp+
                                       this.ts+
                                       this.qty+
                                       this.leverage+
                                       this.uid);
             
             // Hash match ?
             if (!h.equals(hash))
                 return new CResult(false, "Invalid hash", "CFeedMarketgMarketPayload.java", 74);
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
        this.trans.commit(block);
        
         try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Market
           ResultSet rs=s.executeQuery("SELECT * FROM feed_markets WHERE symbol='"+this.mkt_symbol+"'");
           
           // Next
           rs.next();
           
           // PL
           double pl=0;
           pl=-Math.abs((rs.getDouble("price_ask")-rs.getDouble("price_bid"))*this.qty);
               
           
           // Insert position
           UTILS.DB.executeUpdate("INSERT INTO feeds_spec_mkts_pos(uid, "
                                                                + "adr, "
                                                                + "mkt_symbol, "
                                                                + "tip, "
                                                                + "qty, "
                                                                + "leverage, "
                                                                + "open, "
                                                                + "sl, "
                                                                + "tp, "
                                                                + "ts, "
                                                                + "pl, "
                                                                + "status, "
                                                                + "block) VALUES('"
                                                                +this.uid+"', '"
                                                                +this.target_adr+"', '"
                                                                +this.mkt_symbol+"', '"
                                                                +this.tip+"', '"
                                                                +this.qty+"', '"
                                                                +this.leverage+"', '"
                                                                +this.open+"', '"
                                                                +this.sl+"', '"
                                                                +this.tp+"', '"
                                                                +this.ts+"', '"
                                                                +UTILS.FORMAT.format(pl)+"', '"
                                                                +"ID_MARKET', '"
                                                                +this.block+"')");
        
            // Rowhash
            UTILS.ROWHASH.updateLastID("feeds_spec_mkts_pos");
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
        }
         
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}
