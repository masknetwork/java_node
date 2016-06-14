package wallet.network.packets.shop.goods;

import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CUpdateOrderPayload extends CPayload
{
    // Order UID
    public String orderUID;

    // Status
    public String status;
    
    public CUpdateOrderPayload(String adr, 
                               String orderUID, 
                               String status,
                               String sign) throws Exception
    {
        // Constructor
        super(adr);
        
        // Order UID
        this.orderUID=orderUID;
        
        // Status
        this.status=status;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash()+
                                   this.orderUID+
                                   this.status);
        
        // Sign
        if (!sign.equals(""))
            this.sign=sign;
        else
            this.sign();
    }
    
     public CResult check(CBlockPayload block) throws Exception
     {
         // Statement
         Statement s=UTILS.DB.getStatement();
         
         // Load order details
         
         
         // Signer is authorized ?
         // Status
         // Hash
         
         // Return
         return new CResult(true, "Ok", "CNewEscrowerPayload", 77);
     }
     
      public CResult commit(CBlockPayload block) throws Exception
      {
         // Superclass
         super.commit(block);
         
         // Return 
   	 return new CResult(true, "Ok", "CNewEscrowerPayload.java", 149);
      }
        
}
