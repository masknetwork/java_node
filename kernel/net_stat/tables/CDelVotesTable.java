package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CDelVotesTable extends CTable
{
    public CDelVotesTable()
    {
        super("del_votes");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE del_votes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                    + "delegate VARCHAR(500) DEFAULT '', "
                                                    + "adr VARCHAR(500) DEFAULT '', "
				  	            + "type VARCHAR(25) DEFAULT 'ID_UP', "
				    	            + "block BIGINT DEFAULT 0,"
				     	            + "rowhash VARCHAR(100) DEFAULT '')");
				    
	UTILS.DB.executeUpdate("CREATE INDEX del_votes_delegate ON del_votes(delegate)");
        UTILS.DB.executeUpdate("CREATE INDEX del_votes_adr ON del_votes(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX del_votes_rowhash ON del_votes(rowhash)");
        UTILS.DB.executeUpdate("CREATE INDEX del_votes_block ON del_votes(block)");    
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE del_votes "
                                + "SET rowhash=SHA2(CONCAT(delegate, "
                                                         + "adr, "
                                                         + "type, "
                                                         + "block), 256) "
                              + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("ads"))
        {
            UTILS.DB.executeUpdate("UPDATE del_votes "
                                    + "SET del_votes=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM del_votes)"); 
        
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
            
            // Delegate
            String delegate=row.getString("delegate");
            
            // Address
            String adr=row.getString("adr");
               
            // Type
            String type=row.getString("type");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(delegate+
                                         adr+
                                         type+
                                         block);
         
          
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CDelVotesTable.java " + hash);
            
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
            throw new Exception("Invalid grand hash - CDelVotesTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM del_votes ORDER BY ID ASC");
       
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
               
               // Delegate
               this.addRow("delegate", rs.getString("delegate"));
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
               // Type
               this.addRow("type", rs.getString("type"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                         + "SET delegate='"+row.getString("delegate")+"', "
                                             + "adr='"+row.getString("adr")+"', "
                                             + "type='"+row.getString("type")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("del_votes");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE del_votes");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "del_votes.table", crc);
    }
}
