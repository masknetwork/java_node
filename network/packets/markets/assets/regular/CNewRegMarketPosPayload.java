package wallet.network.packets.markets.assets.regular;

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
    String mkt_symbol;
    
    // Tip
    String tip;
                                   
    // Price
    double price;
                                   
    // Qty
    double qty;
                                   
    // Market days
    long mkt_days;
    
    // Order ID
    String uid;
    
    // Asset trans
    CTransPayload asset_trans;
    
    // Currency transaction
    CTransPayload cur_trans;
                                           
    public CNewRegMarketPosPayload(String adr,
                                   String mkt_symbol,
                                   String tip,
                                   double price,
                                   double qty,
                                   long mkt_days)
    {
        // Constructor
        super(adr);
        
        // Market symbol
        this.mkt_symbol=mkt_symbol;
    
        // Tip
        this.tip=tip;
                                   
        // Price
        this.price=price;
                                   
        // Qty
        this.qty=qty;
                                   
        // Market days
        this.mkt_days=mkt_days;
        
        // Order ID
        this.uid=UTILS.BASIC.randString(10);
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.mkt_symbol+
                              this.tip+
                              String.valueOf(this.price)+
                              String.valueOf(this.qty)+
                              String.valueOf(this.mkt_days)+
                              this.uid);
        
        // Sign
        this.sign();
    }
    
     public CResult check(CBlockPayload block)
     {
         try
         {    
            // Market symbol
            if (!UTILS.BASIC.symbolValid(mkt_symbol))
               return new CResult(false, "Invalid symbol", "CNewRegMarketPosPayload.java", 74);
        
            ResultSet rs=null;
            
            // Market symbol
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                                     ResultSet.CONCUR_READ_ONLY);
            
            rs=s.executeQuery("SELECT * "
                              + "FROM assets_markets "
                             + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
            
            if (!UTILS.DB.hasData(rs))
               return new CResult(false, "Invalid market symbol", "CNewRegMarketPayload.java", 74);
            
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
            if (this.mkt_days<1)
               return new CResult(false, "Invalid days", "CNewRegMarketPosPayload.java", 74);
        
            // Asset balance
            double balance_asset=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, 
                                                                     rs.getString("asset_symbol"));
            
            // Currency balance
            double balance_cur=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, 
                                                                   rs.getString("cur_symbol"));
            
            // Sell
            if (this.tip.equals("ID_SELL"))
            {
                // Asset balance
                if (balance_asset<this.qty)
                     return new CResult(false, "Innsufficient assets", "CNewRegMarketPosPayload.java", 74);
                
                // Insert transaction
                UTILS.BASIC.newTrans(this.target_adr, 
                                     -this.qty,
                                     true,
                                     rs.getString("asset_symbol"), 
                                     "New short position on market "+rs.getString("mkt_symbol"), 
                                     "", 
                                     this.hash, 
                                     this.block);
            }
            else
            {
                if (balance_cur<(this.qty*this.price))
                     return new CResult(false, "Innsufficient funds", "CNewRegMarketPosPayload.java", 74);
                
                // Insert transaction
                UTILS.BASIC.newTrans(this.target_adr, 
                                     -(this.qty*this.price),
                                     true,
                                     rs.getString("cur_symbol"), 
                                     "New long position on market "+rs.getString("mkt_symbol"), 
                                     "", 
                                     this.hash, 
                                     this.block);
            }
            
             // Hash
             String h=UTILS.BASIC.hash(this.getHash()+
                                       this.mkt_symbol+
                                       this.tip+
                                       String.valueOf(this.price)+
                                       String.valueOf(this.qty)+
                                       String.valueOf(this.mkt_days)+
                                       this.uid);
             
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
    
    public CResult commit(CBlockPayload block)
    {
        // Super
        CResult res=super.commit(block);
        if (!res.passed) return res;

        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        try
        {
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM assets_markets "
                                       + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
            
            // Next
            rs.next();
            
            // Asset symbol
            String asset_symbol=rs.getString("asset_symbol");
            
            // Currency symbol
            String cur_symbol=rs.getString("asset_symbol");
            
            // Clear
            UTILS.BASIC.clearTrans(hash, "ID_ALL");
            
            // Execute transactions
            if (this.tip.equals("ID_SELL"))
            {
               // Lowest price ?
               rs=s.executeQuery("SELECT * "
                                 + "FROM assets_markets_pos "
                                + "WHERE mkt_symbol='"+this.mkt_symbol+"' "
                                  + "AND tip='ID_SELL' "
                                  + "AND price<"+this.price);
               
               if (!UTILS.DB.hasData(rs))
                  UTILS.DB.executeUpdate("UPDATE assets_markets "
                                          + "SET ask='"+this.price+"' "
                                        + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
            }
            else
            {
                // Highest price ?
                 rs=s.executeQuery("SELECT * "
                                 + "FROM assets_markets_pos "
                                + "WHERE mkt_symbol='"+this.mkt_symbol+"' "
                                  + "AND tip='ID_BUY' "
                                  + "AND price>"+this.price);
               
               if (!UTILS.DB.hasData(rs))
                  UTILS.DB.executeUpdate("UPDATE assets_markets "
                                          + "SET bid='"+this.price+"' "
                                        + "WHERE mkt_symbol='"+this.mkt_symbol+"'");
            }
            
            // Insert position
            String uid=UTILS.BASIC.randString(10);
            UTILS.DB.executeUpdate("INSERT INTO assets_markets_pos(adr,"
                                                                + "mkt_symbol, "
                                                                + "tip, "
                                                                + "qty, "
                                                                + "price, "
                                                                + "uid, "
                                                                + "expires, "
                                                                + "block) VALUES('"+
                                                                this.target_adr+"', '"+
                                                                this.mkt_symbol+"', '"+
                                                                this.tip+"', '"+
                                                                this.qty+"', '"+
                                                                UTILS.FORMAT.format(this.price)+"', '"+
                                                                this.uid+"', '"+
                                                                (this.block+this.mkt_days*864)+
                                                                "', '"+this.block+"')");
            
            // Assets
            UTILS.ROWHASH.updateLastID("assets_markets_pos");
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewRegMarketPosPayload.java", 67); 
    }      
}
