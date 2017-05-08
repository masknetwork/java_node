package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CCommentsTable extends CTable
{
    public CCommentsTable()
    {
        super("comments");
    }
    
    public void expire(long block) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM comments WHERE expire<"+block);
    }
    
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE comments(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"adr VARCHAR(250) NOT NULL DEFAULT '',"
                                                            +"parent_type VARCHAR(25) NOT NULL DEFAULT '', "
                                                            +"parentID BIGINT NOT NULL DEFAULT 0, "
                                                            +"comID BIGINT NOT NULL DEFAULT 0, "
                                                            +"mes VARCHAR(1000) NOT NULL DEFAULT '', "
                                                            +"expire BIGINT DEFAULT 0,"
                                                            +"block BIGINT NOT NULL DEFAULT 0)");
             
        UTILS.DB.executeUpdate("CREATE INDEX comments_parent_adr ON comments(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_parent_type ON comments(parent_type)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_parentID ON comments(parentID)");
        UTILS.DB.executeUpdate("CREATE INDEX comments_block ON comments(block)");
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX comments_comID ON comments(comID)");  
    }
    
    public void expired(long block) throws Exception
    {
        UTILS.DB.executeUpdate("DELETE FROM comments "
                                   + "WHERE expire<='"+block+"'");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE comments");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
    
}   
