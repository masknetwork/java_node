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
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE tweets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                        +"tweetID BIGINT DEFAULT '0', "
                                                        +"adr VARCHAR(250) DEFAULT '', "
                                                        +"mes VARCHAR(1000) DEFAULT '', "
                                                        +"pic_1 VARCHAR(250) DEFAULT '', "
                                                        +"pic_2 VARCHAR(250) DEFAULT '', "
                                                        +"pic_3 VARCHAR(250) DEFAULT '', "
                                                        +"pic_4 VARCHAR(250) DEFAULT '', "
                                                        +"pic_5 VARCHAR(250) DEFAULT '', "
                                                        +"video VARCHAR(250) DEFAULT '', "
                                                        +"rowhash VARCHAR(100) DEFAULT '', "
                                                        +"block BIGINT DEFAULT '0', "
                                                        +"retweet VARCHAR(2) DEFAULT 'N', "
                                                        +"retweet_tweet_ID BIGINT DEFAULT '0', "
                                                        +"likes BIGINT DEFAULT '0', "
                                                        +"comments BIGINT DEFAULT '0', "
                                                        +"retweets BIGINT DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_tweetID ON tweets(tweetID)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_adr ON tweets(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_retweet_tweet_ID ON tweets(retweet_tweet_ID)");
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE tweets "
                                + "SET rowhash=SHA2(CONCAT(tweetID, "
                                                         + "adr, "
                                                         + "mes, "
                                                         + "pic_1, "
                                                         + "pic_2, "
                                                         + "pic_3, "
                                                         + "pic_4, "
                                                         + "pic_5, "
                                                         + "video, "
                                                         + "retweet, "
                                                         + "retweet_tweet_ID, "
                                                         + "likes, "
                                                         + "comments, "
                                                         + "retweets, "
                                                         + "block), 256) WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET tweets=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                                              + "FROM tweets)"); 
    }
    
    public void fromJSON(String data, String crc) throws Exception
    {
        // Grand hash
        String ghash="";
        
        // Parent
        super.fromJSON(data, crc);
        
        // Object
        JSONObject obj = new JSONObject(data); 
        
        // Load rows
        JSONArray rows=obj.getJSONArray("rows");
        
        // Check each row
        for (int a=0; a<=rows.length()-1; a++)
        {
            // Load row
            JSONObject row=rows.getJSONObject(a);
            
             // Tweet ID
            long tweetID=row.getLong("tweetID");
            
            // Address
            String adr=row.getString("adr");
            
            // Mes
            String mes=row.getString("mes");
               
            // Pic 1
            String pic_1=row.getString("pic_1");
            
            // Pic 2
            String pic_2=row.getString("pic_2");
            
            // Pic 3
            String pic_3=row.getString("pic_3");
            
            // Pic 4
            String pic_4=row.getString("pic_4");
            
            // Pic 5
            String pic_5=row.getString("pic_5");
               
            // Video
            String video=row.getString("video");
            
            // Retweet
            String retweet=row.getString("retweet");
            
            // Retweet tweet ID
            long retweet_tweet_ID=row.getLong("retweet_tweet_ID");
            
            // Likes
            long likes=row.getLong("likes");
            
            // Comments
            long comments=row.getLong("comments");
            
            // Retweets
            long retweets=row.getLong("retweets");
            
            // Expire
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(tweetID+
                                         adr+
                                         mes+
                                         pic_1+
                                         pic_2+
                                         pic_3+
                                         pic_4+
                                         pic_5+
                                         video+
                                         retweet+
                                         retweet_tweet_ID+
                                         likes+
                                         comments+
                                         retweets+
                                         block);
                    
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CAdsTable.java");
            
            // Total hash
            if (a>0) 
                ghash=ghash+","+hash;
            else
                ghash=hash;
        }
        
        // Grand hash
        System.out.println(ghash);
        ghash=UTILS.BASIC.hash(ghash);
        System.out.println(ghash);
         
        // Check grand hash
        if (!ghash.equals(crc))
            throw new Exception("Invalid grand hash - CTweetsTable.java");
    }
    
    public void toDB() throws Exception
    {
         // Grand hash
        String ghash="";
        
        // Object
        JSONObject obj = new JSONObject(this.json); 
        
        // Load rows
        JSONArray rows=obj.getJSONArray("rows");
        
        // Check each row
        for (int a=0; a<=rows.length()-1; a++)
        {
            // Load row
            JSONObject row=rows.getJSONObject(a);
            
            UTILS.DB.executeUpdate("INSERT INTO tweets(tweetID, "
                                                 + "adr, "
                                                 + "mes, "
                                                 + "pic_1, "
                                                 + "pic_2, "
                                                 + "pic_3, "
                                                 + "pic_4, "
                                                 + "pic_5, "
                                                 + "video, "
                                                 + "retweet, "
                                                 + "retweet_tweet_ID, "
                                                 + "likes, "
                                                 + "comments, "
                                                 + "retweets, "
                                                 + "block)  VALUES('"
                                                 +row.getLong("tweetID")+"', '"
                                                 +row.getString("adr")+"', '"
                                                 +row.getString("mes")+"', '"
                                                 +row.getString("pic_1")+"', '"
                                                 +row.getString("pic_2")+"', '"
                                                 +row.getString("pic_3")+"', '"
                                                 +row.getString("pic_4")+"', '"
                                                 +row.getString("pic_5")+"', '"
                                                 +row.getString("video")+"', '"
                                                 +row.getString("retweet")+"', '"
                                                 +row.getLong("retweet_tweet_ID")+"', '"
                                                 +row.getLong("likes")+"', '"
                                                 +row.getLong("comments")+"', '"
                                                 +row.getLong("retweets")+"', '"
                                                 +row.getLong("block")+"')");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("tweets");
    }
    
    public void fromDB() throws Exception
    {
       int a=0;
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load data
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM tweets");
       
       // Parse
       if (UTILS.DB.hasData(rs))
       {
           while (rs.next())
           {
               // Pos
               a++;
               
               // Start tag
               if (a==1) 
                   this.json=this.json+"{";
               else
                   this.json=this.json+", {";
               
               // TweetID
               this.addRow("tweetID", rs.getLong("tweetID"));
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
              // Mes
               this.addRow("mes", rs.getString("mes"));
               
               // Pic 1
               this.addRow("pic_1", rs.getString("pic_1"));
               
               // Pic 2
               this.addRow("pic_2", rs.getString("pic_2"));
               
               // Pic 3
               this.addRow("pic_3", rs.getString("pic_3"));
               
               // Pic 4
               this.addRow("pic_4", rs.getString("pic_4"));
               
               // Pic 5
               this.addRow("pic_5", rs.getString("pic_5"));
               
               // Video
               this.addRow("video", rs.getString("video"));
               
               // Retweet
               this.addRow("retweet", rs.getString("retweet"));
               
               // Retweet tweet ID
               this.addRow("retweet_tweet_ID", rs.getLong("retweet_tweet_ID"));
               
               // Likes
               this.addRow("likes", rs.getLong("likes"));
               
               // Comments
               this.addRow("comments", rs.getLong("comments"));
               
               // Retweets
               this.addRow("retweets", rs.getLong("retweets"));
               
               // Block
               this.addRow("block", rs.getLong("block"));
               
               // Rowhash
               this.addRow("rowhash", rs.getString("rowhash"));
               
               // End tag
               this.json=this.json+"}";
           }
       }
       
       // Format
       this.json=this.json.replace(", }", "}");
               
       // Close json
       this.json=this.json+"]}";
       
       // Close
       rs.close();
       s.close();
    }
}
