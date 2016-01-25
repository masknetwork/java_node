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
   
   public CUnfollowPayload(String adr, String unfollow_address)
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.unfollow_adr=unfollow_address;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.unfollow_adr);
 	   
 	   //Sign
 	   this.sign();
   }
   
   public CResult check(CBlockPayload block)
   {
       try
       {
             // Super class
   	     CResult res=super.check(block);
   	     if (res.passed==false) return res;
   	
   	     // Follow address valid
             if (!UTILS.BASIC.adressValid(this.unfollow_adr))
                return new CResult(false, "Invalid hash", "CFollowPayload", 157);
             
             // Statement
             Statement s=UTILS.DB.getStatement();
             
             // Address has tweets ?
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM tweets_follow "
                                        + "WHERE adr='"+this.target_adr+"' "
                                          + "AND follows='"+this.unfollow_adr+"'");
             
             if (!UTILS.DB.hasData(rs))
                return new CResult(false, "Invalid entry data", "CFollowPayload", 157);
             
             // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
 			               this.unfollow_adr);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CFollowPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CFollowPayload", 157);
            
            // Close
            s.close();
       
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CFollowPayload.java", 57);
        }
       
       
 	// Return
 	return new CResult(true, "Ok", "CFollowPayload", 164);
   }
   
   public CResult commit(CBlockPayload block)
   {
       try
       {
          CResult res=this.check(block);
          if (res.passed==false) return res;
	  
          // Superclass
          super.commit(block);
          
          // Unfollow
          UTILS.DB.executeUpdate("DELETE FROM tweets_follow "
                                     + "WHERE adr='"+this.target_adr+"' "
                                       + "AND follows='"+this.unfollow_adr+"'");
          
           // Increase followers
          UTILS.DB.executeUpdate("UPDATE adr "
                                  + "SET followers=followers-1 "
                                + "WHERE adr='"+this.unfollow_adr+"'");
          
          // Increase following
          UTILS.DB.executeUpdate("UPDATE adr "
                                  + "SET following=following-1 "
                                + "WHERE adr='"+this.target_adr+"'");
       }
       catch (Exception ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
       }
       
   	// Return 
   	return new CResult(true, "Ok", "CFollowPayload", 70);
    }
}
