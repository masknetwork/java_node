// Author : Vlad Cristian
// Contact : vcris@gmx.com

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
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr, 0.0001*days);
	   
	   // Sign packet
	   this.sign();
   }
   
     public CResult check(CBlockPayload block) throws Exception
     {
	// Super class
	CResult res=super.check(block);
	if (res.passed==false) return res;
		   	
	// Check type
	if (!this.tip.equals("ID_PROFILE_PACKET")) 
	    return new CResult(false, "Invalid packet type", "CProfilePacket", 44);
		  
	// Deserialize
	CProfilePayload payload=(CProfilePayload) UTILS.SERIAL.deserialize(this.payload);
		  
	// Check fee
	if (this.fee.amount<payload.days*0.0001)
	   return new CResult(false, "Invalid fee", "CProfilePacket", 44);
		  
	// Check sig
	if (!this.checkSign())
	   return new CResult(false, "Invalid signature", "CProfilePacket", 44);
		  
	// Check payload
	res=payload.check(block);
	if (res.passed==false) return res;
                  
        // Footprint
        CFootprint foot=new CFootprint("ID_PROFILE_PACKET", 
                                       this.hash, 
                                       payload.hash, 
                                       this.fee.src, 
                                       this.fee.amount, 
                                       this.fee.hash,
                                       this.block);
        
        // Footprint data
        foot.add("Name", payload.name);
        foot.add("Back pic", payload.pic_back);
        foot.add("Pic", payload.pic);
        foot.add("Description", payload.description);
        foot.add("Website", payload.website);
        foot.add("Email", payload.email);
        foot.add("Days", String.valueOf(payload.days));
        foot.write();
		  
	// Return 
	return new CResult(true, "Ok", "CProfilePacket", 47);
    }
	     
    public CResult commit(CBlockPayload block) throws Exception
    {
	// Superclass
	CResult res=super.commit(block);
	if (res.passed==false) return res;
	   	   
	// Deserialize
	CProfilePayload pay=(CProfilePayload) UTILS.SERIAL.deserialize(this.payload);
	 	  
	// Commit the payload
	res=pay.commit(block);
	if (res.passed==false) return res;
	       
	// Return 
	return new CResult(true, "Ok", "CProfilePacket", 47);
    }
}
