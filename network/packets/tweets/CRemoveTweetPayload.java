package wallet.network.packets.tweets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CRemoveTweetPayload extends CPayload 
{
   // Tweet ID
   long tweetID;
   
   public CRemoveTweetPayload(String adr, long tweetID)
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.tweetID=tweetID;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.tweetID);
 	   
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
   	
   	     // Statement
             Statement s=UTILS.DB.getStatement();
             
             // Address has tweets ?
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM tweets "
                                        + "WHERE adr='"+this.target_adr+"' "
                                          + "AND tweetID='"+this.tweetID+"'");
             
             if (!UTILS.DB.hasData(rs))
                return new CResult(false, "Invalid entry data", "CRemoveTweetPayload", 157);
             
             // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
 			               this.tweetID);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CRemoveTweetPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CRemoveTweetPayload", 157);
            
            // Close
            s.close();
       
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CRemoveTweetPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CRemoveTweetPayload.java", 57);
        }
       
       
 	// Return
 	return new CResult(true, "Ok", "CRemoveTweetPayload", 164);
   }
   
   public CResult commit(CBlockPayload block)
   {
       try
       {
          CResult res=this.check(block);
          if (res.passed==false) return res;
	  
          // Superclass
          super.commit(block);
       
          // Delete tweet
          UTILS.DB.executeUpdate("DELETE FROM tweets "
                                     + "WHERE tweetID='"+this.tweetID+"'");
          
          // Delete retweets
          UTILS.DB.executeUpdate("DELETE FROM tweets "
                                     + "WHERE retweet_tweet_ID='"+this.tweetID+"'");
          
          // Delete likes
          UTILS.DB.executeUpdate("DELETE FROM tweets_likes "
                                     + "WHERE tweetID='"+this.tweetID+"'");
          
          // Delete comments
          UTILS.DB.executeUpdate("DELETE FROM tweets_comments "
                                     + "WHERE tweetID='"+this.tweetID+"'");
          
          // Decrease tweets
          UTILS.DB.executeUpdate("UPDATE adr "
                                  + "SET tweets=tweets-1 "
                                + "WHERE adr='"+this.target_adr+"'");
          
       }
       catch (Exception ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CRemoveTweetPayload.java", 57);
       }
       
   	// Return 
   	return new CResult(true, "Ok", "CRemoveTweetPayload", 70);
    }
}
