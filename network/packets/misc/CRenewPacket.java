// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.ads.CNewAdPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CRenewPacket extends CBroadcastPacket
{
    // Serial
   private static final long serialVersionUID = 100L;
   
    public CRenewPacket(String fee_adr, 
                        String adr, 
                        String table, 
                        long days, 
                        long longID,
                        String stringID,
                        String stringID_2) throws Exception
    {
       super("ID_RENEW_PACKET");
       
      // Builds the payload class
          CRenewPayload dec_payload=new CRenewPayload(adr, 
                                                      table, 
                                                      days,
	                                              longID,
                                                      stringID,
                                                      stringID_2);
						
          // Build the payload
          this.payload=UTILS.SERIAL.serialize(dec_payload);
          
         
	  // Network fee
	   CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
	   this.fee_payload=UTILS.SERIAL.serialize(fee);
       
       
       // Sign packet
       this.sign();
   }
			 
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
	// Super class
        super.check(block);
   	  
        // Check type
   	if (!this.tip.equals("ID_RENEW_PACKET")) 
             throw new Exception("Invalid packet type - CRenewPacket.java");
   	  
        // Deserialize transaction data
   	CRenewPayload dec_payload=(CRenewPayload) UTILS.SERIAL.deserialize(payload);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
	if (fee.amount<dec_payload.days*0.0001)
	   throw new Exception("Invalid fee - CRenewPacket.java");
          
        // Check payload
        dec_payload.check(block);
          
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Table", dec_payload.table);
        foot.add("Days", String.valueOf(dec_payload.days));
        foot.add("ID 1", String.valueOf(dec_payload.longID));
        foot.add("ID 2", String.valueOf(dec_payload.stringID));
        foot.add("ID 3", String.valueOf(dec_payload.stringID_2));
        foot.write();
   }
			 
}
