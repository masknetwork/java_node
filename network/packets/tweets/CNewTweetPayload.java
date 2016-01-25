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
   // Target wall
   String t_adr; 
   
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
   
   // Budget
   double budget;
   
   // Budget currency
   String budget_cur;
   
   // Budget expire
   long budget_expire;
	
   public CNewTweetPayload(String adr, 
		           String target_adr, 
		           String mes, 
                           long retweet_tweet_ID,
		           String pic_1,
                           String pic_2,
                           String pic_3,
                           String pic_4,
                           String pic_5,
                           String video,
                           double budget,
                           String budget_cur,
                           long budget_expire
                           )
   {
	  // Superclass
	   super(adr);
	   
	   // Target wall
           this.t_adr=target_adr;
   
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
           
           // Budget
           this.budget=budget;
           
           // Budget currency
           this.budget_cur=budget_cur;
           
           // Budget expire
           this.budget_expire=budget_expire;
           
           // Retweet tweeet ID
           this.retweet_tweet_ID=retweet_tweet_ID;
           
           // Tweet ID
           this.tweetID=Math.round(Math.random()*1000000000+this.block);
           
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.t_adr+
                                 this.mes+
                                 this.pic_1+
                                 this.pic_2+
                                 this.pic_3+
                                 this.pic_4+
                                 this.pic_5+
                                 this.video+
                                 this.tweetID+
                                 this.budget+
                                 this.budget_cur+
                                 this.budget_expire);
 	   
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
   	
   	    // Target address valid
   	    if (UTILS.BASIC.adressValid(this.t_adr)==false) 
   		return new CResult(false, "Invalid target address", "CNewTweetPayload", 51);
   	  
   	    // Check Message
  	    if (this.mes.length()<10 || this.mes.length()>1000)
              return new CResult(false, "Invalid message.", "CNewTweetPayload", 77);
	  
            // Pic 1
            if (this.pic_1.length()>5)
	      if (!UTILS.BASIC.isLink(this.pic_1))
	         return new CResult(false, "Invalid pic 1 link", "CNewTweetPayload", 77);
          
            // Pic 2
            if (this.pic_2.length()>5)
	       if (!UTILS.BASIC.isLink(this.pic_2))
	         return new CResult(false, "Invalid pic 2 link", "CNewTweetPayload", 77);
          
            // Pic 3
            if (this.pic_3.length()>5)
	       if (!UTILS.BASIC.isLink(this.pic_3))
	         return new CResult(false, "Invalid pic 3 link", "CNewTweetPayload", 77);
          
            // Pic 4
            if (this.pic_4.length()>5)
	      if (!UTILS.BASIC.isLink(this.pic_4))
	        return new CResult(false, "Invalid pic 4 link", "CNewTweetPayload", 77);
          
            // Pic 5
            if (this.pic_5.length()>5)
	      if (!UTILS.BASIC.isLink(this.pic_5))
	        return new CResult(false, "Invalid pic 5 link", "CNewTweetPayload", 77);
          
            // Video
            if (this.video.length()>5)
	      if (!UTILS.BASIC.isLink(this.video))
	        return new CResult(false, "Invalid video link", "CNewTweetPayload", 77);
            
            // Statement
            Statement s=UTILS.DB.getStatement();
            
            // Check if tweetID exist
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM tweets "
                                       + "WHERE tweetID='"+this.tweetID+"'");
            
            if (UTILS.DB.hasData(rs))
               return new CResult(false, "TweetID already exist", "CNewTweetPayload", 77);
            
            // Check if retweet ID exist
            if (this.retweet_tweet_ID>0)
            {
                // Retweet
                rs=s.executeQuery("SELECT * "
                                            + "FROM tweets "
                                           + "WHERE tweetID='"+this.retweet_tweet_ID+"'");
                
                // Has data
                if (!UTILS.DB.hasData(rs))
                   return new CResult(false, "Invalid retweed ID", "CNewTweetPayload", 77);
            }
            
            // Budget exist ?
            if (budget>0)
            {
                // Asset exist ?
                if (this.budget_cur.equals("MSK"))
                {
                   // Funds
                   if (UTILS.BASIC.getBalance(this.target_adr, this.budget_cur)<this.budget)
                       return new CResult(false, "Invalid budget currency", "CNewTweetPayload", 77);
                   
                   // Exire
                   if (this.budget_expire>5)
                       return new CResult(false, "Maxximum 5 days expire date", "CNewTweetPayload", 77);
                   
                   // Take funds
                   UTILS.BASIC.newTrans(this.target_adr, 
                                        "", 
                                        -this.budget, 
                                        true,
                                        this.budget_cur, 
                                        "You have offered incentives for a tweet", 
                                        "", 
                                        this.hash, 
                                        this.block);
                }
            }
            
	    // Check Hash
	    String h=UTILS.BASIC.hash(this.getHash()+
 			              this.t_adr+
                                      this.mes+
                                      this.pic_1+
                                      this.pic_2+
                                      this.pic_3+
                                      this.pic_4+
                                      this.pic_5+
                                      this.video+
                                      this.tweetID+
                                      this.budget+
                                      this.budget_cur+
                                      this.budget_expire);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CNewTweetPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CNewTweetPayload", 157);
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesPayload.java", 57);
        }
       
 	  // Return
 	  return new CResult(true, "Ok", "CNewTweetPayload", 164);
   }
   
   public CResult commit(CBlockPayload block)
   {
       try
       {
          CResult res=this.check(block);
          if (res.passed==false) return res;
	  
          // Superclass
          super.commit(block);
       
          // Status
          String status;
          
          // Clear
          if (this.budget>0)
              UTILS.BASIC.clearTrans(this.hash, "ID_ALL");
       
          // Detect status
          if (this.target_adr==this.t_adr)
              status="ID_APROVED";
          else
              status="ID_PENDING";
          
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
                                                + "budget, "
                                                + "budget_cur, "
                                                + "budget_expires, "
                                                + "status, "
                                                + "retweet_tweet_ID, "
                                                + "received, "
   		   		                + "block)"
   		   		                + "VALUES('"+
                                                this.tweetID+"', '"+
   		   		                this.t_adr+"', '"+
   		   		                UTILS.BASIC.base64_encode(UTILS.BASIC.clean(this.mes))+"', '"+
                                                UTILS.BASIC.base64_encode(this.pic_1)+"', '"+
                                                UTILS.BASIC.base64_encode(this.pic_2)+"', '"+
                                                UTILS.BASIC.base64_encode(this.pic_3)+"', '"+
                                                UTILS.BASIC.base64_encode(this.pic_4)+"', '"+
                                                UTILS.BASIC.base64_encode(this.pic_5)+"', '"+
                                                UTILS.BASIC.base64_encode(this.video)+"', '"+
                                                this.budget+"', '"+
                                                this.budget_cur+"', '"+
                                                (this.block+this.budget_expire*1440)+"', '"+
                                                status+"', '"+
                                                this.retweet_tweet_ID+"', '"+
                                                UTILS.BASIC.tstamp()+"', '"+
   		   		                this.block+"')");
           
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Load tweets number
           ResultSet rs=s.executeQuery("SELECT COUNT(*) AS no "
                                       + "FROM tweets "
                                      + "WHERE adr='"+this.t_adr+"'");
           rs.next();
           
           // Increase tweets number
           UTILS.DB.executeUpdate("UPDATE adr "
                                   + "SET tweets='"+rs.getLong("no")+"' "
                                 + "WHERE adr='"+this.target_adr+"'");
           
           // Retweet ?
           if (this.retweet_tweet_ID>0)
               UTILS.DB.executeUpdate("UPDATE tweets "
                                       + "SET retweets=retweets+1 "
                                     + "WHERE tweetID='"+this.retweet_tweet_ID+"'");
   	}
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesPayload.java", 57);
        }
       
   	  // Return 
   	   return new CResult(true, "Ok", "CReqDataPayload", 70);
    }
}
