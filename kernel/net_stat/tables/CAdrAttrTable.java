package wallet.kernel.net_stat.tables;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAdrAttrTable extends CTable
{
    public CAdrAttrTable()
    {
        // Constructor
        super("adr_attr");
    }
    
    // Create
    public void create() throws Exception
    {
       // Create table
       UTILS.DB.executeUpdate("CREATE TABLE adr_attr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				                  + "adr VARCHAR(500) NOT NULL DEFAULT '', "
				                  + "attr VARCHAR(50) NOT NULL DEFAULT '', "
		                                  + "s1 VARCHAR(500) NOT NULL DEFAULT '', "
                                                  + "s2 VARCHAR(500) NOT NULL DEFAULT '', "
                                                  + "s3 VARCHAR(500) NOT NULL DEFAULT '', "
                                                  + "l1 BIGINT NOT NULL DEFAULT 0, "
                                                  + "l2 BIGINT NOT NULL DEFAULT 0, "
                                                  + "l3 BIGINT NOT NULL DEFAULT 0, "
                                                  + "d1 FLOAT (20,8) NOT NULL DEFAULT 0, "
                                                  + "d2 FLOAT (20,8) NOT NULL DEFAULT 0, "
                                                  + "d3 FLOAT (20,8) NOT NULL DEFAULT 0, "
                                                  + "block BIGINT NOT NULL DEFAULT 0, "
                                                  + "expire BIGINT NOT NULL DEFAULT 0)");
		   
        UTILS.DB.executeUpdate("CREATE INDEX adr_attr_adr ON adr_attr(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX adr_attr_attr ON adr_attr(attr)");
	UTILS.DB.executeUpdate("CREATE INDEX adr_attr_block ON adr(block)");
    }
    
    public void expired(long block) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE "
                               + "FROM adr_attr "
                              + "WHERE expire<="+block);
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE adr_attr");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
