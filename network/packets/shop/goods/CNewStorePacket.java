package wallet.network.packets.shop.goods;

import wallet.kernel.CFootprint;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.shop.escrowers.CNewEscrowerPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewStorePacket extends CBroadcastPacket 
{
    public CNewStorePacket(String net_fee_adr,
                           String adr, 
                           String name, 
                           String desc, 
                           String website, 
                           String pic, 
                           String esc_policy,
                           long days,
                           String packet_sign,
                           String payload_sign) throws Exception
    {
        // Constructs the broadcast packet 
        super("ID_NEW_STORE_PACKET");
        
        // Builds the payload class
        CNewStorePayload dec_payload=new CNewStorePayload(adr,
                                                    name,
                                                    desc,
                                                    website,
                                                    pic, 
                                                    esc_policy,
                                                    days,
                                                    payload_sign);
					
	// Build the payload
  	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	// Network fee
	this.fee=new CFeePayload(net_fee_adr, days*0.0001);
			   
	// Sign packet
        if (!packet_sign.equals(""))
           this.sign=packet_sign;
        else
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
            CNewStorePayload dec_payload=(CNewStorePayload) UTILS.SERIAL.deserialize(payload);
            dec_payload.check(block);
            
            // Fee
            if (this.fee.amount<0.0001*dec_payload.days)
               throw new Exception("Invalid fee");
            
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
                   CNewStorePayload dec_payload=(CNewStorePayload) UTILS.SERIAL.deserialize(payload);
                
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
	    return new CResult(true, "Ok", "CStorePacket.java", 9);
        }
}
