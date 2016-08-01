package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CDomainsTable extends CTable
{
    public CDomainsTable()
    {
        super("domains");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE domains(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 			           + "adr VARCHAR(250) DEFAULT '', "
	 	 		                   + "domain VARCHAR(100), "
	 	 			           + "expire BIGINT DEFAULT 0, "
	 	 			           + "sale_price DOUBLE(10,4) DEFAULT 0, "
	 	 			           + "block BIGINT DEFAULT 0, "
	 	 	 	 	 	   + "rowhash VARCHAR(250) DEFAULT '')");
	 	 	 	 	    
	UTILS.DB.executeUpdate("CREATE INDEX dom_adr ON domains(adr)");
	UTILS.DB.executeUpdate("CREATE INDEX dom_domain ON domains(domain)");
	UTILS.DB.executeUpdate("CREATE INDEX dom_block ON domains(block)");
	UTILS.DB.executeUpdate("CREATE INDEX dom_rowhash ON domains(rowhash)");
    }
    
    public void expired(long block, String adr) throws Exception
    {
       if (adr.equals(""))
          UTILS.DB.executeUpdate("DELETE FROM domains "
                                     + "WHERE expire<="+block);
       else
          UTILS.DB.executeUpdate("DELETE FROM domains "
                                     + "WHERE adr='"+adr+"'");
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE domains "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                        + "domain, "
                                                        + "expire, "
                                                        + "sale_price, "
                                                        + "block), 256) "
                                + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("domains"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET domains=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM domains)"); 
        
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
            
            // Domain
            String domain=row.getString("domain");
               
            // Sale price
            String sale_price=UTILS.BASIC.zeros_4(UTILS.FORMAT_4.format(row.getDouble("sale_price")));
               
            // Expire
            long expire=row.getLong("expire");
            
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         domain+
                                         expire+                     
                                         sale_price+
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
                                   + "FROM domains "
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
               
               // Domain
               this.addRow("domain", rs.getString("domain"));
               
               // Sale price
               this.addRow("sale_price", rs.getString("sale_price"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO domains(adr, "
                                                     + "domain, "
                                                     + "expire, "
                                                     + "sale_price, "
                                                     + "block, "
                                                     + "rowhash"
                                                     + ")  VALUES('"
                                                     +row.getString("adr")+"', '"
                                                     +row.getString("domain")+"', '"
                                                     +row.getLong("expire")+"', '"
                                                     +UTILS.BASIC.zeros_4(UTILS.FORMAT_4.format(row.getDouble("sale_price")))+"', '"
                                                     +row.getLong("block")+"', '"
                                                     +row.getString("rowhash")+"')");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("domains");
    }
    
     public void loadCheckpoint(String hash, String crc) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE domains");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "domains.table", crc);
    }
     
    
}

