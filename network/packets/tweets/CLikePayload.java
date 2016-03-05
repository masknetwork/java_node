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


public class CLikePayload extends CPayload 
{
   // Tweet ID
   long tweetID;
   	
   public CLikePayload(String adr, 
		       long tweetID) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Target wall
           this.tweetID=tweetID;
           
           // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.tweetID);
 	   
 	   //Sign
 	   this.sign();
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
       try
       {
             // Super class
   	     CResult res=super.check(block);
   	     if (res.passed==false) return res;
   	
             // Statement
             Statement s=UTILS.DB.getStatement();
             
   	     // Load tweet data
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM tweets "
                                        + "WHERE tweetID='"+this.tweetID+"'");
            
             // Like tweet exist ?
             if (!UTILS.DB.hasData(rs))
                return new CResult(false, "Invalid tweet.", "CTweetMesStatusPayload", 77);
            
            // Already liked ?
            rs=s.executeQuery("SELECT * "
                                      + "FROM tweets_likes "
                                     + "WHERE tweetID='"+this.tweetID+"' "
                                       + "AND adr='"+this.target_adr+"'");
            
             if (UTILS.DB.hasData(rs))
                 return new CResult(false, "Already liked", "CTweetMesStatusPayload", 77);
                 
             // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
                                       this.tweetID);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CTweetMesStatusPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CTweetMesStatusPayload", 157);
       
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesStatusPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CTweetMesStatusPayload.java", 57);
        }
       
 	// Return
 	return new CResult(true, "Ok", "CTweetMesStatusPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       try
       {
          CResult res=this.check(block);
          if (res.passed==false) return res;
	  
          // Superclass
          super.commit(block);
       
          // Like
          UTILS.DB.executeUpdate("INSERT INTO tweets_likes "
                                       + "SET adr='"+this.target_adr+"', "
                                           + "tweetID='"+this.tweetID+"', "
                                           + "block='"+this.block+"'");
          
          // Update likes
          UTILS.DB.executeUpdate("UPDATE tweets "
                                  + "SET likes=likes+1 "
                                + "WHERE tweetID='"+this.tweetID+"'");
       }  
       catch (Exception ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesStatusPayload.java", 57);
       }
       
   	// Return 
   	return new CResult(true, "Ok", "CTweetMesStatusPayload", 70);
    }
}
