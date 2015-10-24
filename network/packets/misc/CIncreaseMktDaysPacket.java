package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CIncreaseMktDaysPacket extends CBroadcastPacket
{
    public CIncreaseMktDaysPacket(String fee_adr, 
                                  String adr, 
                                  long days, 
                                  String table, 
                                  String rowhash)
    {
       super("ID_INCREASE_MKT_DAYS_PACKET");
       
       try
       {
          // Builds the payload class
          CIncreaseMktDaysPayload dec_payload=new CIncreaseMktDaysPayload(adr, 
                                                                      days,
	                                                              table, 
	                                                              rowhash);
						
          // Build the payload
          this.payload=UTILS.SERIAL.serialize(dec_payload);
          
          // Statement
          Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
          // Load row data
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM "+table
                                     +" WHERE adr='"+adr
                                      +"' AND rowhash='"+rowhash+"'");
          rs.next();
          
          // Market bid
          double mkt_bid=rs.getDouble("mkt_bid");
          
	  // Network fee
	  fee=new CFeePayload(fee_adr, (mkt_bid*days));
       }
       catch (SQLException ex)
       {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CIncreaseMktDaysPacket.java", 79);
       }
       
       // Sign packet
       this.sign();
   }
			 
   // Check 
   public CResult check(CBlockPayload block)
   {
	// Super class
	CResult res=super.check(block);
	if (res.passed==false) return res;
			   	
	// Check type
	if (!this.tip.equals("ID_REMOVE_ITEM_PACKET")) 
	   return new CResult(false, "Invalid packet type", "CIncreaseMktDaysPacket.java", 42);
        
         // Deserialize transaction data
	 CIncreaseMktDaysPayload dec_payload=(CIncreaseMktDaysPayload) UTILS.SERIAL.deserialize(payload);
	   
         // Check payoad
         res=dec_payload.check(block);
         if (!res.passed) return res;
         
         try
         {
            // Statement
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
            // Load row data
            ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM "+dec_payload.table
                                     +" WHERE adr='"+dec_payload.adr
                                      +"' AND rowhash='"+dec_payload.rowhash+"'");
            rs.next();
          
            // Market bid
            double mkt_bid=rs.getDouble("mkt_bid");
          
	    // Network fee
	    if (fee.amount<(mkt_bid*dec_payload.days))
                return new CResult(false, "Invalid network fee", "CIncreaseMktDaysPacket.java", 42);
       }
       catch (SQLException ex)
       {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CIncreaseMktDaysPacket.java", 79);
       }
			   	
	// Return 
	return new CResult(true, "Ok", "CIncreaseMktDaysPacket.java", 74);
   }
			   
   public CResult commit(CBlockPayload block)
   {
	// Superclass
	CResult res=super.commit(block);
	if (res.passed==false) return res;
			   	  
	// Deserialize transaction data
	CIncreaseMktDaysPayload ass_payload=(CIncreaseMktDaysPayload) UTILS.SERIAL.deserialize(payload);
        
        res=ass_payload.commit(block);	  
	if (res.passed==false) return res;
        
        
	// Return 
	return new CResult(true, "Ok", "CIncreaseMktDaysPacket.java", 9);   
   }
}
