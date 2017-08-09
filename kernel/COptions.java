package wallet.kernel;

import java.sql.ResultSet;
import wallet.network.packets.blocks.CBlockPayload;

public class COptions 
{
    public void COptions()
    {
        
    }
    
    public void closeOption(long uid, 
                            String result, 
                            long block, 
                            CBlockPayload block_payload) throws Exception
    {
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
                                    rs.getString("cur"), 
                                    "You have won a bet - "+String.valueOf(uid), 
                                    "", 
                                    UTILS.BASIC.hash(String.valueOf(uid)), 
                                    block);
                
              // Commit
              UTILS.ACC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL", block);
              
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
                                    cur, 
                                    "You have won a bet("+String.valueOf(uid)+")", 
                                    "", 
                                    UTILS.BASIC.hash(String.valueOf(uid)), 
                                    block);
               
               // Remaining
               double remain=budget-invested-invested*p/100;
               
               // Return remaining
               if (remain>0)
               UTILS.ACC.newTrans(adr, 
                                    "", 
                                    remain, 
                                    cur, 
                                    "You have lost a bet and the remaining coins were returned", 
                                    "", 
                                    UTILS.BASIC.hash(String.valueOf(uid)), 
                                    block);
               
              // Commit
              UTILS.ACC.clearTrans(UTILS.BASIC.hash(String.valueOf(uid)), "ID_ALL", block);
              
              // Close option
              UTILS.DB.executeUpdate("UPDATE feeds_bets "
                                      + "SET status='ID_LOST' "
                                    + "WHERE betID='"+uid+"'");
           }
    }
    
  
    public void checkOptions(long block, CBlockPayload block_payload) throws Exception
    {
        // Load option data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_bets "
                                          + "WHERE status='ID_PENDING'");
           
        // Has data ?
        
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
                                            this.closeOption(betID, "ID_LOOSE", block, block_payload); 
                                        
                                        // Expired option
                                        else if (block>=rs.getLong("end_block"))
                                           this.closeOption(betID, "ID_WIN", block, block_payload); 
                                            
                                        break;
                                        
                   // Touch down option
                   case "ID_TOUCH_DOWN" : if (price<=rs.getDouble("val_1")) 
                                            this.closeOption(betID, "ID_LOOSE", block, block_payload); 
                   
                                          // Expired option
                                          else if (block>=rs.getLong("end_block"))
                                             this.closeOption(betID, "ID_WIN", block, block_payload); 
                                        
                                          break;
                                          
                    // Not touch up option
                   case "ID_NOT_TOUCH_UP" : if (price>=rs.getDouble("val_1")) 
                                               this.closeOption(betID, "ID_WIN", block, block_payload); 
                                        
                                            // Expired option
                                            else if (block>=rs.getLong("end_block"))
                                               this.closeOption(betID, "ID_LOOSE", block, block_payload); 
                                            
                                            break;
                                        
                   // Touch down option
                   case "ID_NOT_TOUCH_DOWN" : if (price<=rs.getDouble("val_1")) 
                                                 this.closeOption(betID, "ID_WIN", block, block_payload); 
                   
                                              // Expired option
                                              else if (block>=rs.getLong("end_block"))
                                                 this.closeOption(betID, "ID_LOOSE", block, block_payload); 
                                        
                                              break;
                                              
                   // Close below
                   case "ID_CLOSE_BELOW" :  // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price<rs.getDouble("val_1")) 
                                                   this.closeOption(betID, "ID_LOOSE", block, block_payload);
                                                else
                                                   this.closeOption(betID, "ID_WIN", block, block_payload); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_CLOSE_ABOVE" :  // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price>rs.getDouble("val_1")) 
                                                   this.closeOption(betID, "ID_LOOSE", block, block_payload);
                                                else
                                                   this.closeOption(betID, "ID_WIN", block, block_payload); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_CLOSE_BETWEEN" : // Expired option
                                            if (block>=rs.getLong("end_block"))
                                            {
                                                if (price>=rs.getDouble("val_1") && 
                                                    price<=rs.getDouble("val_2")) 
                                                   this.closeOption(betID, "ID_LOOSE", block, block_payload);
                                                else
                                                   this.closeOption(betID, "ID_WIN", block, block_payload); 
                                            }
                                        
                                            break;
                                            
                   // Close below
                   case "ID_NOT_CLOSE_BETWEEN" : // Expired option
                                                 if (block>=rs.getLong("end_block"))
                                                 {
                                                    if (price>=rs.getDouble("val_1") && 
                                                        price<=rs.getDouble("val_2")) 
                                                    this.closeOption(betID, "ID_WIN", block, block_payload);
                                                      else
                                                    this.closeOption(betID, "ID_LOOSE", block, block_payload); 
                                            }
                                        
                                            break;
                                            
                    // Close below
                   case "ID_CLOSE_EXACT_VALUE" : // Expired option
                                                 if (block>=rs.getLong("end_block"))
                                                 {
                                                    if (price!=rs.getDouble("val_1")) 
                                                       this.closeOption(betID, "ID_WIN", block, block_payload);
                                                    else
                                                       this.closeOption(betID, "ID_LOOSE", block, block_payload); 
                                            }
                                        
                                            break;
               }
           }
           
           
        
 
    }    
    
    
    public void updateMarkets(String feed, 
                              String branch, 
                              double price) throws Exception
    {
        // Check feed
        if (!UTILS.BASIC.isSymbol(feed))
           throw new Exception("Invalid feed - COptions.java, 246");

        // Check branch
        if (!UTILS.BASIC.isSymbol(branch))
           throw new Exception("Invalid branch - COptions.java, 249");
        
       // Update bets
       UTILS.DB.executeUpdate("UPDATE feeds_bets "
                               + "SET last_price='"+price+"' "
                             + "WHERE feed='"+feed+"' "
                               + "AND branch='"+branch+"'");
    }
}
