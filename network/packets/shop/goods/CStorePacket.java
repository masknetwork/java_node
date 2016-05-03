package wallet.network.packets.shop.goods;

import wallet.kernel.CFootprint;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.shop.escrowers.CNewEscrowerPayload;
import wallet.network.packets.trans.CFeePayload;

public class CStorePacket extends CBroadcastPacket 
{
    public CStorePacket(String net_fee_adr,
                        String adr, 
                        String name, 
                        String desc, 
                        String website, 
                        String pic, 
                        long days) throws Exception
    {
        // Constructs the broadcast packet 
        super("ID_NEW_STORE_PACKET");
        
        // Builds the payload class
        CStorePayload dec_payload=new CStorePayload(adr,
                                                        name,
                                                        desc,
                                                        website,
                                                        pic, 
                                                        days);
					
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
            if (!this.tip.equals("ID_NEW_STORE_PACKET")) 
                 return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
            // Check
            CStorePayload dec_payload=(CStorePayload) UTILS.SERIAL.deserialize(payload);
            res=dec_payload.check(block);
            if (!res.passed) return res;
     
            // Footprint
            CFootprint foot=new CFootprint("ID_NEW_STORE", 
                                           this.hash, 
                                           dec_payload.hash, 
                                           this.fee.src, 
                                           this.fee.amount, 
                                           this.fee.hash,
                                           this.block);
                  
            foot.add("Name", dec_payload.name);
            foot.add("Description", dec_payload.desc);
            foot.add("Web Page", dec_payload.website);
            foot.add("Pic", String.valueOf(dec_payload.pic));
            foot.add("Days", String.valueOf(dec_payload.days));
            foot.write();
  	
            // Return 
            return new CResult(true, "Ok", "CStorePacket.java", 45);
       }
		   
       public CResult commit(CBlockPayload block) throws Exception
       {
	    // Superclass
	    CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
            CStorePayload ass_payload=(CStorePayload) UTILS.SERIAL.deserialize(payload);

	    // Fee is 0.0001 / day ?
	    res=ass_payload.commit(block);
	    if (res.passed==false) return res;
			  
	    // Return 
	    return new CResult(true, "Ok", "CStorePacket.java", 9);
        }
}
