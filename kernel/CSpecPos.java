package wallet.kernel;

import java.sql.ResultSet;
import wallet.network.packets.blocks.CBlockPayload;

public class CSpecPos 
{
    public void CSpecPos()
    {
        
    }
    
    public void closePos(long posID, long block) throws Exception
    {
       ResultSet pos_rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM feeds_spec_mkts_pos "
                                             + "WHERE posID='"+posID+"'");
       
       if (UTILS.DB.hasData(pos_rs))
       {
           // Next
           pos_rs.next();
           
           // Load status
           if (!pos_rs.getString("status").equals("ID_CLOSED"))
           {
                // Load market data
                ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT * "
                                                       + "FROM feeds_spec_mkts "
                                                      + "WHERE mktID='"+pos_rs.getString("mktID")+"'");
                
                // Next
                mkt_rs.next();
               
                // Market balance
                double balance=UTILS.ACC.getBalance(mkt_rs.getString("adr"), mkt_rs.getString("cur"));
                
                // PL
                double pl=pos_rs.getDouble("margin")+pos_rs.getDouble("pl");
               
                // Adjust pl
                if (pl>=balance)
                   pl=balance;
                
                if (pl>0)
                {
                    UTILS.ACC.newTransfer(mkt_rs.getString("adr"),
                                          pos_rs.getString("adr"),
                                          pl,
                                          mkt_rs.getString("cur"), 
                                          "One of your speculative positions("+pos_rs.getLong("posID")+") was closed", 
                                          "", 
                                          UTILS.BASIC.hash(String.valueOf(pos_rs.getLong("posID"))), 
                                          block);
                    
                    // Clear
                    UTILS.ACC.clearTrans(UTILS.BASIC.hash(String.valueOf(pos_rs.getLong("posID"))), 
                                         "ID_ALL", 
                                         block);
                       
                    // Close position
                    UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                               + "SET status='ID_CLOSED', "
                                                   + "pl='"+UTILS.FORMAT_8.format(pl)+"', "
                                                   + "closed_pl='"+UTILS.FORMAT_8.format(pl)+"', "
                                                   + "closed_margin='"+pos_rs.getDouble("margin")+"', "
                                                   + "close_reason='ID_EXPIRED', "
                                                   + "block_end='"+block+"' "
                                             + "WHERE posID='"+pos_rs.getString("posID")+"'");
                }
           }
       }
   }
   
   public void checkSpecPos(long block, CBlockPayload block_payload) throws Exception
    {
         // Load positions
         ResultSet pos_rs=UTILS.DB.executeQuery("SELECT * "
                                                + "FROM feeds_spec_mkts_pos "
                                               + "WHERE status='ID_MARKET'");
            
            /// Next
            while (pos_rs.next())
            {
                 // Load market data
                 ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT * "
                                                        + "FROM feeds_spec_mkts "
                                                       + "WHERE mktID='"+pos_rs.getLong("mktID")+"'");
                 
                 // Next
                 mkt_rs.next();
         
                 // Last price
                 double last_price=mkt_rs.getDouble("last_price");
            
                 // SL
                 double sl=pos_rs.getDouble("sl");
            
                 // TP
                 double tp=pos_rs.getDouble("tp");
            
                 // Open
                 double open=pos_rs.getDouble("open");
            
                 // Qty
                 double qty=pos_rs.getDouble("qty");
            
                 // Tip
                 String tip=pos_rs.getString("tip");
            
                 // Margin
                 double margin=pos_rs.getDouble("margin");
            
                 // Position address
                 String pos_adr=pos_rs.getString("adr");
                 
                 // Market address
                 String mkt_adr=mkt_rs.getString("adr");
            
                 // Market currency
                 String mkt_cur=mkt_rs.getString("cur");
            
                 // PL
                 double pl=0;
                 if (tip.equals("ID_BUY"))
                    pl=(last_price-open)*qty;
                 else
                    pl=(open-last_price)*qty;
                
                 // Close
                 boolean close=false;
                 
                // Close pposition ?
                if (pl<0 && Math.abs(pl)>=margin) 
                {
                    close=true;
                    pl=-margin;
                }
                
                // SL hit
                if (tip.equals("ID_BUY") && (last_price<=sl || last_price>=tp)) 
                    close=true;
                
                // TP hit
                if (tip.equals("ID_SELL") && (last_price>=sl || last_price<=tp)) 
                    close=true;
                
                // Close
                if (close)
                {
                    this.closePos(pos_rs.getLong("posID"), block);
                }
                else
                {
                    UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                            + "SET pl='"+UTILS.FORMAT_8.format(pl)+"' "
                                          + "WHERE posID='"+pos_rs.getLong("posID")+"'");
                }
            }
    }
   
   public void closeMarket(long mktID, long block) throws Exception
   {
       // Load market data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts "
                                         + "WHERE mktID='"+mktID+"'");
       
       // Next
       rs.next();
       
       // Mkt address
       String mkt_adr=rs.getString("adr");
       
       // Mkt currency
       String mkt_cur=rs.getString("cur");
               
       // Load positions
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM feeds_spec_mkts_pos "
                               + "WHERE mktID='"+mktID+"'");
       
       // Parse
       while (rs.next())
           this.closePos(rs.getLong("posID"), block);
       
       // Remove market
       UTILS.DB.executeUpdate("DELETE FROM feeds_spec_mkts "
                                  + "WHERE mktID='"+mktID+"'");
       
       // Remove positions
       UTILS.DB.executeUpdate("DELETE FROM feeds_spec_mkts_pos "
                                  + "WHERE mktID='"+mktID+"'");
       
    }

    public void openOrder(long orderID, long block) throws Exception
    {
        // Load order data
        ResultSet order_rs=UTILS.DB.executeQuery("SELECT * "
                                                 + "FROM feeds_spec_mkts_pos "
                                                + "WHERE fsmp.posID='"+orderID+"'");
        
        // Next
        order_rs.next();
        
         // Load market data
        ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM feeds_spec_mkts "
                                              + "WHERE mktID='"+order_rs.getLong("mktID")+"'");
        
        // Margin
        double spread=mkt_rs.getDouble("spread");
        
        // Open val
        double open=0;
        if (order_rs.getString("tip").equals("ID_BUY"))
            open=order_rs.getDouble("open")+spread;
        else
            open=order_rs.getDouble("open")-spread;
        
        // Open order
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET status='ID_MARKET', "
                                    + "open='"+open+"', "
                                    + "block_start='"+block+"' "
                              + "WHERE posID='"+orderID+"'");
        
       
        
    }
    
    public void checkPendingOrders(long block) throws Exception
    {
        // Load pending orders
        ResultSet rs=UTILS.DB.executeQuery("SELECT fsmp.*, fsm.last_price "
                                          + " FROM feeds_spec_mkts_pos AS fsmp "
                                          + " JOIN feeds_spec_mkts AS fsm ON fsm.mktID=fsmp.mktID "
                                         + " WHERE fsmp.status='ID_ORDER'");
        
        // Check
        while (rs.next())
        {
            // Above open line
            if (rs.getString("open_line").equals("ID_ABOVE") && 
                rs.getDouble("last_price")>=rs.getDouble("open"))
            this.openOrder(rs.getLong("posID"), block);
                
            // Below open line
            if (rs.getString("open_line").equals("ID_BELOW") && 
                rs.getDouble("last_price")<=rs.getDouble("open"))
            this.openOrder(rs.getLong("posID"), block);
        }
    }
    
    public void updateMarkets(String feed, 
                              String branch, 
                              double price) throws Exception
    {
       // Update margin markets
       UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts "
                               + "SET last_price='"+price+"' "
                             + "WHERE feed='"+feed+"' "
                               + "AND branch='"+branch+"'");
    }
    
    
    public String getCur(String adr) throws Exception
    {
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts "
                                         + "WHERE adr='"+adr+"'");
       
       // Next
       rs.next();
       
       // Return
       return rs.getString("cur");
    }
    
    public void checkMarkets(long block) throws Exception
    {
        // Load distinct market addresses
        ResultSet rs_adr=UTILS.DB.executeQuery("SELECT DISTINCT(adr) "
                                               + "FROM feeds_spec_mkts");
        
        while (rs_adr.next())
        {
            // Balance
            double balance=UTILS.ACC.getBalance(rs_adr.getString("adr"), 
                                                this.getCur(rs_adr.getString("adr")));
                    
            // Load all positions on markets using this address
            ResultSet rs_pos=UTILS.DB.executeQuery("SELECT SUM(margin+pl) AS total "
                                                   + "FROM feeds_spec_mkts_pos AS fsmp "
                                                   + "JOIN feeds_spec_mkts AS fsm ON fsmp.mktID=fsm.mktID "
                                                  + "WHERE fsm.mktID='"+rs_adr.getString("adr")+"'");
            
            // Next
            rs_pos.next();
            
            // Total
            double total=rs_pos.getDouble("total");
            
            // Total over 95%
            if (total>balance*0.95)
            {
                // Load all markets
                ResultSet rs_mkts=UTILS.DB.executeQuery("SELECT * "
                                                        + "FROM feeds_spec_mkts "
                                                       + "WHERE adr='"+rs_adr.getString("adr")+"'");
                
                // Next
                rs_mkts.next();
                
                // Close market
                this.closeMarket(rs_mkts.getLong("mktID"), block);
            }
        }
    }
}
