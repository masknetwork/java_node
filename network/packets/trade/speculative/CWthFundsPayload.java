package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import wallet.network.packets.CPayload;
import wallet.kernel.UTILS;
import wallet.network.packets.blocks.CBlockPayload;

public class CWthFundsPayload extends CPayload
{
   // Market ID
   long mktID;
   
   // Amount
   double amount;
   
   // Recipient
   String rec;
    
   public CWthFundsPayload(String adr,
                           long mktID,
                           double amount,
                           String rec) throws Exception
   {
       // Constructor
       super(adr);
       
       // UID
       this.mktID=mktID;
           
       // Amount
       this.amount=amount;
       
       // Recipient
       this.rec=rec;
           
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.mktID+
                             this.amount+
                             this.rec);
       
       // Sign
       this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
       super.check(block);
       
       // Recipient
       if (!UTILS.BASIC.isAdr(this.rec))
          throw new Exception("Invalid recipient");
       
       // Amount
       if (this.amount<0.0001)
          throw new Exception("Invalid amount");
       
      // Check address
      ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                         + "FROM feeds_spec_mkts "
                                        + "WHERE mktID='"+this.mktID+"' "
                                          + "AND adr='"+this.target_adr+"'");
      
      // Has data
      if (!UTILS.DB.hasData(rs))
          throw new Exception("Invalid market ID");
      
      // Load market data
      rs.next();
      
      // Market balance
      double balance=UTILS.ACC.getBalance(rs.getString("adr"), rs.getString("cur"));
      
      // Max margin
      double max_margin=rs.getDouble("max_total_margin");
      
      // Currency
      String cur=rs.getString("cur");
      
      // Open positions
      rs=UTILS.DB.executeQuery("SELECT SUM(pl+margin) AS total "
                               + "FROM feeds_spec_mkts_pos "
                              + "WHERE mktID='"+this.mktID+"' "
                                + "AND status='ID_MARKET'");
      
       if (UTILS.DB.hasData(rs))
       {
           // Used margin
           double used_margin=0;
           
           // Next
           rs.next();
           
           // Used margin
           used_margin=rs.getDouble("total");
        
           // Used margin as percent
           used_margin=used_margin*100/(balance-amount);
        
           // Valid
           if (used_margin>25)
               throw new Exception("Invalid amount - CWthFundsPayload.java");
        }
       else
       {
          if (balance<this.amount)
               throw new Exception("Invalid amount - CWthFundsPayload.java");
       }
       
       // Withdraw
       UTILS.ACC.newTransfer(this.target_adr, 
                             this.rec,
                             this.amount,
                             cur, 
                             "Margin market withdraw", 
                             "", 
                             hash, 
                             this.block);
       
       // Hash
       String h=UTILS.BASIC.hash(this.getHash()+
                                 this.mktID+
                                 this.amount+
                                 this.rec);
       
       // Check hash
       if (!h.equals(this.hash))
           throw new Exception("Invalid hash - CWthFundsPayload.java");
   }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Clear trans
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
    }        
}