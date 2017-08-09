// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
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
         // Value
         if (this.val<0)
             throw new Exception("Invalid value");  
         
         // Symbol valid
         if (!UTILS.BASIC.isSymbol(this.symbol))
             throw new Exception("Invalid symbol");     
         
         // Market status
         if (!this.mkt_status.equals("ID_ONLINE") && 
             !this.mkt_status.equals("ID_OFFLINE"))
            throw new Exception("Invalid market status - "+this.mkt_status);   
         
         // Feed symbol valid
         ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                            + "FROM feeds_branches "
                                           + "WHERE feed_symbol='"+feed_symbol+"' "
                                             + "AND symbol='"+this.symbol+"'");
          
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid feed symbol");  
        
    }
    
    public void updateTimeData(String feed, 
                               String branch, 
                               long interval, 
                               long block, 
                               double val) throws Exception
    {
        // No
        long no=Math.round(block/interval);
        
        // Data exist ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_data_time "
                                          + "WHERE feed='"+feed+"' "
                                            + "AND branch='"+branch+"' "
                                            + "AND inter='"+interval+"' "
                                            + "AND no='"+no+"'");
        
        // Open
        double open=0;
        
        // Close
        double close=0;
        
        // Low
        double low=0;
        
        // High
        double high=0;
        
        // Has data ?
        if (UTILS.DB.hasData(rs))
        {
            // Next
            rs.next();
            
            // Open
            open=rs.getDouble("open");
        
            // Close
            close=val;
        
            // Low
            if (val<=rs.getDouble("low")) 
                low=val;
            else
                low=rs.getDouble("low");
        
            // High
            if (val>=rs.getDouble("high")) 
                high=val;
            else
                high=rs.getDouble("high");
            
            // Update
            UTILS.DB.executeUpdate("UPDATE feeds_data_time "
                                    + "SET open='"+open+"', "
                                        + "close='"+close+"', "
                                        + "low='"+low+"', "
                                         + "high='"+high+"' "
                                  + "WHERE feed='"+feed+"' AND "
                                         + "branch='"+branch+"' AND "
                                         + "inter='"+interval+"' AND "
                                         + "no='"+no+"'");
        }
        else
        {
            // Update
            UTILS.DB.executeUpdate("INSERT INTO feeds_data_time "
                                    + "SET open='"+val+"', "
                                        + "close='"+val+"', "
                                        + "low='"+val+"', "
                                        + "high='"+val+"', "
                                        + "feed='"+feed+"', "
                                        + "branch='"+branch+"', "
                                        + "inter='"+interval+"', "
                                        + "no='"+no+"', "
                                        + "block='"+block+"'");
        }
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
        
        // Update chart 5 minutes
        this.updateTimeData(feed_symbol, this.symbol, 5, block, val);
        
        // Update chart 1 hour
        this.updateTimeData(feed_symbol, this.symbol, 60, block, val);
        
        // Update chart 1 day
        this.updateTimeData(feed_symbol, this.symbol, 1440, block, val);
       
        // Update markets
        UTILS.SPEC_POS.updateMarkets(feed_symbol, this.symbol, this.val);
        UTILS.OPTIONS.updateMarkets(feed_symbol, this.symbol, this.val);
    }    
}
