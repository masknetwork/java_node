package wallet.network.packets.app;

import wallet.kernel.CFootprint;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CUpdateAppPacket extends CBroadcastPacket
{
  public CUpdateAppPacket(String net_fee_adr, 
                          String adr, 
                          long appID, 
                          String op, 
                          long days) throws Exception
  {
      super("ID_UPDATE_APP_PACKET");
      
     // Builds the payload class 
     CUpdateAppPayload dec_payload=new CUpdateAppPayload(adr, 
                                                         appID, 
                                                         op, 
                                                         days);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(net_fee_adr);
	   
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
     if (!this.tip.equals("ID_UPDATE_APP_PACKET")) 
        return new CResult(false, "Invalid packet type", "CUpdateAppPacket", 39);
  	  
     // Check
     CUpdateAppPayload dec_payload=(CUpdateAppPayload) UTILS.SERIAL.deserialize(payload);
     res=dec_payload.check(block);
     if (!res.passed) return res;
          
     // Footprint
     CFootprint foot=new CFootprint("ID_UPDATE_APP_PACKET", 
                                    this.hash, 
                                    dec_payload.hash, 
                                    this.fee.src, 
                                    this.fee.amount, 
                                    this.fee.hash,
                                    this.block);
                  
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Operation", String.valueOf(dec_payload.op));
     foot.add("Days", String.valueOf(dec_payload.days));
     foot.write();
      
      // Return 
      return new CResult(true, "Ok", "CUpdateAppPacket", 45);
  }
  
  public CResult commit(CBlockPayload block) throws Exception
  {
      // Check
      CResult res=super.check(block);
      if (res.passed==false) return res;
  	
      // Superclass
      super.commit(block);
      if (res.passed==false) return res;
  	  
      // Deserialize transaction data
      CUpdateAppPayload dec_payload=(CUpdateAppPayload) UTILS.SERIAL.deserialize(payload);
    
      // Fee is 0.0001 / day ?
      res=dec_payload.commit(block);
      if (res.passed==false) return res;
     
      // Return 
      return new CResult(true, "Ok", "CUpdateAppPacket", 62);
  }

}
