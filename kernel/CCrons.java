// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.network.CPeer;
import wallet.network.packets.blocks.CBlockPayload;

public class CCrons 
{
   // Timer
   Timer timer;
    
   // Task
   RemindTask task;
  
   // Last agent ID
   long last_agentID;
   
   public CCrons()
   {
        // Timer
       timer = new Timer();
       task=new RemindTask();
       timer.schedule(task, 0, 1000);
   }
   
   public void checkSpecPos(long block, CBlockPayload block_payload) throws Exception
    {
         // Load positions
         ResultSet rs=UTILS.DB.executeQuery("SELECT fsmp.*, "
                                             + "fsm.last_price, "
                                             + "fsm.cur, "
                                             + "fsm.adr AS mkt_adr, "
                                             + "adr.balance AS mkt_adr_balance "
                                        + "FROM feeds_spec_mkts_pos AS fsmp "
                                        + "JOIN feeds_spec_mkts AS fsm ON fsm.mktID=fsmp.mktID "
                                        + "JOIN adr ON adr.adr=fsm.adr"
                                      + " WHERE fsmp.status='ID_MARKET'");
            
            /// Next
            while (rs.next())
            {
                 // Last price
                 double last_price=rs.getDouble("last_price");
            
                 // SL
                 double sl=rs.getDouble("sl");
            
                 // TP
                 double tp=rs.getDouble("tp");
            
                 // Open
                 double open=rs.getDouble("open");
            
                 // Qty
                 double qty=rs.getDouble("qty");
            
                 // Tip
                 String tip=rs.getString("tip");
            
                 // Margin
                 double margin=rs.getDouble("margin");
            
                 // Position address
                 String pos_adr=rs.getString("adr");
            
                 // Market address
                 String mkt_adr=rs.getString("mkt_adr");
            
                 // Market currency
                 String mkt_cur=rs.getString("cur");
            
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
                    System.out.println("Closed : "+pl+", margin : "+margin);
                }
                
                // SL hit
                if (tip.equals("ID_BUY") && (last_price<=sl || last_price>=tp)) 
                {
                    close=true;
                     System.out.println("Closed : "+last_price+", sl : "+sl+", tp : "+tp);
                }
                
                // TP hit
                if (tip.equals("ID_SELL") && (last_price>=sl || last_price<=tp)) 
                {
                    close=true;
                     System.out.println("Closed : "+last_price+", sl : "+sl+", tp : "+tp);
                }
                
                // Close
                if (close)
                {
                   // To pay
                   if (pl+margin>=0)
                   {
                       // To pay
                       double to_pay=pl+margin;
                       
                       // To bay higher than colateral ?
                       if (to_pay>rs.getDouble("mkt_adr_balance")) to_pay=rs.getDouble("mkt_adr_balance");
                       
                       // Pay
                       if (to_pay>0)
                       {
                            UTILS.ACC.newTrans(pos_adr, 
                                                 mkt_adr,
                                                 to_pay,
                                                 true,
                                                 mkt_cur, 
                                                 "One of your speculative positions was closed ", 
                                                 "", 
                                                 UTILS.BASIC.hash(String.valueOf(rs.getString("mktID"))), 
                                                 block,
                                                 block_payload,
                                                 0);
                       
                            // Take money from market
                            UTILS.ACC.newTrans(mkt_adr, 
                                                 pos_adr,
                                                 -to_pay,
                                                 true,
                                                 mkt_cur, 
                                                 "One of positions was closed ", 
                                                 "", 
                                                 UTILS.BASIC.hash(String.valueOf(rs.getString("mktID"))), 
                                                 block,
                                                 block_payload,
                                                 0);
                       
                            // Clear
                            UTILS.ACC.clearTrans(UTILS.BASIC.hash(String.valueOf(rs.getString("mktID"))), "ID_ALL", UTILS.NET_STAT.last_block);
                       }
                       
                       // Close position
                       UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                               + "SET status='ID_CLOSED', "
                                                   + "pl='"+UTILS.FORMAT_8.format(pl)+"', "
                                                   + "closed_pl='"+UTILS.FORMAT_8.format(pl)+"', "
                                                   + "closed_margin='"+margin+"', "
                                                   + "block_end='"+block+"' "
                                             + "WHERE posID='"+rs.getString("posID")+"'");
                   }
                }
                else
                {
                    UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                            + "SET pl='"+UTILS.FORMAT_8.format(pl)+"' "
                                          + "WHERE posID='"+rs.getLong("posID")+"'");
                }
                
         
            }
            
            
      
    }

    public void openOrder(long orderID, long block) throws Exception
    {
        // Load order data
        ResultSet rs=UTILS.DB.executeQuery("SELECT fsmp.*, "
                                         + "fsm.last_price, "
                                         + "fsm.spread "
                                    + "FROM feeds_spec_mkts_pos AS fsmp "
                                    + "JOIN feeds_spec_mkts AS fsm ON fsm.mktID=fsmp.mktID "
                                   + "WHERE fsmp.posID='"+orderID+"'");
        
        // Next
        rs.next();
        
        // Margin
        double spread=rs.getDouble("spread");
        
        // Open val
        double open=0;
        if (rs.getString("tip").equals("ID_BUY"))
            open=rs.getDouble("open")+spread;
        else
            open=rs.getDouble("open")-spread;
        
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
    
   public void closeOption(long uid, String result, CBlockPayload block_payload) throws Exception
    {
        try
        {
           // Statement
           
           
           // Load option data
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM feeds_bets "
                                      + "WHERE status='ID_PENDING' "
                                        + "AND betID='"+uid+"'");
           
           // Load data
           rs.next();
           
           // Currency
           String cur=rs.getString("cur");
           
           // Profit
           long p=rs.getInt("win_multiplier");
           
           // Issuer
           String adr=rs.getString("adr");
           
           // Budget
           double budget=rs.getDouble("budget");
           
           // Invested
           double invested=rs.getDouble("invested");
           
           
           // Won by issuer ?
           if (result.equals("ID_WIN"))
           {
               // Pay the winnings
               UTILS.ACC.newTrans(rs.getString("adr"), 
                                    "", 
                                    rs.getDouble("budget")+rs.getDouble("invested"), 
                                    true,
                                    rs.getString("cur"), 
                                    "You have won a bet("+String.valueOf(uid)+")", 
                                    "", 
                                    UTILS.BASIC.hash(String.valueOf(uid)), 
                                    UTILS.NET_STAT.last_block,
                                    block_payload,
                                    0);
                
              // Commit
              UTILS.ACC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL", UTILS.NET_STAT.last_block);
              
              // Close option
              UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                      + "SET status='ID_WIN' "
                                    + "WHERE betID='"+uid+"'");
           }
           else
           {
               // Load winners
               rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds_bets_pos "
                                + "WHERE betID='"+uid+"'");
               
               // Execute
               while (rs.next())
               UTILS.ACC.newTrans(rs.getString("adr"), 
                                    "", 
                                    rs.getDouble("amount")+rs.getDouble("amount")*p/100, 
                                    true,
                                    cur, 
                                    "You have won a bet("+String.valueOf(uid)+")", 
                                    "", 
                                    UTILS.BASIC.hash(String.valueOf(uid)), 
                                    UTILS.NET_STAT.last_block,
                                    block_payload,
                                    0);
               
               // Remaining
               double remain=budget-invested-invested*p/100;
               
               // Return remaining
               if (remain>0)
               UTILS.ACC.newTrans(adr, 
                                    "", 
                                    remain, 
                                    true,
                                    cur, 
                                    "You have lost a bet("+String.valueOf(uid)+") and the remaining coins were returned", 
                                    "", 
                                    UTILS.BASIC.hash(String.valueOf(uid)), 
                                    UTILS.NET_STAT.last_block,
                                    block_payload,
                                    0);
               
              // Commit
              UTILS.ACC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL", UTILS.NET_STAT.last_block);
              
              // Close option
              UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                      + "SET status='ID_LOST' "
                                    + "WHERE betID='"+uid+"'");
           }
           
          
        }
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CCrons.java", 115);
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CCrons.java", 119);
        }
        
    }
    
  
    public void checkOptions(long block, CBlockPayload block_payload) throws Exception
    {
        try
        {
           // Statement
           
           
           // Load option data
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM feeds_bets "
                                      + "WHERE status='ID_PENDING'");
           
           
           // Load data
           while (rs.next())
           {
               String tip=rs.getString("tip");
               
               // Price
               double price=rs.getDouble("last_price");
               
               // Bet ID
               long betID=rs.getLong("betID");
               
               switch (tip)
               {
                   // Touch up option
                   case "ID_TOUCH_UP" : if (price>=rs.getDouble("val_1")) 
                                            this.closeOption(betID, "ID_LOOSE", block_payload); 
                                        
                                        // Expired option
                                        else if (block>=rs.getLong("end_block"))
                                           this.closeOption(betID, "ID_WIN", block_payload); 
                                            
                                        break;
                                        
                   // Touch down option
                   case "ID_TOUCH_DOWN" : if (price<=rs.getDouble("val_1")) 
                                            this.closeOption(betID, "ID_LOOSE", block_payload); 
                   
                                          // Expired option
                                          else if (block>=rs.getLong("end_block"))
                                             this.closeOption(betID, "ID_WIN", block_payload); 
                                        
                                          break;
                                          
                    // Not touch up option
                   case "ID_NOT_TOUCH_UP" : if (price>=rs.getDouble("val_1")) 
                                               this.closeOption(betID, "ID_WIN", block_payload); 
                                        
                                            // Expired option
                                            else if (block>=rs.getLong("end_block"))
                                               this.closeOption(betID, "ID_LOOSE", block_payload); 
                                            
                                            break;
                                        
                   // Touch down option
                   case "ID_NOT_TOUCH_DOWN" : if (price<=rs.getDouble("val_1")) 
                                                 this.closeOption(betID, "ID_WIN", block_payload); 
                   
                                              // Expired option
                                              else if (block>=rs.getLong("end_block"))
                                                 this.closeOption(betID, "ID_WIN", block_payload); 
                                        
                                              break;
                                              
                   // Close below
                   case "ID_CLOSE_BELOW" :  // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price<rs.getDouble("val_1")) 
                                                   this.closeOption(betID, "ID_LOOSE", block_payload);
                                                else
                                                   this.closeOption(betID, "ID_WIN", block_payload); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_CLOSE_ABOVE" :  // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price>rs.getDouble("val_1")) 
                                                   this.closeOption(betID, "ID_LOOSE", block_payload);
                                                else
                                                   this.closeOption(betID, "ID_WIN", block_payload); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_CLOSE_BETWEEN" : // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price>rs.getDouble("val_1") && 
                                                    price<rs.getDouble("val_1")) 
                                                   this.closeOption(betID, "ID_LOOSE", block_payload);
                                                else
                                                   this.closeOption(betID, "ID_WIN", block_payload); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_NOT_CLOSE_BETWEEN" : // Expired option
                                                 if (block>=rs.getLong("end_block"))
                                                 {
                                                    if (price>rs.getDouble("val_1") && 
                                                        price<rs.getDouble("val_1")) 
                                                    this.closeOption(betID, "ID_WIN", block_payload);
                                                      else
                                                    this.closeOption(betID, "ID_LOOSE", block_payload); 
                                            }
                                        
                                            break;
                                            
                    // Close below
                   case "ID_CLOSE_EXACT_VALUE" : // Expired option
                                                 if (block>=rs.getLong("end_block"))
                                                 {
                                                    if (price!=rs.getDouble("val_1")) 
                                                       this.closeOption(betID, "ID_WIN", block_payload);
                                                    else
                                                       this.closeOption(betID, "ID_LOOSE", block_payload); 
                                            }
                                        
                                            break;
               }
           }
           
           
        }
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CCrons.java", 274);
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CCrons.java", 278);
        }
    }    
    
    public double getDeltaPercent(double val_1, double val_2, double target) throws Exception
    {
        // Delta
        double delta=Math.abs(val_1-val_2);
           
        // Delta percent for price 1
        double p=delta*target/100;
        
        // Return
        return p;
    }
    
    public double getPrice(double price_1, 
                           double price_2,
                           double max_deviation) throws Exception
    {
       // Delta percent
       double p=this.getDeltaPercent(price_1, price_2, (price_1+price_2)/2);
       if (p>max_deviation) return -1000000000;
           
       // Return average
       return (price_1+price_2)/2; 
    }
    
    public double getPrice(double price_1,
                           double price_2,
                           double price_3,
                           double max_deviation) throws Exception
    {
          // Delta percent
           double p1=this.getDeltaPercent(price_1, price_2, (price_1+price_2)/2);
           double p2=this.getDeltaPercent(price_1, price_3, (price_1+price_3)/2);
           double p3=this.getDeltaPercent(price_2, price_3, (price_2+price_3)/2);
           
           // Get average
           if (p1<max_deviation && 
               p2<max_deviation && 
               p3<max_deviation)
           {
               return ((price_1+price_2+price_3)/3);
           }
           else
           {
               // Price 1 and 2
               if (p1<max_deviation && 
                   p2<max_deviation)
               return ((price_1+price_2)/2);
               
               // Price 1 and 3
               if (p1<max_deviation && 
                   p3<max_deviation)
               return ((price_1+price_3)/2);
               
               // Price 2 and 3
               if (p2<max_deviation && 
                   p3<max_deviation)
               return ((price_2+price_3)/2);
           }
      
        return -1000000000;
    }
    
    public void updateMarkets(String feed, String branch, double price) throws Exception
    {
       // Update margin markets
       UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts "
                               + "SET last_price='"+price+"' "
                             + "WHERE feed='"+feed+"' "
                               + "AND branch='"+branch+"'");
       
       // Update bets
       UTILS.DB.executeUpdate("UPDATE feeds_bets "
                               + "SET last_price='"+price+"' "
                             + "WHERE feed='"+feed+"' "
                               + "AND branch='"+branch+"'");
    }
   
    
    
     public void runCrons(long block, CBlockPayload block_payload) throws Exception
     {
        // Run options
        this.checkOptions(block, block_payload);
     }
     
     public void setStatus()throws Exception
     {
           // Low memory ?
           if (UTILS.runtime.freeMemory()<2000) 
           {
               System.out.println("Exit virtual machine (run out of memory)");
               System.exit(0);
           }
           
           // Update
           UTILS.DB.executeUpdate("UPDATE web_sys_data "
                                   + "SET last_ping='"+UTILS.BASIC.tstamp()+"', "
                                       + "max_memory='"+UTILS.runtime.maxMemory()+"', "
                                       + "version='0.9.0', "
                                       + "free_memory='"+UTILS.runtime.freeMemory()+"', "
                                       + "total_memory='"+UTILS.runtime.totalMemory()+"', "
                                       + "procs='"+UTILS.runtime.availableProcessors()+"', "
                                       + "threads_no='"+Thread.getAllStackTraces().size()+"'");
           
           // Insert into log
           UTILS.DB.executeUpdate("INSERT INTO status_log "
                                        + "SET total_mem='"+UTILS.runtime.totalMemory()+"', "
                                            + "free_mem='"+UTILS.runtime.freeMemory()+"', "
                                            + "threads='"+Thread.getAllStackTraces().size()+"', "
                                            + "tstamp='"+UTILS.BASIC.tstamp()+"'");
           
           // Delete old records
           UTILS.DB.executeUpdate("DELETE FROM status_log "
                                      + "WHERE tstamp<"+(UTILS.BASIC.tstamp()-86400));
     }
     
     class RemindTask extends TimerTask 
     {  
       @Override
       public void run()
       {  
           try
           {
               // Load web ops
               UTILS.WEB_OPS.loadWebOps();
               
               // Sync
               if (UTILS.SYNC!=null) UTILS.SYNC.tick();
               
               // Status
               setStatus();
              
           }
           catch (Exception ex)
           {
               System.out.println(ex.getMessage());
           }
       }
     }
}
