// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewSpecMarketPosPayload extends CPayload
{
   // Market symbol
   long mktID;
   
   // Tip
   String tip;
   
   // Open
   double open;

   // Stop loss
   double sl;

   // Take profit
   double tp;

   // Leverage
   long leverage;

   // Qty
   double qty;   
   
   // Ex type
   String ex_type;   
   
   // Position ID
   long posID;
   
   // Says
   long days;
   
    
   public CNewSpecMarketPosPayload(String adr, 
		                   long mktID, 
				   String tip, 
				   double open, 
				   double sl, 
				   double tp, 
				   long leverage, 
				   double qty,
                                   String ex_type,
                                   long days) throws Exception
   {
       // Constructor
       super(adr);
       
       // Market symbol
       this.mktID=mktID;
   
       // Tip
       this.tip=tip;
          
       // Price
       this.open=open;
   
       // Stop loss
       this.sl=sl;
          
       // Trailing stop
       this.tp=tp;
   
       // Buy qty
       this.qty=qty;
       
       // Execution type
       this.ex_type=ex_type;
   
       // Leverage
       this.leverage=leverage;
          
       // Position ID
       this.posID=Math.round(Math.random()*10000000000L);
       
       // Days
       this.days=days;
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.mktID+
                             this.tip+
                             this.open+
                             this.sl+
                             this.tp+
                             this.qty+
                             this.ex_type+
                             this.leverage+
                             this.days);
    
       
          // Sign
          this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
       super.check(block);
          
       // Address can spend ?
        if (!UTILS.BASIC.canSpend(this.target_adr))
            throw new Exception("Invalid order ID - CNewRegMarketPosPayload.java");
         
        // Order type
        if (!this.tip.equals("ID_BUY") && 
            !this.tip.equals("ID_SELL"))
        throw new Exception("Invalid order type - CNewSpecMarketPosPayload.java"); 
        
        // Mkt ID and pos ID
        if (this.mktID<0 || this.posID<0)
           throw new Exception("Invalid entry data - CNewSpecMarketPosPayload.java"); 
        
        // Check market ID
        if (UTILS.BASIC.targetValid("ID_MARGIN_MKT", mktID))
             throw new Exception("Invalid market ID - CNewSpecMarketPosPayload.java"); 
        
        // Check pos ID
        if (UTILS.BASIC.existID(this.posID))
             throw new Exception("Invalid position ID - CNewSpecMarketPosPayload.java"); 
        
        // Market ID
        ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT fsm.*, adr.balance AS mkt_adr_balance "
                                               + "FROM feeds_spec_mkts AS fsm "
                                               + "JOIN adr ON adr.adr=fsm.adr "
                                              + "WHERE mktID='"+this.mktID+"'");
        if (!UTILS.DB.hasData(mkt_rs))
            throw new Exception("Invalid market ID - CNewSpecMarketPosPayload.java"); 
           
        // Load market data
        mkt_rs.next();
           
        // Spread
        double spread=mkt_rs.getDouble("spread");
        
        // Last price
        double last_price=mkt_rs.getDouble("last_price");
        
        // Feed & branch
        String feed=mkt_rs.getString("feed");
        String branch=mkt_rs.getString("branch");
        
        // Market expire
        long mkt_expire=mkt_rs.getLong("expire");
        
        // Market status
        if (!mkt_rs.getString("status").equals("ID_ONLINE"))
            throw new Exception("Market is not online - CNewSpecMarketPosPayload.java"); 
        
        // Load feed data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+feed+"' "
                                            + "AND symbol='"+branch+"'");
        
        // Next
        rs.next();
        
        // Status online ?
        if (!rs.getString("mkt_status").equals("ID_ONLINE"))
            throw new Exception("Feed market status not online - CNewSpecMarketPosPayload.java"); 
        
        // Expire
        if (this.block+this.days*1440>=mkt_expire)
            throw new Exception("Invalid expiration block - CNewSpecMarketPosPayload.java"); 
        
        // Execution type
        if (!this.ex_type.equals("ID_MARKET") && 
            !this.ex_type.equals("ID_ORDER"))
        throw new Exception("Invalid execution type - CNewSpecMarketPosPayload.java"); 
        
        // Price ?
        if (this.ex_type.equals("ID_MARKET") && last_price!=this.open)
            throw new Exception("Invalid execution type price - CNewSpecMarketPosPayload.java"); 
        
        // Target address ?
        if (this.target_adr.equals(mkt_rs.getString("adr")))
           throw new Exception("Invalid target address - CNewSpecMarketPosPayload.java"); 
        
        // Target address can spend ?
        if (!UTILS.BASIC.canSpend(this.target_adr))
           throw new Exception("Target address can't spend funds - CNewSpecMarketPosPayload.java"); 
        
        // Check sl and pl
        if (this.tip.equals("ID_BUY"))
        {
	    if (this.sl>open-spread)
		throw new Exception("Invalid market SL - CNewSpecMarketPosPayload.java"); 
			    
	    if (this.tp<=open+spread)
	        throw new Exception("Invalid market TP - CNewSpecMarketPosPayload.java"); 
        }
	else
	{
	    if (this.sl<open+spread)
	       throw new Exception("Invalid market SL - CNewSpecMarketPosPayload.java"); 
			 
	    if (this.tp>=open-spread)
	       throw new Exception("Invalid market TP - CNewSpecMarketPosPayload.java"); 
	}
           
        // Leverage
        if (this.leverage>mkt_rs.getLong("max_leverage") || this.leverage<1)
           throw new Exception("Invalid leverage - CNewSpecMarketPosPayload.java"); 
        
        
        // Margin
        double margin=this.qty*mkt_rs.getDouble("last_price")/this.leverage;
        
        // Margin valid ?
        if (margin<0.00000001) 
            throw new Exception("Invalid margin - CNewSpecMarketPosPayload.java");
        
	// Maximum loss
        double max_loss;
           
        if (this.tip.equals("ID_BUY"))
        {
              margin=(this.qty*(open+spread))/this.leverage;    
              max_loss=(open+spread-this.sl)*this.qty;
        }
        else
        {
              margin=(this.qty*(open-spread))/this.leverage;  
              max_loss=(this.sl-open+spread)*this.qty;
        }
		 
	// Max losss bigger than margin ?
	if (max_loss>margin) margin=max_loss;
            
        // Round
        margin=UTILS.BASIC.round(margin, 8);
	    
        // Address balance ?
        double balance=UTILS.ACC.getBalance(this.target_adr, mkt_rs.getString("cur"), block);
        
        // Insuficient funds
	if (balance<margin)
	    throw new Exception("Innsuficient funds - CNewSpecMarketPosPayload.java"); 
        
        // Used margin
        double used_margin=0;
        
        // Calculate free colaterall
         rs=UTILS.DB.executeQuery("SELECT SUM(margin) AS total "
                                           + "FROM feeds_spec_mkts_pos AS fsmp "
                                           + "JOIN feeds_spec_mkts AS fsm ON fsmp.mktID=fsm.mktID "
                                          + "WHERE fsmp.status='ID_MARKET' "
                                            + "AND fsm.adr='"+mkt_rs.getString("adr")+"' "
                                            + "AND fsm.mktID='"+this.mktID+"'");
            
        if (UTILS.DB.hasData(rs))
        {
               // Next
               rs.next();
            
               // Free colateral
               used_margin=rs.getDouble("total");
        }
            
	// Maximum margin
        double max_allowed_margin=mkt_rs.getDouble("mkt_adr_balance")*mkt_rs.getDouble("max_total_margin")/100;
		 
	// Check margin
	if (max_allowed_margin<used_margin+margin)
	    throw new Exception("Invalid margin - CNewSpecMarketPosPayload.java"); 
        
        // Calculate total margin
        rs=UTILS.DB.executeQuery("SELECT SUM(margin) AS total "
                                 + "FROM feeds_spec_mkts_pos AS fsmp "
                                 + "JOIN feeds_spec_mkts AS fsm ON fsmp.mktID=fsm.mktID "
                                + "WHERE fsmp.status='ID_MARKET' "
                                  + "AND fsm.adr='"+mkt_rs.getString("adr")+"'");
            
        if (UTILS.DB.hasData(rs))
        {
               // Next
               rs.next();
            
               // Free colateral
               used_margin=rs.getDouble("total");
        }
        
        if (used_margin+margin>mkt_rs.getDouble("mkt_adr_balance")/2)
            throw new Exception("Total margin too big - CNewSpecMarketPosPayload.java"); 
        
        // Days
        if (this.days<1)
            throw new Exception("Invalid days - CNewSpecMarketPosPayload.java");
        
        // Pay margin
        UTILS.ACC.newTransfer(this.target_adr, 
                              mkt_rs.getString("adr"), 
                              margin,
                              true,
                              mkt_rs.getString("cur"), 
                              "Margin payment", 
                              "", 
                              hash, 
                              this.block, 
                              block, 
                              0);
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.mktID+
                                  this.tip+
                                  this.open+
                                  this.sl+
                                  this.tp+
                                  this.qty+
                                  this.ex_type+
                                  this.leverage+
                                  this.days);
             
        // Hash match ?
        if (!h.equals(hash))
           throw new Exception("Invalid hash - CNewSpecMarketPosPayload.java");  
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Market
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_spec_mkts "
                                          + "WHERE mktID='"+this.mktID+"'");
           
        // Next
        rs.next();
           
        // Spread
        double spread=rs.getDouble("spread");
        
        // Margin
        double margin=0;
       
        // Open line
        String open_line="";
        
        if (this.ex_type.equals("ID_ORDER"))
        {
            if (this.open<rs.getDouble("last_price"))
              open_line="ID_BELOW";
            else
              open_line="ID_ABOVE";
        }
           
        // Open
        double open=0;
        
        if (this.ex_type.equals("ID_MARKET"))
        {
               if (this.tip.equals("ID_BUY"))
                  open=rs.getDouble("last_price")+spread;
               else
                  open=rs.getDouble("last_price")-spread;
        }
        else open=this.open;
           
        // Get pl
        double pl=0;
        
        if (this.ex_type.equals("ID_MARKET"))
            pl=-spread*this.qty;
        else 
            pl=0;
        
        // Insert position
        UTILS.DB.executeUpdate("INSERT INTO feeds_spec_mkts_pos "
                                     + "SET mktID='"+this.mktID+"', "
                                         + "posID='"+this.posID+"', "
                                         + "adr='"+this.target_adr+"', "
                                         + "tip='"+this.tip+"', "
                                         + "open='"+open+"', "
                                         + "sl='"+this.sl+"', "
                                         + "tp='"+this.tp+"', "
                                         + "leverage='"+this.leverage+"', "
                                         + "qty='"+this.qty+"', "
                                         + "margin='"+margin+"', "
                                         + "status='"+this.ex_type+"', "
                                         + "open_line='"+open_line+"', "
                                         + "pl='"+pl+"', "
                                         + "spread='"+spread+"', "
                                         + "block_start='"+this.block+"', "
                                         + "block_end='0', "
                                         + "expire='"+(this.block+(this.days*1440))+"'");
        
        // Clear
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        
        // Vote market
        UTILS.BASIC.voteTarget(this.target_adr, "ID_MARGIN_MKT", this.mktID, block.block);
        
        // Get feed
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds_spec_mkts "
                                + "WHERE mktID='"+this.mktID+"'");
        
        // Next
        rs.next();
        
        // Feed ID
        long feedID=UTILS.BASIC.getFeedID(rs.getString("feed"));
        
        // Vote feed
        UTILS.BASIC.voteTarget(this.target_adr, "ID_FEED", feedID, block.block);
     }        
}
