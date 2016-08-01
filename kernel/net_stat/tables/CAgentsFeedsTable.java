package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAgentsFeedsTable extends CTable
{
    public CAgentsFeedsTable()
    {
        super("agents_feeds");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE agents_feeds(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       + "agentID VARCHAR(2) DEFAULT '', "
                                                       + "feed_symbol VARCHAR(250) DEFAULT '', "
				   	               + "expire BIGINT DEFAULT 0,"
                                                       + "block BIGINT DEFAULT 0,"
				     	               + "rowhash VARCHAR(100) DEFAULT '')");
				    
	UTILS.DB.executeUpdate("CREATE INDEX agents_feeds_adr ON agents_feeds(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX agents_feeds_rowhash ON agents_feeds(rowhash)");
        UTILS.DB.executeUpdate("CREATE INDEX agents_feeds_block ON agents_feeds(block)");    
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM agents_feeds "
                                     + "WHERE expire<="+block);
    }
    
    // Address
    public void refresh(long block) throws Exception
    {
        // Adr
        UTILS.DB.executeUpdate("UPDATE agents_feeds SET rowhash=SHA2(CONCAT(agentID, "
                                                                         + "feed_symbol, "
                                                                         + "expire, "
                                                                         + "block), 256) WHERE block='"+block+"'");
        
        // Table hash
        if (UTILS.BASIC.hasRecords("agents_feeds"))
        {
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET ads=(SELECT SHA2(GROUP_CONCAT(rowhash ORDER BY ID ASC), 256) AS st FROM agents_feeds)"); 
        
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
            
            // AgentID
            long agentID=row.getLong("agentID");
            
            // Feed Symbol
            String feed_symbol=row.getString("feed_symbol");
             
            // Expire
            long expire=row.getLong("expire");
               
            // Block
            long block=row.getLong("block");
               
            // Rowhash
            String rowhash=row.getString("rowhash");
            
            // Hash
            String hash=UTILS.BASIC.hash(agentID+
                                         feed_symbol+
                                         expire+
                                         block);
         
          
            // Check hash
            if (!rowhash.equals(hash))
                throw new Exception("Invalid hash - CAgentsFeedsTable.java " + hash);
            
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
            throw new Exception("Invalid grand hash - CAgentsFeedsTable.java");
    }
    
    public void fromDB() throws Exception
    {
       // Parent
       super.fromDB();
        
       // Init
       int a=0;
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM agents_feeds ORDER BY ID ASC");
       
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
               
               // AgentID
               this.addRow("agentID", rs.getLong("agentID"));
               
               // Feed symbol
               this.addRow("feed_symbol", rs.getString("feed_symbol"));
               
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
            
            UTILS.DB.executeUpdate("INSERT INTO agent_feeds "
                                         + "SET agentID='"+row.getLong("agentID")+"', "
                                              + "feed_symbol='"+row.getString("feed_symbol")+"', "
                                              + "expire='"+row.getLong("expire")+"', "
                                              + "block='"+row.getLong("block")+"', "
                                              + "rowhash='"+row.getString("rowhash")+"'");
        }
        
        // Clear table from sync
        UTILS.SYNC.removeTable("agent_feeds");
    }
    
    public void loadCheckpoint(String hash, String crc) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE agent_feeds");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash, "agent_feeds.table", crc);
    }
}
