package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAgentsTable extends CTable
{
    public CAgentsTable()
    {
        super("agents");
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE ads SET rowhash=SHA2(CONCAT(adr, "
                                                                 + "aID, "
                                                                 + "owner, "
                                                                 + "name, "
                                                                 + "description, "
                                                                 + "pay_adr, "
                                                                 + "website, "
                                                                 + "pic, "
                                                                 + "globals, "
                                                                 + "signals, "
                                                                 + "interface, "
                                                                 + "code, "
                                                                 + "status, "
                                                                 + "categ, "
                                                                 + "ver, "
                                                                 + "run_period, "
                                                                 + "sealed, "
                                                                 + "price, "
                                                                 + "storage, "
                                                                 + "expire, "
                                                                 + "dir, "
                                                                 + "block), 256) WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET agents=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                                              + "FROM agents "
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
            
            // Agent ID
            long aID=row.getLong("aID");
            
            // Owner
            String owner=row.getString("owner");
               
            // Name
            String title=row.getString("name");
               
            // Description
            String description=row.getString("description");
               
            // Pay Address
            String pay_adr=row.getString("pay_adr");
               
            // Website
            String website=row.getString("website");
               
            // Pic
            String pic=row.getString("pic");
               
            // Globals
            String globals=row.getString("globals");
               
            // Signals
            String signals=row.getString("signals");
            
            // Interface
            String inter=row.getString("interface");
            
            // Code
            String code=row.getString("code");
            
            // Status
            String status=row.getString("status");
            
           // Categ
            String categ=row.getString("categ");
            
            // Version
            String ver=row.getString("ver");
            
            // Run period
            long run_period=row.getLong("run_period");
            
            // Sealed
            long sealed=row.getLong("sealed");
            
            // Price
            double price=row.getDouble("price");
            
            // Storage
            String storage=row.getString("storage");
            
            // Expire
            long expire=row.getLong("expire");
            
            // Expire
            long dir=row.getLong("expire");
            
            // Block
            long block=row.getLong("block");
            
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(adr
                                         + aID
                                         +owner
                                         +name
                                         +description
                                         +pay_adr
                                         +website
                                         +pic
                                         +globals
                                         +signals
                                         +inter
                                         +code
                                         +status
                                         +categ
                                         +ver
                                         +run_period
                                         +sealed
                                         +price
                                         +storage
                                         +expire
                                         +dir
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
               
               // Agent ID
               this.addRow("aID", rs.getLong("aID"));
               
               // Owner
               this.addRow("owner", rs.getString("owner"));
               
               // Title
               this.addRow("title", rs.getString("title"));
               
               // Description
               this.addRow("description", rs.getString("description"));
               
               // Pay adr
               this.addRow("pay_adr", rs.getString("pay_adr"));
               
               // Website
               this.addRow("website", rs.getString("website"));
               
               // Pic
               this.addRow("pic", rs.getString("pic"));
               
               // Globals
               this.addRow("globals", rs.getString("globals"));
               
               // Signals
               this.addRow("signals", rs.getString("signals"));
               
               // Interface
               this.addRow("interface", rs.getString("interface"));
               
               // Code
               this.addRow("code", rs.getString("code"));
               
               // Status
               this.addRow("status", rs.getString("status"));
               
               // Categ
               this.addRow("categ", rs.getString("categ"));
               
               // Version
               this.addRow("version", rs.getString("version"));
               
               // Run Period
               this.addRow("run_period", rs.getLong("run_period"));
               
               // Seled
               this.addRow("sealed", rs.getLong("sealed"));
               
               // Price
               this.addRow("price", rs.getDouble("price"));
               
               // Storage
               this.addRow("storage", rs.getString("storage"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
               // Dir
               this.addRow("dir", rs.getString("dir"));
               
               // Block
               this.addRow("block", rs.getLong("block"));
               
               // Rowash
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

