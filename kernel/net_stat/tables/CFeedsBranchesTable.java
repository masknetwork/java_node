package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsBranchesTable extends CTable
{
    public CFeedsBranchesTable()
    {
        super("feeds_branches");
    }
    
    // Create
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds_branches(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                           +"feed_symbol VARCHAR(100) DEFAULT '', "
                                                           +"symbol VARCHAR(10) DEFAULT '', "
                                                           +"name VARCHAR(250) DEFAULT '', "
                                                           +"description VARCHAR(500) DEFAULT '', "
                                                           +"type VARCHAR(50) DEFAULT '', "
                                                           +"rl_symbol VARCHAR(20) DEFAULT '', "
                                                           +"expire BIGINT DEFAULT 0, "
                                                           +"val DOUBLE(20,8) DEFAULT 0, "
                                                           +"mkt_status VARCHAR(50) DEFAULT '', "
                                                           +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_feed_symbol ON feeds_branches(feed_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_symbol ON feeds_branches(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_type ON feeds_branches(type)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_block ON feeds_branches(block)");
    }
    
    public void expired(long block) throws Exception
    {
       // Result set
       ResultSet rs;
       
       // Load expired
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM feeds_branches "
                               + "WHERE expire<"+block);
      
       // Remove
       if (UTILS.DB.hasData(rs))
          while (rs.next())
              this.removeBranch(rs.getString("feed_symbol"), 
                                rs.getString("symbol"));
    }
    
    public void removeBranch(String feed, String branch) throws Exception
    {
        // Has bets
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_bets "
                                          + "WHERE (feed='"+feed+"' AND branch='"+branch+"')");
        if (UTILS.DB.hasData(rs)) return;
        
        // Has margin marjets
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds_spec_mkts "
                                + "WHERE (feed='"+feed+"' AND branch='"+branch+"')");
        if (UTILS.DB.hasData(rs)) return;
        
        // Remove
        UTILS.DB.executeUpdate("DELETE FROM feeds_branches "
                                   + "WHERE feed_symbol='"+feed+"' "
                                     + "AND symbol='"+branch+"'");
     }
    
    
    
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds_branches");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }  
}
