package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsBetsPosTable extends CTable
{
    public CFeedsBetsPosTable()
    {
        super("feeds_bets_pos");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE feeds_bets_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                               +"bet_uid BIGINT DEFAULT 0, "
                                                               +"adr VARCHAR(250) DEFAULT '', "
                                                               +"amount DOUBLE(9,4) DEFAULT 0, "
                                                               +"block BIGINT DEFAULT 0, "
                                                               +"rowhash VARCHAR(100) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_uid ON feeds_bets_pos(bet_uid)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_adr ON feeds_bets_pos(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_block ON feeds_bets_pos(block)");
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
        UTILS.DB.executeUpdate("UPDATE feeds_bets_pos "
                                + "SET rowhash=SHA2(CONCAT(bet_uid, "
                                                         + "adr, "
                                                         + "amount, "
                                                         + "block, "
                                                         + "rowhash), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("feeds"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET feeds_bets_pos=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM feeds_bets_pos)"); 
        
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

            // Bet_uid
            long bet_uid=row.getLong("bet_uid");

            // Adr
            String adr=row.getString("adr");

            // Amount
            double amount=row.getDouble("amount");

            // Block
            long block=row.getLong("block");

            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(bet_uid
                                         +adr
                                         +amount
                                         +block
                                         +rowhash);
                    
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CFeedsBetsPosTable.java");
            
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
            throw new Exception("Invalid grand hash - CFeedsBetsPosTable.java");
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

               // Bet_uid 
               this.addRow("bet_uid", rs.getString("bet_uid"));

               // Adr
               this.addRow("adr", rs.getString("adr"));

               // Amount
               this.addRow("amount", rs.getString("amount"));

               // Block
               this.addRow("block", rs.getString("block"));
 
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
            
            UTILS.DB.executeUpdate("INSERT INTO feeds_bets "
                                         + "SET ID='"+row.getString("ID")+"', "
                                             + "bet_uid='"+row.getString("bet_uid")+"', " 
                                             + "adr='"+row.getString("adr")+"', "
                                             + "amount='"+row.getString("amount")+"', "
                                             + "block='"+row.getString("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
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
