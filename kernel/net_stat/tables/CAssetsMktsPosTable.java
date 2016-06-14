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
        // Refresh
        super.refresh(block);
        
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
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET assets_mkts_pos=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                                                       + "FROM assets_mkts_pos "
                                                   + "ORDER BY rowhash ASC)"); 
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
            double qty=row.getDouble("qty");
               
            // Price
            double price=row.getDouble("price");
               
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
        System.out.println(ghash);
        ghash=UTILS.BASIC.hash(ghash);
        System.out.println(ghash);
         
        // Check grand hash
        if (!ghash.equals(crc))
            throw new Exception("Invalid grand hash - CAdsTable.java");
    }
    
    public void fromDB() throws Exception
    {
       int a=0;
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load data
       ResultSet rs=s.executeQuery("SELECT * FROM assets_mkts_pos ORDER BY rowhash ASC");
       
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
               this.addRow("qty", rs.getDouble("qty"));
               
               // Price
               this.addRow("price", rs.getDouble("price"));
               
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
       rs.close();
       s.close();
    }
}

