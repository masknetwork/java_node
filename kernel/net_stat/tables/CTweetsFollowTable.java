package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CTweetsFollowTable extends CTable
{
    public CTweetsFollowTable()
    {
        super("tweets_follow");
    }
    
    public void create() throws Exception
    {
       UTILS.DB.executeUpdate("CREATE TABLE tweets_follow(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"adr VARCHAR(250) DEFAULT '', "
                                                                +"follows VARCHAR(250) DEFAULT '', "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"rowhash VARCHAR(100) DEFAULT '')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_adr ON tweets_follow(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_follows ON tweets_follow(follows)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_block ON tweets_follow(block)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_rowhash ON tweets_follow(rowhash)");   
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE tweets_follow "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                        + "follows, "
                                                        + "block), 256) WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET tweets_follow=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                               + "FROM tweets_follow "
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
            
            // Follows
            String follows=row.getString("follows");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         follows+
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
                                   + "FROM tweets_follow "
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
               
               // Country
               this.addRow("follows", rs.getString("follows"));
               
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
