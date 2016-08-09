package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsMktsPosTable extends CTable
{
    public CAssetsMktsPosTable()
    {
        super("assets_mkts_pos");
    }
    
    public void expired(long block, String adr) throws Exception
    {
        // Statement
        
        
        // Result
        ResultSet rs;
        
        // Load expired orders
        if (adr.equals(""))
            rs=UTILS.DB.executeQuery("SELECT am.asset, "
                                    + "am.cur, "
                                    + "amp.* "
                               + "FROM assets_mkts_pos AS amp "
                               + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                              + "WHERE amp.expire<="+block);
        else
          rs=UTILS.DB.executeQuery("SELECT am.asset, "
                                  + "am.cur, "
                                  + "amp.* "
                             + "FROM assets_mkts_pos AS amp "
                             + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                            + "WHERE amp.adr='"+adr+"'");
        
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
        }
        
        // Close
        
        
        // Removes
        if (adr.equals(""))
            UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                   + "WHERE expire<="+block);
        else
            UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                   + "WHERE adr='"+block+"'");
    }
    
    // Create
    public void create() throws Exception
    {
       UTILS.DB.executeUpdate("CREATE TABLE assets_mkts_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 				              + "adr VARCHAR(250) DEFAULT '', "
	 	 				              + "mktID BIGINT DEFAULT 0, "
	 	 				              + "tip VARCHAR(10) DEFAULT '', "
	 	 				              + "qty DOUBLE(20, 8) DEFAULT 0, "
	 	 				              + "price DOUBLE(20, 8) DEFAULT 0, "
	 	 				              + "block BIGINT DEFAULT 0, "
                                                              + "orderID BIGINT DEFAULT 0, "
	 	 				              + "rowhash VARCHAR(100) DEFAULT '', "
	 	 				              + "expire BIGINT DEFAULT 0)");
	 	 	   
	    UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_pos_adr ON assets_mkts_pos(adr)");
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_pos_block ON assets_mkts_pos(block)");    
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE assets_mkts_pos "
                                + "SET rowhash=SHA2(CONCAT(adr, "
                                                        + "mktID, "
                                                        + "tip, "
                                                        + "qty, "
                                                        + "price, "
                                                        + "orderID, "
                                                        + "block, "
                                                        + "expire), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("assets_mkts_pos"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET assets_mkts_pos=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM assets_mkts_pos)"); 
        
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
            
            // Mkt ID
            long mktID=row.getLong("mktID");
               
            // Tip
            String tip=row.getString("tip");
               
            // Qty
            String qty=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("qty")));
               
            // Price
            String price=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("price")));
               
            // OrderID
            long orderID=row.getLong("orderID");
               
            // Block
            long block=row.getLong("block");
               
            // Expire
            long expire=row.getLong("expire");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         mktID+
                                         tip+
                                         qty+
                                         price+
                                         orderID+
                                         block+
                                         expire);
                    
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
                                   + "FROM assets_mkts_pos "
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
               
               
               // Market ID
               this.addRow("mktID", rs.getLong("mktID"));
               
               // Orderd ID
               this.addRow("orderID", rs.getLong("orderID"));
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
               // Tip
               this.addRow("tip", rs.getString("tip"));
               
               // Qty
               this.addRow("qty", UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(rs.getDouble("qty"))));
               
               // Price
               this.addRow("price", UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(rs.getDouble("price"))));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO assets_mkts_pos "
                                         + "SET adr='"+row.getString("adr")+"', "
                                             + "mktID='"+row.getLong("mktID")+"', "
                                             + "tip='"+row.getString("tip")+"', "
                                             + "qty='"+row.getDouble("qty")+"', "
                                             + "price='"+row.getDouble("price")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "orderID='"+row.getLong("orderID")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"', "
                                             + "expire='"+row.getLong("expire")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("assets_mkts_pos");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets_mkts_pos");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "assets_mkts_pos.table", crc);
    }
    
}

