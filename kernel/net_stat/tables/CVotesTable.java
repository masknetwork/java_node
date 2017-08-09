package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CVotesTable extends CTable
{
    public CVotesTable()
    {
        super("votes");
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM votes "
                                  + "WHERE block<"+(block-1440));
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE votes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                          +"target_type VARCHAR(20) NOT NULL DEFAULT '0', "
                                                          +"targetID BIGINT NOT NULL DEFAULT '0', "
                                                          +"type VARCHAR(25) NOT NULL DEFAULT '', "
                                                          +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                          +"power FLOAT(9,2) NOT NULL DEFAULT 0, "
                                                          +"block BIGINT NOT NULL DEFAULT '0', "
                                                          +"expire BIGINT NOT NULL DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE INDEX votes_target_type ON votes(target_type)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_targetID ON votes(targetID)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_adr ON votes(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX votes_block ON votes(block)");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE votes");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
   
}

