package wallet.network.packets.app;

import wallet.kernel.CFootprint;
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
                             double price,
                             String packet_sign,
                             String payload_sign) throws Exception
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
  public CResult check(CBlockPayload block) throws Exception
  {
     // Super class
     CResult res=super.check(block);
     if (res.passed==false) return res;
  	
     // Check type
     if (!this.tip.equals("ID_PUBLISH_APP_PACKET")) 
        return new CResult(false, "Invalid packet type", "CDeployAppNetPayload", 39);
  	  
     // Check
     CPublishAppPayload dec_payload=(CPublishAppPayload) UTILS.SERIAL.deserialize(payload);
     res=dec_payload.check(block);
     if (!res.passed) return res;
          
     // Footprint
     CFootprint foot=new CFootprint("ID_PUBLISH_APP_PACKET", 
                                    this.hash, 
                                    dec_payload.hash, 
                                    this.fee.src, 
                                    this.fee.amount, 
                                    this.fee.hash,
                                    this.block);
                  
     foot.add("Target", String.valueOf(dec_payload.target));
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Name", String.valueOf(dec_payload.name));
     foot.add("Description", String.valueOf(dec_payload.desc));
     foot.add("Website", String.valueOf(dec_payload.website));
     foot.add("Pic", String.valueOf(dec_payload.pic));
     foot.add("Version", String.valueOf(dec_payload.ver));
     foot.add("Price", String.valueOf(dec_payload.price));
     foot.write();
      
      // Return 
      return new CResult(true, "Ok", "CDeployAppNetPayload", 45);
  }
  
  public CResult commit(CBlockPayload block) throws Exception
  {
      // Check
      CResult res=this.check(block);
      if (res.passed==false) return res;
  	
      // Superclass
      super.commit(block);
      if (res.passed==false) return res;
  	  
      // Deserialize transaction data
      CPublishAppPayload dec_payload=(CPublishAppPayload) UTILS.SERIAL.deserialize(payload);
    
      // Fee is 0.0001 / day ?
      res=dec_payload.commit(block);
      if (res.passed==false) return res;
     
      // Return 
      return new CResult(true, "Ok", "CDeployAppNetPayload", 62);
  }

}



