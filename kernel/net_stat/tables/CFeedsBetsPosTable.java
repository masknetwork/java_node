package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsBetsPosTable extends CTable
{
    public CFeedsBetsPosTable()
    {
        super("feeds_bets_pos");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE feeds_bets_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                           +"betID BIGINT DEFAULT 0, "
                                                           +"adr VARCHAR(250) DEFAULT '', "
                                                           +"amount DOUBLE(9,4) DEFAULT 0, "
                                                           +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_uid ON feeds_bets_pos(betID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_adr ON feeds_bets_pos(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_block ON feeds_bets_pos(block)");
    }
    
    public void expired(long block, String adr) throws Exception
    {
       
    }
    
  
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds_bets_pos");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }    
}
