package wallet.network.packets.markets.assets.regular;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CCloseRegMarketPosPayload  extends CPayload
{
    /// UID
    String uid;
    
    
    public CCloseRegMarketPosPayload(String adr, String uid)
    {
        // Constructor
        super(adr);
        
        // UID
        this.uid=uid;
          
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.uid);
          
        // Sign
        this.sign();
    }
   
   public CResult check(CBlockPayload block)
    {
        try
        {
           // Order uid
           if (uid.length()!=10)
               return new CResult(false, "Invalid order UID", "CCloseRegMarketPosPayload.java", 74);
           
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           // Load position details
           ResultSet rs_pos=s.executeQuery("SELECT * "
                                           + "FROM assets_markets_pos "
                                          + "WHERE uid='"+this.uid+"'");
           
           // Has data ?
           if (!UTILS.DB.hasData(rs_pos))
              return new CResult(false, "Invalid position", "CCloseRegMarketPosPayload.java", 74);
           
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
           
           // Market
           String pos_mkt=rs_pos.getString("mkt_symbol");
           
           // Load market data
          ResultSet rs_mkt=s.executeQuery("SELECT * "
                                          + "FROM assets_markets "
                                         + "WHERE mkt_symbol='"+pos_mkt+"'");
          rs_mkt.next();
          
          // Asset symbol
          String asset_symbol=rs_mkt.getString("asset_symbol");
          
          // Currency symbol
          String cur_symbol=rs_mkt.getString("cur_symbol");
           
           // Ownership
           if (!this.target_adr.equals(pos_adr))
               return new CResult(false, "Invalid owner", "CCloseRegMarketPosPayload.java", 74);
           
           if (pos_type.equals("ID_SELL"))
           {
              // Insert coins
              UTILS.BASIC.newTrans(this.target_adr, 
                                   "none",
                                   pos_qty, 
                                   false,
                                   asset_symbol, 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
          }
          else
          {
              // Insert assets
              UTILS.BASIC.newTrans(this.target_adr, 
                                   "none",
                                   pos_qty*pos_price, 
                                   false,
                                   cur_symbol, 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block);
          }
           
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.uid);
          
          if (!h.equals(this.hash))
             return new CResult(false, "Invalid hash", "CCloseRegMarketPosPayload.java", 74);
          
          // Close
          s.close();
          
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CCloseRegMarketPosPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CCloseRegMarketPosPayload", 67);
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
          // Statement
          Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
          // Position type
          UTILS.BASIC.clearTrans(hash, "ID_ALL");
           
          // Remove
          UTILS.DB.executeUpdate("DELETE FROM assets_markets_pos "
                                          + "WHERE uid='"+this.uid+"'");         
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CCloseRegMarketPosPayload.java", 57);
        }
        
        // Return
	return new CResult(true, "Ok", "CCloseRegMarketPosPayload", 67); 
    }        
}
