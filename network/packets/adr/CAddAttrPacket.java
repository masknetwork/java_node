package wallet.network.packets.adr;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CAddAttrPacket extends CBroadcastPacket
{
    public CAddAttrPacket(String fee_adr, 
		          String target_adr, 
		          String attr,
                          String s1, 
                          String s2, 
                          String s3,
                          long l1,
                          long l2,
                          long l3,
                          double d1,
                          double d2,
                          double d3,
                          long days) throws Exception
    {
        // Constructor
        super("ID_ADD_ATTR_PACKET");
        
        // Builds the payload class
	  CAddAttrPayload dec_payload=new CAddAttrPayload(target_adr, attr,  s1, s2, s3, l1, l2, l3, d1, d2, d3, days);
					
	// Build the payload
	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
        // Network fee
	CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
	this.fee_payload=UTILS.SERIAL.serialize(fee);
			   
	// Sign packet
	this.sign();
    }
    
    // Check 
    public void check(CBlockPayload block) throws Exception
    {
	  // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_ADD_ATTR_PACKET")) 
             throw new Exception("Invalid packet type - CAddAttrPacket.java");
   	  
          // Deserialize transaction data
   	  CAddAttrPayload dec_payload=(CAddAttrPayload) UTILS.SERIAL.deserialize(payload);
          
          // Deserialize payload
          CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
          // Check fee
	  if (fee.amount<0.0001*dec_payload.days)
	      throw new Exception("Invalid fee - CAddAttrPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
          foot.add("Address", dec_payload.target_adr);
          foot.add("Attribute", dec_payload.attr);
          foot.add("S1", dec_payload.s1);
          foot.add("S2", dec_payload.s2);
          foot.add("S3", dec_payload.s3);
          foot.add("L1", dec_payload.l1);
          foot.add("L2", dec_payload.l2);
          foot.add("L3", dec_payload.l3);
          foot.add("D1", dec_payload.d1);
          foot.add("D2", dec_payload.d2);
          foot.add("D3", dec_payload.d3);
          foot.add("Days", String.valueOf(dec_payload.days));
          foot.write();
   	  
    }
		   
	
  }   
