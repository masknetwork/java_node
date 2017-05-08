package wallet.kernel.net_stat.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAdsTable extends CTable
{
    public CAdsTable()
    {
        super("ads");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE ads(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                     + "country VARCHAR(2) NOT NULL DEFAULT'', "
                                                     + "adr VARCHAR(250) NOT NULL DEFAULT'', "
				    		     + "title VARCHAR(250) NOT NULL DEFAULT'', "
				    		     + "message VARCHAR(1000) NOT NULL DEFAULT'',"
                                                     + "link VARCHAR(500) NOT NULL DEFAULT'',"
                                                     + "mkt_bid DOUBLE(9,4) NOT NULL DEFAULT 0,"
                                                     + "expire BIGINT NOT NULL DEFAULT 0,"
                                                     + "block BIGINT NOT NULL DEFAULT 0)");
				    
	UTILS.DB.executeUpdate("CREATE INDEX ads_adr ON ads(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX ads_block ON ads(block)");    
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM ads "
                                  + "WHERE expire<="+block);
    }
    
   
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE ads");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
