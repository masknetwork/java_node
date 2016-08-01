// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.agents.CAgent;
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
   
   public void check(CBlockPayload block) throws Exception
    {
        // Statement
        
           
        // Load position details
        ResultSet rs_pos=UTILS.DB.executeQuery("SELECT amp.*, am.asset, am.cur "
                                           + "FROM assets_mkts_pos AS amp "
                                           + "JOIN assets_mkts AS am ON am.mktID=amp.mktID "
                                          + "WHERE amp.orderID='"+this.orderID+"'");
           
        // Has data ?
        if (!UTILS.DB.hasData(rs_pos))
           throw new Exception("Invalid position - CCloseRegMarketPosPayload.java");
           
        // Next
        rs_pos.next();
           
        // Asset symbol
        String asset_symbol=rs_pos.getString("asset");
          
        // Currency symbol
        String cur_symbol=rs_pos.getString("cur");
           
        // Ownership
        if (!this.target_adr.equals(rs_pos.getString("adr")))
           throw new Exception("Invalid owner - CCloseRegMarketPosPayload.java");
           
        if (rs_pos.getString("tip").equals("ID_SELL"))
        {
              // Insert coins
              UTILS.ACC.newTrans(this.target_adr, 
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
            UTILS.ACC.newTrans(this.target_adr, 
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
           throw new Exception("Invalid hash - CCloseRegMarketPosPayload.java");
          
        // Close
        
        
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Constructor
        super.commit(block);
      
        
        // Statement
        
        
        // Load market data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM assets_mkts_pos "
                                   + "WHERE orderID='"+this.orderID+"'");
        
        // Market ID
        long mktID=rs.getLong("orderID");
        
        // Close
        
        
        // Asset has contract attached ?
        long asset_aID=UTILS.BASIC.getMarketContract(mktID, "assets_mkts");
        
        // Has contract ?
        if (asset_aID>0)
        {
            // Load message
            CAgent AGENT=new CAgent(asset_aID, false, this.block);
                    
            // Set Message 
            AGENT.VM.SYS.EVENT.loadOpenAssetOrder(this.orderID,
                                                  rs.getString("tip"), 
                                                  rs.getDouble("qty"),
                                                  rs.getDouble("price"));
                    
            // Execute
            AGENT.execute("#close_asset_mkt_order#", false, this.block);
            
            // Aproved ?
            if (!AGENT.VM.SYS.EVENT.CLOSE_ORDER.APPROVED)
                throw new Exception("Rejected - CNewRegMarketPosPayload.java");
        }
       
        // Position type
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
           
        // Remove
        UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                     + "WHERE orderID='"+this.orderID+"'");         
       
    }        
}
