package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CProfilesTable extends CTable
{
    public CProfilesTable()
    {
        super("profiles");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE profiles(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                         +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"name VARCHAR(50) NOT NULL DEFAULT '', "
                                                         +"pic_back VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"pic VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"description VARCHAR(500) NOT NULL DEFAULT '', "
                                                         +"website VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"rowhash VARCHAR(100) NOT NULL DEFAULT '', "
                                                         +"email VARCHAR(200) NOT NULL DEFAULT '', "
                                                         +"expire BIGINT NOT NULL DEFAULT '0', "
                                                         +"block BIGINT NOT NULL DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE INDEX prof_adr ON profiles(adr)");
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM profiles "
                                     + "WHERE expire<="+block);
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE profiles SET rowhash=SHA2(CONCAT(adr, "
                                                                 + "name, "
                                                                 + "pic_back, "
                                                                 + "pic, "
                                                                 + "description, "
                                                                 + "website, "
                                                                 + "email, "
                                                                 + "block, "
                                                                 + "expire), 256) where block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("profiles"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET profiles=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM profiles)"); 
        
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
            
            // Name
            String name=row.getString("name");
               
            // Pic back
            String pic_back=row.getString("pic_back");
               
            // Pic
            String pic=row.getString("pic");
               
            // Description
            String description=row.getString("description");
               
            // Website
            String website=row.getString("website");
               
            // Email
            String email=row.getString("email");
               
            // Expire
            long expire=row.getLong("expire");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         name+
                                         pic_back+
                                         pic+
                                         description+
                                         website+
                                         email+
                                         block+
                                         expire);
                    
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CProfilesTable.java");
            
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
            throw new Exception("Invalid grand hash - CProfilesTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
       
       
       // Statement
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM profiles ORDER BY ID ASC");
       
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
               
               // Name
               this.addRow("name", rs.getString("name"));
               
               // Pic back
               this.addRow("pic_back", rs.getString("pic_back"));
               
               // Pic
               this.addRow("pic", rs.getString("pic"));
               
               // Description
               this.addRow("description", rs.getString("description"));
               
               // Website
               this.addRow("website", rs.getString("website"));
               
               // Email
               this.addRow("email", rs.getString("email"));
               
               // Wxpire
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
            
            UTILS.DB.executeUpdate("INSERT INTO profiles "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "name='"+row.getString("name")+"', "
                                             + "pic_back='"+row.getString("pic_back")+"', "
                                             + "pic='"+row.getString("pic")+"', "
                                             + "description='"+row.getString("description")+"', "
                                             + "website='"+row.getString("website")+"', "
                                             + "email='"+row.getString("email")+"', "
                                             + "expire='"+row.getString("expire")+"', "
                                             + "block='"+row.getString("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("profiles");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE profiles");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "profiles.table", crc);
    }
    
    public void drop() throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE profiles");
        
        // Create table
        this.create(); 
    }
}
