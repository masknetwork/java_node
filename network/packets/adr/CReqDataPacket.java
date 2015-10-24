package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CReqDataPacket extends CBroadcastPacket 
{
   public CReqDataPacket(String fee_adr, 
                         String adr, 
                         String mes,
                         String field_1_name, int field_1_min, int field_1_max, 
                         String field_2_name, int field_2_min, int field_2_max, 
                         String field_3_name, int field_3_min, int field_3_max, 
                         String field_4_name, int field_4_min, int field_4_max, 
                         String field_5_name, int field_5_min, int field_5_max, 
                         int days)
   {
	   // Super class
	   super("ID_REQ_DATA");
	   
	   // Builds the payload class
	   CReqDataPayload dec_payload=new CReqDataPayload(adr, 
                                                           mes,
                                                           field_1_name, field_1_min, field_1_max, 
                                                           field_2_name, field_2_min, field_2_max, 
                                                           field_3_name, field_3_min, field_3_max, 
                                                           field_4_name, field_4_min, field_4_max, 
                                                           field_5_name, field_5_min, field_5_max, 
                                                           days);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  0.0001*days);
	   
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
   	  if (!this.tip.equals("ID_REQ_DATA")) 
   		return new CResult(false, "Invalid packet type", "CReqDataPacket", 39);
   	  
   	  // Check
          CReqDataPayload pay=(CReqDataPayload) UTILS.SERIAL.deserialize(payload);
  	  res=pay.check(block);
         
          if (!res.passed) return res;
   	
   	  // Return 
   	  return new CResult(true, "Ok", "CReqDataPacket", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
          CReqDataPayload dec_payload=(CReqDataPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
         res=dec_payload.commit(block);
	 if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CReqDataPacket", 62);
   }
}
