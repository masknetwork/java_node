// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CVotePacket extends CBroadcastPacket 
{
   // Serial
   private static final long serialVersionUID = 100L;
   
   public CVotePacket(String fee_adr,
                      String adr,
                      String target_type,
                      long targetID,
                      String type) throws Exception
   {
	   // Super class
	   super("ID_VOTE_PACKET");
	   
	   // Builds the payload class
	   CVotePayload dec_payload=new CVotePayload(adr, 
                                                     target_type,
                                                     targetID,
                                                     type);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  0.0001);
	   
	   // Sign packet
           this.sign();
   }
   
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
          // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_VOTE_PACKET")) 
             throw new Exception("Invalid packet type - CLikePacket.java");
   	  
   	  // Deserialize transaction data
   	  CVotePayload dec_payload=(CVotePayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      throw new Exception("Invalid packet type - CLikePacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
          foot.add("Address", dec_payload.target_adr);
          foot.add("Target Type", dec_payload.target_type);
          foot.add("Target ID", dec_payload.targetID);
          foot.add("Type", dec_payload.type);
          foot.write();
   	  
   }
   
  
}
