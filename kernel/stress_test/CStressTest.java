package wallet.kernel.stress_test;

import java.sql.ResultSet;
import wallet.kernel.UTILS;

public class CStressTest 
{
    // Table
    String tab;
    
    public CStressTest(String table)
    {
        this.tab=table;
    }
    
    public String randomSymbol() throws Exception
    {
       return UTILS.BASIC.hash(String.valueOf(Math.random()).substring(1,7).toUpperCase());    
    }

    public String randomAdr() throws Exception
    {
        // Query
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM adr "
                                       + "ORDER BY RAND() "
                                          + "LIMIT 0,5");
        // Next
        rs.next();
        
        // Address
        return rs.getString("adr");
        
    }
    
    public String randomFeed() throws Exception
    {
         // Query
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                       + "ORDER BY RAND() "
                                          + "LIMIT 0,1");
        // Next
        rs.next();
        
        // Address
        return rs.getString("symbol");
    }
    
    public String randomFeedBranch(String feed) throws Exception
    {
         // Query
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+feed+"'"
                                       + "ORDER BY RAND() "
                                          + "LIMIT 0,1");
        // Next
        rs.next();
        
        // Address
        return rs.getString("symbol");
    }
    
    public long randomAgent() throws Exception
    {
         // Query
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM agents "
                                       + "ORDER BY RAND() "
                                          + "LIMIT 0,1");
        // Next
        rs.next();
        
        // Address
        return rs.getLong("aID");
    }
    
    public long randomDays()
    {
        return Math.round(Math.random()*10);
    }
    
   
}
