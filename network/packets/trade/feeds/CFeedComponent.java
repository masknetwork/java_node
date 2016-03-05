// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPayload;


public class CFeedComponent implements Serializable
{
     // Symbol
    public String symbol;
    
    // Value
    public double val;
    
    // Market status
    public String mkt_status;
   
    
    public CFeedComponent(String symbol, double val, String mkt_status) throws Exception
    {
       // Symbol
       this.symbol=symbol;
    
       // Value
       this.val=val;
       
       // Market status
       this.mkt_status=mkt_status;
    }
    
    public CResult check(String feed_symbol) throws Exception
    { 
         // Symbol valid
         if (!UTILS.BASIC.symbolValid(this.symbol))
             return new CResult(false, "Invalid symbol", "CFeedComponent", 67);       
         
         try
         {
           // Feed symbol valid
           Statement s=UTILS.DB.getStatement();
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_branches "
                                      + "WHERE feed_symbol='"+feed_symbol+"' "
                                        + "AND symbol='"+this.symbol+"'");
          
           if (!UTILS.DB.hasData(rs))
           {
              rs.close(); s.close();
              return new CResult(false, "Invalid feed symbol", "CFeedComponent", 67);   
           }
		   
	   // Close
	   rs.close(); s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 60);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(String feed_symbol) throws Exception
    {
        // Statement
        Statement s=UTILS.DB.getStatement();
                
        // Update
        UTILS.DB.executeUpdate("UPDATE feeds_branches "
                                + "SET val='"+this.val+"', "
                                    + "mkt_status='"+this.mkt_status+"' "
                              + "WHERE feed_symbol='"+feed_symbol+"' "
                                + "AND symbol='"+this.symbol+"'");
        
        // Insert value 
        UTILS.DB.executeUpdate("INSERT INTO feeds_data(feed, "
                                                    + "feed_branch, "
                                                    + "val, "
                                                    + "mkt_status, "
                                                    + "tstamp, "
                                                    + "block) "
                                  + "VALUES('"+feed_symbol+"', '"+
                                               this.symbol+"', '"+
                                               this.val+"', '"+
                                               this.mkt_status+"', '"+
                                               UTILS.BASIC.tstamp()+"', '"+
                                               (UTILS.NET_STAT.last_block+1)+"')");
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }    
}
