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


public class CUnfollowPayload extends CPayload 
{
   // Tweet ID
   String unfollow_adr;
   
   // Serial
   private static final long serialVersionUID = 100L;
   
   public CUnfollowPayload(String adr, 
                           String unfollow_address) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.unfollow_adr=unfollow_address;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.unfollow_adr);
 	   
 	   // Sign
 	   this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
        // Super class
        super.check(block);
   	
   	// Follow address valid
        if (!UTILS.BASIC.isAdr(this.unfollow_adr))
           throw new Exception("Invalid address - CUnfollowPayload.java"); 
            
        // Address is followed ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM tweets_follow "
                                          + "WHERE adr='"+this.target_adr+"' "
                                            + "AND follows='"+this.unfollow_adr+"'");
             
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid address - CUnfollowPayload.java"); 
             
        // Check Hash
	String h=UTILS.BASIC.hash(this.getHash()+
 	                          this.unfollow_adr);
	  
   	if (!h.equals(this.hash)) 
           throw new Exception("Invalid hash - CUnfollowPayload.java"); 
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
        // Superclass
        super.commit(block);
          
        // Unfollow
        UTILS.DB.executeUpdate("DELETE FROM tweets_follow "
                                     + "WHERE adr='"+this.target_adr+"' "
                                       + "AND follows='"+this.unfollow_adr+"'");
    }
}
