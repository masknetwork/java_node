// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import wallet.kernel.*;



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
       // Message
       this.mes=mes;
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.key+this.mes);
   }
   
   public void getMessage(String adr, String trans_hash) throws Exception
   {
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
