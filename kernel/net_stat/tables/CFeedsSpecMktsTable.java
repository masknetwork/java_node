package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsSpecMktsTable extends CTable
{
    public CFeedsSpecMktsTable()
    {
        super("feeds_spec_mkts");
    }
    
    public void expired(long block) throws Exception
    {
        // Load expired positions
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts "
                                         + "WHERE expire<"+block);
       
       // Parse
       while (rs.next())
         UTILS.SPEC_POS.closeMarket(rs.getLong("mktID"), block);
    }
    
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds_spec_mkts(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"adr VARCHAR(250) DEFAULT '', "
                                                                +"feed VARCHAR(10) DEFAULT '', "
                                                                +"branch VARCHAR(10) DEFAULT '', "
                                                                +"cur VARCHAR(10) DEFAULT '', "
                                                                +"max_leverage BIGINT DEFAULT 0, "
                                                                +"spread DOUBLE(20,8) DEFAULT 0, "
                                                                +"max_total_margin DOUBLE(20,2) DEFAULT 0, "
                                                                +"title VARCHAR(250) DEFAULT '', "
                                                                +"description VARCHAR(2500) DEFAULT '', "
                                                                +"expire BIGINT DEFAULT 0, "
                                                                +"block BIGINT DEFAULT 0, "
                                                                +"last_price DOUBLE(20,2) DEFAULT 0, "
                                                                +"status VARCHAR(20) DEFAULT 'ID_CLOSED', "
                                                                +"max_down BIGINT NOT NULL DEFAULT 0, "
                                                                +"max_up BIGINT NOT NULL DEFAULT 0, "
                                                                +"mktID BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE UNIQUE INDEX feeds_spec_mkts_mktID ON feeds_spec_mkts(mktID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_block ON feeds_spec_mkts(block)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_adr ON feeds_spec_mkts(adr)");
    }
    
   
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds_spec_mkts");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
     
}
