package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsBetsTable extends CTable
{
     public CFeedsBetsTable()
    {
        super("feeds_bets");
    }
    
    // Feeds bets
    public void expire(long block) 
    {
        
    }
     
    // Create
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds_bets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"betID BIGINT DEFAULT 0, "
                                                            +"adr VARCHAR(250) DEFAULT '', "
                                                            +"feed VARCHAR(10) DEFAULT '', "
                                                            +"branch VARCHAR(10) DEFAULT '', "
                                                            +"last_price DOUBLE(20,8) DEFAULT 0, "
                                                            +"tip VARCHAR(30) DEFAULT 'ID_TOUCH', "
                                                            +"val_1 DOUBLE(20, 8) DEFAULT 0, "
                                                            +"val_2 DOUBLE(20, 8) DEFAULT 0, "
                                                            +"title VARCHAR(150) DEFAULT '', "
                                                            +"description VARCHAR(250) DEFAULT '', "
                                                            +"budget DOUBLE(9,2) DEFAULT 0, "
                                                            +"win_multiplier INT DEFAULT 0, "
                                                            +"start_block BIGINT DEFAULT 0, "
                                                            +"end_block BIGINT DEFAULT 0, "
                                                            +"accept_block BIGINT DEFAULT 0, "
                                                            +"cur VARCHAR(10) DEFAULT '', "
                                                            +"bets BIGINT DEFAULT 0, "
                                                            +"invested DOUBLE(20,8) DEFAULT 0, "
                                                            +"status VARCHAR(10) DEFAULT 'ID_PENDING', "
                                                            +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE UNIQUE INDEX feeds_bets_uid ON feeds_bets(betID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_block ON feeds_bets(block)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_adr ON feeds_bets(adr)");
    }
    
    
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds_bets");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
