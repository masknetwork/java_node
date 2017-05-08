package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CDelegatesTable extends CTable
{
    public CDelegatesTable()
    {
        // Constructor
        super("delegates");
    }
    
    // Create
    public void create() throws Exception
    {
       // Create table
       UTILS.DB.executeUpdate("CREATE TABLE delegates(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				                   + "delegate VARCHAR(500) NOT NULL DEFAULT '', "
				                   + "power BIGINT NOT NULL DEFAULT 0, "
                                                   + "dif VARCHAR(100) NOT NULL DEFAULT '', "
                                                   + "block BIGINT NOT NULL DEFAULT 0)");
		   
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX del_delegate ON delegates(delegate)");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE delegates");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
