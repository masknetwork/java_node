package wallet.kernel;

import java.math.BigInteger;
import java.sql.ResultSet;

public class CDelegates 
{
    public CDelegates() throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total from blocks");
        rs.next();
        this.refresh(0);
    }
    
    public long computePower(String delegate) throws Exception
    {
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(adr.balance) AS total "
                                          + "FROM del_votes AS dv "
                                          + "JOIN adr ON adr.adr=dv.adr "
                                         + "WHERE dv.delegate='"+delegate+"' "
                                           + "AND dv.type='ID_UP'");
       
       // Next
       rs.next();
       
       // Upvotes power
       long up=Math.round(rs.getDouble("total"));
       
       rs=UTILS.DB.executeQuery("SELECT SUM(adr.balance) AS total "
                                          + "FROM del_votes AS dv "
                                          + "JOIN adr ON adr.adr=dv.adr "
                                         + "WHERE dv.delegate='"+delegate+"' "
                                           + "AND dv.type='ID_DOWN'");
       
        // Next
       rs.next();
       
       // Upvotes power
       long down=Math.round(rs.getDouble("total"));
       
       // Return
       return (up-down);
    }
    
    public void refresh(long block) throws Exception
    {
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
                                             + "power='"+power+"'");
        }
        
        // Delete downvoted delegates
        UTILS.DB.executeUpdate("DELETE FROM delegates WHERE power<1");
        
        // Only the first 100
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM delegates "
                             + "ORDER BY power DESC "
                                + "LIMIT 0,100");
        
        double min=0;
        long del_no=0;
        while (rs.next()) 
        {
            del_no++;
            min=rs.getDouble("power");
        }
        
        // Removes 
        if (del_no>100)
           UTILS.DB.executeUpdate("DELETE FROM delegates WHERE power<="+min);
        
        // Power less than 1
        UTILS.DB.executeUpdate("DELETE FROM delegates WHERE power<10");
    }
    
    public long getPower(String delegate) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM delegates "
                                          + "WHERE delegate='"+delegate+"'");
        
        // No delegate
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid delegate");
            
        // Next
        rs.next();
        
        // Power
        return rs.getLong("power");
    }
    
    public boolean isDelegate(String adr) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM delegates "
                                          + "WHERE delegate='"+adr+"'");
        
        if (UTILS.DB.hasData(rs))
            return true;
        else
            return false;
    }
    
    public boolean canMine(String adr) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM delegates "
                                          + "WHERE delegate='"+adr+"'");
        
        if (UTILS.DB.hasData(rs)==false) return false;
        
        // Next 
        rs.next();
        
        if (rs.getLong("mined_24")<=rs.getLong("max_blocks"))
            return true;
        else
            return false;
    }
    
    public BigInteger getDif(String delegate) throws Exception
    {
        return UTILS.NET_STAT.net_dif.multiply(BigInteger.valueOf(this.getPower(delegate)));
    }
}
