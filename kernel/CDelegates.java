package wallet.kernel;

import java.math.BigInteger;
import java.sql.ResultSet;

public class CDelegates 
{
    public CDelegates()
    {
        
    }
    
    public long computePower(String delegate) throws Exception
    {
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(adr.balance) AS total "
                                          + "FROM del_votes AS dv "
                                          + "JOIN adr ON adr.adr=dv.adr "
                                         + "WHERE dv.delegate='"+delegate+"'");
       
       // Retun power
       return Math.round(rs.getDouble("total"));
    }
    
    public void refresh() throws Exception
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
            UTILS.DB.executeUpdate("INSERT INTO delegates SET adr='"+rs.getString("delegate")+"', power='"+power+"'");
        }
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
    
    public BigInteger getDif(String delegate) throws Exception
    {
        return UTILS.NET_STAT.net_dif.multiply(BigInteger.valueOf(this.getPower(delegate)));
    }
}
