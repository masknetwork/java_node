// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import wallet.kernel.*;
import wallet.network.CResult;



public class CMesDetails implements java.io.Serializable
{
   // Message
   public String mes;
   
   // Key
   String key;
   
   // HAsh
   String hash;
   
   // Serial
   private static final long serialVersionUID = 100L;
   
   public CMesDetails(String mes, String adr) throws Exception
   {
       // Has contract attached ?
       if (!UTILS.BASIC.isContractAdr(adr))
       {
           // Secure random
          String k=UTILS.BASIC.randString(25);
          
          // Encrypt message
          this.mes=UTILS.AES.encrypt(mes, k);
       
          // Encrypt key
          CECC enc=new CECC(adr);
          this.key=enc.encrypt(k);
       }
       else this.mes=mes;
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.key+this.mes);
   }
   
   public void getMessage(String adr, String trans_hash) throws Exception
   {
       // Has contract attached ?
       if (!UTILS.BASIC.isContractAdr(adr))
       {
          // Load address
          CAddress a=UTILS.WALLET.getAddress(adr);
       
          // Decrypt key
          String k=a.decrypt(this.key);
       
          // Decrypt message
          this.mes=UTILS.AES.decrypt(mes, k);
       }
       
       // Insert trans details
       UTILS.DB.executeUpdate("UPDATE my_trans "
                               + "SET mes='"+UTILS.BASIC.base64_encode(this.mes)+"' "
                             + "WHERE hash='"+trans_hash+"'");       
   }
   
   public boolean check() throws Exception
   {
       if (this.mes.length()>2500)
           return false;
       else
           return true;
   }
}
