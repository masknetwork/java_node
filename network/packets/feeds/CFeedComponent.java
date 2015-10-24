package wallet.network.packets.feeds;

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
    
    public CFeedComponent(String symbol, double val)
    {
       // Symbol
       this.symbol=symbol;
    
       // Value
       this.val=val;
    }
    
    public CResult check(String feed_symbol)
    { 
         // Symbol valid
         if (!UTILS.BASIC.symbolValid(this.symbol))
             return new CResult(false, "Invalid symbol", "CFeedComponent", 67);       
         
         try
         {
           // Feed symbol valid
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_components "
                                      + "WHERE feed_symbol='"+feed_symbol+"' "
                                        + "AND symbol='"+this.symbol+"'");
           
           if (!UTILS.DB.hasData(rs))
		   {
			   s.close();
              return new CResult(false, "Invalid feed symbol", "CFeedComponent", 67);   
		   }
		   
		   // Close
		   s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 60);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(String feed_symbol)
    {
        // Update
        UTILS.DB.executeUpdate("UPDATE feeds_components "
                                + "SET val='"+this.val+"' "
                              + "WHERE feed_symbol='"+feed_symbol+"' "
                                + "AND symbol='"+this.symbol+"'");
        
        // Rowhash
        UTILS.ROWHASH.update("feeds_components", "feed_symbol", feed_symbol, "symbol", this.symbol);
        
        // Insert value
        UTILS.DB.executeUpdate("INSERT INTO feeds_data(feed, "
                                                    + "feed_branch, "
                                                    + "val, "
                                                    + "block) "
                                  + "VALUES('"+feed_symbol+"', '"+
                                               this.symbol+"', '"+
                                               this.val+"', '"+
                                               UTILS.BASIC.block()+"')");
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }    
}
