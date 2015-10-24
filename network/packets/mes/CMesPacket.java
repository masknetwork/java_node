package wallet.network.packets.mes;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CMesPacket extends CBroadcastPacket 
{
  public CMesPacket(String fee_adr, 
		    String sender_adr, 
		    String receiver_adr, 
		    String subject, 
		    String mes)
  {
	  super("ID_SEND_MES");
	  
	  // Builds the payload class
	  CMesPayload dec_payload=new CMesPayload(sender_adr, 
	                                          receiver_adr, 
	                                          subject, 
	                                          mes);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr);
	   
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
  	  if (!this.tip.equals("ID_SEND_MES")) 
  		return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
  	   // Check
  	  CMesPayload pay=(CMesPayload) UTILS.SERIAL.deserialize(payload);
      res=pay.check(block);
      if (!res.passed) return res;
  	
  	  // Return 
  	  return new CResult(true, "Ok", "CMesPacketPacket", 45);
  }
  
  public CResult commit(CBlockPayload block)
  {
  	  // Superclass
  	  CResult res=super.commit(block);
  	  if (res.passed==false) return res;
  	  
  	  // Deserialize transaction data
  	  CMesPayload dec_payload=(CMesPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	 res=dec_payload.commit(block);
     if (res.passed==false) return res;
	  
	  // Return 
  	  return new CResult(true, "Ok", "CMesPacketPacket", 62);
  }
}
