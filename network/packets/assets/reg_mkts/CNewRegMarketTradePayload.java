// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewRegMarketTradePayload extends CPayload
{
    // Qty
    double qty;
    
    // UID
    String orderID;
  
    
    public CNewRegMarketTradePayload(String adr, 
                                     String orderID,
                                     double qty) throws Exception
    {
        // Constructor
        super(adr);
        
        try
        {
           // Qty
          this.orderID=orderID;
          
          // Qty
          this.qty=qty;
          
          // Statement
          Statement s=UTILS.DB.getStatement();
        
          // Hash
          hash=UTILS.BASIC.hash(this.getHash()+
                                this.orderID+
                                String.valueOf(qty));
          
          // Sign
          this.sign();
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CNewRegMarketOrderPayload.java", 57);
        }
    }
   
   public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Load position details
           ResultSet rs_pos=s.executeQuery("SELECT amp.*, am.asset, am.cur "
                                           + "FROM assets_mkts_pos AS amp "
                                           + "JOIN assets_mkts AS am ON am.mktID=amp.mktID "
                                          + "WHERE orderID='"+this.orderID+"'");
           
           // Has data ?
           if (!UTILS.DB.hasData(rs_pos))
              return new CResult(false, "Invalid position", "CNewRegMarketOrderPayload.java", 74);
           
           // Next
           rs_pos.next();
           
           // Asset symbol
           String asset_symbol=rs_pos.getString("asset");
           
           // Currency symbol
           String cur_symbol=rs_pos.getString("cur");
            
           // Qty
           if (qty<0.00000001)
               return new CResult(false, "Invalid qty", "CNewRegMarketOrderPayload.java", 74);
           
           if (rs_pos.getString("tip").equals("ID_SELL"))
           {
              // Calculates price
              double price=rs_pos.getDouble("price")*this.qty;
              
              // Funds 
              if (UTILS.BASIC.getBalance(this.target_adr, rs_pos.getString("cur"), block)<price)
                  return new CResult(false, "Insuficient funds to execute transaction", "CNewRegMarketOrderPayload.java", 74);
               
              // Sends the currency
              UTILS.BASIC.newTransfer(this.target_adr, 
                                      rs_pos.getString("adr"),
                                      price, 
                                      false,
                                      rs_pos.getString("cur"), 
                                      "", 
                                      "", 
                                      hash, 
                                      this.block,
                                      block,
                                      0);
              
              // Rceives assets
              UTILS.BASIC.newTrans(this.target_adr, 
                                   rs_pos.getString("adr"),
                                   this.qty, 
                                   false,
                                   rs_pos.getString("asset"), 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block,
                                   block,
                                   0);
          }
          else
          {
              // Calculates price
              double price=rs_pos.getDouble("price")*this.qty;
              
              // Funds 
              if (UTILS.BASIC.getBalance(this.target_adr, rs_pos.getString("asset"), block)<this.qty)
                  return new CResult(false, "Insuficient funds to execute transaction", "CNewRegMarketOrderPayload.java", 74);
               
              // Sends assets
              UTILS.BASIC.newTransfer(this.target_adr, 
                                      rs_pos.getString("adr"),
                                      qty, 
                                      false,
                                      rs_pos.getString("asset"), 
                                      "", 
                                      "", 
                                      hash, 
                                      this.block,
                                      block,
                                      0);
              
              // Rceives assets
              UTILS.BASIC.newTrans(this.target_adr, 
                                   rs_pos.getString("adr"),
                                   price, 
                                   false,
                                   rs_pos.getString("cur"), 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block,
                                   block,
                                   0);
          }
           
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.orderID+
                                    String.valueOf(qty));
          
          if (!h.equals(this.hash))
             return new CResult(false, "Invalid hash", "CNewRegMarketOrderPayload.java", 74);
          
          // Close
          rs_pos.close(); 
          s.close();
          
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewRegMarketOrderPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CNewRegMarketOrderPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
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
          UTILS.BASIC.clearTrans(hash, "ID_ALL", this.block);
            
          
           // Update position
           UTILS.DB.executeUpdate("UPDATE assets_mkts_pos "
                                   + "SET qty=qty-"+this.qty+" "
                                 + "WHERE orderID='"+this.orderID+"'");
           
           // Statement
          Statement s=UTILS.DB.getStatement();
        
          // Load order data
          ResultSet rs_pos=s.executeQuery("SELECT * "
                                          + "FROM assets_mkts_pos "
                                         + "WHERE orderID='"+this.orderID+"'");
          
          // Next
          rs_pos.next();
          
           // Update market_price
           UTILS.DB.executeUpdate("UPDATE assets_mkts "
                                   + "SET last_price='"+(rs_pos.getDouble("price"))+"' "
                                 + "WHERE mktID='"+rs_pos.getLong("mktID")+"'");
            
           // Left qty
           if (rs_pos.getDouble("qty")<=0.00000001)
               UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                          + "WHERE orderID='"+this.orderID+"'");
          
           
           // Insert transaction
           String buyer="";
           String seller="";
           
           if (rs_pos.getString("tip").equals("ID_BUY"))
           {
              buyer=rs_pos.getString("adr");
              seller=this.target_adr;
           }
           else
           {
              seller=rs_pos.getString("adr");
              buyer=this.target_adr;
           }
           
           UTILS.DB.executeUpdate("INSERT INTO assets_mkts_trades(mktID,"
                                                                  + "orderID, "
                                                                  + "buyer, "
                                                                  + "seller, "
                                                                  + "qty, "
                                                                  + "price, "
                                                                  + "block) VALUES('"+
                                                                  rs_pos.getLong("mktID")+"', '"+
                                                                  orderID+"', '"+
                                                                  buyer+"', '"+
                                                                  seller+"', '"+
                                                                  this.qty+"', '"+
                                                                  rs_pos.getDouble("price")+"', '"+
                                                                  this.block+"')");
           
           // New market ask
           s=UTILS.DB.getStatement();
           s.executeUpdate("UPDATE assets_mkts "
                            + "SET ask=(SELECT MIN(price) "
                                       + "FROM assets_mkts_pos "
                                      + "WHERE mktID='"+rs_pos.getLong("mktID")+"' "
                                        + "AND tip='ID_SELL') "
                          + "WHERE mktID='"+rs_pos.getLong("mktID")+"'");
           
           // New market bid
           s=UTILS.DB.getStatement();
           s.executeUpdate("UPDATE assets_mkts "
                            + "SET bid=(SELECT MAX(price) "
                                       + "FROM assets_mkts_pos "
                                      + "WHERE mktID='"+rs_pos.getLong("mktID")+"' "
                                        + "AND tip='ID_BUY') "
                          + "WHERE mktID='"+rs_pos.getLong("mktID")+"'");
           
           // Close
           s.close();
           
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewRegMarketOrderPayload", 67); 
    }        
}
