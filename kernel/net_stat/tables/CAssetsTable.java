package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsTable extends CTable
{
    public CAssetsTable()
    {
        super("assets");
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"adr VARCHAR(250) DEFAULT '', "
                                                             +"symbol VARCHAR(10) DEFAULT '', "
                                                             +"title VARCHAR(250) DEFAULT '', "
                                                             +"description VARCHAR(1000) DEFAULT '', "
                                                             +"how_buy VARCHAR(1000) DEFAULT '', "
                                                             +"how_sell VARCHAR(1000) DEFAULT '', "
                                                             +"web_page VARCHAR(250) DEFAULT '', "
                                                             +"pic VARCHAR(250) DEFAULT '', "
                                                             +"expire BIGINT DEFAULT 0, "
                                                             +"qty BIGINT DEFAULT 0, "
                                                             +"trans_fee_adr VARCHAR(250), "
                                                             +"trans_fee DOUBLE(9,2), "
                                                             +"linked_mktID BIGINT DEFAULT 0, "
                                                             +"rowhash VARCHAR(100) DEFAULT '', "
                                                             +"block BIGINT DEFAULT 0)");
             
        UTILS.DB.executeUpdate("CREATE INDEX assets_adr ON assets(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_symbol ON assets(symbol)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_block ON assets(block)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_linked_mktID ON assets(linked_mktID)");    
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE assets SET rowhash=SHA2(CONCAT(adr, "
                                                                 + "symbol, "
                                                                 + "title, "
                                                                 + "description, "
                                                                 + "how_buy, "
                                                                 + "how_sell, "
                                                                 + "web_page, "
                                                                 + "pic, "
                                                                 + "expire, "
                                                                 + "qty, "
                                                                 + "trans_fee_adr, "
                                                                 + "trans_fee, "
                                                                 + "linked_mktID, "
                                                                 + "rowhash, "
                                                                 + "block), 256) WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE assets "
                                + "SET ads=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                                           + "FROM assets "
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
            
            // Symbol
            String symbol=row.getString("symbol");
               
            // Title
            String title=row.getString("title");
               
            // Description
            String description=row.getString("description");
               
            // How to buy
            String how_buy=row.getString("how_buy");
               
            // How to sell
            String how_sell=row.getString("how_sell");
               
            // Web page
            String web_page=row.getString("web_page");
               
            // Pic
            String pic=row.getString("pic");
            
            // Qty
            long qty=row.getLong("qty");
            
            // Trans fee adr
            String trans_fee_adr=row.getString("trans_fee_adr");
            
            // Trans fee 
            String trans_fee=row.getString("trans_fee");
            
            // Linked mktID
            String linked_mktID=row.getString("linked_mktID");
            
            // Expire
            long expire=row.getLong("expire");
            
            // Block
            long block=row.getLong("block");
            
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr+
                                         symbol+
                                         title+
                                         description+
                                         how_buy+
                                         how_sell+
                                         web_page+
                                         pic+
                                         expire+
                                         qty+
                                         trans_fee_adr+
                                         trans_fee+
                                         linked_mktID+
                                         rowhash+
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
               
               // Symbol
               this.addRow("symbol", rs.getString("symbol"));
               
               // Title
               this.addRow("title", rs.getString("title"));
               
               // Description
               this.addRow("description", rs.getString("description"));
               
               // How buy
               this.addRow("how_buy", rs.getString("how_buy"));
               
               // How sell
               this.addRow("how_sell", rs.getString("how_sell"));
               
               // Web page
               this.addRow("web_page", rs.getString("web_page"));
               
               // Pic
               this.addRow("pic", rs.getString("pic"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
               // Qty
               this.addRow("qty", rs.getLong("qty"));
               
               // Trans fee adr
               this.addRow("trans_fee_adr", rs.getString("trans_fee_adr"));
               
               // Trans fee
               this.addRow("trans_fee", rs.getDouble("trans_fee"));
               
               // Linked mktID
               this.addRow("linked_mktID", rs.getLong("linked_mktID"));
               
               // Rowhash
               this.addRow("rowhash", rs.getString("rowhash"));
               
               // Block
               this.addRow("block", rs.getString("rowhash"));
               
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
