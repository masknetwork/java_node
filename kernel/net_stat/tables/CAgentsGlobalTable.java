package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAgentsGlobalTable extends CTable
{
    public CAgentsGlobalTable()
    {
        super("agents_globals");
    }
    
     // Address
    public void refresh(long block) throws Exception
    {
        // Refresh
        super.refresh(block);
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE ads SET rowhash=SHA2(CONCAT(varID, "
                                                                 + "appID, "
                                                                 + "name, "
                                                                 + "data_type, "
                                                                 + "expl, "
                                                                 + "min, "
                                                                 + "max, "
                                                                 + "val), 256) WHERE block='"+block+"'");
        
        UTILS.DB.executeUpdate("UPDATE net_stat "
                                + "SET agents_globals=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st "
                                                      + "FROM agents_globals "
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
            
            // Variable ID
            long varID=row.getLong("varID");
            
            // App ID
            long appID=row.getLong("appID");
               
            // Name
            String name=row.getString("name");
               
            // Data Type
            String data_type=row.getString("data_type");
               
            // Description
            String expl=row.getString("expl");
               
            // Min
            double min=row.getDouble("min");
               
            // Max
            double max=row.getDouble("max");
               
            // Value
            double val=row.getDouble("val");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(varID+
                                         appID+
                                         name+
                                         data_type+
                                         expl+
                                         min+
                                         max+
                                         val);
                    
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
       ResultSet rs=s.executeQuery("SELECT * FROM agents_globals ORDER BY rowhash ASC");
       
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
               
               // Var ID
               this.addRow("varID", rs.getLong("varID"));
               
               // App ID
               this.addRow("appID", rs.getLong("appID"));
               
               // Name
               this.addRow("name", rs.getString("name"));
               
               // Data Type
               this.addRow("data_type", rs.getString("data_type"));
               
               // Expl
               this.addRow("expl", rs.getString("expl"));
               
               // Min
               this.addRow("min", rs.getDouble("min"));
               
               // Max
               this.addRow("max", rs.getDouble("max"));
               
               // Val
               this.addRow("val", rs.getDouble("val"));
               
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
