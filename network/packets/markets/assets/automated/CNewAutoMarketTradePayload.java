package wallet.network.packets.markets.assets.automated;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewAutoMarketTradePayload extends CPayload
{
    // Address
    String adr;
    
    // Market symbol
    String mkt_symbol;
    
    // Tip
    String tip;
    
    // Qty
    double qty;
    
    // Price
    double start_price;
    
    // End price
    double end_price;
    
    // Asset transaction
    CTransPayload trans=null;
    
     
    public CNewAutoMarketTradePayload(String adr, 
                                      String mkt_symbol,
                                      String tip,
                                      double qty)
    {
        // Constructor
        super(adr);
    
        // Market symbol
        this.mkt_symbol=mkt_symbol;
    
        // Tip
        this.tip=tip;
    
        // Qty
        this.qty=qty;
        
        // Trans hash
        String trans_hash;
        
        try
        {
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
           // Load market data
           ResultSet rs_mkt=s.executeQuery("SELECT * "
                                           + "FROM assets_markets "
                                          + "WHERE mkt_symbol='"+this.mkt_symbol+"' "
                                            + "AND tip='ID_AUTO'");
           
           // Next 
           rs_mkt.next();
           
           // Start price
           this.start_price=rs_mkt.getDouble("tmp_price");
           
           // Volatility
           double vol=rs_mkt.getDouble("volatility");
           
           // Initiate transaction
           if (tip.equals("ID_BUY"))
           {
               // Calculate amount to pay
              double price=rs_mkt.getDouble("price");
              
              // Price difference
              double dif=this.qty*vol;
              
               // New price
              this.end_price=price+dif;
              
              // To pay
              double to_pay=(((dif*this.qty)+dif)*this.qty)/2+(price*this.qty);
              
              // Transaction
              this.trans=new CTransPayload(this.target_adr, 
                                           rs_mkt.getString("mkt_adr"), 
                                           to_pay, 
                                           rs_mkt.getString("cur_symbol"), 
                                           "", 
                                           "", 
                                           "");
              
              // Transaction hash
              trans_hash=this.trans.hash;
           }
           else
           {
               // Calculate amount to pay
              double price=rs_mkt.getDouble("price");
              
              // Price difference
              double dif=this.qty*vol;
              
               // New price
              this.end_price=price-dif;
              
              // Transaction
              this.trans=new CTransPayload(this.target_adr, 
                                           rs_mkt.getString("mkt_adr"), 
                                           qty, 
                                           rs_mkt.getString("asset_symbol"), 
                                           "", "", "");
              
              // Transaction hash
              trans_hash=this.trans.hash;
           }
           
           // Hash
           hash=UTILS.BASIC.hash(this.getHash()+
                                 this.adr+
                                 this.mkt_symbol+
                                 this.tip+
                                 String.valueOf(this.qty)+
                                 String.valueOf(this.start_price)+
                                 String.valueOf(this.end_price)+
                                 trans_hash);
        
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CAutoMarketTradePayload.java", 57);
        }
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {
        double price=0;
        
        // Market symbol
        if (!UTILS.BASIC.symbolValid(this.mkt_symbol))
           return new CResult(false, "Invalid market symbol", "CAutoMarketTradePayload.java", 74);
        
        try
        {
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           ResultSet rs_mkt=s.executeQuery("SELECT * "
                                           + "FROM assets_markets "
                                          + "WHERE mkt_symbol='"+this.mkt_symbol+"'"
                                            + "AND tip='ID_AUTO'");
           
           if (!UTILS.DB.hasData(rs_mkt))
              return new CResult(false, "Invalid market", "CAutoMarketTradePayload.java", 74);
           
           // Next
           rs_mkt.next();
          
           // Assets balance
           double assets_balance=UTILS.NETWORK.TRANS_POOL.getBalance(rs_mkt.getString("mkt_adr"), rs_mkt.getString("asset_symbol"));
           
           // Currency balance
           double cur_balance=UTILS.NETWORK.TRANS_POOL.getBalance(rs_mkt.getString("mkt_adr"), rs_mkt.getString("cur_symbol"));
           
           // Tip
           if (!tip.equals("ID_BUY") && !tip.equals("ID_SELL"))
              return new CResult(false, "Invalid type", "CAutoMarketTradePayload.java", 74);
           
           // Qty
           if (qty<0.0001)
              return new CResult(false, "Invalid qty", "CAutoMarketTradePayload.java", 74);
           
           // Check transaction
           String trans_hash;
           if (tip.equals("ID_BUY"))
           {
              // Transaction source 
              if (!this.target_adr.equals(this.trans.src))
                   return new CResult(false, "Invalid transaction source", "CAutoMarketTradePayload.java", 74);
                
              // Transaction recipient
              if (!rs_mkt.getString("mkt_adr").equals(this.trans.dest))
                   return new CResult(false, "Invalid transaction recipient", "CAutoMarketTradePayload.java", 74);
              
              // Calculate amount to pay
              double p=rs_mkt.getDouble("price");
              
              // Price difference
              double dif=this.qty*rs_mkt.getDouble("volatility");
              
               // New price
              double new_price=p+dif;
              if (this.end_price!=new_price)
                  return new CResult(false, "Invalid end price", "CAutoMarketTradePayload.java", 74);
              
              // To pay
              double to_pay=(((dif*this.qty)+dif)*this.qty)/2+(p*this.qty);
              
              // Transaction qty
              if (this.trans.amount<to_pay)
                   return new CResult(false, "Invalid transaction amount", "CAutoMarketTradePayload.java", 74);
              
              // Transaction currency
              if (!this.trans.cur.equals(rs_mkt.getString("cur_symbol")))
                   return new CResult(false, "Invalid transaction amount", "CAutoMarketTradePayload.java", 74);
              
              // Escrower
              if (!this.trans.escrower.equals(""))
                 return new CResult(false, "No escrower allowed", "CAutoMarketTradePayload.java", 74);
              
              // Transaction hash
              trans_hash=this.trans.hash;
              
              // Check transaction
              CResult res=this.trans.check(block);
              if (!res.passed) return res;
              
              // Update price
              UTILS.DB.executeUpdate("UPDATE assets_markets "
                                      + "SET tmp_price='"+new_price+"' "
                                    + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
              
              // Send assets
              UTILS.BASIC.newTrans(rs_mkt.getString("mkt_adr"), 
                                   -this.qty, 
                                   true,
                                   rs_mkt.getString("asset_symbol"), 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
              
              // Receive assets
              UTILS.BASIC.newTrans(this.target_adr, 
                                   this.qty, 
                                   true,
                                   rs_mkt.getString("asset_symbol"), 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
          }
          else
          {
              // Transaction source 
              if (!this.target_adr.equals(this.trans.src))
                   return new CResult(false, "Invalid transaction source", "CAutoMarketTradePayload.java", 74);
                
              // Transaction recipient
              if (!rs_mkt.getString("mkt_adr").equals(this.trans.dest))
                   return new CResult(false, "Invalid transaction recipient", "CAutoMarketTradePayload.java", 74);
               
              // Transaction qty
              if (this.trans.amount!=this.qty)
                   return new CResult(false, "Invalid transaction amount", "CAutoMarketTradePayload.java", 74);
              
              // Transaction currency
              if (!this.trans.cur.equals(rs_mkt.getString("asset_symbol")))
                   return new CResult(false, "Invalid transaction amount", "CAutoMarketTradePayload.java", 74);
              
              // Escrower
              if (!this.trans.escrower.equals(""))
                 return new CResult(false, "No escrower allowed", "CAutoMarketTradePayload.java", 74);
              
              // Transaction hash
              trans_hash=this.trans.hash;
              
              // Check transaction
              CResult res=this.trans.check(block);
              if (!res.passed) return res;
              
              // Calculate amount to pay
              double p=rs_mkt.getDouble("price");
              
              // Price difference
              double dif=this.qty*rs_mkt.getDouble("volatility");
              
               // New price
              double new_price=p-dif;
              if (this.end_price!=new_price)
                  return new CResult(false, "Invalid end price", "CAutoMarketTradePayload.java", 74);
              
              // To pay
              double to_pay=(p*this.qty)-((dif*this.qty+dif)*this.qty/2);
              
              // Send coins
              UTILS.BASIC.newTrans(rs_mkt.getString("mkt_adr"), 
                                   -to_pay, 
                                   true,
                                   rs_mkt.getString("cur_symbol"), 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
              
              // Receive acoins
              UTILS.BASIC.newTrans(this.target_adr, 
                                   to_pay, 
                                   true,
                                   rs_mkt.getString("cur_symbol"), 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
          }
           
          // Update temp price
          UTILS.DB.executeUpdate("UPDATE assets_markets "
                                  + "SET tmp_price='"+this.end_price+"' "
                                + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
          
          // Insert transaction
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM assets_auto_mkts_trans "
                                     + "WHERE hash='"+this.hash+"'");
          
          if (!UTILS.DB.hasData(rs))
          UTILS.DB.executeUpdate("INSERT INTO assets_auto_mkts_trans (adr, "
                                                             + "mkt_symbol, "
                                                             + "qty, "
                                                             + "order_type, "
                                                             + "old_price, "
                                                             + "new_price, "
                                                             + "block, "
                                                             + "hash) VALUES('"
                                                             +this.target_adr+"', '"
                                                             +this.mkt_symbol+"', '"
                                                             +this.qty+"', '"
                                                             +this.tip+"', '"
                                                             +this.start_price+"', '"
                                                             +this.end_price+"', '"
                                                             +this.block+"', '"
                                                             +this.hash+"')");
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CAutoMarketTradePayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CAutoMarketTradePayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
         // Super
        CResult res=super.commit(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Commit transaction
        res=this.trans.commit(block);
        if (!res.passed) return res;
        
        // Commit reverse transaction
        UTILS.BASIC.clearTrans(hash, "ID_ALL");
        
        // Update price
        UTILS.DB.executeUpdate("UPDATE assets_markets "
                                + "SET price=tmp_price "
                              + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
        
        // Clear trasnaction
        UTILS.DB.executeUpdate("UPDATE assets_auto_mkts_trans "
                                + "SET status='ID_CLEARED' "
                              + "WHERE hash='"+this.hash+"'");
             
        // Return
	return new CResult(true, "Ok", "CAutoMarketTradePayload", 67); 
    }       
}
