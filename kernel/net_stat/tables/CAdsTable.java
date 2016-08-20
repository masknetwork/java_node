package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAdsTable extends CTable
{
    public CAdsTable()
    {
        super("ads");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE ads(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                     + "country VARCHAR(2) NOT NULL DEFAULT'', "
                                                     + "adr VARCHAR(250) NOT NULL DEFAULT'', "
				    		     + "title VARCHAR(250) NOT NULL DEFAULT'', "
				    		     + "message VARCHAR(1000) NOT NULL DEFAULT'',"
                                                     + "link VARCHAR(500) NOT NULL DEFAULT'',"
                                                     + "mkt_bid DOUBLE(9,4) NOT NULL DEFAULT 0,"
                                                     + "expire BIGINT NOT NULL DEFAULT 0,"
                                                     + "block BIGINT NOT NULL DEFAULT 0,"
				    		     + "rowhash VARCHAR(100) NOT NULL DEFAULT '')");
				    
	UTILS.DB.executeUpdate("CREATE INDEX ads_adr ON ads(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX ads_rowhash ON ads(rowhash)");
        UTILS.DB.executeUpdate("CREATE INDEX ads_block ON ads(block)");    
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM ads "
                                  + "WHERE expire<="+block);
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE ads SET rowhash=SHA2(CONCAT(country, "
                                                                 + "adr, "
                                                                 + "title, "
                                                                 + "message, "
                                                                 + "link, "
                                                                 + "mkt_bid, "
                                                                 + "expire, "
                                                                 + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("ads"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET ads=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM ads)"); 
        
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
            
            // Address
            String adr=row.getString("adr");
            
            // Country
            String country=row.getString("country");
               
            // Pic back
            String title=row.getString("title");
               
            // Pic
            String message=row.getString("message");
               
            // Description
            String link=row.getString("link");
               
            // Website
            String mkt_bid=UTILS.BASIC.zeros_4(UTILS.FORMAT_4.format(row.getDouble("mkt_bid")));
               
            // Email
            long expire=row.getLong("expire");
               
            // Expire
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(country+
                                         adr+
                                         title+
                                         message+
                                         link+
                                         mkt_bid+
                                         expire+
                                         block);
         
          
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CAdsTable.java " + hash);
            
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
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM ads ORDER BY ID ASC");
       
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
               this.addRow("country", rs.getString("country"));
               
               // Title
               this.addRow("title", rs.getString("title"));
               
               // Message
               this.addRow("message", rs.getString("message"));
               
               // Link
               this.addRow("link", rs.getString("link"));
               
               // Bid
               this.addRow("mkt_bid", rs.getString("mkt_bid"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO ads "
                                         + "SET country='"+row.getString("country")+"', "
                                             + "adr='"+row.getString("adr")+"', "
                                             + "title='"+row.getString("title")+"', "
                                             + "message='"+row.getString("message")+"', "
                                             + "link='"+row.getString("link")+"', "
                                             + "mkt_bid='"+UTILS.BASIC.zeros_4(UTILS.FORMAT_4.format(row.getDouble("mkt_bid")))+"', "
                                             + "expire='"+row.getString("expire")+"', "
                                             + "block='"+row.getString("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("ads");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE ads");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "ads.table", crc);
    }
}
