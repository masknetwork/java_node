package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CTweetsFollowTable extends CTable
{
    public CTweetsFollowTable()
    {
        super("tweets_follow");
    }
    
    public void expired(long block) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM tweets_follow "
                                   + "WHERE expire<"+block);
    }
    
    public void create() throws Exception
    {
       UTILS.DB.executeUpdate("CREATE TABLE tweets_follow(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"follows VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"expire BIGINT NOT NULL DEFAULT '0', "
                                                                +"block BIGINT NOT NULL DEFAULT '0')");
             
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_adr ON tweets_follow(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_follows ON tweets_follow(follows)");
        UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_block ON tweets_follow(block)");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE tweets_follow");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
    
}