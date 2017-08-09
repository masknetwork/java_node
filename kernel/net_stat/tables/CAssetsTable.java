package wallet.kernel.net_stat.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsTable extends CTable
{
    public CAssetsTable()
    {
        super("assets");
    }
    
    public void expired(long block) throws Exception
    {
        // Load expired
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM assets "
                                         + "WHERE expire<"+block);
       
       // Remove
       while (rs.next())
       {
           // Address
           String symbol=rs.getString("symbol");
        
           // Delete owners
           UTILS.DB.executeUpdate("DELETE FROM assets_owners "
                                      + "WHERE symbol='"+symbol+"'");
           
           // Delete asset
           UTILS.DB.executeUpdate("DELETE FROM assets "
                                      + "WHERE symbol='"+symbol+"'");
       }
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"assetID BIGINT NOT NULL DEFAULT 0, "
                                                             +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                             +"symbol VARCHAR(10) NOT NULL DEFAULT '', "
                                                             +"title VARCHAR(250) NOT NULL DEFAULT '', "
                                                             +"description VARCHAR(1000) NOT NULL DEFAULT '', "
                                                             +"how_buy VARCHAR(1000) NOT NULL DEFAULT '', "
                                                             +"how_sell VARCHAR(1000) NOT NULL DEFAULT '', "
                                                             +"web_page VARCHAR(250) NOT NULL DEFAULT '', "
                                                             +"pic VARCHAR(250) NOT NULL DEFAULT '', "
                                                             +"expire BIGINT NOT NULL DEFAULT 0, "
                                                             +"qty BIGINT NOT NULL DEFAULT 0, "
                                                             +"trans_fee_adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                             +"trans_fee DOUBLE(9,2) NOT NULL DEFAULT 0, "
                                                             +"block BIGINT NOT NULL DEFAULT 0)");
             
        UTILS.DB.executeUpdate("CREATE INDEX assets_adr ON assets(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_ID ON assets(assetID)");
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX assets_symbol ON assets(symbol)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_block ON assets(block)");  
    }
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
