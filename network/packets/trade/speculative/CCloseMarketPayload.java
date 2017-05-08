package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import wallet.network.packets.CPayload;
import wallet.kernel.UTILS;
import wallet.network.packets.blocks.CBlockPayload;

public class CCloseMarketPayload extends CPayload
{
    // Market ID
   public long mktID;
   
   public CCloseMarketPayload(String adr,
                              long mktID) throws Exception
   {
       // Constructor
       super(adr);
       
       // UID
       this.mktID=mktID;
           
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.mktID);
       
       // Sign
       this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
       super.check(block);
          
      // Check address
      ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT * "
                                         + "FROM feeds_spec_mkts "
                                        + "WHERE mktID='"+this.mktID+"' "
                                          + "AND adr='"+this.target_adr+"'");
      
      // Has data
      if (!UTILS.DB.hasData(mkt_rs))
          throw new Exception("Invalid market ID");
      
      // Next
      mkt_rs.next();
      
      // Balance
      double mkt_balance=UTILS.ACC.getBalance(mkt_rs.getString("adr"), mkt_rs.getString("cur"));
      
      // Open positions
      ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                               + "FROM feeds_spec_mkts_pos "
                              + "WHERE mktID='"+this.mktID+"' "
                                + "AND (status='ID_MARKET' OR "
                                      + "status='ID_PENDING')");
      
       if (UTILS.DB.hasData(rs))
       {
           // Total paid    
           double total=0;
         
           while (rs.next())
           {
              // Amount to pay
              double amount=rs.getDouble("pl")+rs.getDouble("margin");
              
              // Add tot total
              total=total+amount;
              
              // Pay
              if (amount>0 && total<mkt_balance)
              UTILS.ACC.newTransfer(this.target_adr, 
                                    rs.getString("adr"),
                                    amount,
                                    true,
                                    mkt_rs.getString("cur"), 
                                    "Market has been closed by owner", 
                                    "", 
                                    hash, 
                                    this.block, 
                                    block, 
                                    0);
         }
       }
       
      // Hash
       String h=UTILS.BASIC.hash(this.getHash()+
                                 this.mktID);
       
       // Check hash
       if (h.equals(this.hash))
           throw new Exception("Invalid hash - CCloseMarketPayload.java");
   }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Clear trans
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
        
        // Removes market
        UTILS.DB.executeUpdate("DELETE FROM feeds_spec_mkts "
                                   + "WHERE mktID='"+this.mktID+"'");
        
        // Removes positions
        UTILS.DB.executeUpdate("DELETE FROM feeds_spec_mkts_pos "
                                   + "WHERE mktID='"+this.mktID+"'");
    }        
}