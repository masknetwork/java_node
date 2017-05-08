// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CFollowPayload extends CPayload 
{
   // Tweet ID
   String follow_adr;
   
   // Months
   long months;
   
   // Serial
   private static final long serialVersionUID = 1;
   
   public CFollowPayload(String adr, 
		         String follow_adr,
                         long months) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.follow_adr=follow_adr;
           
           // Months
           this.months=months;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.follow_adr+
                                 this.months);
 	   
 	   // Sign
           this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
       super.check(block);
       
       // Follow address same as target
       if (this.follow_adr.equals(this.target_adr))
           throw new Exception("Invalid follow address - CFollowPayload.java");
   	
       // Follow address valid
       if (!UTILS.BASIC.isAdr(this.follow_adr))
          throw new Exception("Invalid follow address - CFollowPayload.java");
       
       // Already follow ?
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM tweets_follow "
                                         + "WHERE adr='"+this.target_adr+"' "
                                           + "AND follows='"+this.follow_adr+"'");
       
       // Has data
       if (UTILS.DB.hasData(rs))
           throw new Exception("Already following - CFollowPayload.java");
       
       // Months
       if (this.months<1)
           throw new Exception("Address period - CFollowPayload.java");
             
       // Check Hash
       String h=UTILS.BASIC.hash(this.getHash()+
 			         this.follow_adr+
                                 this.months);
	  
       if (!h.equals(this.hash)) 
   	   throw new Exception("Invalid hash - CFollowPayload.java");
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
       UTILS.DB.executeUpdate("INSERT INTO tweets_follow "
                                    + "SET adr='"+this.target_adr+"', "
                                        + "follows='"+this.follow_adr+"', "
                                        + "block='"+this.block+"', "
                                        + "expire='"+(this.block+this.months*43200)+"'");
    }
}
