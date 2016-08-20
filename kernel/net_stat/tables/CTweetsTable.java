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
           this.removeByID(rs.getLong("tweetID"));
      
    }
    
   
    public void removeByID(long ID) throws Exception
    {
        // Remove tweet
        UTILS.DB.executeUpdate("DELETE FROM tweets "
                                   + "WHERE tweetID='"+ID+"'");
        
        // Remove comments
        UTILS.DB.executeUpdate("DELETE FROM comments "
                                   + "WHERE parent_type='ID_POST' "
                                     + "AND parentID='"+ID+"'");
        
        // Remove upvotes
        UTILS.DB.executeUpdate("DELETE FROM votes "
                                   + "WHERE target_type='ID_POST' "
                                     + "AND targetID='"+ID+"'");
        
        // Remove retweets
        UTILS.DB.executeUpdate("DELETE FROM tweets "
                                   + "WHERE retweet_tweet_ID='"+ID+"'");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE tweets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                        +"tweetID BIGINT NOT NULL DEFAULT '0', "
                                                        +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                        +"mes VARCHAR(10000) NOT NULL DEFAULT '', "
                                                        +"title VARCHAR(250) NOT NULL DEFAULT '', "
                                                        +"pic VARCHAR(250) NOT NULL DEFAULT '', "
                                                        +"rowhash VARCHAR(100) NOT NULL DEFAULT '', "
                                                        +"block BIGINT NOT NULL DEFAULT '0', "
                                                        +"expire BIGINT NOT NULL DEFAULT '0', "
                                                        +"retweet VARCHAR(2) NOT NULL DEFAULT 'N', "
                                                        +"retweet_tweet_ID BIGINT NOT NULL DEFAULT '0', "
                                                        +"comments BIGINT NOT NULL DEFAULT '0', "
                                                        +"retweets BIGINT NOT NULL DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_tweetID ON tweets(tweetID)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_adr ON tweets(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_retweet_tweet_ID ON tweets(retweet_tweet_ID)");
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE tweets "
                                + "SET rowhash=SHA2(CONCAT(tweetID, "
                                                         + "adr, "
                                                         + "mes, "
                                                         + "title, "
                                                         + "pic, "
                                                         + "retweet, "
                                                         + "retweet_tweet_ID, "
                                                         + "expire, "
                                                         + "comments, "
                                                         + "retweets, "
                                                         + "block), 256) WHERE block='"+block+"'");
        
         // Table hash
        if (UTILS.BASIC.hasRecords("tweets"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET tweets=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM tweets)"); 
        
            // Refresh
            super.refresh(block);
        
            // Reload hash
            loadHash();
        }
    }
    
    public void fromJSON(String data, String crc) throws Exception
    {
        // No data
        if (crc.equals("")) return;
        
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
            
            // Title
            String title=row.getString("title");
            
            // Mes
            String mes=row.getString("mes");
            
            // Pic
            String pic=row.getString("pic");
            
            // Retweet
            String retweet=row.getString("retweet");
            
            // Retweet tweet ID
            long retweet_tweet_ID=row.getLong("retweet_tweet_ID");
            
            // Expire
            long expire=row.getLong("expire");
            
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
                                         title+
                                         pic+
                                         retweet+
                                         retweet_tweet_ID+
                                         expire+
                                         comments+
                                         retweets+
                                         block);
                    
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CTweetsTable.java");
            
            // Total hash
            if (a>0) 
                ghash=ghash+","+hash;
            else
                ghash=hash;
        }
        
        // Grand hash
        ghash=UTILS.BASIC.hash(ghash);
         
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
            
            UTILS.DB.executeUpdate("INSERT INTO tweets "
                                         + "SET tweetID='"+row.getLong("tweetID")+"', "
                                             + "adr='"+row.getString("adr")+"', "
                                             + "title='"+row.getString("title")+"', "
                                             + "mes='"+row.getString("mes")+"', "
                                             + "pic='"+row.getString("pic")+"', "
                                             + "retweet='"+row.getString("retweet")+"', "
                                             + "retweet_tweet_ID='"+row.getLong("retweet_tweet_ID")+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "comments='"+row.getLong("comments")+"', "
                                             + "retweets='"+row.getLong("retweets")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"', "
                                             + "block='"+row.getLong("block")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("tweets");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
        super.fromDB();
        
       // Init
       int a=0;
       
       // Statement
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM tweets ORDER BY ID ASC");
       
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
               
               // Title
               this.addRow("title", rs.getString("title"));
               
               // Mes
               this.addRow("mes", rs.getString("mes"));
               
               // Pic
               this.addRow("pic", rs.getString("pic"));
               
               // Retweet
               this.addRow("retweet", rs.getString("retweet"));
               
               // Retweet tweet ID
               this.addRow("retweet_tweet_ID", rs.getLong("retweet_tweet_ID"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
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
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE tweets");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "tweets.table", crc);
    }
    
    
}
