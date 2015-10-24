package wallet.network.packets.ads;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CNewAdPacket extends CBroadcastPacket 
{
   public CNewAdPacket(String fee_adr, 
		       String adr, 
		       String country, 
		       long hours, 
		       double price, 
		       String title, 
		       String mes, 
		       String link)
   {
	   // Super class
	   super("ID_NEW_AD_PACKET");
	   
	   // Builds the payload class
	   CNewAdPayload dec_payload=new CNewAdPayload(adr, 
			                               country, 
			                               hours, 
			                               price, 
			                               title, 
			                               mes, 
			                               link);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  price*hours);
	   
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
   	  if (!this.tip.equals("ID_NEW_AD_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CNewAdPacket", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CNewAdPacket", 39);
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CNewAdPacket", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CNewAdPayload dec_payload=(CNewAdPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CNewAdPacket", 62);
   }
}
