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

public class CRenewPacket extends CBroadcastPacket
{
    // Serial
   private static final long serialVersionUID = 100L;
   
    public CRenewPacket(String fee_adr, 
                        String adr, 
                        long days, 
                        String table, 
                        String rowhash) throws Exception
    {
       super("ID_INCREASE_MKT_DAYS_PACKET");
       
      // Builds the payload class
          CRenewPayload dec_payload=new CRenewPayload(adr, 
                                                                      days,
	                                                              table, 
	                                                              rowhash);
						
          // Build the payload
          this.payload=UTILS.SERIAL.serialize(dec_payload);
          
          // Statement
          
			
          // Load row data
          ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                      + "FROM "+table
                                     +" WHERE adr='"+adr
                                      +"' AND rowhash='"+rowhash+"'");
          rs.next();
          
          // Market bid
          double mkt_bid=rs.getDouble("mkt_bid");
          
	  // Network fee
	  fee=new CFeePayload(fee_adr, (mkt_bid*days));
       
       
       // Sign packet
       this.sign();
   }
			 
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
	// Super class
	super.check(block);
			   	
	// Check type
	if (!this.tip.equals("ID_REMOVE_ITEM_PACKET")) 
	   throw new Exception("Invalid packet type - CRenewPacket.java");
        
         // Deserialize transaction data
	 CRenewPayload dec_payload=(CRenewPayload) UTILS.SERIAL.deserialize(payload);
	   
         // Check payoad
         dec_payload.check(block);
         
        // Statement
            
			
            // Load row data
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                      + "FROM "+dec_payload.table
                                     +" WHERE adr='"+dec_payload.adr
                                      +"' AND rowhash='"+dec_payload.rowhash+"'");
            rs.next();
          
            // Market bid
            double mkt_bid=rs.getDouble("mkt_bid");
          
	    // Network fee
	    if (fee.amount<(mkt_bid*dec_payload.days))
                throw new Exception("Invalid packet type - CRenewPacket.java");
      
   }
			   
   
}
