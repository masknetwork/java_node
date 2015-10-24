package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class COTPPacket extends CBroadcastPacket
{
   public COTPPacket(String pay_adr, 
		     String otp_adr, 
		     String next_pass, 
		     String def_address, 
		     long days) 
	{
		// Constructor
	    super("ID_OTP_PACKET");
		
	    
		// Builds the transaction
	    COTPPayload otp_payload=new COTPPayload(otp_adr, 
	    		                                next_pass, 
	    		                                def_address, 
	    		                                days);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(otp_payload);
				
		// Network fee
		fee=new CFeePayload(pay_adr, days*0.0001);
	    
		// Sign packet
		this.sign();
	}
	
	public CResult check(CBlockPayload block)
	{
	    // Super class
	    CResult res=super.check(block);
            if (res.passed==false) return res;
		   	
	    // Check type
	    if (!this.tip.equals("ID_OTP_PACKET")) 
		return new CResult(false, "Invalid packet type", "COTPPacket", 42);
		   
            // Deserialize transaction data
	    COTPPayload ass_payload=(COTPPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check fee
	    if (this.fee.amount<ass_payload.days*0.0001) 
	        return new CResult(false, "Invalid fee", "COTPPacket", 42);
		   
	    // Check
	    COTPPayload pay=(COTPPayload) UTILS.SERIAL.deserialize(payload);
	    res=pay.check(block);
	    if (!res.passed) return res;
               
            // Footprint
            CFootprint foot=new CFootprint("ID_OTP_PACKET", 
                                         this.hash, 
                                         pay.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
          
          foot.add("Address", pay.target_adr);
          foot.add("Next Password Hash", String.valueOf(pay.next_hash));
          foot.add("Emergency Address", String.valueOf(pay.def_address));
          foot.add("Days", String.valueOf(pay.days));
          foot.write();
		   	
          // Return 
          return new CResult(true, "Ok", "COTPPacket", 74);
   }
		   
   public CResult commit(CBlockPayload block)
   {
       // Superclass
       CResult res=super.commit(block); 
       if (res.passed==false) return res;
		   	  
       // Deserialize transaction data
       COTPPayload ass_payload=(COTPPayload) UTILS.SERIAL.deserialize(payload);

       // Fee is 0.0001 / day ?
       res=ass_payload.commit(block);
       if (res.passed==false) return res;
			  
       // Return 
       return new CResult(true, "Ok", "CPublicAddressPacket", 9);
   }
}
