package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CCommentsTable extends CTable
{
    public CCommentsTable()
    {
        super("comments");
    }
    
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE comments(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"adr VARCHAR(250) NOT NULL DEFAULT '',"
                                                            +"parent_type VARCHAR(25) NOT NULL DEFAULT '', "
                                                            +"parentID BIGINT NOT NULL DEFAULT 0, "
                                                            +"comID BIGINT NOT NULL DEFAULT 0, "
                                                            +"mes VARCHAR(1000) NOT NULL DEFAULT '', "
                                                            +"rowhash VARCHAR(100) NOT NULL DEFAULT '', "
                                                            +"block BIGINT NOT NULL DEFAULT 0)");
             
        UTILS.DB.executeUpdate("CREATE INDEX comments_parent_type ON comments(parent_type)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_parentID ON comments(parentID)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_block ON comments(block)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_status ON comments(status)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_comID ON comments(comID)");  
    }
    
    public void removeByAdr(String adr) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM comments "
                                   + "WHERE adr='"+adr+"'");
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE comments "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                         + "parent_type, "
                                                         + "parentID, "
                                                         + "comID, "
                                                         + "mes, "
                                                         + "block), 256) "
                                     + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("comments"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET comments=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM comments)"); 
        
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
            
            // Address
            String adr=row.getString("adr");
            
            // Parent type
            String parent_type=row.getString("parent_type");
            
            // ParentID
            long parentID=row.getLong("parentID");
               
            // Comment ID
            long comID=row.getLong("comID");
               
            // Message
            String mes=row.getString("mes");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         parent_type+
                                         parentID+
                                         comID+
                                         mes+
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
                                   + "FROM comments "
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
               
               // Parent type
               this.addRow("parent_type", rs.getString("parent_type"));
               
               // Parent ID
               this.addRow("parentID", rs.getLong("parentID"));
               
               // Comment ID
               this.addRow("comID", rs.getLong("comID"));
               
               // Message
               this.addRow("mes", rs.getString("mes"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO comments "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "parent_type='"+row.getString("parent_type")+"', "
                                             + "parentID='"+row.getLong("parentID")+"', "
                                             + "comID='"+row.getLong("comID")+"', "
                                             + "mes='"+row.getString("mes")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("comments");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE comments");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "comments.table", crc);
    }
    
    
}   
