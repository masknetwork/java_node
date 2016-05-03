package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CUpdatePosPayload extends CPayload
{
   // Pos uid
   long posID;
   
   // SL
   double sl;
   
   // TP
   double tp;
   
   // Transaction
   CTransPayload trans;
   
   
   public CUpdatePosPayload(String adr,
                            long posID,
                            double sl,
                            double tp) throws Exception
   {
       // Constructor
       super(adr);
       
       try
       {
           // UID
           this.posID=posID;
           
           // SL
           this.sl=sl;
           
           // TP
           this.tp=tp;
       
           // Statement
           Statement s=UTILS.DB.getStatement();
          
           // Load position data
           ResultSet pos_rs=s.executeQuery("SELECT * "
                                           + "FROM feeds_spec_mkts_pos "
                                          + "WHERE posID='"+this.posID+"' "
                                            + "AND adr='"+this.target_adr+"' "
                                            + "AND status<>'ID_CLOSED'");
          
           // Next
           pos_rs.next();
           
           // New statement
          Statement s1=UTILS.DB.getStatement();
          
          // Load market data
          ResultSet mkt_rs=s1.executeQuery("SELECT * "
                                           + "FROM feeds_spec_mkts "
                                          + "WHERE mktID='"+pos_rs.getLong("mktID")+"'");
          
          // Next
          mkt_rs.next();
           
           // Check if more margin is required
           double margin=0;
           
           if (pos_rs.getString("tip").equals("ID_BUY") && sl<pos_rs.getDouble("sl"))
                margin=(pos_rs.getDouble("sl")-this.sl)*mkt_rs.getDouble("last_price")*mkt_rs.getDouble("qty")/mkt_rs.getLong("leverage");
           
           if (pos_rs.getString("tip").equals("ID_SELL") && sl>pos_rs.getDouble("sl"))
               margin=(this.sl-pos_rs.getDouble("sl"))*mkt_rs.getDouble("last_price")*mkt_rs.getDouble("qty")/mkt_rs.getLong("leverage");
                
           // Additional Margin
           if (margin>pos_rs.getDouble("margin")) margin=margin-pos_rs.getDouble("margin");
           
           // Pay additional margin 
           String trans_hash="";
           if (margin>0)
           {
                this.trans=new CTransPayload(this.target_adr, 
                                             mkt_rs.getString("adr"), 
                                             margin, 
                                             mkt_rs.getString("cur"), 
                                             "", "", "");
                
                trans_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.trans));
           }
           else trans_hash="";
           
           
            // Hash
            hash=UTILS.BASIC.hash(this.getHash()+
                                  this.posID+
                                  this.sl+
                                  this.tp+
                                  trans_hash);
       
            // Sign
            this.sign();
            
            // Close
            s.close();
            s1.close();
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CUpdatePosPayload.java", 57);
        }
   }
   
   public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
          // Statement
          Statement s=UTILS.DB.getStatement();
          
          // Load position data
          ResultSet pos_rs=s.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts_pos "
                                         + "WHERE posID='"+this.posID+"' "
                                           + "AND adr='"+this.target_adr+"' "
                                           + "AND status<>'ID_CLOSED'");
          
          // Valid ?
          if (!UTILS.DB.hasData(pos_rs))
               return new CResult(false, "Invalid order", "CUpdatePosPayload", 42);
          
          // Next
          pos_rs.next();
          
          // Signer valid
          if (!pos_rs.getString("adr").equals(this.target_adr))
              return new CResult(false, "Invalid signer", "CUpdatePosPayload", 42);
          
          // New statement
          Statement s1=UTILS.DB.getStatement();
          
          // Load market data
          ResultSet mkt_rs=s1.executeQuery("SELECT * "
                                           + "FROM feeds_spec_mkts "
                                          + "WHERE mktID='"+pos_rs.getLong("mktID")+"'");
          
          // Next
          mkt_rs.next();
          
          // Spread
          double spread=mkt_rs.getDouble("spread");
          
          // Open
          double open=0;
          
          if (pos_rs.getString("status").equals("ID_PENDING")) 
             open=pos_rs.getDouble("open");
          else
             open=mkt_rs.getDouble("last_price");
          
          // Margin
          double margin=0;
          
         // Check tp and sl
         if (pos_rs.getString("tip").equals("ID_BUY"))
         {
               if (this.sl>open-(spread*2))
                   return new CResult(false, "Invalid sl", "CUpdatePosPayload", 42);
	       
               if (this.tp<open+spread)
	          return new CResult(false, "Invalid tp", "CUpdatePosPayload", 42);
			 
		// SL lower ?
		if (this.sl<pos_rs.getDouble("sl")) 
	            margin=(pos_rs.getDouble("sl")-this.sl)*mkt_rs.getDouble("last_price")*mkt_rs.getDouble("qty")/mkt_rs.getLong("leverage");          
         }
         else
         {
             // Invalid SL
             if (this.sl<open+(spread*2))
	        return new CResult(false, "Invalid sl", "CUpdatePosPayload", 42);
	     
             // Invalid TP
	     if (this.tp>open-spread)
		return new CResult(false, "Invalid tp", "CUpdatePosPayload", 42);
			 
	     // Margin
	     if (this.sl>pos_rs.getDouble("sl")) 
	            margin=(this.sl-pos_rs.getDouble("sl"))*mkt_rs.getDouble("last_price")*mkt_rs.getDouble("qty")/mkt_rs.getLong("leverage");
         }
          
          // More margin required ?
          if (margin>0)
          {
              // Funds ?
              double balance=0;
              if (block==null)
                  balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.target_adr, mkt_rs.getString("cur"));
              else
                   balance=UTILS.BASIC.getBalance(this.target_adr, mkt_rs.getString("cur"));
              
              // Funds ?
              if (margin>balance)
                  return new CResult(false, "Insufficient funds to cover the margin", "CUpdatePosPayload", 42);
              
              // Check transaction source
              if (!this.trans.src.equals(this.target_adr))
                 return new CResult(false, "Invalid transaction source", "CUpdatePosPayload", 42);
              
              // Check transaction dest
              if (!this.trans.dest.equals(mkt_rs.getString("adr")))
                 return new CResult(false, "Invalid transaction destination", "CUpdatePosPayload", 42);
              
              // Check transaction amount
              if (this.trans.amount<margin)
                 return new CResult(false, "Invalid transaction amount", "CUpdatePosPayload", 42);
              
              // Check transaction currency
              if (!this.trans.cur.equals(mkt_rs.getString("cur")))
                  return new CResult(false, "Invalid transaction currency", "CUpdatePosPayload", 42);
          }
          
          // Check hash
          String trans_hash;
          if (this.trans==null)
             trans_hash="";
          else
              trans_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.trans));
          
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.posID+
                                    this.sl+
                                    this.tp+
                                    trans_hash);
           
          // Check hash
          if (!h.equals(this.hash))
              return new CResult(false, "Invalid hash", "CUpdatePosPayload", 42);
          
           // Close
           pos_rs.close(); 
           mkt_rs.close();
           s.close();
           s1.close();
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CUpdatePosPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CUpdatePosPayload", 67);
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
        UTILS.BASIC.clearTrans(hash, "ID_ALL");
         
        // Updates
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET sl='"+this.sl+"', "
                                    + "tp='"+this.tp+"' "
                              + "WHERE posID='"+this.posID+"'");
        
        // Return
	return new CResult(true, "Ok", "CUpdatePosPayload", 67); 
    }        
}
