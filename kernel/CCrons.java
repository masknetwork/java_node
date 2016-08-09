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
                                    +"' WHERE betID='"+rs.getLong("betID")+"'");
               }
               
               // Two feeds
               if (rs.getString("feed_2").length()==6  && 
                   rs.getString("feed_3").length()!=6)
               {
                     UTILS.DB.executeUpdate("UPDATE "+table
                                       + " SET last_price='"+this.getPrice(rs.getDouble("p1"), 
                                                                          rs.getDouble("p2"), 
                                                                          5)
                                    +"' WHERE betID='"+rs.getLong("betID")+"'");
                     
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
                                    +"' WHERE betID='"+rs.getLong("betID")+"'");
                    
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
   
    
    
     public void runCrons(long block, CBlockPayload block_payload) throws Exception
     {
         try
         {
             // Run options
             this.checkOptions(block, block_payload);
         
             // Update markets
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
