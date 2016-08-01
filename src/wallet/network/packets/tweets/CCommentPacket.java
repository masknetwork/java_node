// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CCommentPacket extends CBroadcastPacket 
{
   // Serial
   private static final long serialVersionUID = 100L;
   
   public CCommentPacket(String fee_adr,
                          String adr, 
		          String parent_type,
                          long parentID,
		          String mes) throws Exception
   {
	   // Super class
	   super("ID_TWEET_COMMENT_PACKET");
	   
	   // Builds the payload class
	   CCommentPayload dec_payload=new CCommentPayload(adr, 
		                                             parent_type, 
                                                             parentID,
		                                             mes);
			
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
   	  if (!this.tip.equals("ID_TWEET_COMMENT_PACKET")) 
   		throw new Exception("Invalid packet type - CTweetMesPacket.java");
   	  
   	   // Deserialize transaction data
   	  CCommentPayload dec_payload=(CCommentPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      throw new Exception("Invalid fee - CTweetMesPacket.java"); 
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Parent Type", dec_payload.parent_type);
          foot.add("Parent ID", String.valueOf(dec_payload.parentID));
          foot.add("Comment ID", String.valueOf(dec_payload.comID));
          foot.add("Mes", dec_payload.mes);
          foot.write();
   	 
   }
   
  
}
