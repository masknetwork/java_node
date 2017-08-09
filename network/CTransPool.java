// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.sql.ResultSet;
import java.sql.SQLException;

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
                // Source
                if (!UTILS.BASIC.isAdr(src))
                    throw new Exception("Invalid source address - CTransPool.java, 32");
                
                // Hash
                if (!UTILS.BASIC.isHash(hash))
                    throw new Exception("Invalid hash - CTransPool.java, 36");
                
                // Currency
                if (!UTILS.BASIC.isCur(cur))
                    throw new Exception("Invalid currency - CTransPool.java, 40");
                
                // Hash exist ?
                ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                   + "FROM trans_pool "
                                                  + "WHERE src='"+src+"' "
                                                    + "AND hash='"+hash+"' "
                                                    + "AND amount='"+amount+"' "
                                                    + "AND block='"+block+"'");
                
                if (!UTILS.DB.hasData(rs))
	        UTILS.DB.executeUpdate("INSERT INTO trans_pool "
                                             + "SET src='"+src+"', "
				                 + "amount='"+UTILS.FORMAT_8.format(amount)+"', "
				                 + "cur='"+cur+"', " 
                                                 + "hash='"+hash+"', " 
              	                                 + "block='"+block+"'");
            }
            catch (SQLException e) 
            { 
                System.out.println(e.getMessage() + " - CTransPool.java, 55");
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
                // Source
                if (!UTILS.BASIC.isAdr(adr))
                    throw new Exception("Invalid source address - CTransPool.java, 77");
                
                // Currency
                if (!UTILS.BASIC.isCur(cur))
                    throw new Exception("Invalid currency - CTransPool.java, 81");
                
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
                    System.out.println(e.getMessage() + " - CTransPool.java, 108");
                }
	    
		// Return
		return 0;
	}
}