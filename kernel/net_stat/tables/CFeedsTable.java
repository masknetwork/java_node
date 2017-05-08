package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsTable extends CTable
{
     public CFeedsTable()
    {
        super("feeds");
    }
    
    // Create
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       +"adr VARCHAR(250) DEFAULT '', "
                                                       +"feedID BIGINT DEFAULT 0, "
                                                       +"name VARCHAR(100) DEFAULT '', "
                                                       +"description VARCHAR(1000) DEFAULT '', "
                                                       +"website VARCHAR(250) DEFAULT '', "
                                                       +"symbol VARCHAR(10) DEFAULT '', "
                                                       +"expire BIGINT DEFAULT 0, "
                                                       +"branches BIGINT DEFAULT 0, "
                                                       +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_adr ON feeds(adr)");
             UTILS.DB.executeUpdate("CREATE UNIQUE INDEX feeds_feedID ON feeds(feedID)");
             UTILS.DB.executeUpdate("CREATE UNIQUE INDEX feeds_symbol ON feeds(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_block ON feeds(block)");
    }
    
    public void expired(long block) throws Exception
    {
       // Result set
       ResultSet rs;
       
       // Load expired
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM feeds "
                               + "WHERE expire<"+block);
      
       // Remove
       if (UTILS.DB.hasData(rs))
          while (rs.next())
              this.removeFeed(rs.getString("symbol"));
    }
    
    public void removeFeed(String symbol) throws Exception
    {
        // Has branches ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+symbol+"'");
        if (UTILS.DB.hasData(rs)) return;
        
        // Remove
        UTILS.DB.executeUpdate("DELETE FROM feeds "
                                   + "WHERE symbol='"+symbol+"'");
    }
    
    
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
     
     
}
