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
    
    public void check(String feed_symbol) throws Exception
    { 
         // Symbol valid
         if (!UTILS.BASIC.isSymbol(this.symbol))
             throw new Exception("Invalid symbol");       
         
         // Feed symbol valid
         ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM feeds_branches "
                                      + "WHERE feed_symbol='"+feed_symbol+"' "
                                        + "AND symbol='"+this.symbol+"'");
          
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid feed symbol");  
        
    }
    
    public void commit(String feed_symbol, long block) throws Exception
    {
        // Update
        UTILS.DB.executeUpdate("UPDATE feeds_branches "
                                + "SET val='"+this.val+"', "
                                    + "mkt_status='"+this.mkt_status+"' "
                              + "WHERE feed_symbol='"+feed_symbol+"' "
                                + "AND symbol='"+this.symbol+"'");
        
        // Insert value 
        UTILS.DB.executeUpdate("INSERT INTO feeds_data "
                                     + "SET feed='"+feed_symbol+"', "
                                         + "feed_branch='"+this.symbol+"', "
                                         + "val='"+this.val+"', "
                                         + "mkt_status='"+this.mkt_status+"', "
                                         + "tstamp='"+UTILS.BASIC.tstamp()+"', "
                                         + "block='"+block+"'");
       
        // Update markets
        UTILS.CRONS.updateMarkets(feed_symbol, this.symbol, this.val);
    }    
}
