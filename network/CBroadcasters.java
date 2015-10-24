package wallet.network;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wallet.kernel.*;

public class CBroadcasters 
{
    // Has items
	boolean hasItems=false;
	
	public CBroadcasters() 
	{
		this.refreshBroadcasters();
	}
	
	public boolean canBroadcast(String adr)
	{
            try
            {
		CAddress address=UTILS.WALLET.getAddress(adr);
		
		// Can broadcast
		if (address.balance>1) 
		{	
                    Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		     ResultSet rs=s.executeQuery("SELECT * "
					           + "FROM blocks "
						  + "WHERE signer='"+adr+"' "
						    + "AND block>"+(UTILS.BASIC.block()-2));
					
			if (UTILS.DB.hasData(rs)==true) 
                        {
                            // Close
                            if (s!=null) s.close();
                            
                            // Return
			    return false;
                        }
			else
                        {
                            // Close
                            if (s!=null) s.close();
                            
                            // Return
			    return true;
                        }
		}
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CBroadcasters.java", 53);
            }
                
		// Return
		return false;
	}
	
	public void refreshBroadcasters()
	{
		// Clear db
		UTILS.DB.executeUpdate("DELETE FROM broadcasters");
		
		// Load broadcasters
		for (int a=0; a<=UTILS.WALLET.addresses.size()-1; a++)
		{
			CAddress adr=(CAddress)UTILS.WALLET.addresses.get(a);
			if (this.canBroadcast(adr.getPublic())==true) 
			{
				this.hasItems=true;
				String stage=UTILS.STATUS.getStage(adr.balance);
				UTILS.DB.executeUpdate("INSERT INTO broadcasters (adr, "
						                                       + "section, "
						                                       + "balance, "
						                                       + "second) "
						                                 +"VALUES ('"+adr.getPublic()+"', "
						                                        + "'"+stage+"', "
						                                        + "'"+adr.balance+"', "
						                                        + "'"+UTILS.STATUS.getSecond(stage)+"')");
				
		   }
		}
	}
	
    public String getFirstSigner()
    {
    	if (this.hasItems==true)
    	{
    		try
    		{
    		   // Load data
                    Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs=s.executeQuery("SELECT * "
                                                  + "FROM broadcasters "
                                              + "ORDER BY second ASC");
    		  
                    // Next
                    rs.next();
    		  
                    // Get address
                    String adr=rs.getString("adr");
                    
                    // Close
                    if (s!=null) s.close();
                    
                    // Return
                    return adr;
    		}
    		catch (SQLException ex)
    		{
    			UTILS.LOG.log("SQLException", ex.getMessage(), "CBroadcasters.java", 77);
    			return "";
    		}
    	}
    	else return "";
    }
}
