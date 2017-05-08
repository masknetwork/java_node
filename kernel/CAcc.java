package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPayload;

public class CAcc 
{
    public CAcc()
    {
        
    }
    
    public void clearTrans(String hash, String tip, long block) throws Exception
    {
            // Load trans data
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM trans "
                                               + "WHERE hash='"+hash+"' "
                                                 + "AND status='ID_PENDING'");
                
                // Clear
                while (rs.next())
                {
                    if ((rs.getDouble("amount")<0 && (tip.equals("ID_ALL") || tip.equals("ID_SEND"))) ||
                        (rs.getDouble("amount")>0 && (tip.equals("ID_ALL") || tip.equals("ID_RECEIVE"))))
                    {
                      if (!rs.getString("cur").equals("MSK"))
                       this.doAssetTrans(rs.getString("src"), 
                                         rs.getDouble("amount"), 
                                         rs.getString("cur"),
                                         hash, 
                                         rs.getDouble("invested"),
                                         block);
                      else
                        this.doTrans(rs.getString("src"), 
                                     rs.getDouble("amount"), 
                                     hash,
                                     block);
                      
                      // Update trans
                      UTILS.DB.executeUpdate("UPDATE trans "
                                              + "SET status='ID_CLEARED' "
                                            + "WHERE ID='"+rs.getLong("ID")+"'");
                    }
                }
                
             
        }
        
        
       
        
        public void newTransfer(String src, 
                                String dest, 
                                double amount, 
                                boolean send_trans,
                                String cur, 
                                String expl, 
                                String escrower, 
                                String hash, 
                                long block,
                                CBlockPayload block_payload, 
                                double invested) throws Exception
        {
            this.newTrans(src, 
                          dest, 
                          -amount, 
                          send_trans,
                          cur, 
                          expl, 
                          escrower, 
                          hash, 
                          block,
                          block_payload,
                          invested);  
            
            this.newTrans(dest, 
                          src, 
                          amount, 
                          send_trans,
                          cur, 
                          expl, 
                          escrower, 
                          hash, 
                          block,
                          block_payload,
                          invested);  
        }
        
        public void newTrans(String adr, 
                             String adr_assoc, 
                             double amount, 
                             boolean send_trans,
                             String cur, 
                             String expl, 
                             String escrower, 
                             String hash, 
                             long block,
                             CBlockPayload block_payload,
                             double invested) throws Exception
        {
            // ResultSet
            ResultSet rs;
            
            // Asset fee
            double fee=0;
            
            // Asset fee address
            String fee_adr="";
            
            // Market fee
            double mkt_fee=0;
            
            // Address valid
            if (!UTILS.BASIC.isAdr(adr)) throw new Exception("Invalid address");
            
                    
            // Trans ID
            long tID=UTILS.BASIC.getID();
                
            // Add trans to trans pool
            if (amount<0)
            {
                // Balance
                double balance=this.getBalance(adr, cur);
                   
                // Funds
                if (balance<Math.abs(amount))
                   throw new Exception("Insufficient funds");
                    
                // Add to pool
	        UTILS.NETWORK.TRANS_POOL.addTrans(adr, 
		    		                  amount, 
		    		                  cur, 
		    		                  hash, 
		    		                  block);
            }
                
            // Credit transaction ?
            if (amount>0)
            {
                if (!cur.equals("MSK"))
                {
                    // Loads asset data
                    rs=UTILS.DB.executeQuery("SELECT * "
                                      + "FROM assets "
                                     + "WHERE symbol='"+cur+"'");
                  
                    // Next
                    rs.next();
                  
                    if (rs.getDouble("trans_fee")>0)
                    {
                        // Fee
                        fee=Math.abs(rs.getDouble("trans_fee")*amount/100);
                  
                        // Fee address
                        fee_adr=rs.getString("trans_fee_adr");
                    }
                }
            }
            
            if (fee<0) fee=0;
            
            // Mine ?
	    if (UTILS.WALLET.isMine(adr))
	    {
                // Insert into my transactions
                UTILS.DB.executeUpdate("INSERT INTO my_trans "
                                             + "SET userID='"+this.getAdrUserID(adr)+"', "
                                                 + "adr='"+adr+"', "
                                                 + "adr_assoc='"+adr_assoc+"', "
                                                 + "amount='"+UTILS.FORMAT_8.format(amount)+"', "
                                                 + "cur='"+cur+"', "
                                                 + "expl='"+expl+"', "
                                                 + "hash='"+hash+"', "
                                                 + "block='"+block+"', "
                                                 + "block_hash='"+UTILS.NET_STAT.actual_block_hash+"', "
                                                 + "tstamp='"+UTILS.BASIC.tstamp()+"'");
                
                if (fee>0)
                {
                    // Extract the fee
                    UTILS.DB.executeUpdate("INSERT INTO my_trans "
                                             + "SET userID='"+this.getAdrUserID(adr)+"', "
                                                 + "adr='"+adr+"', "
                                                 + "adr_assoc='"+fee_adr+"', "
                                                 + "amount='"+UTILS.FORMAT_8.format(-fee)+"', "
                                                 + "cur='"+cur+"', "
                                                 + "expl='"+expl+"', "
                                                 + "hash='"+hash+"', "
                                                 + "block='"+block+"', "
                                                 + "block_hash='"+UTILS.NET_STAT.actual_block_hash+"', "
                                                 + "tstamp='"+UTILS.BASIC.tstamp()+"'");
                
                    // Deposit the fee
                    if (!fee_adr.equals("default"))
                    UTILS.DB.executeUpdate("INSERT INTO my_trans "
                                             + "SET userID='"+this.getAdrUserID(fee_adr)+"', "
                                                 + "adr='"+fee_adr+"', "
                                                 + "adr_assoc='"+adr+"', "
                                                 + "amount='"+UTILS.FORMAT_8.format(fee)+"', "
                                                 + "cur='"+cur+"', "
                                                 + "expl='"+expl+"', "
                                                 + "hash='"+hash+"', "
                                                 + "block='"+block+"', "
                                                 + "block_hash='"+UTILS.NET_STAT.actual_block_hash+"', "
                                                 + "tstamp='"+UTILS.BASIC.tstamp()+"'");
                }
                
                UTILS.DB.executeUpdate("UPDATE web_users "
                                        + "SET unread_trans=unread_trans+1 "
                                      + "WHERE ID='"+getAdrUserID(adr)+"' ");
           }
               
            // Insert into transactions
            UTILS.DB.executeUpdate("INSERT INTO trans "
                                             + "SET src='"+adr+"', "
                                                 + "amount='"+UTILS.FORMAT_8.format(amount)+"', "
                                                 + "cur='"+cur+"', "
                                                 + "hash='"+hash+"', "
                                                 + "block='"+block+"', "
                                                 + "block_hash='"+UTILS.NET_STAT.actual_block_hash+"', "
                                                 + "tstamp='"+UTILS.BASIC.tstamp()+"', "
                                                 + "status='ID_PENDING'");
            
            if (amount>0 && !adr.equals("default") && fee>0)
            {
                // Insert fee into transactions
                UTILS.DB.executeUpdate("INSERT INTO trans "
                                             + "SET src='"+adr+"', "
                                                 + "amount='"+UTILS.FORMAT_8.format(-fee)+"', "
                                                 + "cur='"+cur+"', "
                                                 + "hash='"+hash+"', "
                                                 + "block='"+block+"', "
                                                 + "block_hash='"+UTILS.NET_STAT.actual_block_hash+"', "
                                                 + "tstamp='"+UTILS.BASIC.tstamp()+"', "
                                                 + "status='ID_PENDING'");
           
            
                // Insert fee into transactions
                UTILS.DB.executeUpdate("INSERT INTO trans "
                                             + "SET src='"+fee_adr+"', "
                                                 + "amount='"+UTILS.FORMAT_8.format(fee)+"', "
                                                 + "cur='"+cur+"', "
                                                 + "hash='"+hash+"', "
                                                 + "block='"+block+"', "
                                                 + "block_hash='"+UTILS.NET_STAT.actual_block_hash+"', "
                                                 + "tstamp='"+UTILS.BASIC.tstamp()+"', "
                                                 + "status='ID_PENDING'");
            }
            
       
            
        }
        
        // Get address owner
        public long getAdrUserID(String adr) throws Exception
        {
            if (adr.equals("default")) return 0;
            
            // Load source
               ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM my_adr WHERE adr='"+adr+"'"); 
               
               // None ?
               if (!UTILS.DB.hasData(rs)) return 0;
               
               // Next
               rs.next();
            
               // Return
               return rs.getLong("userID");
            
        }
        
        // Transfer assets
        public void doAssetTrans(String adr, 
                                    double amount, 
                                    String cur,
                                    String hash,
                                    double inv,
                                    long block) throws Exception
        {
            double balance=0;
            double new_balance=0;
            double invested=0;
                    
            // Load asset data
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM assets "
                                              + "WHERE symbol='"+cur+"'");
                     
            // Next
            rs.next();
                     
            // Load source
            rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM assets_owners "
                                    + "WHERE owner='"+adr+"' "
                                     + " AND symbol='"+cur+"'");
		         
            // Address exist ?
            if (!UTILS.DB.hasData(rs))
            {
                // Insert address
                UTILS.DB.executeUpdate("INSERT INTO assets_owners "
                                             + "SET owner='"+adr+"', "
                                                 + "symbol='"+cur+"', "
                                                 + "qty='0', "
                                                 + "block='"+block+"'");
                         
                // New balance
                new_balance=amount;
            }
            else
            {
                // Next
                rs.next();
                     
                // Balance
		balance=rs.getDouble("qty");
		     
                // New balance
                new_balance=balance+amount;
            }
                      
            // Source balance update
            UTILS.DB.executeUpdate("UPDATE assets_owners "
		                    + "SET qty="+new_balance+", "
                                        + "block='"+block+"' "
                                  + "WHERE owner='"+adr+"' "
                                    + "AND symbol='"+cur+"'");
                 
        }
        
        public void doTrans(String adr, 
                               double amount, 
                               String hash, 
                               long block) throws Exception
        {
            double balance;
            double new_balance;
            
            // Adr
            if (!UTILS.BASIC.isAdr(adr)) throw new Exception("Invalid address");
            
            // Hash
            if (!UTILS.BASIC.isHash(hash))  throw new Exception("Invalid hash");
                
            // Statement
            
		     
            // Load source
            ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM adr WHERE adr='"+adr+"'");
		         
                     // Address exist ?
                     if (!UTILS.DB.hasData(rs))
                     {
                        UTILS.DB.executeUpdate("INSERT INTO adr  "
                                                     + "SET adr='"+adr+"', "
                                                         + "balance='0', "
                                                         + "block='"+block+"', "
                                                         + "created='"+block+"'");
                        
                        // New balance
                        new_balance=Double.parseDouble(UTILS.FORMAT_8.format(amount));
                     }
                     else
                     {
                        // Next
                        rs.next();
                     
                        // Balance
		        balance=rs.getDouble("balance");
		     
                        // New balance
                        new_balance=balance+amount;
                     
                        // Format
                        new_balance=Double.parseDouble(UTILS.FORMAT_8.format(new_balance));
                     }
                     
		     // Source balance update
		     UTILS.DB.executeUpdate("UPDATE adr "
		   		             + "SET balance="+UTILS.FORMAT_8.format(new_balance)+", "
		   		                 + "block='"+block+
                                           "' WHERE adr='"+adr+"'");
         }
        
        public double getBalance(String adr, String cur) throws Exception
	{
	   ResultSet rs;
                 if (cur.equals("MSK"))
                    rs=UTILS.DB.executeQuery("SELECT * "
                                      + "FROM adr "
                                     + "WHERE adr='"+adr+"'");
                 else
                    rs=UTILS.DB.executeQuery("SELECT * "
                                      + "FROM assets_owners "
                                     + "WHERE owner='"+adr+"' "
                                       + "AND symbol='"+cur+"'");
                 
                if (UTILS.DB.hasData(rs)==true)
                {
                   // Next
                   rs.next();
                   
                   // Balance
                   double balance;
                   if (cur.equals("MSK"))
                      balance=rs.getDouble("balance");
                   else
                      balance=rs.getDouble("qty");
                   
                  
                   // Return
                   return balance;
                } 
                else
                {
                   
                    // Return
                    return 0;
                }
	   
	}
        
    public double getBalance(String adr, String cur, CBlockPayload block) throws Exception
    {
        if (block==null)
            return UTILS.NETWORK.TRANS_POOL.getBalance(adr, cur);
        else
            return UTILS.ACC.getBalance(adr, cur);
    }
    
    public void payFeed(String adr, 
                        String feed, 
                        String branch, 
                        long days, 
                        String hash, 
                        long block, 
                        CBlockPayload block_payload) throws Exception
    {
        // Load branch data
        ResultSet rs=UTILS.DB.executeQuery("SELECT feeds.adr, fb.fee "
                                           + "FROM feeds_branches AS fb "
                                           + "JOIN feeds ON feeds.symbol=fb.feed_symbol "
                                          + "WHERE fb.feed_symbol='"+feed+"' "
                                            + "AND fb.symbol='"+branch+"'");
        
        // Next
        rs.next();
        
        // Feed address
        String feed_adr=rs.getString("adr");
        
        // Fee
        double fee=rs.getDouble("fee");
        
        // Amount
        double amount=fee*days;
                
        // Transfer
        if (amount>0)
        this.newTransfer(adr, 
                         feed_adr, 
                         amount, 
                         true,
                         "MSK", 
                         "Feed payment", 
                         "", 
                         hash, 
                         block,
                         block_payload,
                         0);
    }
}
