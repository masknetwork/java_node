// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.shop.escrowers;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CNewEscrowerPacket extends CBroadcastPacket 
{
   public CNewEscrowerPacket(String net_fee_adr, 
                             String adr,
                             String title,
                             String description,
                             String web_page,
                             double fee,
                             long days) throws Exception
   {
	   // Constructs the broadcast packet
	   super("ID_NEW_ESCROWER_PACKET");
			  
	   // Builds the payload class
	   CNewEscrowerPayload dec_payload=new CNewEscrowerPayload(adr,
                                                                   title,
                                                                   description,
                                                                   web_page,
                                                                   days,
                                                                   fee);
					
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	   // Network fee
	   this.fee=new CFeePayload(net_fee_adr, days*0.0001);
			   
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
            if (!this.tip.equals("ID_NEW_ESCROWER_PACKET")) 
                 return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
            // Check
            CNewEscrowerPayload dec_payload=(CNewEscrowerPayload) UTILS.SERIAL.deserialize(payload);
            res=dec_payload.check(block);
            if (!res.passed) return res;
     
            // Footprint
            CFootprint foot=new CFootprint("ID_ESCROWED_TRANS_SIGN", 
                                           this.hash, 
                                           dec_payload.hash, 
                                           this.fee.src, 
                                           this.fee.amount, 
                                           this.fee.hash,
                                           this.block);
                  
            foot.add("Title", dec_payload.title);
            foot.add("Description", dec_payload.description);
            foot.add("Web Page", dec_payload.web_page);
            foot.add("Fee", String.valueOf(dec_payload.fee));
            foot.write();
  	
            // Return 
            return new CResult(true, "Ok", "CMesPacketPacket", 45);
       }
		   
       public CResult commit(CBlockPayload block) throws Exception
       {
	    // Superclass
	    CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
            CNewEscrowerPayload ass_payload=(CNewEscrowerPayload) UTILS.SERIAL.deserialize(payload);

	    // Fee is 0.0001 / day ?
	    res=ass_payload.commit(block);
	    if (res.passed==false) return res;
			  
	    // Return 
	    return new CResult(true, "Ok", "CNewEscrowerPacket", 9);
        }
}
