// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.pegged_assets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewPeggedAssetOrderPayload extends CPayload
{
    // Market
    long mktID;
    
    // Tip
    String tip;
    
    // Qty
    double qty;
    
    public CNewPeggedAssetOrderPayload(String adr,
                                      long mktID, 
                                      String tip, 
                                      double qty) throws Exception
    {
        // Constructor
        super(adr);
        
        try
        {
           // Market
           this.mktID=mktID;
    
           // Tip
           this.tip=tip;
    
           // Qty
           this.qty=qty;
        
           // Hash
           this.hash=UTILS.BASIC.hash(this.getHash()+
                                      mktID+
                                      tip+
                                      String.valueOf(qty));
            
         }
         catch (Exception ex) 
       	 {  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CNewFeedMarketOrderPayload.java", 57);
         }
 
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
            // Statement
            Statement s=UTILS.DB.getStatement(); 
           
            // Market exist ?
            ResultSet mkt_rs=s.executeQuery("SELECT * "
                                            + "FROM feeds_assets_mkts "
                                           + "WHERE mktID='"+this.mktID+"'");
           
            // Data ?
            if (!UTILS.DB.hasData(mkt_rs))
              return new CResult(false, "Invalid market ID", "CNewFeedMarketOrderPayload.java", 74); 
           
            // Next 
            mkt_rs.next();
            
            // Curency
            String cur=mkt_rs.getString("cur");
            
            // Asset
            String asset=mkt_rs.getString("asset_symbol");
            
            // Market address
            String mkt_adr=mkt_rs.getString("adr");
           
            // Tip
            if (!tip.equals("ID_BUY") && !tip.equals("ID_SELL"))
                return new CResult(false, "Invalid operation", "CNewFeedMarketOrderPayload.java", 74); 
    
            // Qty
            if (this.qty<0.00000001)
              return new CResult(false, "Invalid qty", "CNewFeedMarketOrderPayload.java", 74); 
            
             // Price
             double price=(mkt_rs.getDouble("last_price")+mkt_rs.getDouble("spread"))*this.qty;
                
            // Funds ?
            if (tip.equals("ID_BUY"))
            {
                // Check balance
                if (UTILS.BASIC.getBalance(this.target_adr, cur, block)<price)
                    return new CResult(false, "Insufficient funds to execute this operation", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Enough assets
                if (qty>UTILS.BASIC.getBalance(mkt_adr, asset, block))
                    return new CResult(false, "Insufficient funds to execute this operation", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Transfer assets
                UTILS.BASIC.newTransfer(mkt_adr, 
                                        this.target_adr,
                                        this.qty, 
                                        false,
                                        asset, 
                                        "", 
                                        "", 
                                        hash, 
                                        this.block,
                                        block,
                                        this.qty*price);
                
                // Transfer currency
                UTILS.BASIC.newTransfer(this.target_adr, 
                                        mkt_adr,
                                        price, 
                                        false,
                                        cur, 
                                        "", 
                                        "", 
                                        hash, 
                                        this.block,
                                        block,
                                        0);
            }
            else
            {
                // Check asset balance
                if (UTILS.BASIC.getBalance(this.target_adr, asset, block)<qty)
                    return new CResult(false, "Insufficient funds to execute this operation", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Enough assets
                if (price>UTILS.BASIC.getBalance(mkt_adr, cur, block))
                    return new CResult(false, "Insufficient funds to execute this operation", "CNewFeedMarketOrderPayload.java", 74); 
                
                // Transfer asset
                UTILS.BASIC.newTransfer(this.target_adr, 
                                        mkt_adr,
                                        this.qty, 
                                        false,
                                        asset, 
                                        "", 
                                        "", 
                                        hash, 
                                        this.block,
                                        block,
                                        price*this.qty);
                
                // Transfer currency
                UTILS.BASIC.newTransfer(mkt_adr, 
                                        this.target_adr,
                                        price, 
                                        false,
                                        cur, 
                                        "", 
                                        "", 
                                        hash, 
                                        this.block,
                                        block,
                                        0);
            }
            
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
                                      this.mktID+
                                      this.tip+
                                      this.qty);
            
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
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement(); 
           
           // Market exist ?
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_assets_mkts "
                                      + "WHERE mktID='"+this.mktID+"'");
           
           // Data ?
           if (!UTILS.DB.hasData(rs))
              return new CResult(false, "Invalid market symbol", "CNewFeedMarketOrderPayload.java", 74); 
           
           // Next
           rs.next();
           
           // Price
           double price=0;
           
           // Price
           if (this.tip.equals("ID_BUY"))
              price=rs.getDouble("last_price")+rs.getDouble("spread");
           else
              price=rs.getDouble("last_price");
           
           // Commit transactions
           UTILS.BASIC.clearTrans(hash, "ID_ALL");
           
           // Insert transaction
           UTILS.DB.executeUpdate("INSERT INTO feeds_assets_mkts_trans(adr, "
                                                                    + "mktID, "
                                                                    + "tip, "
                                                                    + "qty, "
                                                                    + "price, "
                                                                    + "block) VALUES('"+
                                                                    this.target_adr+"', '"+
                                                                    this.mktID+"', '"+
                                                                    this.tip+"', '"+
                                                                    this.qty+"', '"+
                                                                    price+"', '"+
                                                                    this.block+"')");
           
           // Update invested
           if (this.tip.equals("ID_BUY"))
           UTILS.DB.executeUpdate("UPDATE assets_owners "
                                   + "SET invested=invested+"+(price*this.qty)
                                 +" WHERE owner='"+this.target_adr
                                  +"' AND symbol='"+rs.getString("asset_symbol")+"'");
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedMarketOrderPayload.java", 57);
        }
          
        
        
        // Return
	return new CResult(true, "Ok", "CNewFeedMarketOrderPayload", 67); 
    }  
}
    

