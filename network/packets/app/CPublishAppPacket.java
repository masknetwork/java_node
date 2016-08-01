package wallet.network.packets.app;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CPublishAppPacket extends CBroadcastPacket
{
    public CPublishAppPacket(String fee_adr,
                             String adr,
                             String target, 
                             long appID, 
                             String categ,
                             String name, 
                             String desc, 
                             String pay_adr,
                             String website, 
                             String pic, 
                             String ver, 
                             double price) throws Exception
  {
      super("ID_PUBLISH_APP_PACKET");
      
     // Builds the payload class 
     CPublishAppPayload dec_payload=new CPublishAppPayload(target, 
                                                           appID, 
                                                           adr, 
                                                           categ,
                                                           name, 
                                                           desc,
                                                           pay_adr,
                                                           website, 
                                                           pic, 
                                                           ver, 
                                                           price);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(fee_adr);
	   
     // Sign packet
     this.sign();
  }
  
  // Check 
  public void check(CBlockPayload block) throws Exception
  {
     // Super class
     super.check(block);
     	
     // Check type
     if (!this.tip.equals("ID_PUBLISH_APP_PACKET")) 
        throw new Exception("Invalid packet type - CPublishAppPacket.java");
  	  
     // Check
     CPublishAppPayload dec_payload=(CPublishAppPayload) UTILS.SERIAL.deserialize(payload);
     
     // check
     dec_payload.check(block);
          
     // Footprint
     CPackets foot=new CPackets(this);
                  
     foot.add("Target", String.valueOf(dec_payload.target));
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Name", String.valueOf(dec_payload.name));
     foot.add("Description", String.valueOf(dec_payload.desc));
     foot.add("Website", String.valueOf(dec_payload.website));
     foot.add("Pic", String.valueOf(dec_payload.pic));
     foot.add("Version", String.valueOf(dec_payload.ver));
     foot.add("Price", String.valueOf(dec_payload.price));
     foot.write();
      
  }
  
  

}



