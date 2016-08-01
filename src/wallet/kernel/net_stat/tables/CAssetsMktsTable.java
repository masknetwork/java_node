package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsMktsTable extends CTable
{
    public CAssetsMktsTable()
    {
        super("assets_mkts");
    }
    
    public void expired(long block) throws Exception
    {
        // Statement
        
        
        // Result
        ResultSet rs;
        
        // Load  markets
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM assets_mkts_pos AS amp "
                          + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                         + "WHERE am.expire<="+block);
        
        while (rs.next())
          this.removeByID(rs.getLong("mktID"));
        
        // Close
        
    }
    
    public void removeByAdr(String adr) throws Exception
    {
        // Statement
        
        
        // Result
        ResultSet rs;
        
        // Load  markets
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM assets_mkts_pos AS amp "
                          + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                         + "WHERE am.adr='"+adr+"'");
        
        while (rs.next())
          this.removeByID(rs.getLong("mktID"));
        
        // Close
        
    }
    
    public void removeByAsset(String symbol) throws Exception
    {
        // Statement
        
        
        // Result
        ResultSet rs;
        
        // Load  markets
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM assets_mkts_pos AS amp "
                              + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                             + "WHERE am.asset='"+symbol+"' "
                                + "OR am.cur='"+symbol+"'");
        
        while (rs.next())
          this.removeByID(rs.getLong("mktID"));
        
        // Close
        
    }
    
    public void removeByID(long mktID) throws Exception
    {
        // Statement
        
        
        ResultSet rs=UTILS.DB.executeQuery("SELECT am.asset, "
                                         + "am.cur, "
                                         + "amp.* "
                                    + "FROM assets_mkts_pos AS amp "
                                    + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                                   + "WHERE am.expire='"+mktID+"'");
        
        // Removes and refund orders
        while (rs.next())
        {
            // Market asset
            String asset=rs.getString("asset");
        
            // Market currency
            String cur=rs.getString("cur");
        
            // Order type
            String type=rs.getString("tip");
        
            // Order qty
            double qty=rs.getDouble("qty");
        
            // Order price
            double price=rs.getDouble("price");
        
            // Order owner
            String owner=rs.getString("owner");
            
            // Order ID
            long orderID=rs.getLong("orderID");
        
            // Refund
            if (type.equals("ID_BUY"))
            {
               if (cur.equals("MSK"))
                   UTILS.DB.executeUpdate("UPDATE adr "
                                           + "SET balance=balance+"+UTILS.FORMAT_8.format(qty*price)+" "
                                         + "WHERE adr='"+owner+"'");
               else
                   UTILS.DB.executeUpdate("UPDATE assets_owners "
                                           + "SET qty=qty+"+UTILS.FORMAT_8.format(qty*price)+" "
                                         + "WHERE owner='"+owner+"' "
                                           + "AND symbol='"+asset+"'");
            }
            else
            {
                UTILS.DB.executeUpdate("UPDATE assets_owners "
                                       + "SET qty=qty+"+UTILS.FORMAT_8.format(qty)+" "
                                     + "WHERE owner='"+owner+"' "
                                       + "AND symbol='"+asset+"'");
            }
            
            // Remove order
            UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                       + "WHERE orderID<"+orderID);
        }
        
        // Removes markets
        UTILS.DB.executeUpdate("DELETE FROM assets_mkts "
                                   + "WHERE mktID='"+mktID+"'");
       
        // Close
        
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets_mkts(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 				          + "adr VARCHAR(250) DEFAULT '', "
	 	 				          + "asset VARCHAR(10) DEFAULT '', "
	 	 				          + "cur VARCHAR(10) DEFAULT '', "
	 	 				          + "name VARCHAR(500) DEFAULT '', "
	 	 				          + "description VARCHAR(2500) DEFAULT '', "
	 	 				          + "decimals BIGINT DEFAULT 0, "
	 	 				          + "block BIGINT DEFAULT 0, "
	 	 				          + "expire BIGINT DEFAULT 0, "
                                                          + "last_price DOUBLE(20,8) DEFAULT 0, "
                                                          + "ask DOUBLE(20,8) DEFAULT 0, "
                                                          + "bid DOUBLE(20,8) DEFAULT 0, "
	 	 				          + "rowhash VARCHAR(100) DEFAULT '', "
	 	 				          + "mktID BIGINT DEFAULT 0)");
	 	 	   
	UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_block ON assets_mkts(block)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_rowhash ON assets_mkts(rowhash)");
    }
    
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE assets_mkts SET rowhash=SHA2(CONCAT(adr, "
                                                                        + "asset, "
                                                                        + "cur, "
                                                                        + "name, "
                                                                        + "description, "
                                                                        + "decimals, "
                                                                        + "last_price, "
                                                                        + "ask, "
                                                                        + "bid, "
                                                                        + "mktID, "
                                                                        + "expire, "
                                                                        + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("assets_mkts"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET assets_mkts=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM assets_mkts)"); 
        
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
            
            // Asset
            String asset=row.getString("asset");
               
            // Currency
            String cur=row.getString("cur");
               
            // Name
            String name=row.getString("name");
               
            // Description
            String description=row.getString("description");
               
            // Decimals
            long decimals=row.getLong("decimals");
               
            // Last price
            String last_price=UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("last_price")));
            
            // Ask
            String ask=UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("ask")));
            
            // Bid
            String bid=UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("bid")));
            
            // MktID
            long mktID=row.getLong("mktID");
               
            // Block
            long block=row.getLong("block");
            
            // Expire
            long expire=row.getLong("expire");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         asset+
                                         cur+
                                         name+
                                         description+
                                         decimals+
                                         last_price+
                                         ask+
                                         bid+
                                         mktID+
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
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM assets_mkts ORDER BY ID ASC");
       
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
               
               // Asset
               this.addRow("asset", rs.getString("asset"));
               
               // Cur
               this.addRow("cur", rs.getString("cur"));
               
               // Name
               this.addRow("name", rs.getString("name"));
               
               // Description
               this.addRow("description", rs.getString("description"));
               
               // Decimals
               this.addRow("decimals", rs.getLong("decimals"));
               
               // Ask
               this.addRow("ask", UTILS.BASIC.zeros(UTILS.FORMAT_8.format(rs.getDouble("ask"))));
               
               // Bid
               this.addRow("bid", UTILS.BASIC.zeros(UTILS.FORMAT_8.format(rs.getDouble("bid"))));
               
               // Last price
               this.addRow("last_price", UTILS.BASIC.zeros(UTILS.FORMAT_8.format(rs.getDouble("last_price"))));
               
               // Mkt ID
               this.addRow("mktID", rs.getLong("mktID"));
               
               // Block
               this.addRow("block", rs.getLong("block"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO assets_mkts "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "asset='"+row.getString("asset")+"', "
                                             + "cur='"+row.getString("cur")+"', "
                                             + "name='"+row.getString("name")+"', "
                                             + "description='"+row.getString("description")+"', "
                                             + "decimals='"+row.getLong("decimals")+"', "
                                             + "last_price='"+UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("last_price")))+"', "
                                             + "ask='"+UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("ask")))+"', "
                                             + "bid='"+UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("bid")))+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "mktID='"+row.getLong("mktID")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("assets_mkts");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets_mkts");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "assets_mkts.table", crc);
    }
    
    
}
