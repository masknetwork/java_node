package wallet.network.packets.shop.goods;

import wallet.kernel.CFootprint;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CUpdateOrderPacket extends CBroadcastPacket 
{
    public CUpdateOrderPacket(String adr, 
                              String orderUID, 
                              String status,
                              String packet_sign,
                              String payload_sign) throws Exception
    {
        // Constructs the broadcast packet 
        super("ID_UPDATE_ORDER_PACKET");
        
        // Builds the payload class
        CUpdateOrderPayload dec_payload=new CUpdateOrderPayload(adr,
                                                               orderUID,
                                                               status,
                                                               sign);
					
	// Build the payload
  	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	// Network fee
	this.fee=new CFeePayload(adr, 0.0001);
			   
	// Sign packet
        if (!sign.equals(""))
            this.sign=sign;
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
            if (!this.tip.equals("ID_UPDATE_ORDER_PACKET")) 
                 throw new Exception("Invalid packet type - CUpdateOrderPacket");
  	  
            // Check
            CUpdateOrderPayload dec_payload=(CUpdateOrderPayload) UTILS.SERIAL.deserialize(payload);
            dec_payload.check(block);
            
            // Fee
            if (this.fee.amount<0.0001)
               throw new Exception("Invalid fee");
            
            // Footprint
            CFootprint foot=new CFootprint("ID_UPDATE_ORDER_PACKET", 
                                           this.hash, 
                                           dec_payload.hash, 
                                           this.fee.src, 
                                           this.fee.amount, 
                                           this.fee.hash,
                                           this.block);
                  
            foot.add("Order UID", dec_payload.orderUID);
            foot.add("Status", dec_payload.status);
            foot.write();
  	
            // Return 
            return new CResult(true, "Ok", "CUpdateOrderPacket.java", 45);
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
                   CPayload dec_payload=(CPayload) UTILS.SERIAL.deserialize(payload);
                
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
	    return new CResult(true, "Ok", "CUpdateOrderPacket.java", 9);
        }
}
