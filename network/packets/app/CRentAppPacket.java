package wallet.network.packets.app;

import wallet.kernel.CFootprint;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CRentAppPacket extends CBroadcastPacket
{
   public CRentAppPacket(String net_fee_adr, 
                        String adr, 
                        long appID, 
                        long days) throws Exception
  {
      super("ID_RENT_APP_PACKET");
      
     // Builds the payload class 
     CRentAppPayload dec_payload=new CRentAppPayload(adr, 
                                                   appID,
                                                   days);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(net_fee_adr, days*0.0001);
	   
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
     if (!this.tip.equals("ID_RENT_APP_PACKET")) 
        return new CResult(false, "Invalid packet type", "CUpdateAppPacket", 39);
  	  
     // Check
     CRentAppPayload dec_payload=(CRentAppPayload) UTILS.SERIAL.deserialize(payload);
     res=dec_payload.check(block);
     if (!res.passed) return res;
          
     // Footprint
     CFootprint foot=new CFootprint("ID_RENT_APP_PACKET", 
                                    this.hash, 
                                    dec_payload.hash, 
                                    this.fee.src, 
                                    this.fee.amount, 
                                    this.fee.hash,
                                    this.block);
                  
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Days", String.valueOf(dec_payload.days));
     foot.write();
      
      // Return 
      return new CResult(true, "Ok", "CBuyAppPacket", 45);
  }
  
  public CResult commit(CBlockPayload block) throws Exception
  {
      // Check
      CResult res=super.check(block);
      if (res.passed==false) return res;
      
       // Deserialize transaction data
      CRentAppPayload dec_payload=(CRentAppPayload) UTILS.SERIAL.deserialize(payload);
      
      // Check payload
       res=dec_payload.check(block);
      if (res.passed==false) return res;
      
      // Superclass
      super.commit(block);
      if (res.passed==false) return res;
  	  
      // Fee is 0.0001 / day ?
      res=dec_payload.commit(block);
      if (res.passed==false) return res;
     
      // Return 
      return new CResult(true, "Ok", "CBuyAppPacket", 62);
  }

}
