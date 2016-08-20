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
        super("votes");
    }
    
    public void removeByAdr(String adr) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM votes "
                                   + "WHERE adr='"+adr+"'");
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM votes WHERE expire<"+block);
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE votes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                          +"target_type VARCHAR(20) NOT NULL DEFAULT '0', "
                                                          +"targetID BIGINT NOT NULL DEFAULT '0', "
                                                          +"type VARCHAR(25) NOT NULL DEFAULT '', "
                                                          +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                          +"power FLOAT(9,2) NOT NULL DEFAULT 0, "
                                                          +"block BIGINT NOT NULL DEFAULT '0', "
                                                          +"expire BIGINT NOT NULL DEFAULT '0', "
                                                          +"rowhash VARCHAR(100) NOT NULL DEFAULT '')");
             
        UTILS.DB.executeUpdate("CREATE INDEX votes_target_type ON votes(target_type)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_targetID ON votes(targetID)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_adr ON votes(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_block ON votes(block)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_rowhash ON votes(rowhash)");
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE votes "
                                + "SET rowhash=SHA2(CONCAT(target_type, "
                                                        + "targetID,"
                                                        + "adr, "
                                                        + "type, "
                                                        + "power,"
                                                        + "expire,"
                                                        + "block), 256) "
                              + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("votes"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET votes=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM votes)"); 
        
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
            
            // Target type
            String target_type=row.getString("target_type");
            
            // Target ID
            long targetID=row.getLong("targetID");
            
            // Adr
            String adr=row.getString("adr");
            
            // Type
            String type=row.getString("type");
            
            // Power
            String power=UTILS.BASIC.zeros_2(UTILS.FORMAT_2.format(row.getDouble("power")));
               
            // Block
            long block=row.getLong("block");
            
            // Expire
            long expire=row.getLong("expire");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(target_type+
                                         targetID+
                                         adr+
                                         type+
                                         power+
                                         expire+
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
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM votes "
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
               
               // Type
               this.addRow("type", rs.getString("type"));
               
               // Power
               this.addRow("power", UTILS.BASIC.zeros_2(UTILS.FORMAT_2.format(rs.getDouble("power"))));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO votes "
                                         + "SET target_type='"+row.getString("target_type")+"', "
                                             + "targetID='"+row.getLong("targetID")+"', "
                                             + "adr='"+row.getString("adr")+"', "
                                             + "type='"+row.getString("type")+"', "
                                             + "power='"+UTILS.BASIC.zeros_2(UTILS.FORMAT_2.format(row.getDouble("power")))+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("votes");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE votes");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "votes.table", crc);
    }
    
   
}

