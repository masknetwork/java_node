package wallet.kernel.net_stat.tables;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAdrTable extends CTable
{
    public CAdrTable()
    {
        // Constructor
        super("adr");
    }
    
    // Create
    public void create(boolean fill) throws Exception
    {
       // Create table
       UTILS.DB.executeUpdate("CREATE TABLE adr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				              + "adr VARCHAR(500), "
				              + "balance DOUBLE(20,8) DEFAULT 0, "
		                              + "created BIGINT DEFAULT 0, "		              
                                              + "block BIGINT DEFAULT 0, "
                                              + "sealed BIGINT DEFAULT 0, "
				              + "rowhash VARCHAR(100) DEFAULT '')");
		   
        UTILS.DB.executeUpdate("CREATE INDEX adr ON adr(adr)");
	UTILS.DB.executeUpdate("CREATE INDEX block ON adr(block)");
	UTILS.DB.executeUpdate("CREATE INDEX rowhash ON adr(rowhash)");
        
        // Fill inital addresses
        if (fill) this.fillTest();    
    }
    
    public void expired(long block) throws Exception
    {
        
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE adr SET rowhash=SHA2(CONCAT(adr, "
                                                                 + "balance, "
                                                                 + "created, "
                                                                 + "sealed, "
                                                                 + "block), 256) where block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET adr=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM adr)");
        
        // Refresh
        super.refresh(block);
        
        // Reload hash
        loadHash();
        
        
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
            
            // Blance
            double balance=row.getDouble("balance");
               
            // Created
            long created=row.getLong("created");
            
            
            // Last interest
            long sealed=row.getLong("sealed");
            
            // Block
            long block=row.getLong("block");
            
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(balance))+
                                         created+
                                         sealed+
                                         block);
                    
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CAdrTable.java "+adr+
                                         UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(balance))+
                                         created+
                                         sealed+
                                         block);
            
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
            throw new Exception("Invalid grand hash - CAdrTable.java");
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
            
            UTILS.DB.executeUpdate("INSERT INTO adr "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "balance='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("balance")))+"', "
                                             + "created='"+row.getLong("created")+"', "
                                             + "sealed='"+row.getLong("sealed")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("adr");
    }
    
    
    
    public void fromDB() throws Exception
    {
        int a=0;
        
        // Constructor
        super.fromDB();
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM adr ORDER BY ID ASC");
       
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
               
               // Balance
               this.addRow("balance", UTILS.FORMAT_8.format(rs.getDouble("balance")));
               
               // Created
               this.addRow("created", rs.getLong("created"));
               
               // Sealed
               this.addRow("sealed", rs.getLong("sealed"));
               
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
    
    
  public void insertAdr(String adr, double balance) throws Exception
    {
        UTILS.DB.executeUpdate("INSERT INTO adr(adr, "
                                             + "balance, "
                                                      + "rowhash, "
                                                      + "block) "
                                          + "VALUES('"+adr+"', "
                                                 + "'"+balance+"', "
                                                   + "'0000000000000000000000000000000000000000000000000000000000000000', "
                                                   + "'0')");
                
                
    }
     
    public void fillTest() throws Exception
    {
        this.insertAdr("default", 99000000);
	this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 1000000);
		   
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE adr");
        
        // Create table
        this.create(false);
        
        // From file
        this.fromFile(hash, "adr.table", crc);
    }
}
