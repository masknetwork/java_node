package wallet.network;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import wallet.kernel.*;

public class CTransPool 
{
	public CTransPool() 
	{
		UTILS.DB.executeUpdate("DELETE "+
				           "FROM trans_pool "+
				          "WHERE block<"+UTILS.BASIC.block());
	}
	
	public void addTrans(String src, 
			     double amount, 
			     String cur, 
			     String hash, 
			     long block)
	{
	    // Insert transaction in pool
	    UTILS.DB.executeUpdate("INSERT INTO trans_pool(src, "
				                        + "amount, "
				                        + "cur, " 
				                        + "block) "
				               + "VALUES('"+src+"', '"
				                           +UTILS.FORMAT.format(amount)+"', '"
				                           +cur+"', '"
				                           +block+"')");
	}
	
	public void delBlock(long block)
	{
		UTILS.DB.executeUpdate("DELETE "
				 + "FROM trans_pool "
				+ "WHERE block='"+block+"'");
	}
	
	public double getBalance(String adr, String cur)
	{	
            try
	    {
                    Statement s=UTILS.DB.getStatement();
                    
		    String q="SELECT SUM(amount) AS total "
		    		            + "FROM trans_pool "
		      		           + "WHERE src='"+adr+"' "
		      		             + "AND cur='"+cur+"'"; 
		    ResultSet rs=s.executeQuery(q);
                            
		    // Another transaction exists in pool ?
		    if (UTILS.DB.hasData(rs))
		    {
		    	// Load record
		        rs.next();
		        
		        // Source ?
		        double spent=rs.getDouble("total");
                        
                        // Actual balance
                        double act_balance=UTILS.BASIC.getBalance(adr, cur);
                        
                        // Final balance
                        double balance=act_balance-spent;
                        
                        // Close 
                        if (s!=null) s.close();
                        
                        // Return
                        return balance;
		    }
                    else 
		    {
			s.close();
			return UTILS.BASIC.getBalance(adr, cur);
		    }
		    
		}
		catch (SQLException e) 
                { 
                    UTILS.LOG.log("Exception", e.getMessage(), "CTransPool.java", 85);
                }
	    
		// Return
		return 0;
	}
}