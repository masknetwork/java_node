package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CTweetsTable extends CTable
{
    public CTweetsTable() 
    {
        super("tweets");
    }
    
    public void expired(long block) throws Exception
    {
       // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM tweets "
                                         + "WHERE expire<='"+block+"'");
       
       // Remove
       while (rs.next())
       {
          // Tweet ID
          long tweetID=rs.getLong("tweetID");
           
          // Remove tweet
          UTILS.DB.executeUpdate("DELETE FROM tweets "
                                     + "WHERE tweetID='"+tweetID+"'");
        
          // Remove comments
          UTILS.DB.executeUpdate("DELETE FROM comments "
                                     + "WHERE parent_type='ID_POST' "
                                       + "AND parentID='"+tweetID+"'");
        
          // Remove upvotes
          UTILS.DB.executeUpdate("DELETE FROM votes "
                                     + "WHERE target_type='ID_POST' "
                                       + "AND targetID='"+tweetID+"'");
        
          // Remove retweets
          UTILS.DB.executeUpdate("DELETE FROM tweets "
                                     + "WHERE retweet_tweet_ID='"+tweetID+"'");
       }
      
    }
    
   
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE tweets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                        +"tweetID BIGINT NOT NULL DEFAULT '0', "
                                                        +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                        +"mes VARCHAR(20000) NOT NULL DEFAULT '', "
                                                        +"title VARCHAR(250) NOT NULL DEFAULT '', "
                                                        +"pic VARCHAR(250) NOT NULL DEFAULT '', "
                                                        +"block BIGINT NOT NULL DEFAULT '0', "
                                                        +"expire BIGINT NOT NULL DEFAULT '0', "
                                                        +"retweet VARCHAR(2) NOT NULL DEFAULT 'N', "
                                                        +"retweet_tweet_ID BIGINT NOT NULL DEFAULT '0', "
                                                        +"comments BIGINT NOT NULL DEFAULT '0', "
                                                        +"retweets BIGINT NOT NULL DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX tweets_tweetID ON tweets(tweetID)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_adr ON tweets(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_retweet_tweet_ID ON tweets(retweet_tweet_ID)");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE tweets");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
    
}
