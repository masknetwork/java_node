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
    
    public void expired(long block, String adr) throws Exception
    {
        if (adr.equals(""))
            UTILS.DB.executeUpdate("DELETE FROM tweets_follow "
                                   + "WHERE expire<"+block);
        else
            UTILS.DB.executeUpdate("DELETE FROM tweets_follow "
                                       + "WHERE adr='"+adr+"' "
                                          + "OR follows='"+adr+"'");
    }
    
    public void create() throws Exception
    {
       UTILS.DB.executeUpdate("CREATE TABLE tweets_follow(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"follows VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"expire BIGINT NOT NULL DEFAULT '0', "
                                                                +"block BIGINT NOT NULL DEFAULT '0', "
                                                                +"rowhash VARCHAR(100) DEFAULT '')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_adr ON tweets_follow(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_follows ON tweets_follow(follows)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_block ON tweets_follow(block)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_rowhash ON tweets_follow(rowhash)");   
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE tweets_follow "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                        + "follows,"
                                                        + "expire, "
                                                        + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("tweets_follow"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET tweets_follow=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM tweets_follow)"); 
        
            // Refresh
            super.refresh(block);
        
            // Reload hash
            loadHash();
        }
    }
    
    public void fromJSON(String data, String crc) throws Exception
    {
        System.out.println(data);
        
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
            
            // Expire
            long expire=row.getLong("expire");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         follows+
                                         expire+
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
        ghash=UTILS.BASIC.hash(ghash);
         
        // Check grand hash
        if (!ghash.equals(crc))
            throw new Exception("Invalid grand hash - CAdsTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
       
       // Statement
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                   + "FROM tweets_follow "
                               + "ORDER BY ID ASC");
       
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
               
               // Follows
               this.addRow("follows", rs.getString("follows"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO tweets_follow "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "follows='"+row.getString("follows")+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "block='"+row.getString("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
           
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("tweets_follow");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE tweets_follow");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "tweets_follow.table", crc);
    }
    
    
}