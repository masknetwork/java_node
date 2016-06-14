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
   
   public CFollowPayload(String adr, 
		         String follow_adr,
                         String sig) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.follow_adr=follow_adr;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.follow_adr);
 	   
 	   // Sign
           this.sign(sig);
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
       // Super class
       CResult res=super.check(block);
       if (res.passed==false) return res;
   	
       // Follow address valid
       if (!UTILS.BASIC.adressValid(this.follow_adr))
          throw new Exception("Invalid follow address - CFollowPayload.java");
             
       // Statement
       Statement s=UTILS.DB.getStatement();
             
       // Address has tweets ?
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM tweets "
                                  + "WHERE adr='"+this.follow_adr+"'");
             
       if (!UTILS.DB.hasData(rs))
          throw new Exception("Address has no twits - CFollowPayload.java");
             
       // Check Hash
       String h=UTILS.BASIC.hash(this.getHash()+
 			         this.follow_adr);
	  
       if (!h.equals(this.hash)) 
   	  return new CResult(false, "Invalid hash", "CFollowPayload", 157);
   	    
       // Close
       s.close();
      
       // Return
       return new CResult(true, "Ok", "CFollowPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       // Superclass
       super.commit(block);
       
       // Statement
       Statement s=UTILS.DB.getStatement();
          
       // Already following
       ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM tweets_follow "
                                     + "WHERE adr='"+this.target_adr+"' "
                                       + "AND follows='"+this.follow_adr+"'");
          
       if (UTILS.DB.hasData(rs))
       UTILS.DB.executeUpdate("UPDATE tweets_follow "
                                  + "SET block='"+this.block+"' "
                                + "WHERE adr='"+this.target_adr+"' "
                                  + "AND follows='"+this.follow_adr+"'");    
       else
       UTILS.DB.executeUpdate("INSERT INTO tweets_follow (adr, "
                                                          + "follows, "
                                                          + "block) VALUES('"
                                                          +this.target_adr+"', '"
                                                          +this.follow_adr+"', '"
                                                          +this.block+"')");
    
          
       // Close
       s.close();
       
       // Return 
       return new CResult(true, "Ok", "CFollowPayload", 70);
    }
}
