// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import wallet.kernel.*;

public class CTransPool 
{
	public CTransPool()  throws Exception
	{
		UTILS.DB.executeUpdate("DELETE "+
				           "FROM trans_pool "+
				          "WHERE block<"+(UTILS.NET_STAT.last_block+1));
	}
	
	public void addTrans(String src, 
			     double amount, 
			     String cur, 
			     String hash, 
			     long block) throws Exception
	{
            try
            {
                // Statement
                
                
                // Hash exist ?
                ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM trans_pool "
                                      + "WHERE src='"+src+"' "
                                        + "AND hash='"+hash+"' "
                                        + "AND amount='"+amount+"' "
                                        + "AND block='"+block+"'");
                
                if (!UTILS.DB.hasData(rs))
	        UTILS.DB.executeUpdate("INSERT INTO trans_pool(src, "
				                        + "amount, "
				                        + "cur, " 
                                                        + "hash, " 
				                        + "block) "
				               + "VALUES('"+src+"', '"
				                           +UTILS.FORMAT_8.format(amount)+"', '"
				                           +cur+"', '"
                                                           +hash+"', '" 
				                           +block+"')");
            }
            catch (SQLException e) 
            { 
                UTILS.LOG.log("Exception", e.getMessage(), "CTransPool.java", 85);
            }
	}
	
	public void newBlock(long block) throws Exception
	{
		UTILS.DB.executeUpdate("DELETE "
				       + "FROM trans_pool "
				      + "WHERE block<="+block);
	}
	
	public double getBalance(String adr, String cur) throws Exception
	{	
            try
	    {
                    
                    
		    String q="SELECT SUM(amount) AS total "
		    		            + "FROM trans_pool "
		      		           + "WHERE src='"+adr+"' "
		      		             + "AND cur='"+cur+"'"
                                             + "AND amount<0"; 
		    ResultSet rs=UTILS.DB.executeQuery(q);
                            
		    // Another transaction exists in pool ?
		    if (UTILS.DB.hasData(rs))
		    {
		    	// Load record
		        rs.next();
		        
		        // Source ?
		        double spent=rs.getDouble("total");
                        
                        // Actual balance
                        double act_balance=UTILS.ACC.getBalance(adr, cur);
                        
                        // Final balance
                        double balance=act_balance+spent;
                        
                      
                        
                        // Return
                        return balance;
		    }
                    else 
		    {
			
			return UTILS.ACC.getBalance(adr, cur);
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