// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CClosePosPayload extends CPayload
{
   // Pos uid
   long posID;
   
   // Percent
   long percent;
   
   public CClosePosPayload(String adr, 
                           long posID, 
                           long percent) throws Exception
   {
       // Constructor
       super(adr);
       
       // UID
       this.posID=posID;
       
       // Percent
       this.percent=percent;
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.posID+
                             this.percent);
       
       // Sign
       this.sign();
   }
   
   public CResult check(CBlockPayload block) throws Exception
    {
        try
       {
          // Statement
          Statement s=UTILS.DB.getStatement();
          
          // Load position data
          ResultSet pos_rs=s.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts_pos "
                                         + "WHERE posID='"+this.posID+"' "
                                           + "AND adr='"+this.target_adr+"' "
                                           + "AND status<>'ID_CLOSED'");
          
          // Valid ?
          if (!UTILS.DB.hasData(pos_rs))
               return new CResult(false, "Invalid order", "CClosePosPayload", 42);
          
          // Next
          pos_rs.next();
          
          // Signer valid
          if (!pos_rs.getString("adr").equals(this.target_adr))
              return new CResult(false, "Invalid signer", "CClosePosPayload", 42);
          
          // New statement
          Statement s1=UTILS.DB.getStatement();
          
          // Load market data
          ResultSet mkt_rs=s1.executeQuery("SELECT * "
                                           + "FROM feeds_spec_mkts "
                                          + "WHERE mktID='"+pos_rs.getLong("mktID")+"'");
          
          // Next
          mkt_rs.next();
          
          // To pay
          double pay=(pos_rs.getDouble("pl")+pos_rs.getDouble("margin"))*this.percent/100;
          
          // Market balance lower than payment owned ?
          double mkt_balance=0;
          if (block==null) 
              mkt_balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, mkt_rs.getString("cur"));
          else
               mkt_balance=UTILS.BASIC.getBalance(this.target_adr, mkt_rs.getString("cur"));
          
          // Balance
          if (mkt_balance<pay) pay=mkt_balance;
          
          // Debit market address
          UTILS.BASIC.newTrans(mkt_rs.getString("adr"), 
                               pos_rs.getString("adr"),
                               -pay,
                               true,
                               mkt_rs.getString("cur"), 
                               "Order "+this.posID+" has been closed ("+this.percent+"%) and the profit / losss payed", 
                               "", 
                               this.hash, 
                               this.block,
                               block,
                               0);
         
          // Credit owner address
          UTILS.BASIC.newTrans(pos_rs.getString("adr"),
                               mkt_rs.getString("adr"), 
                               pay,
                               true,
                               mkt_rs.getString("cur"), 
                               "Order "+this.posID+" has been closed ("+this.percent+"%) and the profit / losss payed", 
                               "", 
                               this.hash, 
                               this.block,
                               block,
                               0);
          
           // Close
           pos_rs.close(); 
           mkt_rs.close();
           s.close();
           s1.close();
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CClosePosPayload.java", 57);
        }
         // Return
	 return new CResult(true, "Ok", "CClosePosPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Commit payment
        UTILS.BASIC.clearTrans(hash, "ID_ALL", this.block);
         
        // Updates
        if (this.percent==100)
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET status='ID_CLOSED', "
                                    + "close_reason='ID_MANUAL', "
                                    + "closed_pl=closed_pl+pl,  "
                                    + "closed_margin=closed_margin+margin " 
                              + "WHERE posID='"+this.posID+"'");
        else
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET closed_pl=closed_pl+(pl*"+this.percent+"/100), "
                                     + "closed_margin=closed_margin+(margin*"+this.percent+"/100), "
                                     + "margin=margin-(margin*"+this.percent+"/100), "
                                     + "qty=qty-(qty*"+this.percent+"/100), "
                                     + "pl=pl-(pl*"+this.percent+"/100) "
                              + "WHERE posID='"+this.posID+"'");   
        
       
        // Return
	return new CResult(true, "Ok", "CClosePosPayload", 67); 
    }        
}
