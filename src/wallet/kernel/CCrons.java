// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.agents.CAgent;
import wallet.network.CPeer;
import wallet.network.packets.blocks.CBlockPayload;

public class CCrons 
{
   // Timer
   Timer timer;
    
   // Task
   RemindTask task;
   
   public CCrons()
   {
        // Timer
       timer = new Timer();
       task=new RemindTask();
       timer.schedule(task, 0, 1000);
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
                                        + "AND mktID='"+uid+"'");
           
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
                                    + "WHERE mktID='"+uid+"'");
           }
           else
           {
               // Load winners
               rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds_bets_pos "
                                + "WHERE bet_uid='"+uid+"'");
               
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
                                    + "WHERE mktID='"+uid+"'");
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
               long betID=rs.getLong("mktID");
               
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
    
    public void updateMarkets(String table) throws Exception
    {
        // Price 1
           ResultSet rs=UTILS.DB.executeQuery("SELECT fsm.*, "
                                             + "branch_1.val AS p1, "
                                             + "branch_2.val AS p2, "
                                             + "branch_3.val AS p3 "
                                       + "FROM "+table+" AS fsm "
                                  + "LEFT JOIN feeds_branches AS branch_1 ON (branch_1.feed_symbol=fsm.feed_1 "
                                                                           + "AND branch_1.symbol=fsm.branch_1) "
                                  + "LEFT JOIN feeds_branches AS branch_2 ON (branch_2.feed_symbol=fsm.feed_2 "
                                                                           + "AND branch_2.symbol=fsm.branch_2) "
                                  + "LEFT JOIN feeds_branches AS branch_3 ON (branch_3.feed_symbol=fsm.feed_3 "
                                                                           + "AND branch_3.symbol=fsm.branch_3)");
          
           
           // Load markets
           double price=0;
           while (rs.next())
           {
               // One feed
               if (rs.getString("feed_2").length()!=6  && 
                   rs.getString("feed_3").length()!=6)
               {
                   // Set price
                   price=rs.getDouble("p1");
                   
                   UTILS.DB.executeUpdate("UPDATE "+table
                                       + " SET last_price='"+rs.getDouble("p1")
                                    +"' WHERE mktID='"+rs.getLong("mktID")+"'");
               }
               
               // Two feeds
               if (rs.getString("feed_2").length()==6  && 
                   rs.getString("feed_3").length()!=6)
               {
                     UTILS.DB.executeUpdate("UPDATE "+table
                                       + " SET last_price='"+this.getPrice(rs.getDouble("p1"), 
                                                                          rs.getDouble("p2"), 
                                                                          5)
                                    +"' WHERE mktID='"+rs.getLong("mktID")+"'");
                     
                     // Set price
                     price=this.getPrice(rs.getDouble("p1"), 
                                         rs.getDouble("p2"), 5);
               }
               
               // Three feeds
               if (rs.getString("feed_2").length()==6  && 
                   rs.getString("feed_3").length()==6)
               {
                    UTILS.DB.executeUpdate("UPDATE "+table
                                       + " SET last_price='"+this.getPrice(rs.getDouble("p1"), 
                                                                          rs.getDouble("p2"), 
                                                                          rs.getDouble("p3"), 
                                                                          5)
                                    +"' WHERE mktID='"+rs.getLong("mktID")+"'");
                    
                    // Set price
                    price=this.getPrice(rs.getDouble("p1"), 
                                        rs.getDouble("p2"), 
                                        rs.getDouble("p3"), 
                                        5);
;               
               }
               
           }
           
           // Close
          
           
       
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
                                                 rs.getString("rowhash"), 
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
                                                 rs.getString("rowhash"), 
                                                 block,
                                                 block_payload,
                                                 0);
                       
                            // Clear
                            UTILS.ACC.clearTrans(rs.getString("rowhash"), "ID_ALL", UTILS.NET_STAT.last_block);
                       }
                       
                       // Close position
                       UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                               + "SET status='ID_CLOSED', "
                                                   + "pl='"+pl+"', closed_pl='"+pl+"', "
                                                   + "closed_margin='"+margin+"' "
                                             + "WHERE posID='"+rs.getString("posID")+"'");
                   }
                }
                else
                {
                    UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                            + "SET pl='"+pl+"', "
                                                + "last_block='"+block+"' "
                                          + "WHERE posID='"+rs.getLong("posID")+"'");
                }
                
         
            }
            
            
      
    }
    
    public void payInterest(CBlockPayload block_payload) throws Exception
    {
        // Statement
        
        
        // Load address balance
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM adr "
                                   + "WHERE adr='default'");
        
        // Next
        rs.next();
        
        // Balance
        double balance=rs.getDouble("balance");
        
        // In circulation
        double in_circ=100000000-balance;
        
        // Interest
        double interest=UTILS.BASIC.round(50000000/in_circ/365/24, 4);
        
        // Amount to pay
        rs=UTILS.DB.executeQuery("SELECT SUM(balance) AS total "
                          + "FROM adr "
                         + "WHERE (last_interest<="+(block_payload.block-(UTILS.NET_STAT.blocks_per_day/24))+" OR last_interest=0) "
                           + "AND adr<>'default' AND balance>=1");
      
        // Has data
        rs.next();
        
        // Amount
        double total=rs.getDouble("total");
        
        if (total>0)
        {
            // Amount to pay
            double amount=total*interest/100;
            
            // Debit game fund
            UTILS.ACC.newTrans("default",
                                 "default",
                                 -amount, 
                                 false,
                                "MSK", 
                                 "Interest paid", 
                                 "", 
                                 block_payload.hash, 
                                 block_payload.block,
                                 block_payload,
                                 0);
            
            // Clear
            UTILS.ACC.clearTrans(block_payload.hash, "ID_ALL", UTILS.NET_STAT.last_block);
            
            // Credit addresses
            UTILS.DB.executeUpdate("UPDATE adr "
                                    + "SET balance=balance+("+interest+"*balance/100), "
                                        + "last_interest='"+block_payload.block+"', "
                                        + "block='"+block_payload.block+"' "
                                  + "WHERE (last_interest<="+(block_payload.block-60)+" OR last_interest=0) "
                                    + "AND adr<>'default' "
                                    + "AND balance>=1");
        }
        
        
        
    }
    
    public void openOrder(long orderID) throws Exception
    {
        // Statement
        
        
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
                                    + "open='"+open+"' "
                              + "WHERE posID='"+orderID+"'");
        
       
        
    }
    
    public void checkPendingOrders() throws Exception
    {
        // Statement
        
        
        // Load pending orders
        ResultSet rs=UTILS.DB.executeQuery("SELECT fsmp.*, fsm.last_price "
                                   + " FROM feeds_spec_mkts_pos AS fsmp "
                                   + " JOIN feeds_spec_mkts AS fsm ON fsm.mktID=fsmp.mktID "
                                  + " WHERE fsmp.status='ID_PENDING'");
        
        // Check
        while (rs.next())
        {
            // Buy order
            if (rs.getString("tip").equals("ID_BUY"))
            {
                // Above open line
                if (rs.getString("open_line").equals("ID_ABOVE") && 
                    rs.getDouble("last_price")<=rs.getDouble("open"))
                this.openOrder(rs.getLong("posID"));
                
                // Below open line
                if (rs.getString("open_line").equals("ID_BELOW") && 
                    rs.getDouble("last_price")>=rs.getDouble("open"))
                this.openOrder(rs.getLong("posID"));
            }
            else
            {
                
            }
        }
        
        
    }
    
     public void runCrons(long block, CBlockPayload block_payload) throws Exception
     {
         try
         {
             // Run options
             this.checkOptions(block, block_payload);
         
             // Check spec positions
             this.checkSpecPos(block, block_payload);
         
             // Pending orders
             this.checkPendingOrders();
         
             // Update markets
             this.updateMarkets("feeds_spec_mkts");
             this.updateMarkets("feeds_bets");
         }
         catch (Exception ex)
         {
             UTILS.LOG.log("SQLException", ex.getMessage(), "CCrons.java", 57);
         }
     }
     
    
     public void checkMyAgents() throws Exception
     {
         try
         {
         // Statement
         
         
         // Load agents
         ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM agents_mine "
                                    + "WHERE compiler='SURfUEVORElORw=='");
         
         // Compile
         while (rs.next())
         {
             CAgent agent=new CAgent(rs.getLong("ID"), true, UTILS.NET_STAT.last_block);
             agent.parse();
         }
         
         // Load agents
         rs=UTILS.DB.executeQuery("SELECT * "
                           + "FROM agents_mine "
                           + "WHERE run='ID_PENDING'");
         
         // Compile
         while (rs.next())
         {
             UTILS.DB.executeUpdate("UPDATE agents_mine "
                                     + "SET run='' "
                                   + "WHERE ID='"+rs.getLong("ID")+"'");
              
             CAgent agent=new CAgent(rs.getLong("ID"), true, UTILS.NET_STAT.last_block);
             
             // Load transaction
             if (rs.getString("simulate_target").equals("ID_TRANS"))
             agent.VM.SYS.EVENT.loadTrans(rs.getString("trans_sender"), 
                                          rs.getDouble("trans_amount"), 
                                          rs.getString("trans_cur"), 
                                          UTILS.BASIC.base64_decode(rs.getString("trans_mes")), 
                                          rs.getString("trans_escrower"), 
                                          UTILS.BASIC.hash(String.valueOf(Math.random())));
             
             // Message
             if (rs.getString("simulate_target").equals("ID_MES"))
             agent.VM.SYS.EVENT.loadMessage(rs.getString("mes_sender"), 
                                            UTILS.BASIC.base64_decode(rs.getString("mes_subj")),
                                            UTILS.BASIC.base64_decode(rs.getString("mes_mes")),
                                            UTILS.BASIC.hash(String.valueOf(Math.random())));
             
             // Block
             if (rs.getString("simulate_target").equals("ID_BLOCK"))
             agent.VM.SYS.EVENT.loadBlock(rs.getString("block_hash"), 
                                          rs.getLong("block_no"),
                                          rs.getLong("block_nonce"));
            
             // Target
             switch (rs.getString("simulate_target"))
             {
                 case "ID_TRANS" : agent.execute("#transaction#", true, rs.getLong("block_no")); 
                                   break;
                                   
                 case "ID_MES" : agent.execute("#message#", true, rs.getLong("block_no")); 
                                 break;
                                 
                 case "ID_BLOCK" : agent.execute("#block#", true, rs.getLong("block_no")); 
                                   break;
                                   
                 case "ID_DEFAULT" : agent.execute("#start#", true, rs.getLong("block_no")); 
                                     break;
             }
             
            
         }
         
           
           
         }
         catch (Exception ex)
         {
            throw new Exception(ex.getMessage());
         }
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
               
               // My agents
               checkMyAgents();
              
           }
           catch (Exception ex)
           {
               System.out.println(ex.getMessage());
           }
       }
     }
}