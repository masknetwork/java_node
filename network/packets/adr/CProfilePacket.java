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
   public CProfilePacket(String fee_adr, 
		         String target_adr, 
		         String name, 
		         String description,
                         String email, 
                         String website, 
                         String pic_back, 
                         String pic, 
		         long days,
                         String packet_sign,
                         String payload_sign) throws Exception
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
		                                           days,
                                                           payload_sign);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr, 0.0001*days);
	   
	   // Sign packet
           this.sign(packet_sign);
   }
   
     public CResult check(CBlockPayload block) throws Exception
     {
	// Super class
	CResult res=super.check(block);
	if (res.passed==false) return res;
		   	
	// Check type
	if (!this.tip.equals("ID_PROFILE_PACKET")) 
	    throw new Exception("Invalid packet type - CProfilePacket.java");
		  
	// Deserialize
	CProfilePayload payload=(CProfilePayload) UTILS.SERIAL.deserialize(this.payload);
		  
	// Check fee
	if (this.fee.amount<payload.days*0.0001)
	   throw new Exception("Invalid fee - CProfilePacket.java");
		  
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
	// Check
        CResult res=this.check(block);
                
	// Superclass
	res=super.commit(block);
	if (res.passed==false) return res;
            
        try
        {
            // Begin
            UTILS.DB.begin();
                
            if (res.passed)
            {
                // Deserialize transaction data
                CProfilePayload dec_payload=(CProfilePayload) UTILS.SERIAL.deserialize(payload);
                
	        // Commit
	        res=dec_payload.commit(block);
	        if (res.passed==false) throw new Exception(res.reason); 
            }
            else throw new Exception(res.reason); 
                    
            // Commit
            UTILS.DB.commit();
        }
        catch (Exception ex)
        {
            // Rollback
            UTILS.DB.rollback();
                
            // Exception
            throw new Exception(ex.getMessage());
        }
			  
	    // Return 
	    return new CResult(true, "Ok", "CProfilePacket.java", 9);
    }
}
