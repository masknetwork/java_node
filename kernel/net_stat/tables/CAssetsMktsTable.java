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
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
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
        
        UTILS.DB.executeUpdate("UPDATE assets_mkts "
                                + "SET ads=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                                           + "FROM assets_mkts "
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
            double last_price=row.getDouble("last_price");
            
            // Ask
            double ask=row.getDouble("ask");
            
            // Bid
            double bid=row.getDouble("bid");
            
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
       ResultSet rs=s.executeQuery("SELECT * FROM ads ORDER BY rowhash ASC");
       
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
               this.addRow("ask", rs.getDouble("ask"));
               
               // Bid
               this.addRow("bid", rs.getDouble("bid"));
               
               // Last price
               this.addRow("last_price", rs.getDouble("last_price"));
               
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
       rs.close();
       s.close();
    }


}
