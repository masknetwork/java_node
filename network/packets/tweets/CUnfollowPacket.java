// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CUnfollowPacket extends CBroadcastPacket 
{
   // Serial
   private static final long serialVersionUID = 1L;
   
   public CUnfollowPacket(String fee_adr,
                          String adr,
                          String unfollow_address) throws Exception
   {
	   // Super class
	   super("ID_UNFOLLOW_PACKET");
	   
	   // Builds the payload class
	   CUnfollowPayload dec_payload=new CUnfollowPayload(adr, 
                                                             unfollow_address);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
           CFeePayload fee=new CFeePayload(fee_adr,  0.0001);
	   this.fee_payload=UTILS.SERIAL.serialize(fee);
	   
	   // Sign packet
           this.sign();
   }
   
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
          // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_UNFOLLOW_PACKET")) 
   		throw new Exception("Invalid packet type - CFollowPayload.java"); 
          
          // Deserialize payload
          CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
          
          // Check fee
	  if (fee.amount<0.0001)
	      throw new Exception("Invalid fee - CFollowPayload.java"); 
          
          // Deserialize transaction data
   	  CUnfollowPayload dec_payload=(CUnfollowPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check payload
          dec_payload.check(block);
           
          // Footprint
          CPackets foot=new CPackets(this);
          foot.add("Address", dec_payload.target_adr);
          foot.add("Unfollow Address", dec_payload.unfollow_adr);
          foot.write();
   	 
   }
   
  
}
