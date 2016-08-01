package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsBranchesTable extends CTable
{
    public CFeedsBranchesTable()
    {
        super("feeds_branches");
    }
    
    // Create
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds_branches(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                 +"feed_symbol VARCHAR(100) DEFAULT '', "
                                                                 +"symbol VARCHAR(10) DEFAULT '', "
                                                                 +"name VARCHAR(250) DEFAULT '', "
                                                                 +"description VARCHAR(500) DEFAULT '', "
                                                                 +"type VARCHAR(50) DEFAULT '', "
                                                                 +"rl_symbol VARCHAR(20) DEFAULT '', "
                                                                 +"fee DOUBLE(9, 4) DEFAULT 0.0001, "
                                                                 +"expire BIGINT DEFAULT 0, "
                                                                 +"val DOUBLE(20,8) DEFAULT 0, "
                                                                 +"mkt_status VARCHAR(50) DEFAULT '', "
                                                                 +"rowhash VARCHAR(100) DEFAULT '', "
                                                                 +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_feed_symbol ON feeds_branches(feed_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_symbol ON feeds_branches(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_type ON feeds_branches(type)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_rowhash ON feeds_branches(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_block ON feeds_branches(block)");
    }
    
    public void expired(long block) throws Exception
    {
       // Result set
       ResultSet rs;
       
       // Load expired
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM feeds_branches "
                               + "WHERE expire<"+block);
      
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
                                           + "FROM feeds_branches "
                                          + "WHERE ID='"+ID+"'");
        
       
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE feeds_branches "
                                + "SET rowhash=SHA2(CONCAT(feed_symbol, "
                                                        + "symbol, "
                                                        + "name, "
                                                        + "description, "
                                                        + "type, "
                                                        + "rl_symbol, "
                                                        + "fee, "
                                                        + "expire, "
                                                        + "val, "
                                                        + "mkt_status, "
                                                        + "rowhash, "
                                                        + "block), 256) "
                                + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("feeds_branches"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET feeds_branches=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM feeds_branches)"); 
        
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
            
            // Feed symbol
            String feed_symbol=row.getString("feed_symbol");
            
            // Symbol
            String symbol=row.getString("symbol");
            
            // Name
            String name=row.getString("name");
            
            // Description
            String description=row.getString("description");
               
            // Type
            String type=row.getString("type");
               
            // RL Symbol
            String rl_symbol=row.getString("cur");
            
            // Fee
            double fee=row.getDouble("fee");
            
            // Expire
            long expire=row.getLong("expire");
            
            // Val
            double val=row.getDouble("val");
            
            // Market status
            String mkt_status=row.getString("nkt_status");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(feed_symbol+
                                         symbol+
                                         name+                     
                                         description+
                                         type+
                                         rl_symbol+
                                         fee+
                                         expire+
                                         val+
                                         mkt_status+
                                         rowhash);
                    
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
            throw new Exception("Invalid grand hash - CEscrowedTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
      
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds_branches "
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
               
               // Feed_symbol
               this.addRow("feed_symbol", rs.getString("feed_symbol"));

               // Symbol
               this.addRow("symbol", rs.getString("symbol"));

               // Name
               this.addRow("name", rs.getString("name"));

               // Description
               this.addRow("description", rs.getString("description"));

               // Type
               this.addRow("type", rs.getString("type"));

               // Rl_symbol 
               this.addRow("rl_symbol", rs.getString("rl_symbol"));

               // Fee
               this.addRow("fee", rs.getString("fee"));

               // Expire
               this.addRow("expire", rs.getString("expire"));

               // Val
               this.addRow("val", rs.getString("val"));

               // Mkt_status
               this.addRow("mkt_status", rs.getString("mkt_status"));

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
            
            UTILS.DB.executeUpdate("INSERT INTO feeds_branches "
                                         + "SET feed_symbol='"+row.getString("feed_symbol")+"', "
                                             + "symbol='"+row.getString("symbol")+"', "
                                             + "name='"+row.getString("name")+"', "
                                             + "description='"+row.getString("description")+"', "
                                             + "type='"+row.getString("type")+"', "
                                             + "rl_symbol='"+row.getString("rl_symbol")+"', "
                                             + "fee='"+row.getDouble("fee")+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "val='"+row.getDouble("val")+"', "
                                             + "mkt_status='"+row.getString("mkt_status")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("feeds_branches");
    }
    
     public void loadCheckpoint(String hash, String crc) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds_branches");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "feeds_branches.table", crc);
    }
     
     public void drop() throws Exception
     {
       UTILS.DB.executeUpdate("DROP TABLE feeds_branches");
       this.create();
     }    
}
