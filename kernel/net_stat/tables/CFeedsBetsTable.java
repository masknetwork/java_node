package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsBetsTable extends CTable
{
     public CFeedsBetsTable()
    {
        super("feeds_bets");
    }
    
    // Create
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds_bets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"betID BIGINT DEFAULT 0, "
                                                            +"adr VARCHAR(250) DEFAULT '', "
                                                            +"feed_1 VARCHAR(10) DEFAULT '', "
                                                            +"branch_1 VARCHAR(10) DEFAULT '', "
                                                            +"feed_2 VARCHAR(10) DEFAULT '', "
                                                            +"branch_2 VARCHAR(10) DEFAULT '', "
                                                            +"feed_3 VARCHAR(10) DEFAULT '', "
                                                            +"branch_3 VARCHAR(10) DEFAULT '', "
                                                             +"last_price DOUBLE(20,8) DEFAULT 0, "
                                                            +"tip VARCHAR(30) DEFAULT 'ID_TOUCH', "
                                                            +"val_1 DOUBLE(20, 8) DEFAULT 0, "
                                                            +"val_2 DOUBLE(20, 8) DEFAULT 0, "
                                                            +"title VARCHAR(150) DEFAULT '', "
                                                            +"description VARCHAR(250) DEFAULT '', "
                                                            +"budget DOUBLE(9,2) DEFAULT 0, "
                                                            +"win_multiplier INT DEFAULT 0, "
                                                            +"start_block BIGINT DEFAULT 0, "
                                                            +"end_block BIGINT DEFAULT 0, "
                                                            +"accept_block BIGINT DEFAULT 0, "
                                                            +"cur VARCHAR(10) DEFAULT '', "
                                                            +"bets BIGINT DEFAULT 0, "
                                                            +"invested DOUBLE(20,8) DEFAULT 0, "
                                                            +"status VARCHAR(10) DEFAULT 'ID_PENDING', "
                                                            +"rowhash VARCHAR(100) DEFAULT '', "
                                                            +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_uid ON feeds_bets(betID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_block ON feeds_bets(block)");
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
        UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                + "SET rowhash=SHA2(CONCAT(betID, "
                                                          + "adr, "
                                                          + "feed_1, branch_1, "
                                                          + "feed_2, branch_2, "
                                                          + "feed_3, branch_3, "
                                                          + "last_price, "
                                                          + "tip, "
                                                          + "val_1, "
                                                          + "val_2,"
                                                          + "title, "
                                                          + "description, "
                                                          + "budget, "
                                                          + "win_multiplier, "
                                                          + "start_block, "
                                                          + "end_block, "
                                                          + "accept_block, "
                                                          + "cur, "
                                                          + "bets, "
                                                          + "invested, "
                                                          + "status, "
                                                          + "rowhash, "
                                                          + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("feeds"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET feeds_bets=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM feeds_bets)"); 
        
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
            
            // MktID
            long betID=row.getLong("betID");

            // Adr
            String adr=row.getString("adr");

            // Feed_1
            String feed_1=row.getString("feed_1");

            // Branch_1
            String branch_1=row.getString("branch_1");

            // Feed_2
            String feed_2=row.getString("feed_2");

            // Branch_2
            String branch_2=row.getString("branch_2");

            // Feed_3
            String feed_3=row.getString("feed_3");

            // Branch_3
            String branch_3=row.getString("branch_3");

            // Last_price
            double last_price=row.getDouble("last_price");

            // Tip
            String tip=row.getString("tip");

            // Val_1
            double val_1=row.getDouble("val_1");

            // Val_2
            double val_2=row.getDouble("val_2");

            // Title
            String title=row.getString("title");

            // Description
            String description=row.getString("description");

            // Budget
            double budget=row.getDouble("budget");

            // Win_multiplier
            double win_multiplier=row.getDouble("win_multiplier");

            // Start_block
            long start_block=row.getLong("start_block");

            // End_block 
            long end_block=row.getLong("end_block");

            // Accept_block  
            long accept_block=row.getLong("accept_block");

            // Cur
            String cur=row.getString("cur");
 
            // Bets
            long bets=row.getLong("bets");

            // Invested
            double invested=row.getDouble("invested");

            // Status
            String status=row.getString("status");

            // Rowhash
            String rowhash=row.getString("rowhash");

            // Block
            long block=row.getLong("block");

            // Hash
            String hash=UTILS.BASIC.hash(betID
                                         +adr
                                         +feed_1
                                         +branch_1
                                         +feed_2
                                         +branch_2
                                         +feed_3
                                         +branch_3
                                         +last_price
                                         +tip
                                         +val_1
                                         +val_2
                                         +title
                                         +description
                                         +budget
                                         +win_multiplier
                                         +start_block
                                         +end_block
                                         +accept_block
                                         +cur
                                         +bets
                                         +invested
                                         +status
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
               
               // MktID
               this.addRow("betID", rs.getLong("betID"));

               // Adr
               this.addRow("adr", rs.getString("adr"));

               // Feed_1
               this.addRow("feed_1", rs.getString("feed_1"));

               // Branch_1
               this.addRow("branch_1", rs.getString("branch_1"));

               // Feed_2
               this.addRow("feed_2", rs.getString("feed_2"));

               // Branch_2
               this.addRow("branch_2", rs.getString("branch_2"));

               // Feed_3
               this.addRow("feed_3", rs.getString("feed_3"));

               // Branch_3
               this.addRow("branch_3", rs.getString("branch_3"));

               // Last_price
               this.addRow("last_price", rs.getString("last_price"));

               // Tip
               this.addRow("tip", rs.getString("tip"));

               // Val_1 
               this.addRow("val_1", rs.getString("val_1"));

               // Val_2
               this.addRow("val_2", rs.getString("val_2"));

               // Title
               this.addRow("title", rs.getString("title"));

               // Description
               this.addRow("description", rs.getString("description"));

               // Budget
               this.addRow("budget", rs.getString("budget"));

               // Win_multiplier
               this.addRow("win_multiplier", rs.getString("win_multiplier"));

               // Start_block
               this.addRow("start_block", rs.getString("start_block"));

               // End_block
               this.addRow("end_block", rs.getString("end_block"));

               // Accept_block
               this.addRow("accept_block", rs.getString("accept_block"));

               // Cur
               this.addRow("cur", rs.getString("cur"));

               // Bets
               this.addRow("bets", rs.getString("bets"));

               // Invested
               this.addRow("invested", rs.getString("invested"));

               // Status
               this.addRow("status", rs.getString("status"));

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
            
            UTILS.DB.executeUpdate("INSERT INTO feeds_bets "
                                         + "SET ID='"+row.getString("ID")+"', "
                                             + "betID='"+row.getLong("betID")+"', "
                                             + "adr='"+row.getString("adr")+"', "
                                             + "feed_1='"+row.getString("feed_1")+"', "
                                             + "branch_1='"+row.getString("branch_1")+"', "
                                             + "feed_2='"+row.getString("feed_2")+"', "
                                             + "branch_2='"+row.getString("branch_2")+"', "
                                             + "feed_3='"+row.getString("feed_3")+"', "
                                             + "branch_3='"+row.getString("branch_3")+"', "
                                             + "last_price='"+row.getString("last_price")+"', "
                                             + "tip='"+row.getString("tip")+"', "
                                             + "val_1='"+row.getString("val_1")+"', "
                                             + "val_2='"+row.getString("val_2")+"', "
                                             + "title='"+row.getString("title")+"', "
                                             + "description='"+row.getString("description")+"', "
                                             + "budget='"+row.getString("budget")+"', "
                                             + "win_multiplier='"+row.getString("win_multiplier")+"', "
                                             + "start_block='"+row.getString("start_block")+"', "
                                             + "end_block='"+row.getString("end_block")+"', "
                                             + "accept_block='"+row.getString("accept_block")+"', "
                                             + "cur='"+row.getString("cur")+"', "
                                             + "bets='"+row.getString("bets")+"', "
                                             + "invested='"+row.getString("invested")+"', "
                                             + "status='"+row.getString("status")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"', "
                                             + "block='"+row.getString("block")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("feeds_bets");
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
