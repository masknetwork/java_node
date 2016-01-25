package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class COptions 
{
    public COptions()
    {
        
    }
    
    public void closeOption(long uid, String result)
    {
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Load option data
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_bets "
                                      + "WHERE status='ID_PENDING' "
                                        + "AND uid='"+uid+"'");
           
           // Load data
           rs.next();
           
           // Currency
           String cur=rs.getString("cur");
           
           // Profit
           long p=rs.getInt("win_multiplier");
           
           // Issuer
           String adr=rs.getString("cur");
           
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
                                    UTILS.NET_STAT.last_block);
                
              // Commit
              UTILS.BASIC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL");
              
              // Close option
              UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                      + "SET status='ID_WIN' "
                                    + "WHERE uid='"+uid+"'");
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
                                    UTILS.NET_STAT.last_block);
               
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
                                    UTILS.NET_STAT.last_block);
               
              // Commit
              UTILS.BASIC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL");
              
              // Close option
              UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                      + "SET status='ID_LOST' "
                                    + "WHERE uid='"+uid+"'");
           }
        }
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 367);
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CTransPayload.java", 367);
        }
        
    }
    
  
    public void checkOptions(long block)
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
               double price=rs.getDouble("price_1");
               
               // Feed 2
               if (rs.getString("feed_symbol_2").length()==6)
                   price=(price+rs.getDouble("price_2"))/2;
               
               // Feed 3
               if (rs.getString("feed_symbol_3").length()==6)
                   price=(price+rs.getDouble("price_3"))/2;
               
               
               // Insert price
               UTILS.DB.executeUpdate("INSERT INTO feeds_pos_data(pos_type, "
                                                               + "posID, "
                                                               + "pos_symbol, "
                                                               + "val, "
                                                               + "block) VALUES("
                                                               + "'ID_OPTION', '"
                                                               +rs.getLong("uid")+"', '', '"
                                                               +price+"', '"
                                                               +UTILS.NET_STAT.last_block+"')");
               
               switch (tip)
               {
                   // Touch up option
                   case "ID_TOUCH_UP" : if (price>=rs.getDouble("val_1")) 
                                            this.closeOption(rs.getLong("uid"), "ID_LOOSE"); 
                                        
                                        // Expired option
                                        else if (block>=rs.getLong("end_block"))
                                           this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                            
                                        break;
                                        
                   // Touch down option
                   case "ID_TOUCH_DOWN" : if (price<=rs.getDouble("val_1")) 
                                            this.closeOption(rs.getLong("uid"), "ID_LOOSE"); 
                   
                                          // Expired option
                                          else if (block>=rs.getLong("end_block"))
                                             this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                        
                                          break;
                                          
                    // Not touch up option
                   case "ID_NOT_TOUCH_UP" : if (price>=rs.getDouble("val_1")) 
                                               this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                        
                                            // Expired option
                                            else if (block>=rs.getLong("end_block"))
                                               this.closeOption(rs.getLong("uid"), "ID_LOOSE"); 
                                            
                                            break;
                                        
                   // Touch down option
                   case "ID_NOT_TOUCH_DOWN" : if (price<=rs.getDouble("val_1")) 
                                                 this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                   
                                              // Expired option
                                              else if (block>=rs.getLong("end_block"))
                                                 this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                        
                                              break;
                                              
                   // Close below
                   case "ID_CLOSE_BELOW" :  // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price<rs.getDouble("val_1")) 
                                                   this.closeOption(rs.getLong("uid"), "ID_LOOSE");
                                                else
                                                   this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_CLOSE_ABOVE" :  // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price>rs.getDouble("val_1")) 
                                                   this.closeOption(rs.getLong("uid"), "ID_LOOSE");
                                                else
                                                   this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_CLOSE_BETWEEN" : // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price>rs.getDouble("val_1") && 
                                                    price<rs.getDouble("val_1")) 
                                                   this.closeOption(rs.getLong("uid"), "ID_LOOSE");
                                                else
                                                   this.closeOption(rs.getLong("uid"), "ID_WIN"); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_NOT_CLOSE_BETWEEN" : // Expired option
                                                 if (block>=rs.getLong("end_block"))
                                                 {
                                                    if (price>rs.getDouble("val_1") && 
                                                        price<rs.getDouble("val_1")) 
                                                    this.closeOption(rs.getLong("uid"), "ID_WIN");
                                                      else
                                                    this.closeOption(rs.getLong("uid"), "ID_LOOSE"); 
                                            }
                                        
                                            break;
                                            
                    // Close below
                   case "ID_CLOSE_EXACT_VALUE" : // Expired option
                                                 if (block>=rs.getLong("end_block"))
                                                 {
                                                    if (price!=rs.getDouble("val_1")) 
                                                       this.closeOption(rs.getLong("uid"), "ID_WIN");
                                                    else
                                                       this.closeOption(rs.getLong("uid"), "ID_LOOSE"); 
                                            }
                                        
                                            break;
               }
           }
        }
        catch (SQLException ex) 
       	{  
       	   UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 367);
        }
        catch (Exception ex) 
       	{  
       	   UTILS.LOG.log("Exception", ex.getMessage(), "CTransPayload.java", 367);
        }
    }
}
