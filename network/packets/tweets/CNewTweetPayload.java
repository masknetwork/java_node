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
   
   // Serial
   private static final long serialVersionUID = 100L;
   
   public CNewTweetPayload(String adr, 
		           String title, 
                           String mes, 
                           String pic,
                           long retweet_tweet_ID) throws Exception
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
           
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.title+
                                 this.mes+
                                 this.pic+
                                 this.retweet_tweet_ID+
                                 this.tweetID);
 	   
 	   // Sign
           this.sign(); 
   }
   
   public void check(CBlockPayload block) throws Exception
   {
        // Super class
   	super.check(block);
        
        // Check tweetID
   	if (!UTILS.BASIC.validID(this.tweetID))
           throw new Exception ("Invalid tweet ID - CNewTweetPayload.java");
        
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
        else
        {
            // Title or pic
            if (!this.pic.equals("") || 
                !this.title.equals(""))
            throw new Exception ("Invalid entry data - CNewTweetPayload.java");
        }
        
        
            
        // Check if retweet ID exist
        if (this.retweet_tweet_ID>0)
        {
                // Retweet
                ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                   + "FROM tweets "
                                                  + "WHERE tweetID='"+this.retweet_tweet_ID+"'");
                
                // Has data
                if (!UTILS.DB.hasData(rs))
                   throw new Exception ("Invalid retweet ID - CNewTweetPayload.java");
        }
        
            
	// Check Hash
	String h=UTILS.BASIC.hash(this.getHash()+
 			          this.title+
                                  this.mes+
                                  this.pic+
                                  this.retweet_tweet_ID+
                                  this.tweetID);
	  
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
