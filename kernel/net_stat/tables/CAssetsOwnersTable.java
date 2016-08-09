package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsOwnersTable extends CTable 
{
    public CAssetsOwnersTable()
    {
        super("assets_owners");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets_owners(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                          +"owner VARCHAR(250) DEFAULT '', "
                                                          +"symbol VARCHAR(10) DEFAULT '', "
                                                          +"qty DOUBLE(20,8) DEFAULT 0, "
                                                          +"invested DOUBLE(20,8) DEFAULT 0, "
                                                          +"rowhash VARCHAR(100) DEFAULT '', "
                                                          +"block BIGINT DEFAULT 0)");
             
        UTILS.DB.executeUpdate("CREATE INDEX assets_owners_owner ON assets_owners(owner)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_owners_symbol ON assets_owners(symbol)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_owners_block ON assets_owners(block)");
    }
    
    public void removeByAdr(String adr) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM assets_owners "
                                   + "WHERE owner='"+adr+"'");
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE assets_owners "
                                + "SET rowhash=SHA2(CONCAT(owner, "
                                                         + "symbol, "
                                                         + "qty, "
                                                         + "invested, "
                                                         + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("assets_owners"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET assets_owners=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM assets_owners)"); 
        
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
            
            // Owner
            String owner=row.getString("owner");
            
            // Symbol
            String symbol=row.getString("symbol");
               
            // Qty
            String qty=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("qty")));
               
            // Invested
            String invested=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("invested")));
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(owner+
                                         symbol+
                                         qty+
                                         invested+
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
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM assets_owners ORDER BY ID ASC");
       
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
               
               // Owner
               this.addRow("owner", rs.getString("owner"));
               
               // Symbol
               this.addRow("symbol", rs.getString("symbol"));
               
               // Qty
               this.addRow("qty", UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(rs.getDouble("qty"))));
               
               // Invested
               this.addRow("invested", UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(rs.getDouble("invested"))));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO assets_owners "
                                         + "SET owner='"+row.getString("owner")+"', "
                                             + "symbol='"+row.getString("symbol")+"', "
                                             + "qty='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("qty")))+"', "
                                             + "invested='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("invested")))+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("assets_owners");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets_owners");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "assets_owners.table", crc);
    }
    
    
}



