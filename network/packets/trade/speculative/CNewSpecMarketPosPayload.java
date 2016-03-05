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
   
   // Execution type
   String ex_type;

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
   
   // Position ID
   long posID;
   
   // Margin
   double margin;
   
   // Transaction
   CTransPayload trans;
    
   public CNewSpecMarketPosPayload(String adr, 
		                   long mktID, 
				   String tip, 
				   String ex_type,
				   double open, 
				   double sl, 
				   double tp, 
				   long leverage, 
				   double qty) throws Exception
   {
       // Constructor
       super(adr);
       
       try
       {
          // Market symbol
          this.mktID=mktID;
   
          // Tip
          this.tip=tip;
          
          // Execution type
          this.ex_type=ex_type;
   
          // Price
          this.open=open;
   
          // Stop loss
          this.sl=sl;
          
          // Trailing stop
          this.tp=tp;
   
          // Buy qty
          this.qty=qty;
   
          // Leverage
          this.leverage=leverage;
          
          // Position ID
          this.posID=Math.round(Math.random()*10000000000L);
       
          // Statement
          Statement s=UTILS.DB.getStatement();
           
          // Load Market
          ResultSet rs=s.executeQuery("SELECT fsm.*, adr.balance AS mkt_adr_balance "
                                      + "FROM feeds_spec_mkts AS fsm "
                                      + "JOIN adr ON adr.adr=fsm.adr "
                                     + "WHERE mktID='"+this.mktID+"'");
          
          // Next
          rs.next();
          
           // Open
           if (this.ex_type.equals("ID_MARKET"))
              open=rs.getDouble("last_price");
           else
               open=this.open;
           
          // Calculate margin
          double max_loss=0;
          double margin=0;
          
          // Maximum loss
          if (this.tip.equals("ID_BUY"))
          {
              margin=(this.qty*open)/this.leverage;    
              max_loss=(open-this.sl)*this.qty;
          }
          else
          {
              margin=(this.qty*open)/this.leverage;  
              max_loss=(this.sl-open)*this.qty;
          }
          
          
          // Margin
          if (margin<max_loss) margin=max_loss;
          
          // Margin
          this.margin=UTILS.BASIC.round(margin, 8);
          
          // Transaction
          this.trans=new CTransPayload(adr, 
                                       rs.getString("adr"), 
                                       this.margin, 
                                       rs.getString("cur"), 
                                       "", "", "", "", "",
                                       "", "", "", "", 0);
          
          // Hash
          hash=UTILS.BASIC.hash(this.getHash()+
                                this.mktID+
                                this.tip+
                                this.open+
                                this.sl+
                                this.tp+
                                this.ex_type+
                                this.qty+
                                this.leverage+
                                UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.trans)));
           
           // Close
           rs.close(); 
           s.close();
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewSpecMarketPosPayload.java", 57);
        }
       
       // Sign
       this.sign();
   }
   
   public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Order type
           if (!this.tip.equals("ID_BUY") && 
               !this.tip.equals("ID_SELL"))
           return new CResult(false, "Invalid order type", "CNewSpecMarketPosPayload.java", 74); 
           
           // Execution type
           if (this.ex_type.equals("ID_MARKET") && 
               this.ex_type.equals("ID_PEDNING"))
           return new CResult(false, "Invalid order execution type", "CNewSpecMarketPosPayload.java", 74); 
           
           // Market ID
           ResultSet mkt_rs=s.executeQuery("SELECT fsm.*, adr.balance AS mkt_adr_balance "
                                           + "FROM feeds_spec_mkts AS fsm "
                                           + "JOIN adr ON adr.adr=fsm.adr "
                                          + "WHERE mktID='"+this.mktID+"'");
           if (!UTILS.DB.hasData(mkt_rs))
              return new CResult(false, "Invalid market ID", "CNewSpecMarketPosPayload.java", 74); 
           
           // Load market data
           mkt_rs.next();
           
           // Spread
           double spread=mkt_rs.getDouble("spread");
           
            // Open
           double open;
           if (this.ex_type.equals("ID_MARKET"))
              open=mkt_rs.getDouble("last_price");
           else
               open=this.open;
           
           // Check sl and pl
           if (this.tip.equals("ID_BUY"))
           {
		if (this.sl>open-(spread*1.2))
		    return new CResult(false, "Invalid market stop loss.", "CNewSpecMarketPosPayload.java", 74); 
			    
	        if (this.tp<=open)
	            return new CResult(false, "Invalid take profit", "CNewSpecMarketPosPayload.java", 74); 
            }
	    else
	    {
	        if (this.sl<open+(spread*1.2))
		   return new CResult(false, "Invalid market stop loss.", "CNewSpecMarketPosPayload.java", 74); 
			 
	        if (this.tp>=open-spread/2)
		    return new CResult(false, "Invalid take profit.", "CNewSpecMarketPosPayload.java", 74); 
	    }
           
            // Leverage
            if (this.leverage>mkt_rs.getLong("max_leverage"))
               return new CResult(false, "Invalid leverage.", "CNewSpecMarketPosPayload.java", 74); 
            
            // Margin
            double margin=this.qty*mkt_rs.getDouble("last_price")/this.leverage;
            	 
	   // Maximum loss
           double max_loss;
           if (this.tip.equals("ID_BUY"))
           {
              margin=(this.qty*open)/this.leverage;    
              max_loss=(open-this.sl)*this.qty;
           }
           else
           {
              margin=(this.qty*open)/this.leverage;  
              max_loss=(this.tp-open)*this.qty;
           }
		 
	    // Max losss bigger than margin ?
	    if (max_loss>margin) margin=max_loss;
            
            // Round
            margin=UTILS.BASIC.round(margin, 8);
	    
            // Check margin
            if (this.margin<margin)
                return new CResult(false, "Invalid margin.", "CNewSpecMarketPosPayload.java", 74); 
            
	    // Address balance ?
            double balance=0;
            if (block==null)
                balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, mkt_rs.getString("cur"));
            else
                balance=UTILS.BASIC.getBalance(this.target_adr, mkt_rs.getString("cur"));
            
            // Insuficient funds
	    if (balance<margin)
	       return new CResult(false, "Insufficient funds to cover the margin", "CNewSpecMarketPosPayload.java", 74);
            
            // Calculate free colaterall
            s=UTILS.DB.getStatement();
            ResultSet rs=s.executeQuery("SELECT SUM(pl) AS total "
                                        + "FROM feeds_spec_mkts_pos "
                                       + "WHERE mktID='"+this.mktID+"' "
                                         + "AND (status='ID_MARKET' OR "
                                              + "status='ID_PENDING')");
            
            // Free colateral
            double free_colateral=mkt_rs.getDouble("mkt_adr_balance");
            
            if (!UTILS.DB.hasData(rs))
            {
               // Next
               rs.next();
            
               // Free colateral
               free_colateral=free_colateral-rs.getDouble("total");
            }
            
	    // Maximum margin
	    double max_margin=free_colateral*mkt_rs.getDouble("max_margin")/100;
		 
	    // Check margin
	    if (margin>max_margin)
	       return new CResult(false, "Invalid margin", "CNewSpecMarketPosPayload.java", 74);
            
            // Check transaction
            CResult res=this.trans.check(block);
            if (!res.passed) return res;
            
            // Transaction source address
            if (!this.trans.src.equals(this.target_adr))
               return new CResult(false, "Invalid transaction source address", "CNewSpecMarketPosPayload.java", 74);
            
            // Transaction destination address
            if (!this.trans.dest.equals(mkt_rs.getString("adr")))
               return new CResult(false, "Invalid transaction destination address", "CNewSpecMarketPosPayload.java", 74);
            
            // Amount
            if (this.trans.amount<margin)
                return new CResult(false, "Invalid transaction amount", "CNewSpecMarketPosPayload.java", 74);
            
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
                                      this.mktID+
                                      this.tip+
                                      this.open+
                                      this.sl+
                                      this.tp+
                                      this.ex_type+
                                      this.qty+
                                      this.leverage+
                                      UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.trans)));
             
             // Hash match ?
             if (!h.equals(hash))
                 return new CResult(false, "Invalid hash", "CNewSpecMarketPosPayload.java", 74);
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewSpecMarketPosPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Commit payment
        this.trans.commit(block);
        
         try
         {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Market
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_spec_mkts "
                                      + "WHERE mktID='"+this.mktID+"'");
           
           // Next
           rs.next();
           
           // Spread
           double spread=rs.getDouble("spread");
           
           // Open line
           String open_line="";
           if (!this.ex_type.equals("ID_MARKET"))
           {
               if (this.open<rs.getDouble("last_price"))
                   open_line="ID_ABOVE";
               else
                   open_line="ID_BELOW";
           }
           
           // Open
           double open;
           if (this.ex_type.equals("ID_MARKET"))
           {
               if (this.tip.equals("ID_BUY"))
                  open=rs.getDouble("last_price")+spread;
               else
                  open=rs.getDouble("last_price")-spread;
           }
           else
               open=this.open;
           
           // Get pl
           double pl=0;
           if (this.ex_type.equals("ID_MARKET"))
              pl=-spread*this.qty;
           else 
              pl=0;
           
           // Insert position
           UTILS.DB.executeUpdate("INSERT INTO feeds_spec_mkts_pos(mktID, "
                                                                + "posID, "
                                                                + "adr, "
                                                                + "tip, "
                                                                + "open, "
                                                                + "sl, "
                                                                + "tp, "
                                                                + "leverage, "
                                                                + "qty, "
                                                                + "margin, "
                                                                + "status, "
                                                                + "open_line, "
                                                                + "pl, "
                                                                + "spread, "
                                                                + "block) VALUES('"
                                                                +this.mktID+"', '"
                                                                +this.posID+"', '"
                                                                +this.target_adr+"', '"
                                                                +this.tip+"', '"
                                                                +open+"', '"
                                                                +this.sl+"', '"
                                                                +this.tp+"', '"
                                                                +this.leverage+"', '"
                                                                +this.qty+"', '"
                                                                +this.margin+"', '"
                                                                +this.ex_type+"', '"
                                                                +open_line+"', '"
                                                                +pl+"', '"
                                                                +rs.getDouble("spread")+"', '"
                                                                +this.block+"')");
        
            
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewSpecMarketPosPayload.java", 57);
        }
         
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}
