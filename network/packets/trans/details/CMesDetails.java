package wallet.network.packets.trans.details;

import wallet.kernel.*;
import wallet.network.CResult;


public class CMesDetails extends CPayDetails 
{
   // Message
   public String mes;
   
   public CMesDetails(String mes, String adr)
   {
       // Secure random
       String k=UTILS.BASIC.randString(25);
          
       // Encrypt message
       this.mes=UTILS.AES.encrypt(mes, k);
       
       // Encrypt key
       CECC enc=new CECC(adr);
       this.key=enc.encrypt(k);
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.key+this.mes);
   }
   
   public void getMessage(String adr, String trans_hash)
   {
       // Load address
       CAddress a=UTILS.WALLET.getAddress(adr);
       
       // Decrypt key
       String k=a.decrypt(this.key);
       
       // Decrypt message
       this.mes=UTILS.AES.decrypt(mes, k);
       
       // Insert trans details
       UTILS.DB.executeUpdate("UPDATE my_trans "
                               + "SET mes='"+UTILS.BASIC.base64_encode(this.mes)+"' "
                             + "WHERE hash='"+trans_hash+"'");
       
   }
   
   public CResult check()
   {
       return new CResult(true, "Ok", "CTransPayload", 164);
   }
}
