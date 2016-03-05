// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.blocks.CBlockPayload;

public class CCrons 
{
   public void CCrons()
   {
       
   }
   
   public void closeOption(long uid, String result, CBlockPayload block_payload) throws Exception
    {
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Load option data
           ResultSet rs=s.executeQuery("SELECT * "
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
               UTILS.BASIC.newTrans(rs.getString("adr"), 
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
              UTILS.BASIC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL");
              
              // Close option
              UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                      + "SET status='ID_WIN' "
                                    + "WHERE mktID='"+uid+"'");
           }
           else
           {
               // Load winners
               rs=s.executeQuery("SELECT * "
                                 + "FROM feeds_bets_pos "
                                + "WHERE bet_uid='"+uid+"'");
               
               // Execute
               while (rs.next())
               UTILS.BASIC.newTrans(rs.getString("adr"), 
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
               UTILS.BASIC.newTrans(adr, 
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
              UTILS.BASIC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL");
              
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
           Statement s=UTILS.DB.getStatement();
           
           // Load option data
           ResultSet rs=s.executeQuery("SELECT * "
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
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
        
           // Price 1
           ResultSet rs=s.executeQuery("SELECT fsm.*, "
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
           s.close();
        }
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CCrons.java", 433);
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CCrons.java", 437);
        }
    }
    
    public void checkSpecPos(long block, CBlockPayload block_payload) throws Exception
    {
        try
        {
            // Statement
            Statement s=UTILS.DB.getStatement();
            
            // Load positions
            ResultSet rs=s.executeQuery("SELECT fsmp.*, "
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
                            UTILS.BASIC.newTrans(pos_adr, 
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
                            UTILS.BASIC.newTrans(mkt_adr, 
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
                            UTILS.BASIC.clearTrans(rs.getString("rowhash"), "ID_ALL");
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
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CCrons.java", 585);
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CCrons.java", 589);
        }
    }
    
    public void payInterest(CBlockPayload block_payload) throws Exception
    {
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load address balance
        ResultSet rs=s.executeQuery("SELECT * "
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
        rs=s.executeQuery("SELECT SUM(balance) AS total "
                          + "FROM adr "
                         + "WHERE (last_interest<="+(block_payload.block-(UTILS.NET_STAT.blocks_per_day/24))+" OR last_interest=0) "
                           + "AND adr<>'default'");
      
        // Has data
        rs.next();
        
        // Amount
        double total=rs.getDouble("total");
        
        if (total>0)
        {
            // Amount to pay
            double amount=total*interest/100;
            
            // Debit game fund
            UTILS.BASIC.newTrans("default",
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
            UTILS.BASIC.clearTrans(block_payload.hash, "ID_ALL");
            
            // Credit addresses
            UTILS.DB.executeUpdate("UPDATE adr "
                                    + "SET balance=balance+("+UTILS.FORMAT.format(interest)+"*balance/100), "
                                        + "last_interest='"+block_payload.block+"' "
                                  + "WHERE (last_interest<="+(block_payload.block-(UTILS.NET_STAT.blocks_per_day/24))+" OR last_interest=0) "
                                    + "AND adr<>'default'");
           
        }
    }
    
    public void openOrder(long orderID) throws Exception
    {
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load order data
        ResultSet rs=s.executeQuery("SELECT fsmp.*, "
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
        Statement s=UTILS.DB.getStatement();
        
        // Load pending orders
        ResultSet rs=s.executeQuery("SELECT fsmp.*, fsm.last_price "
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
         
             // Pay interest
             this.payInterest(block_payload);
             
             // Pending orders
             this.checkPendingOrders();
         
             // Update markets
             this.updateMarkets("feeds_spec_mkts");
             this.updateMarkets("feeds_assets_mkts");
             this.updateMarkets("feeds_bets");
         }
         catch (Exception ex)
         {
             UTILS.LOG.log("SQLException", ex.getMessage(), "CCrons.java", 57);
         }
     }
}
