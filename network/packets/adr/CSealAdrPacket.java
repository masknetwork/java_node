package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CSealAdrPacket extends CBroadcastPacket 
{
   public CSealAdrPacket(String fee_adr, String adr, long days)
   {
	   super("ID_SEAL_ADR_PACKET");
	   
	   // Builds the payload class
	   CSealAdrPayload dec_payload=new CSealAdrPayload(adr, days);
					
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	   // Network fee
	   fee=new CFeePayload(fee_adr, days*0.0001);
			   
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
	   if (!this.tip.equals("ID_SEAL_ADR_PACKET")) 
	   	  return new CResult(false, "Invalid packet type", "CSealAdrPayload", 42);
	   
	   // Deserialize transaction data
	   CSealAdrPayload pay=(CSealAdrPayload) UTILS.SERIAL.deserialize(payload);
	   
	   // Check fee
	   if (this.fee.amount<(pay.days*0.0001)) 
		   return new CResult(false, "Invalid fee", "CSealAdrPayload", 42);
	   
	   // Check sig
	   if (this.checkSign()==false)
		   return new CResult(false, "Invalid signature", "CSealAdrPayload", 42);
           
            // Footprint
                  CFootprint foot=new CFootprint("ID_SEAL_ADR_PACKET", 
                                                 this.hash, 
                                                 pay.hash, 
                                                 this.fee.src, 
                                                 this.fee.amount, 
                                                 this.fee.hash,
                                                 this.block);
                  foot.add("Address", pay.adr);
                  foot.add("Days", String.valueOf(pay.days));
                  foot.write();
	   	
	   // Return 
	   return new CResult(true, "Ok", "CSealAdrPayload", 74);
	}
	   
	public CResult commit(CBlockPayload block)
	{
	   	  // Superclass
	   	  CResult res=super.commit(block);
	   	  if (res.passed==false) return res;
	   	  
	   	  // Deserialize transaction data
	      CSealAdrPayload ass_payload=(CSealAdrPayload) UTILS.SERIAL.deserialize(payload);

		  // Fee is 0.0001 / day ?
		  res=ass_payload.commit(block);
	      if (res.passed==false) return res;
		  
		  // Return 
	   	  return new CResult(true, "Ok", "CSealAdrPayload", 9);
	   }
	}
