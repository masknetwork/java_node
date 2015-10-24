package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CReqInterestPacket extends CBroadcastPacket 
{
    public CReqInterestPacket(String fee_adr, String adr)
   {
	   // Super class
	   super("ID_REQ_INTEREST_PACKET");
	   
	   // Builds the payload class
	   CReqInterestPayload dec_payload=new CReqInterestPayload(adr);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  0.0001);
	   
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
      if (!this.tip.equals("ID_REQ_INTEREST_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CReqDataPacket", 39);
   	  
      // Check
      CReqInterestPayload pay=(CReqInterestPayload) UTILS.SERIAL.deserialize(payload);
      res=pay.check(block);
      
      // Result
      if (!res.passed) return res;
   	
      // Footprint
      CFootprint foot=new CFootprint("ID_REQ_INTEREST_PACKET", 
                                     this.hash, 
                                     pay.hash, 
                                     this.fee.src, 
                                     this.fee.amount, 
                                     this.fee.hash,
                                     this.block);
      foot.add("Address", pay.adr);
      foot.write();
      
      // Return 
      return new CResult(true, "Ok", "CReqDataPacket", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
      // Superclass
      CResult res=super.commit(block);
      if (res.passed==false) return res;
   	  
      // Deserialize transaction data
      CReqInterestPayload dec_payload=(CReqInterestPayload) UTILS.SERIAL.deserialize(payload);

      // Fee is 0.0001 / day ?
      res=dec_payload.commit(block);
      if (res.passed==false) return res;
	  
      // Return 
      return new CResult(true, "Ok", "CReqDataPacket", 62);
   }
}
