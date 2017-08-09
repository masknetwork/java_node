package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;
import wallet.network.packets.blocks.CBlockPayload;

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
			 	 		       + "trans_hash VARCHAR(250) NOT NULL DEFAULT '', "
			 	 	 	       + "sender_adr VARCHAR(250) NOT NULL DEFAULT '', "
			 	 	 	       + "rec_adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                       + "escrower VARCHAR(250) NOT NULL DEFAULT '', "
                                                       + "amount FLOAT(20,8) NOT NULL DEFAULT 0, "
                                                       + "cur VARCHAR(10) NOT NULL DEFAULT '', "
                                                       + "expire BIGINT NOT NULL DEFAULT 0, "
                                                       + "block BIGINT NOT NULL DEFAULT 0)");
				    
	UTILS.DB.executeUpdate("CREATE UNIQUE INDEX escrowed_trans_hash ON escrowed(trans_hash)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_sender_adr ON escrowed(sender_adr)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_rec_adr ON escrowed(rec_adr)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_escrower ON escrowed(escrower)");
        UTILS.DB.executeUpdate("CREATE INDEX escrowed_block ON escrowed(block)"); 
    }
    
    public void expired(long block) throws Exception
    {
       // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM escrowed "
                                         + "WHERE expire<"+block);
       
       // Remove
       while (rs.next())
           this.removeByID(rs.getLong("ID"), block);
    }
   
    public void removeByID(long ID, long block) throws Exception
    {
        // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM escrowed "
                                         + "WHERE ID='"+ID+"'");
       
       // Next
       rs.next();
       
       // Return the funds
       UTILS.ACC.newTrans(rs.getString("sender_adr"), 
                          "", 
                          rs.getDouble("amount"), 
                          rs.getString("cur"), 
                          "Escrowed funds returned", 
                          "", 
                          UTILS.BASIC.hash(rs.getString("hash")), 
                          block);
       
       // Clear
       UTILS.ACC.clearTrans(UTILS.BASIC.hash(rs.getString("hash")), "ID_ALL", block);
       
       // Remove
       UTILS.DB.executeUpdate("DELETE FROM escrowed WHERE ID='"+ID+"'");
   }
    
    
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE escrowed");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
