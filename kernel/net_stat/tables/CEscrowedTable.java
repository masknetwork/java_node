package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CEscrowedTable extends CTable
{
    public CEscrowedTable()
    {
        super("escrowed");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE escrowed(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "trans_hash VARCHAR(250) DEFAULT '', "
			 	 	 	       + "sender_adr VARCHAR(250) DEFAULT '', "
			 	 	 	       + "rec_adr VARCHAR(250) DEFAULT '', "
                                                       + "escrower VARCHAR(250) DEFAULT '', "
                                                       + "amount FLOAT(20,8) DEFAULT 0, "
                                                       + "cur VARCHAR(10) DEFAULT '', "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "rowhash VARCHAR(250) DEFAULT '')");
				    
	UTILS.DB.executeUpdate("CREATE INDEX escrowed_trans_hash ON escrowed(trans_hash)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_sender_adr ON escrowed(sender_adr)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_rec_adr ON escrowed(rec_adr)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_escrower ON escrowed(escrower)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_block ON escrowed(block)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_rowhash ON escrowed(rowhash)");   
    }
    
    public void expired(long block) throws Exception
    {
       // Statement
       
       
       // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                  + "FROM escrowed "
                                 + "WHERE block<"+(block-50000));
       
       // Remove
       while (rs.next())
           this.removeByID(rs.getLong("ID"));
       
       // Close
       
    }
    
    public void removeByAdr(String adr) throws Exception
    {
       // Statement
       
       
       // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM escrowed "
                                         + "WHERE sender_adr='"+adr+"' "
                                            + "OR rec_adr='"+adr+"' "
                                            + "OR escrower='"+adr+"'");
       
       // Remove
       while (rs.next())
           this.removeByID(rs.getLong("ID"));
       
       // Close
       
    }
   
    public void removeByID(long ID) throws Exception
    {
        // Statement
       
       
       // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                  + "FROM escrowed "
                                 + "WHERE ID='"+ID+"'");
       
       // Next
       rs.next();
       
       // Return the funds
       if (rs.getString("cur").equals("MSK"))
          UTILS.DB.executeUpdate("UPDATE adr "
                                  + "SET balance=balance+"+rs.getDouble("amount")+" "
                                + "WHERE adr='"+rs.getString("sender_adr")+"'");
       else
          UTILS.DB.executeUpdate("UPDATE assets_owners "
                                  + "SET qty=qty+"+rs.getDouble("amount")+" "
                                + "WHERE owner='"+rs.getString("sender_adr")+"' "
                                  + "AND symbol='"+rs.getString("cur")+"'");
       
       // Remove
       UTILS.DB.executeUpdate("DELETE FROM escrowed WHERE ID='"+ID+"'");
           
       // Close
       
   }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE escrowed "
                                + "SET rowhash=SHA2(CONCAT(trans_hash, "
                                                        + "sender_adr, "
                                                        + "rec_adr, "
                                                        + "escrower, "
                                                        + "amount, "
                                                        + "cur, "
                                                        + "block), 256) "
                                + "WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("escrowed"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET escrowed=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM escrowed)"); 
        
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
            
            // Transaction hash
            String trans_hash=row.getString("trans_hash");
            
            // Sender
            String sender=row.getString("sender_adr");
            
            // Receiver
            String receiver=row.getString("rec_adr");
            
            // Escrower
            String escrower=row.getString("escrower");
               
            // Amount
            String amount=UTILS.BASIC.zeros(UTILS.FORMAT_8.format(row.getDouble("amount")));
               
            // Currency
            String cur=row.getString("cur");
            
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(trans_hash+
                                         sender+
                                         receiver+                     
                                         escrower+
                                         amount+
                                         cur+
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
            throw new Exception("Invalid grand hash - CEscrowedTable.java");
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
                                   + "FROM escrowed "
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
               
               // Transaction Hash
               this.addRow("trans_hash", rs.getString("trans_hash"));
               
               // Sender
               this.addRow("sender_adr", rs.getString("sender_adr"));
               
               // Receiver
               this.addRow("rec_adr", rs.getString("rec_adr"));
               
               // Escrower
               this.addRow("escrower", rs.getString("escrower"));
               
               // Amount
               this.addRow("amount", rs.getDouble("amount"));
               
               // Currency
               this.addRow("cur", rs.getString("cur"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO escrowed "
                                         + "SET trans_hash='"+row.getString("trans_hash")+"', "
                                             + "sender_adr='"+row.getString("sender_adr")+"', "
                                             + "rec_adr='"+row.getString("rec_adr")+"', "
                                             + "escrower='"+row.getString("escrower")+"', "
                                             + "amount='"+row.getDouble("amount")+"', "
                                             + "cur='"+row.getString("cur")+"', "
                                             + "block='"+row.getLong("block")+"', "
                                             + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("escrowed");
    }
    
     public void loadCheckpoint(String hash, String crc) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE escrowed");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "escrowed.table", crc);
    }
     
     public void drop() throws Exception
     {
       UTILS.DB.executeUpdate("DROP table escrowed");
       this.create();
     }
}
