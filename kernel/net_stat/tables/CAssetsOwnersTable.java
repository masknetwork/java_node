package wallet.kernel.net_stat.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsOwnersTable extends CTable 
{
    public CAssetsOwnersTable()
    {
        super("assets_owners");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets_owners(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                          +"owner VARCHAR(250) NOT NULL DEFAULT '', "
                                                          +"symbol VARCHAR(10) NOT NULL DEFAULT '', "
                                                          +"qty DOUBLE(20,8) NOT NULL DEFAULT 0, "
                                                          +"block BIGINT NOT NULL DEFAULT 0)");
             
        UTILS.DB.executeUpdate("CREATE INDEX assets_owners_owner ON assets_owners(owner)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_owners_symbol ON assets_owners(symbol)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_owners_block ON assets_owners(block)");
    }
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets_owners");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
    
}



