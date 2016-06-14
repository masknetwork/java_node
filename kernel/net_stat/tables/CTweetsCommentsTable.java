package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CTweetsCommentsTable extends CTable
{
    public CTweetsCommentsTable()
    {
        super("tweets_comments");
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE tweets_comments(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"tweetID BIGINT DEFAULT '0', "
                                                                +"mes VARCHAR(1000) DEFAULT '', "
                                                                +"rowhash VARCHAR(100) DEFAULT '', "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"status VARCHAR(25) DEFAULT '', "
                                                                +"rowID BIGINT DEFAULT 0, "
                                                                +"comID BIGINT DEFAULT 0, "
                                                                +"adr VARCHAR(250) DEFAULT '')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_tweetID ON tweets_comments(tweetID)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_rowhash ON tweets_comments(rowhash)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_block ON tweets_comments(block)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_status ON tweets_comments(status)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_rowID ON tweets_comments(rowID)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_comID ON tweets_comments(comID)");   
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE tweets_comments "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                         + "tweetID, "
                                                         + "mes, "
                                                         + "status, "
                                                         + "rowID, "
                                                         + "comID, "
                                                         + "block), 256) "
                                     + "WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET tweets_comments=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                               + "FROM tweets_comments "
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
            
            // Address
            String adr=row.getString("adr");
            
            // Tweet ID
            long tweetID=row.getLong("tweetID");
            
            // Mes
            String mes=row.getString("mes");
               
            // Status
            String status=row.getString("status");
               
            // Row ID
            long rowID=row.getLong("rowID");
               
            // Com ID
            long comID=row.getLong("comID");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         tweetID+
                                         mes+
                                         status+
                                         rowID+
                                         comID+
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
                                   + "FROM tweets_comments "
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
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
               // TweetID
               this.addRow("tweetID", rs.getLong("adr"));
               
               // Mes
               this.addRow("mes", rs.getString("mes"));
               
               // Title
               this.addRow("title", rs.getString("title"));
               
               // Row ID
               this.addRow("rowID", rs.getLong("rowID"));
               
               // Com ID
               this.addRow("comID", rs.getLong("comID"));
               
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
