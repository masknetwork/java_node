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
		         String follow_adr)
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.follow_adr=follow_adr;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.follow_adr);
 	   
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
             if (!UTILS.BASIC.adressValid(this.follow_adr))
                return new CResult(false, "Invalid hash", "CFollowPayload", 157);
             
             // Statement
             Statement s=UTILS.DB.getStatement();
             
             // Address has tweets ?
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM tweets "
                                        + "WHERE adr='"+this.follow_adr+"'");
             
             if (!UTILS.DB.hasData(rs))
                return new CResult(false, "Invalid follow address", "CFollowPayload", 157);
             
             // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
 			               this.follow_adr);
	  
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
          
          // Increase followers
          UTILS.DB.executeUpdate("UPDATE adr "
                                  + "SET followers=followers+1 "
                                + "WHERE adr='"+this.follow_adr+"'");
          
          // Increase following
          UTILS.DB.executeUpdate("UPDATE adr "
                                  + "SET following=following+1 "
                                + "WHERE adr='"+this.target_adr+"'");
       }
       catch (SQLException ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
       }
       catch (Exception ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
       }
       
   	// Return 
   	return new CResult(true, "Ok", "CFollowPayload", 70);
    }
}
