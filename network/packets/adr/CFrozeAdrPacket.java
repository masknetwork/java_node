package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CFrozeAdrPacket extends CBroadcastPacket 
{
    public CFrozeAdrPacket(String fee_adr, String block_adr, int days)
    {
		   // Constructor
		   super("ID_FROZE_ADR_PACKET");
		   
		   // Builds the transaction
		   CFrozeAdrPayload block_payload=new CFrozeAdrPayload(block_adr, days);
				
		   // Build the payload
		   this.payload=UTILS.SERIAL.serialize(block_payload);
				
		   // Network fee
		   fee=new CFeePayload(fee_adr, days*0.0001);
		   
		   // Sign
		   this.sign();
		  
	   }
	   
	   public CResult check(CBlockPayload block)
	   {
		  // Super class
		  CResult res=super.check(block);
		  if (res.passed==false) return res;
		   	
		  // Check type
		  if (!this.tip.equals("ID_FROZE_ADR_PACKET")) 
		   	  return new CResult(false, "Invalid packet type", "CBlockAdrPacket", 44);
		  
		  // Deserialize
		  CFrozeAdrPayload pay=(CFrozeAdrPayload) UTILS.SERIAL.deserialize(this.payload);
		  
		  // Check fee
		  if (this.fee.amount<pay.days*0.0001)
			  return new CResult(false, "Invalid packet type", "CBlockAdrPacket", 44);
		  
		  // Check sig
		  if (!this.checkSign())
			  return new CResult(false, "Invalid signature", "CBlockAdrPacket", 44);
		  
		  // Check payload
		  CFrozeAdrPayload payload=(CFrozeAdrPayload) UTILS.SERIAL.deserialize(this.payload);
		  res=payload.check(block);
		  if (res.passed==false) return res;
                  
                  // Footprint
                  CFootprint foot=new CFootprint("ID_FROZE_ADR_PACKET", 
                                                 this.hash, 
                                                 pay.hash, 
                                                 this.fee.src, 
                                                 this.fee.amount, 
                                                 this.fee.hash,
                                                 this.block);
                  foot.add("Address", payload.adr);
                  foot.add("Days", String.valueOf(payload.days));
                  foot.write();
		  
		  // Return 
		  return new CResult(true, "Ok", "CAddEscrowPacket", 47);
	   }
	   
	   public CResult commit(CBlockPayload block)
	   {
		   // Superclass
	   	   CResult res=super.commit(block);
	   	   if (res.passed==false) return res;
	   	   
	      // Deserialize
	   	  CFrozeAdrPayload pay=(CFrozeAdrPayload) UTILS.SERIAL.deserialize(this.payload);
	 	  
	       // Commit the payload
	       res=pay.commit(block);
	       if (res.passed==false) return res;
	       
	      // Return 
	 	  return new CResult(true, "Ok", "CAddEscrowPacket", 47);
	   }
	}
