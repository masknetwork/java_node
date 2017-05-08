package wallet.kernel.net_stat.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CDelVotesTable extends CTable
{
    public CDelVotesTable()
    {
        super("del_votes");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE del_votes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                    + "delegate VARCHAR(500) NOT NULL DEFAULT '', "
                                                    + "adr VARCHAR(500) NOT NULL DEFAULT '', "
				  	            + "type VARCHAR(25) NOT NULL DEFAULT 'ID_UP', "
                                                    + "power BIGINT NOT NULL DEFAULT '0', "
				    	            + "block BIGINT NOT NULL DEFAULT 0)");
				    
	UTILS.DB.executeUpdate("CREATE INDEX del_votes_delegate ON del_votes(delegate)");
        UTILS.DB.executeUpdate("CREATE INDEX del_votes_adr ON del_votes(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX del_votes_type ON del_votes(type)");
        UTILS.DB.executeUpdate("CREATE INDEX del_votes_block ON del_votes(block)");    
    }
    
   
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE del_votes");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
