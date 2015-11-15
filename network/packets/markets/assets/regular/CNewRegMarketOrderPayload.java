package wallet.network.packets.markets.assets.regular;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewRegMarketOrderPayload extends CPayload
{
    // Qty
    double qty;
    
    // UID
    String uid;
    
    // Transaction to send the asset to asset buyer
    CTransPayload trans=null;
    
    public CNewRegMarketOrderPayload(String adr, 
                                     String uid,
                                     double qty)
    {
        // Constructor
        super(adr);
        
        try
        {
           // Qty
          this.uid=uid;
          
          // Qty
          this.qty=qty;
          
          // Statement
          Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
          // Load order data
          ResultSet rs_order=s.executeQuery("SELECT * "
                                            + "FROM assets_markets_pos"
                                          + " WHERE uid='"+uid+"'");
          
          rs_order.next();
          
          // Order type
          String tip=rs_order.getString("tip");
          
          // Mkt symbol
          String mkt_symbol=rs_order.getString("mkt_symbol");
          
          // Order address
          String order_adr=rs_order.getString("adr");
          
          // Price
          double price=rs_order.getDouble("price");
        
          // Load market data
          ResultSet rs_mkt=s.executeQuery("SELECT * "
                                          + "FROM assets_markets "
                                         + "WHERE mkt_symbol='"+mkt_symbol+"'");
          rs_mkt.next();
          
          // Asset symbol
          String asset_symbol=rs_mkt.getString("asset_symbol");
          
          // Currency symbol
          String cur_symbol=rs_mkt.getString("cur_symbol");
           
          // Sell order
          String trans_hash;
          
          if (tip.equals("ID_BUY"))
              this.trans=new CTransPayload(this.target_adr, 
                                           order_adr, 
                                           qty, 
                                           asset_symbol, 
                                           "",
                                           "", 
                                           "", 
                                           "",
                                           "",
                                           "", 
                                           "", 
                                           "", 
                                           "", 
                                           0);
          else
              this.trans=new CTransPayload(this.target_adr, 
                                           order_adr, 
                                           price*qty, 
                                           cur_symbol, 
                                           "",
                                           "", 
                                           "", 
                                           "",
                                           "",
                                           "", 
                                           "", 
                                           "", 
                                           "", 
                                           0);
          
          // Transaction hash
          trans_hash=this.trans.hash;
          
          // Hash
          hash=UTILS.BASIC.hash(this.getHash()+
                                this.uid+
                                String.valueOf(qty)+
                                trans_hash);
          
          // Sign
          this.sign();
        }
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CNewRegMarketOrderPayload.java", 57);
        }
    }
   
   public CResult check(CBlockPayload block)
    {
        try
        {
           // Order uid
           if (uid.length()!=10)
               return new CResult(false, "Invalid order UID", "CNewRegMarketOrderPayload.java", 74);
           
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           // Load position details
           ResultSet rs_pos=s.executeQuery("SELECT * "
                                           + "FROM assets_markets_pos "
                                          + "WHERE uid='"+this.uid+"'");
           
           // Has data ?
           if (!UTILS.DB.hasData(rs_pos))
              return new CResult(false, "Invalid position", "CNewRegMarketOrderPayload.java", 74);
           
           // Next
           rs_pos.next();
           
           // Position address
           String pos_adr=rs_pos.getString("adr");
           
           // Position price
           double pos_price=rs_pos.getDouble("price");
           
           // Position size
           double pos_qty=rs_pos.getDouble("qty");
           
           // Type
           String pos_type=rs_pos.getString("tip");
           
           // Market data
           ResultSet rs_mkt=s.executeQuery("SELECT * "
                                           + "FROM assets_markets "
                                          + "WHERE mkt_symbol='"+rs_pos.getString("mkt_symbol")+"'");
          
           // Has data ?
           if (!UTILS.DB.hasData(rs_mkt))
              return new CResult(false, "Invalid market", "CNewRegMarketOrderPayload.java", 74);
           
           // Next 
           rs_mkt.next();
           
           // Asset symbol
           String asset_symbol=rs_mkt.getString("asset_symbol");
           
           // Currency symbol
           String cur_symbol=rs_mkt.getString("cur_symbol");
            
           // Qty
           if (qty<0.0001)
               return new CResult(false, "Invalid qty", "CNewRegMarketOrderPayload.java", 74);
           
           // Transaction source
           if (!this.trans.src.equals(this.target_adr))
                   return new CResult(false, "Invalid transaction source", "CNewRegMarketOrderPayload.java", 74);
           
           // Check transaction
           String trans_hash=this.trans.hash;
              
           // Transaction recipient
           if (!pos_adr.equals(this.trans.dest))
               return new CResult(false, "Invalid transaction recipient", "CNewRegMarketOrderPayload.java", 74);
           
           if (pos_type.equals("ID_SELL"))
           {
              // Transaction qty
              if (this.trans.amount<this.qty*pos_price)
                   return new CResult(false, "Invalid transaction amount", "CNewRegMarketOrderPayload.java", 74);
              
              // Transaction currency
              if (!this.trans.cur.equals(cur_symbol))
                   return new CResult(false, "Invalid transaction amount", "CNewRegMarketOrderPayload.java", 74);
              
              // Escrower
              if (!this.trans.escrower.equals(""))
                 return new CResult(false, "No escrower allowed", "CNewRegMarketOrderPayload.java", 74);
              
              // Check transaction
              CResult res=this.trans.check(block);
              if (!res.passed) return res;
              
              // Insert coins
              UTILS.BASIC.newTrans(this.target_adr, 
                                   "none",
                                   this.qty, 
                                   false,
                                   asset_symbol, 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
          }
          else
          {
              // Transaction qty
              if (this.trans.amount<this.qty)
                   return new CResult(false, "Invalid transaction amount", "CNewRegMarketOrderPayload.java", 74);
              
              // Qty
              if (this.qty>pos_qty)
                  return new CResult(false, "Invalid qty", "CNewRegMarketOrderPayload.java", 74);
              
              // Transaction currency
              if (!this.trans.cur.equals(asset_symbol))
                   return new CResult(false, "Invalid transaction amount", "CNewRegMarketOrderPayload.java", 74);
              
              // Escrower
              if (!this.trans.escrower.equals(""))
                 return new CResult(false, "No escrower allowed", "CNewRegMarketOrderPayload.java", 74);
              
              // Check transaction
              CResult res=this.trans.check(block);
              if (!res.passed) return res;
              
              // Insert assets
              UTILS.BASIC.newTrans(this.target_adr, 
                                   "none",
                                   this.qty*pos_price, 
                                   false,
                                   cur_symbol, 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
          }
           
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.uid+
                                    String.valueOf(qty)+
                                    trans_hash);
          
          if (!h.equals(this.hash))
             return new CResult(false, "Invalid hash", "CNewRegMarketOrderPayload.java", 74);
          
          // Close
          s.close();
          
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewRegMarketOrderPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CNewRegMarketOrderPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Constructor
        CResult res=super.commit(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        try
        {
          // Clear
          UTILS.BASIC.clearTrans(hash, "ID_ALL");
            
          
          // Commit transaction
          this.trans.commit(block);
          
           // Update position
           UTILS.DB.executeUpdate("UPDATE assets_markets_pos "
                                   + "SET qty=qty-"+this.qty+" "
                                 + "WHERE uid='"+this.uid+"'");
           
           // Statement
          Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
          // Load order data
          ResultSet rs_pos=s.executeQuery("SELECT * "
                                          + "FROM assets_markets_pos "
                                         + "WHERE uid='"+this.uid+"'");
          
          // Next
          rs_pos.next();
          
          
          // Market symbol
          String mkt_symbol=rs_pos.getString("mkt_symbol");
          
          // Price
          double price=rs_pos.getDouble("price");
          
          // Left qty
          double left_qty=rs_pos.getDouble("qty");
          
          // Address
          String adr=rs_pos.getString("adr");
          
          // Address
          String tip=rs_pos.getString("tip");
          
          // Update market_price
           UTILS.DB.executeUpdate("UPDATE assets_markets "
                                   + "SET price='"+price+"' "
                                 + "WHERE mkt_symbol='"+mkt_symbol+"'");
            
           // Left qty
           if (left_qty-this.qty<0.0001)
               UTILS.DB.executeUpdate("DELETE FROM assets_markets_pos "
                                          + "WHERE uid='"+this.uid+"'");
           else
            // Rowhash
            UTILS.ROWHASH.update("assets_markets_pos", "uid", this.uid);
           
           // Insert transaction
           String buyer="";
           String seller="";
           
           if (tip.equals("ID_BUY"))
           {
              buyer=adr;
              seller=this.target_adr;
           }
           else
           {
              seller=adr;
              buyer=this.target_adr;
           }
           
           UTILS.DB.executeUpdate("INSERT INTO assets_reg_mkts_trans(uid, "
                                                                  + "buyer, "
                                                                  + "seller, "
                                                                  + "qty, "
                                                                  + "price, "
                                                                  + "block) VALUES('"+
                                                                  this.uid+"', '"+
                                                                  buyer+"', '"+
                                                                  seller+"', '"+
                                                                  this.qty+"', '"+
                                                                  price+"', '"+
                                                                  this.block+"')");
           
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewRegMarketOrderPayload", 67); 
    }        
}
