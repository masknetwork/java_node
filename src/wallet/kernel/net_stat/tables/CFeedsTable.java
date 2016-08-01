package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsTable extends CTable
{
     public CFeedsTable()
    {
        super("feeds");
    }
    
    // Create
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       +"adr VARCHAR(250) DEFAULT '', "
                                                       +"name VARCHAR(100) DEFAULT '', "
                                                       +"description VARCHAR(1000) DEFAULT '', "
                                                       +"website VARCHAR(250) DEFAULT '', "
                                                       +"symbol VARCHAR(10) DEFAULT '', "
                                                       +"expire BIGINT DEFAULT 0, "
                                                       +"branches BIGINT DEFAULT 0, "
                                                       +"rowhash VARCHAR(100) DEFAULT '', "
                                                       +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_adr ON feeds(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_symbol ON feeds(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_rowhash ON feeds(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_block ON feeds(block)");
    }
    
    public void expired(long block, String adr) throws Exception
    {
       // Result set
       ResultSet rs;
       
       // Load expired
       if (adr.equals(""))
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM feeds "
                               + "WHERE expire<"+block);
       else
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM feeds "
                               + "WHERE adr='"+adr+"'");
       
       // Remove
       if (UTILS.DB.hasData(rs))
       {
          while (rs.next())
              this.removeByID(rs.getLong("ID"));
       }
    }
    
    public void removeByID(long ID) throws Exception
    {
        // Load feed
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE ID='"+ID+"'");
        
        // Load all branches
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds_branches "
                                + "WHERE feed_symbol='"+rs.getString("symbol")+"'");
                
        // Close all bets 
        // Close speculative markets
        // Removes all branches
        // Removes all bets
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE feeds "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                        + "name, "
                                                        + "description, "
                                                        + "website, "
                                                        + "symbol, "
                                                        + "expire, "
                                                        + "block), 256) "
                                + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("feeds"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET feeds=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM feeds)"); 
        
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
            
            // Name
            String name=row.getString("name");
            
            // Description
            String description=row.getString("description");
            
            // Website
            String website=row.getString("website");
               
            // Symbol
            String symbol=row.getString("symbol");
               
            // Expire
            long expire=row.getLong("expire");
            
            // Block
            long block=row.getLong("block");
            
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         name+
                                         description+                     
                                         website+
                                         symbol+
                                         expire+
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
            throw new Exception("Invalid grand hash - CFeedsTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
      
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds "
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
               
               // ID
               this.addRow("ID", rs.getString("ID"));

               // Adr
               this.addRow("adr", rs.getString("adr"));

               // Name
               this.addRow("name", rs.getString("name"));

               // Description 
               this.addRow("description", rs.getString("description"));

               // Website
               this.addRow("website", rs.getString("website"));

               // Symbol
               this.addRow("symbol", rs.getString("symbol"));

               // Expire
               this.addRow("expire", rs.getString("expire"));

               // Rowhash
               this.addRow("rowhash", rs.getString("rowhash"));

               // Block
               this.addRow("block", rs.getString("block"));          
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO feeds "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "name='"+row.getString("name")+"', "
                                             + "description='"+row.getString("description")+"', "
                                             + "website='"+row.getString("website")+"', "
                                             + "symbol='"+row.getString("symbol")+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"', "
                                             + "block='"+row.getLong("block")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("feeds");
    }
    
     public void loadCheckpoint(String hash, String crc) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "feeds.table", crc);
    }
     
     public void drop() throws Exception
     {
       UTILS.DB.executeUpdate("DROP TABLE feeds");
       this.create();
     }
}
