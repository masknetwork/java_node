package wallet.kernel;

import java.math.BigInteger;
import java.sql.ResultSet;

public class CDelegates 
{
    public CDelegates() throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                           + "FROM blocks");
        rs.next();
        
        if (rs.getLong("total")==1) 
            this.refresh(0);
    }
    
    public long computePower(String delegate) throws Exception
    {
       // Check delegate
        if (!UTILS.BASIC.isAdr(delegate))
            throw new Exception("Invalid delegate - CDelegates.java, 22");
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM del_votes "
                                         + "WHERE delegate='"+delegate+"' "
                                           + "AND type='ID_UP'");
       
       // Next
       rs.next();
       
       // Upvotes power
       long up=Math.round(rs.getDouble("total"));
       
       rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                + "FROM del_votes "
                               + "WHERE delegate='"+delegate+"' "
                                 + "AND type='ID_DOWN'");
       
        // Next
       rs.next();
       
       // Upvotes power
       long down=Math.round(rs.getDouble("total"));
       
       // Return
       return (up-down);
    }
    
    public void refreshVotesPower() throws Exception
    {
        // Load votes    
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM del_votes AS dv "
                                           + "JOIN adr ON adr.adr=dv.adr");
        
        while (rs.next())
        UTILS.DB.executeUpdate("UPDATE del_votes "
                                + "SET power='"+Math.round(rs.getDouble("balance"))+"' "
                              + "WHERE ID='"+rs.getLong("ID")+"'");
    }
    
    public void logDelegates(long block) throws Exception
    {
        // Remove delegates with the same block
        UTILS.DB.executeUpdate("DELETE FROM delegates_log "
                                  + "WHERE block='"+block+"'");
        
        // Load delegates 
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM delegates");
        
        while (rs.next())
           UTILS.DB.executeUpdate("INSERT INTO delegates_log "
                                        + "SET delegate='"+rs.getString("delegate")+"', "
                                            + "power='"+rs.getLong("power")+"', "
                                            + "block='"+rs.getLong("block")+"'");
    }
    
    public void refresh(long block) throws Exception
    {
        // Votes power
        this.refreshVotesPower();
        
        // Delete delegates
        UTILS.DB.executeUpdate("DELETE FROM delegates");
        
        // Load distinct delegates
        ResultSet rs=UTILS.DB.executeQuery("SELECT DISTINCT(delegate) FROM del_votes");
        
        while (rs.next())
        {
            // Get delegate power
            long power=this.computePower(rs.getString("delegate"));
            
            // Insert
            UTILS.DB.executeUpdate("INSERT INTO delegates "
                                         + "SET delegate='"+rs.getString("delegate")+"', "
                                             + "power='"+power+"', "
                                             + "block='"+block+"'");
        }
        
        // Load delegates                                     
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM delegates");
        
        // Parse
        while (rs.next())
            UTILS.DB.executeUpdate("UPDATE delegates "
                                    + "SET dif='"+UTILS.BASIC.formatDif(this.getDif(rs.getString("delegate"), block).toString(16))+"'");
        
        // Delete downvoted delegates
        UTILS.DB.executeUpdate("DELETE FROM delegates WHERE power<10");
        
        // Log 
        this.logDelegates(block);
    }
    
    public long getPower(String delegate, long block) throws Exception
    {
        ResultSet rs;
        
        // Check delegate
        if (!UTILS.BASIC.isAdr(delegate))
            throw new Exception("Invalid adr - CDelegates.java, 129");
        
        if (block>101)
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM delegates_log "
                                + "WHERE delegate='"+delegate+"' "
                                  + "AND block="+(block-50));
        else
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM delegates "
                                + "WHERE delegate='"+delegate+"'");
        
        // No delegate
        if (!UTILS.DB.hasData(rs))
            return 1;
            
        // Next
        rs.next();
        
        // Power
        return rs.getLong("power");
    }
    
    public long getTotalPower() throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                           + "FROM delegates");
        
        // Next
        rs.next();
        
        // Power
        return rs.getLong("total");
    }
    
    public boolean isDelegate(String adr) throws Exception
    {
        // Check delegate
        if (!UTILS.BASIC.isAdr(adr))
            throw new Exception("Invalid adr - CDelegates.java, 168");
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM delegates "
                                          + "WHERE delegate='"+adr+"'");
        
        if (UTILS.DB.hasData(rs))
            return true;
        else
            return false;
    }
    
    
    
    public BigInteger getDif(String delegate, long block) throws Exception
    {
        return UTILS.NET_STAT.net_dif.multiply(BigInteger.valueOf(this.getPower(delegate, block)));
    }
    
   
    
    public long getMinedBlocks(String delegate, long block) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS mined "
                                           + "FROM blocks "
                                          + "WHERE block<"+(block-1440));
        
        // Next
        rs.next();
        
        // Return
        return rs.getLong("mined");
    }
    
   
}
