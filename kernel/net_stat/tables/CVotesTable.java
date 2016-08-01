package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CVotesTable extends CTable
{
    public CVotesTable()
    {
        super("upvotes");
    }
    
    public void removeByAdr(String adr) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM upvotes "
                                   + "WHERE adr='"+adr+"'");
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE upvotes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                          +"target_type BIGINT DEFAULT '0', "
                                                          +"targetID BIGINT DEFAULT '0', "
                                                          +"adr VARCHAR(250) DEFAULT '', "
                                                          +"power FLOAT(9,2) DEFAULT 0, "
                                                          +"block BIGINT DEFAULT '0', "
                                                          +"rowhash VARCHAR(100) DEFAULT '')");
             
        UTILS.DB.executeUpdate("CREATE INDEX upvotes_tweetID ON upvotes(tweetID)");
        UTILS.DB.executeUpdate("CREATE INDEX upvotes_adr ON upvotes(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX upvotes_block ON upvotes(block)");
        UTILS.DB.executeUpdate("CREATE INDEX upvotes_rowhash ON upvotes(rowhash)");
    }
    
 // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE upvotes "
                                + "SET rowhash=SHA2(CONCAT(target_type, "
                                                        + "targetID,"
                                                        + "adr, "
                                                        + "power,"
                                                        + "block), 256) "
                              + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("upvotes"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET upvotes=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM upvotes)"); 
        
            // Refresh
            super.refresh(block);
        
            // Reload hash
            loadHash();
        }
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
            
            // Target type
            String target_type=row.getString("target_type");
            
            // Target ID
            long targetID=row.getLong("targetID");
            
            // Adr
            String adr=row.getString("adr");
            
            // Power
            double power=row.getDouble("power");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(target_type+
                                         targetID+
                                         adr+
                                         power+
                                         block);
                    
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CTweetsLikesTable.java");
            
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
            throw new Exception("Invalid grand hash - CTweetsLikesTable.java");
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
                                          + "FROM upvotes "
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
               
               // Target type
               this.addRow("target_type", rs.getString("target_type"));
               
               // Target ID
               this.addRow("targetID", rs.getLong("targetID"));
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
               // Power
               this.addRow("power", rs.getDouble("power"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO upvotes "
                                         + "SET target_type='"+row.getString("target_type")+"', "
                                             + "targetID='"+row.getLong("targetID")+"', "
                                             + "adr='"+row.getString("adr")+"', "
                                             + "power='"+row.getDouble("adr")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("upvotes");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE upvotes");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "upvotes.table", crc);
    }
    
   
}

