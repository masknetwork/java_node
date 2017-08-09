// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.ads.CNewAdPayload;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CProfilePacket extends CBroadcastPacket 
{
   // Serial
   private static final long serialVersionUID = 1L;
    
   public CProfilePacket(String fee_adr, 
		         String target_adr, 
		         String name, 
		         String description,
                         String email, 
                         String website, 
                         String pic_back, 
                         String pic, 
		         long days) throws Exception
   {
	   // Super class
	   super("ID_PROFILE_PACKET");
	   
	   // Builds the payload class
	   CProfilePayload dec_payload=new CProfilePayload(target_adr, 
		                                           name, 
		                                           description,
                                                           email, 
                                                           website, 
                                                           pic_back,
                                                           pic,
		                                           days);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize (dec_payload);
			
	   // Network fee
	   CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
	   this.fee_payload=UTILS.SERIAL.serialize(fee);
	   
	   // Sign packet
           this.sign();
   }
   
     public void check(CBlockPayload block) throws Exception
     {
	// Super class
	super.check(block);
		   	
	// Check type
	if (!this.tip.equals("ID_PROFILE_PACKET")) 
	    throw new Exception("Invalid packet type - CProfilePacket.java");
		  
	// Deserialize
	CProfilePayload payload=(CProfilePayload) UTILS.SERIAL.deserialize(this.payload);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
	// Check fee
	if (fee.amount<payload.days*0.0001)
	   throw new Exception("Invalid fee - CProfilePacket.java");
		  
	// Check payload
	payload.check(block);
	          
        // Footprint
        CPackets foot=new CPackets(this);
        
        // Footprint data
        foot.add("Name", payload.name);
        foot.add("Back pic", payload.pic_back);
        foot.add("Pic", payload.pic);
        foot.add("Description", payload.description);
        foot.add("Website", payload.website);
        foot.add("Email", payload.email);
        foot.add("Days", String.valueOf(payload.days));
        foot.write();
    }
	     
   
}
