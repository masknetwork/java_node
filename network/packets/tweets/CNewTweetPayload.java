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
   // Title
   String title;
   
   // Message
   String mes;
   
   // Pic
   String pic;
   
   // Retweet tweet id
   long retweet_tweet_ID;
   
   // TweetID
   long tweetID;
   
   // Days
   long days;
   
   // Serial
   private static final long serialVersionUID = 1L;
   
   public CNewTweetPayload(String adr, 
		           String title, 
                           String mes, 
                           String pic,
                           long retweet_tweet_ID,
                           long days) throws Exception
   {
	  // Superclass
	   super(adr);
	   
           // Title
           this.title=title;
           
	   // Message
           this.mes=mes;
           
           // Pic
           this.pic=pic;
              
           // Retweet tweeet ID
           this.retweet_tweet_ID=retweet_tweet_ID;
           
           // Tweet ID
           this.tweetID=UTILS.BASIC.getID();
           
           // Days
           this.days=days;
           
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.title+
                                 this.mes+
                                 this.pic+
                                 this.retweet_tweet_ID+
                                 this.tweetID+
                                 this.days);
 	   
 	   // Sign
           this.sign(); 
   }
   
   public void check(CBlockPayload block) throws Exception
   {
        // Days
        if (days<1)
            throw new Exception("Invalid days - CNewTweetPayload.java");
        
        // ID valid ?
        if (UTILS.BASIC.existID(this.tweetID))
           throw new Exception("Invalid tweetID - CNewTweetPayload.java");
            
        // Not a retweet
        if (this.retweet_tweet_ID==0)
        {
           // Check title
           if (!UTILS.BASIC.isTitle(this.title))
               throw new Exception ("Invalid title - CNewTweetPayload.java");
        
   	   // Check Message
           if (!UTILS.BASIC.isDesc(this.mes, 10000))
            throw new Exception ("Invalid message - CNewTweetPayload.java");
           
           // Pic
           if (!this.pic.equals(""))
            if (!UTILS.BASIC.isPic(this.pic))
                throw new Exception ("Invalid pic - CNewTweetPayload.java");
        }
            
        // Check if retweet ID exist
        if (this.retweet_tweet_ID>0)
           if (!UTILS.BASIC.targetValid("ID_POST", this.retweet_tweet_ID))
              throw new Exception("Invalid retweet_tweet_ID - CNewTweetPayload.java");
        
        // Check Hash
	String h=UTILS.BASIC.hash(this.getHash()+
 			          this.title+
                                  this.mes+
                                  this.pic+
                                  this.retweet_tweet_ID+
                                  this.tweetID+
                                  this.days);
	  
   	if (!h.equals(this.hash)) 
           throw new Exception ("Invalid hash - CNewTweetPayload.java");
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
          // Superclass
          super.commit(block);
       
          // Commit
          UTILS.DB.executeUpdate("INSERT INTO tweets "
                                       + "SET tweetID='"+this.tweetID+"', "
   		   		            + "adr='"+this.target_adr+"', "
   		   		            + "title='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                            + "mes='"+UTILS.BASIC.base64_encode(this.mes)+"', "
   		                            + "pic='"+UTILS.BASIC.base64_encode(this.pic)+"', "
                                            + "expire='"+(this.block+(this.days*1440))+"', "
                                            + "retweet_tweet_ID='"+this.retweet_tweet_ID+"', "
                                            + "block='"+this.block+"'");
           
           // Retweet ?
           if (this.retweet_tweet_ID>0)
           UTILS.DB.executeUpdate("UPDATE tweets "
                                   + "SET retweets=retweets+1, "
                                       + "block='"+this.block+"' "
                                 + "WHERE tweetID='"+this.retweet_tweet_ID+"'");
   	
    }
}
