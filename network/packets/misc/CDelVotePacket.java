package wallet.network.packets.misc;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CDelVotePacket extends CBroadcastPacket
{
     // Serial
   private static final long serialVersionUID = 100L;
   
    public CDelVotePacket(String fee_adr,
                          String adr, 
                          String delegate,
                          String type) throws Exception
    {
        super("ID_VOTE_DEL_PACKET");
       
        // Builds the payload class
        CDelVotePayload dec_payload=new CDelVotePayload(adr, 
                                                        delegate,
                                                        type);
						
        // Build the payload
        this.payload=UTILS.SERIAL.serialize(dec_payload);
          
         
	// Network fee
        CFeePayload fee=new CFeePayload(fee_adr,  0.0001);
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
   	if (!this.tip.equals("ID_VOTE_DEL_PACKET")) 
             throw new Exception("Invalid packet type - CRenewPacket.java");
   	  
        // Deserialize transaction data
   	CDelVotePayload dec_payload=(CDelVotePayload) UTILS.SERIAL.deserialize(payload);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
	if (fee.amount<0.0001)
	   throw new Exception("Invalid fee - CRenewPacket.java");
          
        // Check payload
        dec_payload.check(block);
          
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Delegate", dec_payload.delegate);
        foot.add("Address", dec_payload.target_adr);
        foot.add("Type", dec_payload.type);
        foot.write();
   }
}
