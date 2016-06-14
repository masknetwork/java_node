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


public class CNewTweetPayload extends CPayload 
{
   // Message
   String mes;
   
   // Pic 1
   String pic_1;
   
   // Pic 2
   String pic_2;
   
   // Pic 3
   String pic_3;
   
   // Pic 4
   String pic_4;
   
   // Pic 5
   String pic_5;
   
   // Video
   String video;
   
   // Retweet tweet id
   long retweet_tweet_ID;
   
   // TweetID
   long tweetID;
   
   	
   public CNewTweetPayload(String adr, 
		           String mes, 
                           long retweet_tweet_ID,
		           String pic_1,
                           String pic_2,
                           String pic_3,
                           String pic_4,
                           String pic_5,
                           String video,
                           String sig) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Message
           this.mes=mes;
   
           // Pic 1
           this.pic_1=pic_1;
   
           // Pic 2
           this.pic_2=pic_2;
   
           // Pic 3
           this.pic_3=pic_3;
   
           // Pic 4
           this.pic_4=pic_4;
   
           // Pic 5
           this.pic_5=pic_5;
   
           // Video
           this.video=video;
           
           // Retweet tweeet ID
           this.retweet_tweet_ID=retweet_tweet_ID;
           
           // Tweet ID
           this.tweetID=Math.round(Math.random()*1000000000+this.block);
           
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.mes+
                                 this.pic_1+
                                 this.pic_2+
                                 this.pic_3+
                                 this.pic_4+
                                 this.pic_5+
                                 this.video+
                                 this.retweet_tweet_ID+
                                 this.tweetID);
 	   
 	   // Sign
           this.sign(sig); 
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
        // Super class
   	CResult res=super.check(block);
   	if (res.passed==false) return res;
   	
   	  
   	// Check Message
        if (this.mes.length()<10 || this.mes.length()>1000)
           throw new Exception ("Invalid message - CNewTweetPayload.java");
	
        // Message valid ?
        if (!UTILS.BASIC.isString(mes))
            throw new Exception ("Invalid message - CNewTweetPayload.java");
        
        // Pic 1
        if (this.pic_1.length()>5)
	   if (!UTILS.BASIC.isLink(this.pic_1))
	      throw new Exception ("Invalid pic 1 - CNewTweetPayload.java");
          
        // Pic 2
        if (this.pic_2.length()>5)
	   if (!UTILS.BASIC.isLink(this.pic_2))
	      throw new Exception ("Invalid pic 2 - CNewTweetPayload.java");
          
        // Pic 3
        if (this.pic_3.length()>5)
	   if (!UTILS.BASIC.isLink(this.pic_3))
	     throw new Exception ("Invalid pic 3 - CNewTweetPayload.java");
          
        // Pic 4
        if (this.pic_4.length()>5)
	   if (!UTILS.BASIC.isLink(this.pic_4))
	      throw new Exception ("Invalid pic 4 - CNewTweetPayload.java");
          
        // Pic 5
        if (this.pic_5.length()>5)
	   if (!UTILS.BASIC.isLink(this.pic_5))
	     throw new Exception ("Invalid pic 5 - CNewTweetPayload.java");
          
        // Video
        if (this.video.length()>5)
	   if (!UTILS.BASIC.isLink(this.video))
	      throw new Exception ("Invalid video - CNewTweetPayload.java");
            
        // Statement
        Statement s=UTILS.DB.getStatement();
            
        // Check if tweetID exist
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM tweets "
                                   + "WHERE tweetID='"+this.tweetID+"'");
            
        if (UTILS.DB.hasData(rs))
           throw new Exception ("Invalid tweet ID - CNewTweetPayload.java");
            
        // Check if retweet ID exist
        if (this.retweet_tweet_ID>0)
        {
                // Retweet
                rs=s.executeQuery("SELECT * "
                                            + "FROM tweets "
                                           + "WHERE tweetID='"+this.retweet_tweet_ID+"'");
                
                // Has data
                if (!UTILS.DB.hasData(rs))
                   throw new Exception ("Invalid retweet ID - CNewTweetPayload.java");
        }
        else if (this.retweet_tweet_ID<0)
            throw new Exception ("Invalid retweet ID - CNewTweetPayload.java");
            
	// Check Hash
	String h=UTILS.BASIC.hash(this.getHash()+
 			          this.mes+
                                  this.pic_1+
                                  this.pic_2+
                                  this.pic_3+
                                  this.pic_4+
                                  this.pic_5+
                                  this.video+
                                  this.retweet_tweet_ID+
                                  this.tweetID);
	  
   	if (!h.equals(this.hash)) 
           throw new Exception ("Invalid hash - CNewTweetPayload.java");
   	
        // Return
 	return new CResult(true, "Ok", "CNewTweetPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
          // Superclass
          super.commit(block);
       
          // Commit
          UTILS.DB.executeUpdate("INSERT INTO tweets(tweetID, "
   		   		                  + "adr, "
   		   		                  + "mes, "
   		   		                  + "pic_1, "
                                                  + "pic_2, "
   		   		                  + "pic_3, "
   		   		                  + "pic_4, "
                                                  + "pic_5, "
                                                  + "video, "
                                                  + "retweet_tweet_ID, "
                                                  + "block)"
   		   		                  + "VALUES('"+
                                                  this.tweetID+"', '"+
                                                  this.target_adr+"', '"+
   		   		                  UTILS.BASIC.base64_encode(this.mes)+"', '"+
                                                  UTILS.BASIC.base64_encode(this.pic_1)+"', '"+
                                                  UTILS.BASIC.base64_encode(this.pic_2)+"', '"+
                                                  UTILS.BASIC.base64_encode(this.pic_3)+"', '"+
                                                  UTILS.BASIC.base64_encode(this.pic_4)+"', '"+
                                                  UTILS.BASIC.base64_encode(this.pic_5)+"', '"+
                                                  UTILS.BASIC.base64_encode(this.video)+"', '"+
                                                  this.retweet_tweet_ID+"', '"+
                                                  this.block+"')");
           
           // Retweet ?
           if (this.retweet_tweet_ID>0)
               UTILS.DB.executeUpdate("UPDATE tweets "
                                       + "SET retweets=retweets+1, "
                                           + "block='"+this.block+"' "
                                     + "WHERE tweetID='"+this.retweet_tweet_ID+"'");
   	
           
   	   // Return 
   	   return new CResult(true, "Ok", "CReqDataPayload", 70);
    }
}
