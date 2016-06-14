// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CIncreaseMktBidPacket extends CBroadcastPacket
{
    public CIncreaseMktBidPacket(String fee_adr, 
                                 String adr,
                                 double mkt_bid, 
                                 String table, 
                                 String rowhash,
                                 String packet_sign,
                                 String payload_sign) throws Exception
    {
        super("ID_INCREASE_MKT_BID_PACKET");
        
        try
        {
          // Builds the payload class
          CIncreaseMktBidPayload dec_payload=new CIncreaseMktBidPayload(adr, 
                                                                        mkt_bid,
	                                                                table, 
	                                                                rowhash);
						
          // Build the payload
          this.payload=UTILS.SERIAL.serialize(dec_payload);
          
          // Statement
          Statement s=UTILS.DB.getStatement();
			
          // Load row data
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM "+table
                                     +" WHERE adr='"+adr
                                      +"' AND rowhash='"+rowhash+"'");
          rs.next();
          
          // Finds expire date in days
          long expires=rs.getLong("expires");
          long expires_hours=(expires-this.block)/36;
           
          // Finds market bid
          double old_mkt_bid=rs.getDouble("mkt_bid");
          
	  // Network fee
	  fee=new CFeePayload(fee_adr, ((mkt_bid-old_mkt_bid)*expires_hours));
       }
       catch (SQLException ex)
       {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CIncreaseMktBidPacket.java", 79);
       }
       
       // Sign packet
       this.sign();
   }
			 
   // Check 
   public CResult check(CBlockPayload block) throws Exception
   {
	// Super class
	CResult res=super.check(block);
	if (res.passed==false) return res;
			   	
	// Check type
	if (!this.tip.equals("ID_INCREASE_MKT_BID_PACKET")) 
	   return new CResult(false, "Invalid packet type", "CIncreaseMktBidPacket.java", 42);
        
         // Deserialize transaction data
	 CIncreaseMktBidPayload dec_payload=(CIncreaseMktBidPayload) UTILS.SERIAL.deserialize(payload);
	   
         // Check payoad
         res=dec_payload.check(block);
         if (!res.passed) return res;
         
         try
         {
            // Statement
            Statement s=UTILS.DB.getStatement();
			
            // Load row data
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM "+dec_payload.table
                                       +" WHERE adr='"+dec_payload.adr
                                        +"' AND rowhash='"+dec_payload.rowhash+"'");
            rs.next();
          
            // Finds expire date in days
            long expires=rs.getLong("expires");
            long expires_hours=(expires-this.block)/36;
           
            // Finds market bid
            double old_mkt_bid=rs.getDouble("mkt_bid");
          
	    // Network fee
	    if (fee.amount<((dec_payload.mkt_bid-old_mkt_bid)*expires_hours))
                return new CResult(false, "Invalid fee", "CIncreaseMktBidPacket.java", 42);
       }
       catch (SQLException ex)
       {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CIncreaseMktBidPacket.java", 79);
       }
			   	
	// Return 
	return new CResult(true, "Ok", "CIncreaseMktBidPacket.java", 74);
   }
			   
   public CResult commit(CBlockPayload block) throws Exception
   {
	// Superclass
	CResult res=super.commit(block);
	if (res.passed==false) return res;
			   	  
	// Deserialize transaction data
	CIncreaseMktBidPayload ass_payload=(CIncreaseMktBidPayload) UTILS.SERIAL.deserialize(payload);
        
        res=ass_payload.commit(block);	  
	if (res.passed==false) return res;
        
        
	// Return 
	return new CResult(true, "Ok", "CIncreaseMktBidPacket.java", 9);   
   }
}

