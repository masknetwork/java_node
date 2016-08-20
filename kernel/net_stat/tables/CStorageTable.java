package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CStorageTable extends CTable
{
    public CStorageTable()
    {
        super("storage");
    }
    
    // Expired
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM storage WHERE expire<"+block);
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE storage(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                     + "aID BIGINT NOT NULL DEFAULT 0, "
                                                     + "tab VARCHAR(50) NOT NULL DEFAULT '', "
				    		     + "s1 VARCHAR(1000) NOT NULL DEFAULT '', "
				    		     + "s2 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s3 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s4 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s5 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s6 VARCHAR(1000) NOT NULL DEFAULT '', "
				    		     + "s7 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s8 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s9 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s10 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s11 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s12 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s13 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s14 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s15 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s16 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s17 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s18 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s19 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "s20 VARCHAR(1000) NOT NULL DEFAULT '',"
                                                     + "d1 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d2 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d3 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d4 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d5 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d6 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d7 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d8 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d9 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d10 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d11 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d12 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d13 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d14 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d15 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d16 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d17 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d18 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d19 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "d20 FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "expire BIGINT NOT NULL DEFAULT 0,"
                                                     + "block BIGINT NOT NULL DEFAULT 0,"
				    		     + "rowhash VARCHAR(100) NOT NULL DEFAULT '')");
				    
	UTILS.DB.executeUpdate("CREATE INDEX storage_aID ON storage(aID)");
        UTILS.DB.executeUpdate("CREATE INDEX storage_rowhash ON storage(rowhash)");
        UTILS.DB.executeUpdate("CREATE INDEX storage_block ON storage(block)");    
    }
    
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Storage
        UTILS.DB.executeUpdate("UPDATE storage "
                                + "SET rowhash=SHA2(CONCAT(aID, "
                                                        + "tab, "
                                                        + "s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20,"
                                                        + "d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14, d15, d16, d17, d18, d19, d20,"
                                                        + "expire, block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("storage"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET storage=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM storage)"); 
        
            // Refresh
            super.refresh(block);
        
            // Reload hash
            loadHash();
        }
    }
    
    public void fromJSON(String data, String crc) throws Exception
    {
        // No data
        if (crc.equals("")) return;
        
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
            
            // AID
            long aID=row.getLong("aID");
            
            // Table
            String tab=row.getString("tab");
               
            // S1....S20
            String s1=row.getString("s1");
            String s2=row.getString("s2");
            String s3=row.getString("s3");
            String s4=row.getString("s4");
            String s5=row.getString("s5");
            String s6=row.getString("s6");
            String s7=row.getString("s7");
            String s8=row.getString("s8");
            String s9=row.getString("s9");
            String s10=row.getString("s10");
            String s11=row.getString("s11");
            String s12=row.getString("s12");
            String s13=row.getString("s13");
            String s14=row.getString("s14");
            String s15=row.getString("s15");
            String s16=row.getString("s16");
            String s17=row.getString("s17");
            String s18=row.getString("s18");
            String s19=row.getString("s19");
            String s20=row.getString("s20");
            
            // D1....D20
            String d1=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d1")));
            String d2=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d2")));
            String d3=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d3")));
            String d4=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d4")));
            String d5=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d5")));
            String d6=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d6")));
            String d7=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d7")));
            String d8=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d8")));
            String d9=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d9")));
            String d10=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d10")));
            String d11=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d11")));
            String d12=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d12")));
            String d13=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d13")));
            String d14=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d14")));
            String d15=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d15")));
            String d16=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d16")));
            String d17=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d17")));
            String d18=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d18")));
            String d19=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d19")));
            String d20=UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d20")));
            
            // Expire
            long expire=row.getLong("expire");
            
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(aID+
                                         tab+
                                         s1+s2+s3+s4+s5+s6+s7+s8+s9+s10+s11+s12+s13+s14+s15+s16+s17+s18+s19+s20+
                                         d1+d2+d3+d4+d5+d6+d7+d8+d9+d10+d11+d12+d13+d14+d15+d16+d17+d18+d19+d20+
                                         expire+block);
         
          
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CStorageTable.java " + hash);
            
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
            throw new Exception("Invalid grand hash - CStorageTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM storage ORDER BY ID ASC");
       
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
               
               // AID
               this.addRow("aID", rs.getString("aID"));
               
               // Tab
               this.addRow("tab", rs.getString("tab"));
               
               // S1...S20
               this.addRow("s1", rs.getString("s1"));
               this.addRow("s2", rs.getString("s2"));
               this.addRow("s3", rs.getString("s3"));
               this.addRow("s4", rs.getString("s4"));
               this.addRow("s5", rs.getString("s5"));
               this.addRow("s6", rs.getString("s6"));
               this.addRow("s7", rs.getString("s7"));
               this.addRow("s8", rs.getString("s8"));
               this.addRow("s9", rs.getString("s9"));
               this.addRow("s10", rs.getString("s10"));
               this.addRow("s11", rs.getString("s11"));
               this.addRow("s12", rs.getString("s12"));
               this.addRow("s13", rs.getString("s13"));
               this.addRow("s14", rs.getString("s14"));
               this.addRow("s15", rs.getString("s15"));
               this.addRow("s16", rs.getString("s16"));
               this.addRow("s17", rs.getString("s17"));
               this.addRow("s18", rs.getString("s18"));
               this.addRow("s19", rs.getString("s19"));
               this.addRow("s20", rs.getString("s20"));
               
               // D1...D20
               this.addRow("d1", rs.getDouble("d1"));
               this.addRow("d2", rs.getDouble("d2"));
               this.addRow("d3", rs.getDouble("d3"));
               this.addRow("d4", rs.getDouble("d4"));
               this.addRow("d5", rs.getDouble("d5"));
               this.addRow("d6", rs.getDouble("d6"));
               this.addRow("d7", rs.getDouble("d7"));
               this.addRow("d8", rs.getDouble("d8"));
               this.addRow("d9", rs.getDouble("d9"));
               this.addRow("d10", rs.getDouble("d10"));
               this.addRow("d11", rs.getDouble("d11"));
               this.addRow("d12", rs.getDouble("d12"));
               this.addRow("d13", rs.getDouble("d13"));
               this.addRow("d14", rs.getDouble("d14"));
               this.addRow("d15", rs.getDouble("d15"));
               this.addRow("d16", rs.getDouble("d16"));
               this.addRow("d17", rs.getDouble("d17"));
               this.addRow("d18", rs.getDouble("d18"));
               this.addRow("d19", rs.getDouble("d19"));
               this.addRow("d20", rs.getDouble("d20"));
               
               // Expire
               this.addRow("expire", rs.getLong("expire"));
               
               // Block
               this.addRow("block", rs.getLong("block"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO storage "
                                         + "SET aID='"+row.getLong("aID")+"', "
                                             + "tab='"+row.getString("tab")+"', "
                                             + "s1='"+row.getString("s1")+"', "
                                             + "s2='"+row.getString("s2")+"', "
                                             + "s3='"+row.getString("s3")+"', "
                                             + "s4='"+row.getString("s4")+"', "
                                             + "s5='"+row.getString("s5")+"', "
                                             + "s6='"+row.getString("s6")+"', "
                                             + "s7='"+row.getString("s7")+"', "
                                             + "s8='"+row.getString("s8")+"', "
                                             + "s9='"+row.getString("s9")+"', "
                                             + "s10='"+row.getString("s10")+"', "
                                             + "s11='"+row.getString("s11")+"', "
                                             + "s12='"+row.getString("s12")+"', "
                                             + "s13='"+row.getString("s13")+"', "
                                             + "s14='"+row.getString("s14")+"', "
                                             + "s15='"+row.getString("s15")+"', "
                                             + "s16='"+row.getString("s16")+"', "
                                             + "s17='"+row.getString("s17")+"', "
                                             + "s18='"+row.getString("s18")+"', "
                                             + "s19='"+row.getString("s19")+"', "
                                             + "s20='"+row.getString("s20")+"', "
                                             + "d1='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d1")))+"', "
                                             + "d2='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d2")))+"', "
                                             + "d3='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d3")))+"', "
                                             + "d4='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d4")))+"', "
                                             + "d5='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d5")))+"', "
                                             + "d6='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d6")))+"', "
                                             + "d7='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d7")))+"', "
                                             + "d8='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d8")))+"', "
                                             + "d9='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d9")))+"', "
                                             + "d10='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d10")))+"', "
                                             + "d11='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d11")))+"', "
                                             + "d12='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d12")))+"', "
                                             + "d13='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d13")))+"', "
                                             + "d14='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d14")))+"', "
                                             + "d15='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d15")))+"', "
                                             + "d16='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d16")))+"', "
                                             + "d17='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d17")))+"', "
                                             + "d18='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d18")))+"', "
                                             + "d19='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d19")))+"', "
                                             + "d20='"+UTILS.BASIC.zeros_8(UTILS.FORMAT_8.format(row.getDouble("d20")))+"', "
                                             + "expire='"+row.getLong("expire")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("storage");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE storage");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "storage.table", crc);
    }
}
