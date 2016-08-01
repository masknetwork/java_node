package wallet.network.packets.adr;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.assets.reg_mkts.CCloseRegMarketPosPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CSealPacket extends CBroadcastPacket
{
    public CSealPacket(String fee_adr, 
		       String target_adr, 
		       long days) throws Exception
    {
        // Constructor
        super("ID_SEAL_PACKET");
        
        // Builds the payload class
	  CSealPayload dec_payload=new CSealPayload(target_adr, days);
					
	// Build the payload
	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
        // Network fee
	fee=new CFeePayload(fee_adr, 0.0001*days);
			   
	// Sign packet
	this.sign();
    }
    
    // Check 
    public void check(CBlockPayload block) throws Exception
    {
	  // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_SEAL_PACKET")) 
             throw new Exception("Invalid packet type - CSealPacket.java");
   	  
          // Deserialize transaction data
   	  CSealPayload dec_payload=(CSealPayload) UTILS.SERIAL.deserialize(payload);
           
          // Check fee
	  if (this.fee.amount<0.0001*dec_payload.days)
	      throw new Exception("Invalid fee - CSealPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Days", String.valueOf(dec_payload.days));
          foot.write();
   	  
    }
		   
	
  }   
