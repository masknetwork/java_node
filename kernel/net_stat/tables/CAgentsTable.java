package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAgentsTable extends CTable
{
    public CAgentsTable()
    {
        super("agents");
    }
    
    public void expired(long block, String adr) throws Exception
    {
       if (adr.equals(""))
          UTILS.DB.executeUpdate("DELETE FROM agents "
                                     + "WHERE expire<="+block);
       else
          UTILS.DB.executeUpdate("DELETE FROM agents "
                                     + "WHERE adr='"+adr+"'");
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE agents(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                              +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"owner VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"name VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"description VARCHAR(1000) NOT NULL DEFAULT '', "
                                                              +"pay_adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"website VARCHAR(1000) NOT NULL DEFAULT '', "
                                                              +"pic VARCHAR(1000) NOT NULL DEFAULT '', "
                                                              +"globals LONGTEXT NOT NULL, "
                                                              +"signals LONGTEXT NOT NULL, "
                                                              +"interface LONGTEXT NOT NULL, "
                                                              +"code LONGTEXT NOT NULL, "
                                                              +"status VARCHAR(50) NOT NULL DEFAULT 'ID_ONLINE', "
                                                              +"exec_log LONGTEXT, "
                                                              +"categ VARCHAR(50) NOT NULL DEFAULT '', "
                                                              +"ver VARCHAR(50) NOT NULL DEFAULT '', "
                                                              +"run_period BIGINT NOT NULL DEFAULT 0, "
                                                              +"sealed BIGINT NOT NULL DEFAULT 0, "
                                                              +"price FLOAT(9,4) NOT NULL DEFAULT 0, "
                                                              +"storage LONGTEXT NOT NULL, "
                                                              +"expire BIGINT NOT NULL DEFAULT 0, "
                                                              +"aID BIGINT NOT NULL DEFAULT 0, "
                                                              +"dir BIGINT NOT NULL DEFAULT 0, "
                                                              +"block BIGINT NOT NULL DEFAULT 0, "
                                                              +"rowhash VARCHAR(100) NOT NULL DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX agents_adr ON agents(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX agents_name ON agents(name)");
             UTILS.DB.executeUpdate("CREATE INDEX agents_block ON agents(block)");
             UTILS.DB.executeUpdate("CREATE INDEX agents_rowhash ON agents(rowhash)");    
    }
    
    public void refresh(long block) throws Exception
    {
        // Agents
        UTILS.DB.executeUpdate("UPDATE agents SET rowhash=SHA2(CONCAT(adr, "
                                                                 + "aID, "
                                                                 + "owner, "
                                                                 + "name, "
                                                                 + "description, "
                                                                 + "pay_adr, "
                                                                 + "website, "
                                                                 + "pic, "
                                                                 + "globals, "
                                                                 + "signals, "
                                                                 + "interface, "
                                                                 + "code, "
                                                                 + "status, "
                                                                 + "categ, "
                                                                 + "ver, "
                                                                 + "run_period, "
                                                                 + "price, "
                                                                 + "storage, "
                                                                 + "expire, "
                                                                 + "dir, "
                                                                 + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("agents"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET agents=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM agents)"); 
        
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
            
            // Agent ID
            long aID=row.getLong("aID");
            
            // Owner
            String owner=row.getString("owner");
               
            // Name
            String title=row.getString("name");
               
            // Description
            String description=row.getString("description");
               
            // Pay Address
            String pay_adr=row.getString("pay_adr");
               
            // Website
            String website=row.getString("website");
               
            // Pic
            String pic=row.getString("pic");
               
            // Globals
            String globals=row.getString("globals");
               
            // Signals
            String signals=row.getString("signals");
            
            // Interface
            String inter=row.getString("interface");
            
            // Code
            String code=row.getString("code");
            
            // Status
            String status=row.getString("status");
            
           // Categ
            String categ=row.getString("categ");
            
            // Version
            String ver=row.getString("ver");
            
            // Run period
            long run_period=row.getLong("run_period");
            
            // Price
            String price=UTILS.BASIC.zeros_4(UTILS.FORMAT_4.format(row.getDouble("price")));
            
            // Storage
            String storage=row.getString("storage");
            
            // Expire
            long expire=row.getLong("expire");
            
            // Expire
            long dir=row.getLong("dir");
            
            // Block
            long block=row.getLong("block");
            
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr
                                         + aID
                                         +owner
                                         +title
                                         +description
                                         +pay_adr
                                         +website
                                         +pic
                                         +globals
                                         +signals
                                         +inter
                                         +code
                                         +status
                                         +categ
                                         +ver
                                         +run_period
                                         +price
                                         +storage
                                         +expire
                                         +dir
                                         +block); 
          
                    
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
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
       
       
       // Parent
       super.fromDB();
       
       // Statement
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM agents ORDER BY ID ASC");
       
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
               
               // Agent ID
               this.addRow("aID", rs.getLong("aID"));
               
               // Owner
               this.addRow("owner", rs.getString("owner"));
               
               // Name
               this.addRow("name", rs.getString("name"));
               
               // Description
               this.addRow("description", rs.getString("description"));
               
               // Pay adr
               this.addRow("pay_adr", rs.getString("pay_adr"));
               
               // Website
               this.addRow("website", rs.getString("website"));
               
               // Pic
               this.addRow("pic", rs.getString("pic"));
               
               // Globals
               this.addRow("globals", rs.getString("globals"));
               
               // Signals
               this.addRow("signals", rs.getString("signals"));
               
               // Interface
               this.addRow("interface", rs.getString("interface"));
               
               // Code
               this.addRow("code", rs.getString("code"));
               
               // Status
               this.addRow("status", rs.getString("status"));
               
               // Categ
               this.addRow("categ", rs.getString("categ"));
               
               // Version
               this.addRow("ver", rs.getString("ver"));
               
               // Run Period
               this.addRow("run_period", rs.getLong("run_period"));
               
               // Price
               this.addRow("price", rs.getDouble("price"));
               
               // Storage
               this.addRow("storage", rs.getString("storage"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
               // Dir
               this.addRow("dir", rs.getLong("dir"));
               
               // Block
               this.addRow("block", rs.getLong("block"));
               
               // Rowash
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
            
            UTILS.DB.executeUpdate("INSERT INTO agents SET adr='"+row.getString("adr")+"', "
                                                         + "owner='"+row.getString("owner")+"', "
                                                         + "name='"+row.getString("name")+"', "
                                                         + "description='"+row.getString("description")+"', "
                                                         + "pay_adr='"+row.getString("pay_adr")+"', "
                                                         + "website='"+row.getString("website")+"', "
                                                         + "pic='"+row.getString("pic")+"', "
                                                         + "globals='"+row.getString("globals")+"', "
                                                         + "signals='"+row.getString("signals")+"', "
                                                         + "interface='"+row.getString("interface")+"', "
                                                         + "code='"+row.getString("code")+"', "
                                                         + "status='"+row.getString("status")+"', "
                                                         + "exec_log='', "
                                                         + "categ='"+row.getString("categ")+"', "
                                                         + "ver='"+row.getString("ver")+"', "
                                                         + "run_period='"+row.getLong("run_period")+"', "
                                                         + "price='"+UTILS.BASIC.zeros(UTILS.FORMAT_4.format(row.getDouble("price")))+"', "
                                                         + "storage='"+row.getString("storage")+"', "
                                                         + "expire='"+row.getLong("expire")+"', "
                                                         + "aID='"+row.getLong("aID")+"', "
                                                         + "dir='"+row.getLong("dir")+"', "
                                                         + "block='"+row.getLong("block")+"', "
                                                         + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("agents");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE agents");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "agents.table", crc);
    }
    
    
}


