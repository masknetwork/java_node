package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CProfilePacket extends CBroadcastPacket 
{
   public CProfilePacket(String fee_adr, 
		         String target_adr, 
		         String name, 
		         String description,
                         String email, 
                         String tel,
                         String website, 
                         String facebook, 
                         String avatar, 
		        int days)
   {
	   // Super class
	   super("ID_PROFILE");
	   
	   // Builds the payload class
	   CProfilePayload dec_payload=new CProfilePayload(target_adr, 
		                                           name, 
		                                           description,
                                                           email, 
                                                           tel,
                                                           website, 
                                                           facebook, 
                                                           avatar, 
		                                           days);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr, 0.0001);
	   
	   // Sign packet
	   this.sign();
   }
}
