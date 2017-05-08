package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CProfilesTable extends CTable
{
    public CProfilesTable()
    {
        super("profiles");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE profiles(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                         +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"name VARCHAR(50) NOT NULL DEFAULT '', "
                                                         +"pic_back VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"pic VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"description VARCHAR(500) NOT NULL DEFAULT '', "
                                                         +"website VARCHAR(250) NOT NULL DEFAULT '', "
                                                         +"email VARCHAR(200) NOT NULL DEFAULT '', "
                                                         +"expire BIGINT NOT NULL DEFAULT '0', "
                                                         +"block BIGINT NOT NULL DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE INDEX prof_adr ON profiles(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX prof_block ON profiles(block)");
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM profiles "
                                     + "WHERE expire<="+block);
    }
    
   
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE profiles");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
}
