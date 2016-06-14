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

public class CNewRegMarketPosPayload extends CPayload
{
    // Market symbol
    long mktID;
    
    // Tip
    String tip;
                                   
    // Price
    double price;
                                   
    // Qty
    double qty;
                                   
    // Market days
    long days;
    
    // Order ID
    long orderID;
    
    // Asset trans
    CTransPayload asset_trans;
    
    // Currency transaction
    CTransPayload cur_trans;
                                           
    public CNewRegMarketPosPayload(String adr,
                                   long mktID,
                                   String tip,
                                   double price,
                                   double qty,
                                   long mkt_days) throws Exception
    {
        // Constructor
        super(adr);
        
        // Market symbol
        this.mktID=mktID;
    
        // Tip
        this.tip=tip;
                                   
        // Price
        this.price=price;
                                   
        // Qty
        this.qty=qty;
                                   
        // Market days
        this.days=mkt_days;
        
        // Order ID
        this.orderID=UTILS.BASIC.getID();
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.mktID+
                              this.tip+
                              String.valueOf(this.price)+
                              String.valueOf(this.qty)+
                              String.valueOf(this.days)+
                              this.orderID);
        
        // Sign
        this.sign();
    }
    
     public CResult check(CBlockPayload block) throws Exception
     {
         try
         {    
            // Market symbol
            Statement s=UTILS.DB.getStatement();
            
            // Check order ID
            ResultSet rs=s.executeQuery("SELECT * "
                              + "FROM assets_mkts_pos "
                             + "WHERE orderID='"+this.orderID+"'");
            
            if (UTILS.DB.hasData(rs))
                return new CResult(false, "Invalid order ID", "CNewRegMarketPosPayload.java", 74);
            
            rs=s.executeQuery("SELECT * "
                                        + "FROM assets_mkts "
                                       + "WHERE mktID='"+this.mktID+"'");
            
            if (!UTILS.DB.hasData(rs))
               return new CResult(false, "Invalid market symbol", "CNewRegMarketPosPayload.java", 74);
            
            // Next
            rs.next();
            
            // Tip
            if (!this.tip.equals("ID_BUY") && 
                !this.tip.equals("ID_SELL"))
                return new CResult(false, "Invalid op type", "CNewRegMarketPosPayload.java", 74);
                                   
            // Price
            if (this.price<0.00000001)
               return new CResult(false, "Invalid price", "CNewRegMarketPosPayload.java", 74);
        
            // Qty
            if (this.qty<0.00000001)
               return new CResult(false, "Invalid qty", "CNewRegMarketPosPayload.java", 74);
        
            // Market days
            if (this.days<1)
               return new CResult(false, "Invalid days", "CNewRegMarketPosPayload.java", 74);
        
            // Asset balance
            double balance_asset=UTILS.BASIC.getBalance(this.target_adr, rs.getString("asset"), block);
                    
            // Currency balance
            double balance_cur=UTILS.BASIC.getBalance(this.target_adr, rs.getString("cur"), block);
            
            // Sell
            if (this.tip.equals("ID_SELL"))
            {
                // Asset balance
                if (balance_asset<this.qty)
                     return new CResult(false, "Innsufficient assets", "CNewRegMarketPosPayload.java", 74);
                
                // Insert transaction
                UTILS.BASIC.newTrans(this.target_adr, 
                                     "none",
                                     -this.qty,
                                     true,
                                     rs.getString("asset"), 
                                     "New short position on market "+rs.getString("mktID"), 
                                     "", 
                                     this.hash, 
                                     this.block,
                                     block,
                                     0);
            }
            else
            {
                if (balance_cur<(this.qty*this.price))
                     return new CResult(false, "Innsufficient funds", "CNewRegMarketPosPayload.java", 74);
                
                // Insert transaction
                UTILS.BASIC.newTrans(this.target_adr, 
                                     "none",
                                     -(this.qty*this.price),
                                     true,
                                     rs.getString("cur"), 
                                     "New long position on market "+rs.getString("mktID"), 
                                     "", 
                                     this.hash, 
                                     this.block,
                                     block,
                                     0);
            }
            
             // Hash
             String h=UTILS.BASIC.hash(this.getHash()+
                                       this.mktID+
                                       this.tip+
                                       String.valueOf(this.price)+
                                       String.valueOf(this.qty)+
                                       String.valueOf(this.days)+
                                       this.orderID);
             
             // Valid hash
             if (!this.hash.equals(h))
                 return new CResult(false, "Invalid hash", "CNewRegMarketPosPayload.java", 74);
         }
         catch (SQLException ex) 
       	 {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
         }
         
         // Return
	 return new CResult(true, "Ok", "CNewRegMarketPosPayload.java", 67);
     }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.commit(block);
        if (!res.passed) return res;

        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        try
        {
            Statement s=UTILS.DB.getStatement();
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM assets_mkts "
                                       + "WHERE mktID='"+this.mktID+"'");
            
            // Next
            rs.next();
            
            // Asset symbol
            String asset_symbol=rs.getString("asset");
            
            // Currency symbol
            String cur_symbol=rs.getString("cur");
            
            // Clear
            UTILS.BASIC.clearTrans(hash, "ID_ALL", this.block);
            
            // Execute transactions
            if (this.tip.equals("ID_SELL"))
            {
               // Lowest price ?
               rs=s.executeQuery("SELECT * "
                                 + "FROM assets_mkts_pos "
                                + "WHERE mktID='"+this.mktID+"' "
                                  + "AND tip='ID_SELL' "
                                  + "AND price<"+this.price);
               
               if (!UTILS.DB.hasData(rs))
                  UTILS.DB.executeUpdate("UPDATE assets_mkts "
                                          + "SET ask='"+this.price+"' "
                                        + "WHERE mktID='"+this.mktID+"'");
            }
            else
            {
                // Highest price ?
                 rs=s.executeQuery("SELECT * "
                                 + "FROM assets_mkts_pos "
                                + "WHERE mktID='"+this.mktID+"' "
                                  + "AND tip='ID_BUY' "
                                  + "AND price>"+this.price);
               
               if (!UTILS.DB.hasData(rs))
                  UTILS.DB.executeUpdate("UPDATE assets_mkts "
                                          + "SET bid='"+this.price+"' "
                                        + "WHERE mktID='"+this.mktID+"'");
            }
            
            // Insert position
            UTILS.DB.executeUpdate("INSERT INTO assets_mkts_pos(adr,"
                                                                + "mktID, "
                                                                + "tip, "
                                                                + "qty, "
                                                                + "price, "
                                                                + "orderID, "
                                                                + "expire, "
                                                                + "block) VALUES('"+
                                                                this.target_adr+"', '"+
                                                                this.mktID+"', '"+
                                                                this.tip+"', '"+
                                                                this.qty+"', '"+
                                                                UTILS.FORMAT.format(this.price)+"', '"+
                                                                this.orderID+"', '"+
                                                                UTILS.BASIC.getExpireBlock(this.days)+
                                                                "', '"+this.block+"')");
            
            
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CNewRegMarketPosPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       		UTILS.LOG.log("Exception", ex.getMessage(), "CNewRegMarketPosPayload.java", 57);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewRegMarketPosPayload.java", 67); 
    }      
}
