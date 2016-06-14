// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CClosePosPacket extends CBroadcastPacket
{
   public CClosePosPacket(String net_fee_adr, 
                          String adr, 
                          long posID, 
                          long percent,
                          String packet_sign,
                          String payload_sign) throws Exception
   {
          super("ID_CLOSE_SPEC_POS_PACKET");
	  
	  // Builds the payload class
	  CClosePosPayload dec_payload=new CClosePosPayload(adr, 
                                                            posID, 
                                                            percent);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(net_fee_adr, 0.0001);
			   
	   // Sign packet
	   this.sign(payload_sign);
   }
   
   // Check 
   public CResult check(CBlockPayload block) throws Exception
   {
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
		   	
	    // Check type
	    if (!this.tip.equals("ID_CLOSE_SPEC_POS_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CClosePosPacket", 42);
		   
	    // Deserialize transaction data
	    CClosePosPayload dec_payload=(CClosePosPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CClosePosPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CClosePosPacket", 74);
    }
		   
    public CResult commit(CBlockPayload block) throws Exception
    {
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CClosePosPayload ass_payload=(CClosePosPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CClosePosPacket", 9);
    }
}
