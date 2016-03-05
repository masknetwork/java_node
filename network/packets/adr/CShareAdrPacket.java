// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CShareAdrPacket extends CBroadcastPacket 
{
	public CShareAdrPacket(String fee_adr, 
			        String pub_key, 
			        String share_adr) throws Exception
	{
		// Super class
		super("ID_SHARE_ADR_PACKET");
		
                // Load address
                CAddress adr=UTILS.WALLET.getAddress(pub_key);
                
		// Builds the payload class
		CShareAdrPayload dec_payload=new CShareAdrPayload(pub_key, 
				                                  adr.getPrivate(), 
				                                  share_adr);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(dec_payload);
				
		// Network fee
		fee=new CFeePayload(fee_adr,  0.0001);
		   
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
	    if (!this.tip.equals("ID_SHARE_ADR_PACKET")) 
	        return new CResult(false, "Invalid packet type", "CShareAdrPacket", 39);
	   	  
            // Deserialize transaction data
	    CShareAdrPayload dec_payload=(CShareAdrPayload) UTILS.SERIAL.deserialize(payload);
	   
	   // Check fee
	   if (this.fee.amount<0.0001) 
	      return new CResult(false, "Invalid fee", "CSealAdrPayload", 42);
	   
	   // Check sig
	   if (this.checkSign()==false)
	      return new CResult(false, "Invalid signature", "CSealAdrPayload", 42);
           
           // Footprint
           CFootprint foot=new CFootprint("ID_SHARE_ADR_PACKET", 
                                          this.hash, 
                                          dec_payload.hash, 
                                          this.fee.src, 
                                          this.fee.amount, 
                                          this.fee.hash,
                                          this.block);
           
          foot.add("Share with Address", dec_payload.target_adr);
          foot.add("Address", dec_payload.pub_key);
          foot.write();
           
	   // Return 
	   return new CResult(true, "Ok", "CRemoveEscrowPacket", 45);
	}
	   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
	    CResult res=super.commit(block);
	    if (res.passed==false) return res;
	   	  
	    // Deserialize transaction data
	    CShareAdrPayload dec_payload=(CShareAdrPayload) UTILS.SERIAL.deserialize(payload);

	    // Fee is 0.0001 / day ?
	    if (this.fee.amount>=0.0001) 
	    {
		res=dec_payload.commit(block);
		if (res.passed==false) return res;
	    }
		  
	    // Return 
	    return new CResult(true, "Ok", "CShareAdrPacket", 62);
	}
}
