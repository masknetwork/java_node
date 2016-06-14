package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CTweetsLikesTable extends CTable
{
    public CTweetsLikesTable()
    {
        super("tweets_likes");
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE tweets_likes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"tweetID BIGINT DEFAULT '0', "
                                                                +"adr VARCHAR(250) DEFAULT '', "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"rowhash VARCHAR(100) DEFAULT '')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_tweetID ON tweets_likes(tweetID)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_adr ON tweets_likes(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_block ON tweets_likes(block)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_rowhash ON tweets_likes(rowhash)");
    }
    
 // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE tweets_likes "
                                + "SET rowhash=SHA2(CONCAT(tweetID, "
                                                        + "adr, "
                                                        + "block), 256) "
                              + "WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE tweets_likes "
                                + "SET ads=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                               + "FROM tweets_likes "
                           + "ORDER BY rowhash ASC)"); 
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
            
            // TweetID
            String tweetID=row.getString("tweetID");
            
            // Adr
            String adr=row.getString("adr");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(tweetID+
                                         adr+
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
            throw new Exception("Invalid grand hash - CAdsTable.java");
    }
    
    public void fromDB() throws Exception
    {
       int a=0;
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load data
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM tweets_likes "
                               + "ORDER BY rowhash ASC");
       
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
               
               // Tweet ID
               this.addRow("tweetID", rs.getString("tweetID"));
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
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

