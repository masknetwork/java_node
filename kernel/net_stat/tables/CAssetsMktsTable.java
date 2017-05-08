package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsMktsTable extends CTable
{
    public CAssetsMktsTable()
    {
        super("assets_mkts");
    }
    
    public void expired(long block) throws Exception
    {
        // Result
        ResultSet rs;
        
        // Load  markets
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM assets_mkts  "
                                + "WHERE expire<="+block);
        
        while (rs.next())
          this.removeByID(rs.getLong("mktID"));
    }
    
    public void removeByID(long mktID) throws Exception
    {
       // Has open positions ?
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM assets_mkts_pos "
                                         + "WHERE mktID='"+mktID+"'");
       
       // Next
       rs.next();
       
       // Has data
       if (UTILS.DB.hasData(rs)) return;
       
       // Remove
       UTILS.DB.executeUpdate("DELETE FROM assets_mkts "
                                  + "WHERE mktID='"+mktID+"'");
    }
    
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets_mkts(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 				          + "adr VARCHAR(250) DEFAULT '', "
	 	 				          + "asset VARCHAR(10) DEFAULT '', "
	 	 				          + "cur VARCHAR(10) DEFAULT '', "
	 	 				          + "name VARCHAR(500) DEFAULT '', "
	 	 				          + "description VARCHAR(2500) DEFAULT '', "
	 	 				          + "decimals BIGINT DEFAULT 0, "
	 	 				          + "block BIGINT DEFAULT 0, "
	 	 				          + "expire BIGINT DEFAULT 0, "
                                                          + "last_price DOUBLE(20,8) DEFAULT 0, "
                                                          + "ask DOUBLE(20,8) DEFAULT 0, "
                                                          + "bid DOUBLE(20,8) DEFAULT 0, "
	 	 				          + "mktID BIGINT DEFAULT 0)");
	 	 	   
	UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_block ON assets_mkts(block)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_name ON assets_mkts(name)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_adr ON assets_mkts(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_cur ON assets_mkts(cur)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_asset ON assets_mkts(asset)");
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX assets_mkts_mktID ON assets_mkts(mktID)");
    }
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets_mkts");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
    
}
