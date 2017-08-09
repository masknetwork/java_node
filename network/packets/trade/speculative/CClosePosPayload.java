// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
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
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
       super.check(block);
       
       // Percent
       if (percent<1 || percent>100)
           throw new Exception("Invalid percent - CClosePosPayload.java");
       
        // Load position data
        ResultSet pos_rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM feeds_spec_mkts_pos "
                                              + "WHERE posID='"+this.posID+"' "
                                                + "AND adr='"+this.target_adr+"' "
                                                + "AND status<>'ID_CLOSED'");
          
        // Valid ?
        if (!UTILS.DB.hasData(pos_rs))
               throw new Exception("Invalid order - CClosePosPayload.java");
          
        // Next
        pos_rs.next();
          
        // Load market data
        ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT * "
                                                 + "FROM feeds_spec_mkts "
                                                + "WHERE mktID='"+pos_rs.getLong("mktID")+"'");
          
        // Next
        mkt_rs.next();
          
        // To pay
        double pay=(pos_rs.getDouble("pl")+pos_rs.getDouble("margin"))*this.percent/100;
          
        // Market balance lower than payment owned ?
        double mkt_balance=UTILS.ACC.getBalance(this.target_adr, mkt_rs.getString("cur"), block);
          
        // Balance
        if (mkt_balance<pay) pay=mkt_balance;
          
        // Debit market address
        if (pay>0)
        UTILS.ACC.newTransfer(mkt_rs.getString("adr"), 
                              pos_rs.getString("adr"),
                              pay,
                              mkt_rs.getString("cur"), 
                              "Order "+this.posID+" has been closed ("+this.percent+"%) and the profit / losss payed", 
                              "", 
                              this.hash, 
                              this.block);
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Commit payment
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
         
        // Updates
        if (this.percent==100)
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET status='ID_CLOSED', "
                                    + "close_reason='ID_MANUAL', "
                                    + "closed_pl=closed_pl+pl,  "
                                    + "closed_margin=closed_margin+margin, "
                                    + "block_end='"+this.block+"' " 
                              + "WHERE posID='"+this.posID+"'");
        else
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET closed_pl=closed_pl+(pl*"+this.percent+"/100), "
                                     + "closed_margin=closed_margin+(margin*"+this.percent+"/100), "
                                     + "margin=margin-(margin*"+this.percent+"/100), "
                                     + "qty=qty-(qty*"+this.percent+"/100), "
                                     + "pl=pl-(pl*"+this.percent+"/100), "
                                     + "block_end='"+this.block+"' " 
                              + "WHERE posID='"+this.posID+"'");   
    }        
}
