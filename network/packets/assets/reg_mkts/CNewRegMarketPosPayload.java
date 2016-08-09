// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.agents.CAgent;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewRegMarketPosPayload extends CPayload
{
    // Market symbol
    long mktID;
    
    // Tip
    String tip;
                                   
    // Price
    double price;
                                   
    // Qty
    double qty;
                                   
    // Market days
    long days;
    
    // Order ID
    long orderID;
    
    
                                           
    public CNewRegMarketPosPayload(String adr,
                                   long mktID,
                                   String tip,
                                   double price,
                                   double qty,
                                   long days) throws Exception
    {
        // Constructor
        super(adr);
        
        // Market symbol
        this.mktID=mktID;
    
        // Tip
        this.tip=tip;
                                   
        // Price
        this.price=price;
        
        // Load market data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM assets_mkts "
                                   + "WHERE mktID='"+this.mktID+"'");
        
        // Next
        rs.next();
        
        // Decimals
        long decimals=rs.getLong("decimals");
         
        // Qty
        this.qty=UTILS.BASIC.round(qty, (int)decimals);
                                   
        // Market days
        this.days=days;
        
        // Order ID
        this.orderID=UTILS.BASIC.getID();
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.mktID+
                              this.tip+
                              String.valueOf(this.price)+
                              String.valueOf(this.qty)+
                              String.valueOf(this.days)+
                              this.orderID);
        
        // Sign
        this.sign();
    }
    
     public void check(CBlockPayload block) throws Exception
     {
        // Check order ID
        if (!UTILS.BASIC.validID(this.orderID))
           throw new Exception("Invalid order ID - CNewRegMarketPosPayload.java");
        
        // Check marketID
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM assets_mkts "
                                          + "WHERE mktID='"+this.mktID+"'");
            
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid market ID - CNewRegMarketPosPayload.java");
            
        // Next
        rs.next();
        
        // Order expire after market ?
        long expire=this.block+this.days*1440;
        if (expire>rs.getLong("expire"))
           throw new Exception("Invalid expiration date - CNewRegMarketPosPayload.java");
        
        // Tip
        if (!this.tip.equals("ID_BUY") && 
           !this.tip.equals("ID_SELL"))
             throw new Exception("Invalid order type- CNewRegMarketPosPayload.java");
        
        // Can spend
        if (!UTILS.BASIC.canSpend(this.target_adr, this.block))
           throw new Exception("Address can't spend coins - CNewRegMarketPosPayload.java");
        
        // Price
        if (this.price<0.00000001)
           throw new Exception("Invalid price - CNewRegMarketPosPayload.java");
        
        // Qty
        this.qty=UTILS.BASIC.round(qty, (int)rs.getLong("decimals"));
        
        if (this.qty<0.00000001)
           throw new Exception("Invalid qty - CNewRegMarketPosPayload.java");
        
        // Market days
        if (this.days<1)
           throw new Exception("Invalid days - CNewRegMarketPosPayload.java");
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.mktID+
                                  this.tip+
                                  String.valueOf(this.price)+
                                  String.valueOf(this.qty)+
                                  String.valueOf(this.days)+
                                  this.orderID);
             
        // Valid hash
        if (!this.hash.equals(h))
           throw new Exception("Invalid hash - CNewRegMarketPosPayload.java");
        
        // Asset
        String asset=rs.getString("asset");
        
        // Cur
        String cur=rs.getString("cur");
        
        // Asset balance
        double balance_asset=UTILS.ACC.getBalance(this.target_adr, rs.getString("asset"), block);
                    
        // Currency balance
        double balance_cur=UTILS.ACC.getBalance(this.target_adr, rs.getString("cur"), block);
            
        // Sell
        if (this.tip.equals("ID_SELL"))
        {
                // Asset balance
                if (balance_asset<this.qty)
                     throw new Exception("Innsuficient assets - CNewRegMarketPosPayload.java");
                
                // Buy orders
                rs=UTILS.DB.executeQuery("SELECT * "
                                  + "FROM assets_mkts_pos "
                                 + "WHERE mktID='"+this.mktID+"' "
                                   + "AND tip='ID_BUY' "
                                   + "AND price>="+this.price+" "
                              + "ORDER BY price DESC");
                
               // Sold
               double remain=this.qty;
                
               // Qty
               double qty;
               
                // Has data ?
                if (UTILS.DB.hasData(rs))
                {
                   // Load assets
                   while (rs.next())
                   {
                      if (remain>0)
                      {
                         // Remain
                         if (remain>=rs.getDouble("qty"))
                            qty=rs.getDouble("qty");
                         else
                            qty=remain;
                          
                         // Transfer assets
                         UTILS.ACC.newTransfer(this.target_adr, 
                                                 rs.getString("adr"),
                                                 qty,
                                                 true,
                                                 asset, 
                                                 "New short position on market "+rs.getString("mktID"), 
                                                 "", 
                                                 this.hash, 
                                                 this.block,
                                                 block,
                                                 0);
                      
                         // Receive coins
                         UTILS.ACC.newTrans(this.target_adr,
                                              "none", 
                                              qty*rs.getDouble("price"),
                                              true,
                                              cur, 
                                              "New short position on market "+rs.getString("mktID"), 
                                              "", 
                                              this.hash, 
                                              this.block,
                                              block,
                                              0);
                         
                         // Remain
                         remain=remain-qty;
                      }
                   }
                }
                
                // Put the rest on market
                if (remain>0)
                UTILS.ACC.newTrans(this.target_adr,
                                     "none", 
                                     -remain,
                                     true,
                                     asset, 
                                     "New short position on market "+this.mktID, 
                                     "", 
                                     this.hash, 
                                     this.block,
                                     block,
                                     0);
        }
        else
        {
                // Asset balance
                if (balance_cur<this.qty*this.price)
                     throw new Exception("Innsuficient funds - CNewRegMarketPosPayload.java");
                
                // Buy orders
                rs=UTILS.DB.executeQuery("SELECT * "
                                  + "FROM assets_mkts_pos "
                                 + "WHERE mktID='"+this.mktID+"' "
                                   + "AND tip='ID_SELL' "
                                   + "AND price<="+this.price+" "
                              + "ORDER BY price ASC");
                
               // Sold
               double remain=this.qty;
                
               // Qty
               double qty;
               
                // Has data ?
                if (UTILS.DB.hasData(rs))
                {
                   // Load assets
                   while (rs.next())
                   {
                      if (remain>0)
                      {
                         // Remain
                         if (remain>=rs.getDouble("qty"))
                            qty=rs.getDouble("qty");
                         else
                            qty=remain;
                          
                         // Transfer currency
                         UTILS.ACC.newTransfer(this.target_adr, 
                                                 rs.getString("adr"),
                                                 qty*rs.getDouble("price"),
                                                 true,
                                                 cur, 
                                                 "New long position on market "+rs.getString("mktID"), 
                                                 "", 
                                                 this.hash, 
                                                 this.block,
                                                 block,
                                                 0);
                      
                         // Receive assets
                         UTILS.ACC.newTrans(this.target_adr,
                                              "none", 
                                              qty,
                                              true,
                                              asset, 
                                              "New long position on market "+rs.getString("mktID"), 
                                              "", 
                                              this.hash, 
                                              this.block,
                                              block,
                                              0);
                         
                        // Remain
                        remain=remain-qty;
                      }
                   }
                }
                
                // Receive coins
                if (remain>0)
                UTILS.ACC.newTrans(this.target_adr,
                                     "none", 
                                     -remain*this.price,
                                     true,
                                     cur, 
                                     "New long position on market "+this.mktID, 
                                     "", 
                                     this.hash, 
                                     this.block,
                                     block,
                                     0);
            }
            
        
     }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Asset has contract attached ?
        long asset_aID=UTILS.BASIC.getMarketContract(this.mktID, "assets_mkts");
        
        // Has contract ?
        if (asset_aID>0)
        {
            // Load message
            CAgent AGENT=new CAgent(asset_aID, false, this.block);
                    
            // Set Message 
            AGENT.VM.SYS.EVENT.loadOpenAssetOrder(this.orderID,
                                                  this.tip, 
                                                  this.qty,
                                                  this.price);
                    
            // Execute
            AGENT.execute("#new_asset_mkt_order#", false, this.block);
            
            // Aproved ?
            if (!AGENT.VM.SYS.EVENT.OPEN_ORDER.APPROVED)
                throw new Exception("Rejected - CNewRegMarketPosPayload.java");
        }
       
        // Statement
        
        
        // Load mkt data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM assets_mkts "
                                   + "WHERE mktID='"+this.mktID+"'");
            
        // Next
        rs.next();
        
            
        // Asset
        String asset=rs.getString("asset");
        
        // Cur
        String cur=rs.getString("cur");
        
        // Asset balance
        double balance_asset=UTILS.ACC.getBalance(this.target_adr, rs.getString("asset"), block);
                    
        // Currency balance
        double balance_cur=UTILS.ACC.getBalance(this.target_adr, rs.getString("cur"), block);
            
        // Sell
        if (this.tip.equals("ID_SELL"))
        {
            // Buy orders
            rs=UTILS.DB.executeQuery("SELECT * "
                              + "FROM assets_mkts_pos "
                             + "WHERE mktID='"+this.mktID+"' "
                               + "AND tip='ID_BUY' "
                               + "AND price>="+this.price+" "
                          + "ORDER BY price DESC");
                
            // Sold
            double remain=this.qty;
                
            // Qty
            double qty=0;
               
            // Has data ?
            if (UTILS.DB.hasData(rs))
            {
                   // Load assets
                   while (rs.next())
                   {
                      if (remain>0)
                      {
                         // Remain
                         if (remain>=rs.getDouble("qty"))
                         {
                            // Qty
                            qty=rs.getDouble("qty");
                            
                            // Remove order
                            UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                                       + "WHERE orderID='"+rs.getLong("orderID")+"'");
                         }
                         else
                         {
                            // Qty
                            qty=remain;
                            
                            // Update
                            UTILS.DB.executeUpdate("UPDATE assets_mkts_pos "
                                                    + "SET qty=qty-"+qty+", "
                                                        + "block='"+this.block+"' "
                                                  + "WHERE orderID='"+rs.getLong("orderID")+"'");
                         }
                         
                         // Remain
                         remain=remain-qty;
                      }
                      
                      // Insert order
                      UTILS.DB.executeUpdate("INSERT INTO assets_mkts_trades "
                                                   + "SET mktID='"+this.mktID+"', "
                                                       + "orderID='"+this.orderID+"', "
                                                       + "buyer='"+this.target_adr+"', "
                                                       + "seller='"+rs.getString("adr")+"', "
                                                       + "qty='"+qty+"', "
                                                       + "price='"+rs.getDouble("price")+"', "
                                                       + "block='"+this.block+"'");
                   }
                }
                
                // Put the rest on market
                if (remain>0)
                UTILS.DB.executeUpdate("INSERT INTO assets_mkts_pos(adr,"
                                                                + "mktID, "
                                                                + "tip, "
                                                                + "qty, "
                                                                + "price, "
                                                                + "orderID, "
                                                                + "expire, "
                                                                + "block) VALUES('"+
                                                                this.target_adr+"', '"+
                                                                this.mktID+"', '"+
                                                                this.tip+"', '"+
                                                                remain+"', '"+
                                                                UTILS.FORMAT_8.format(this.price)+"', '"+
                                                                this.orderID+"', '"+
                                                                (this.block+(this.days*1440))+
                                                                "', '"+this.block+"')");
        }
        else
        {
            // Buy orders
            rs=UTILS.DB.executeQuery("SELECT * "
                                  + "FROM assets_mkts_pos "
                                 + "WHERE mktID='"+this.mktID+"' "
                                   + "AND tip='ID_SELL' "
                                   + "AND price<="+this.price+" "
                              + "ORDER BY price ASC");
                
               // Sold
               double remain=this.qty;
                
               // Qty
               double qty=0;
               
                // Has data ?
                if (UTILS.DB.hasData(rs))
                {
                   // Load assets
                   while (rs.next())
                   {
                      if (remain>0)
                      {
                         // Remain
                         if (remain>=rs.getDouble("qty"))
                         {
                            // Qty
                            qty=rs.getDouble("qty");
                            
                            // Remove order
                            UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                                       + "WHERE orderID='"+rs.getLong("orderID")+"'");
                         }
                         else
                         {
                            // Qty
                            qty=remain;
                            
                            // Update
                            UTILS.DB.executeUpdate("UPDATE assets_mkts_pos "
                                                    + "SET qty=qty-"+qty+", "
                                                        + "block='"+this.block+"' "
                                                  + "WHERE orderID='"+rs.getLong("orderID")+"'");
                         }
                         
                         // Remain
                         remain=remain-qty;
                      }
                      
                      // Insert order
                      UTILS.DB.executeUpdate("INSERT INTO assets_mkts_trades "
                                                   + "SET mktID='"+this.mktID+"', "
                                                       + "orderID='"+this.orderID+"', "
                                                       + "buyer='"+this.target_adr+"', "
                                                       + "seller='"+rs.getString("adr")+"', "
                                                       + "qty='"+qty+"', "
                                                       + "price='"+rs.getDouble("price")+"', "
                                                       + "block='"+this.block+"'");
                   }
                }
                
                // Receive coins
                if (remain>0)
                UTILS.DB.executeUpdate("INSERT INTO assets_mkts_pos(adr,"
                                                                + "mktID, "
                                                                + "tip, "
                                                                + "qty, "
                                                                + "price, "
                                                                + "orderID, "
                                                                + "expire, "
                                                                + "block) VALUES('"+
                                                                this.target_adr+"', '"+
                                                                this.mktID+"', '"+
                                                                this.tip+"', '"+
                                                                remain+"', '"+
                                                                UTILS.FORMAT_8.format(this.price)+"', '"+
                                                                this.orderID+"', '"+
                                                                (this.block+(this.days*1440))+
                                                                "', '"+this.block+"')");
            }
            
        
            
        // Clear
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        
    }      
    
    
}
