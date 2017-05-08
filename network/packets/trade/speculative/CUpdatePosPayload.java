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
   
    
   public CUpdatePosPayload(String adr,
                            long posID,
                            double sl,
                            double tp) throws Exception
   {
       // Constructor
       super(adr);
       
       // UID
       this.posID=posID;
           
       // SL
       this.sl=sl;
           
       // TP
       this.tp=tp;
           
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
                             this.posID+
                             this.sl+
                             this.tp);
       
       // Sign
       this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
    {
        // Super class
   	super.check(block);
          
        // Load position data
          ResultSet pos_rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts_pos "
                                         + "WHERE posID='"+this.posID+"' "
                                           + "AND adr='"+this.target_adr+"' "
                                           + "AND status<>'ID_CLOSED'");
          
          // Valid ?
          if (!UTILS.DB.hasData(pos_rs))
               throw new Exception("Invalid order - CUpdatePosPayload.java");
          
          // Next
          pos_rs.next();
          
          // Signer valid
          if (!pos_rs.getString("adr").equals(this.target_adr))
              throw new Exception("Invalid signer - CUpdatePosPayload.java");
          
          // Load market data
          ResultSet mkt_rs=UTILS.DB.executeQuery("SELECT * "
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
                   throw new Exception("Invalid SL - CUpdatePosPayload.java");
	       
               if (this.tp<open+spread)
	          throw new Exception("Invalid TP - CUpdatePosPayload.java");
			 
		// SL lower ?
		if (this.sl<pos_rs.getDouble("sl")) 
	            margin=(pos_rs.getDouble("sl")-this.sl)*mkt_rs.getDouble("last_price")*mkt_rs.getDouble("qty")/mkt_rs.getLong("leverage");          
         }
         else
         {
             // Invalid SL
             if (this.sl<open+(spread*2))
	        throw new Exception("Invalid SL - CUpdatePosPayload.java");
	     
             // Invalid TP
	     if (this.tp>open-spread)
		throw new Exception("Invalid TP - CUpdatePosPayload.java");
			 
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
                   balance=UTILS.ACC.getBalance(this.target_adr, mkt_rs.getString("cur"));
              
              // Funds ?
              if (margin>balance)
                  throw new Exception("Innsuficient funds - CUpdatePosPayload.java");
          }
          
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.posID+
                                    this.sl+
                                    this.tp);
           
          // Check hash
          if (!h.equals(this.hash))
              throw new Exception("Invalid hash - CUpdatePosPayload.java");
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Commit payment
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
         
        // Updates
        UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                + "SET sl='"+this.sl+"', "
                                    + "tp='"+this.tp+"' "
                              + "WHERE posID='"+this.posID+"'");
    }        
}
