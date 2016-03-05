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

public class CCloseRegMarketPosPayload  extends CPayload
{
    /// UID
    long orderID;
    
    
    public CCloseRegMarketPosPayload(String adr, long orderID) throws Exception
    {
        // Constructor
        super(adr);
        
        // UID
        this.orderID=orderID;
          
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.orderID);
          
        // Sign
        this.sign();
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
                                          + "WHERE amp.orderID='"+this.orderID+"'");
           
           // Has data ?
           if (!UTILS.DB.hasData(rs_pos))
              return new CResult(false, "Invalid position", "CCloseRegMarketPosPayload.java", 74);
           
           // Next
           rs_pos.next();
           
           // Asset symbol
           String asset_symbol=rs_pos.getString("asset");
          
          // Currency symbol
           String cur_symbol=rs_pos.getString("cur");
           
           // Ownership
           if (!this.target_adr.equals(rs_pos.getString("adr")))
               return new CResult(false, "Invalid owner", "CCloseRegMarketPosPayload.java", 74);
           
           if (rs_pos.getString("tip").equals("ID_SELL"))
           {
              // Insert coins
              UTILS.BASIC.newTrans(this.target_adr, 
                                   "none",
                                   rs_pos.getDouble("qty"), 
                                   false,
                                   asset_symbol, 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block,
                                   block,
                                   0);
          }
          else
          {
              // Insert assets
              UTILS.BASIC.newTrans(this.target_adr, 
                                   "none",
                                   rs_pos.getDouble("qty")*rs_pos.getDouble("price"),
                                   false,
                                   cur_symbol, 
                                   "", 
                                   "", 
                                   hash, 
                                   this.block,
                                   block,
                                   0);
          }
           
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.orderID);
          
          if (!h.equals(this.hash))
             return new CResult(false, "Invalid hash", "CCloseRegMarketPosPayload.java", 74);
          
          // Close
          rs_pos.close(); s.close();
          
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CCloseRegMarketPosPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CCloseRegMarketPosPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Constructor
        CResult res=super.commit(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Position type
          UTILS.BASIC.clearTrans(hash, "ID_ALL");
           
          // Remove
          UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                     + "WHERE orderID='"+this.orderID+"'");         
       
        
        // Return
	return new CResult(true, "Ok", "CCloseRegMarketPosPayload", 67); 
    }        
}
