// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import java.text.DecimalFormat;

import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.kernel.*;
import wallet.network.packets.adr.CProfilePayload;
        
public class CTransPacket extends CBroadcastPacket 
{		
    // Serial
    private static final long serialVersionUID = 100L;
   
    public CTransPacket(String fee_adr, 
			String src, 
	                String dest, 
	    	        double amount, 
			String cur,
			String mes,
                        String escrower) throws Exception
	{
		// Super class
		super("ID_TRANS_PACKET");
		   
		CTransPayload dec_payload = new CTransPayload(src, 
                                                              dest,
                                                              amount,
                                                              cur,
                                                              mes,
                                                              escrower);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(dec_payload);
				
		// Network fee
                double f=0;
                if (cur.equals("MSK"))
		    f=amount*0.001;
                else
                    f=0.0001*amount;
                
                // Min fee ?
                if (f<UTILS.CONSTANTS.MIN_FEE_AMOUNT) 
                    f=UTILS.CONSTANTS.MIN_FEE_AMOUNT;
                
                // Escrower ?
                if (!escrower.equals(""))
                    f=f+0.003;
	        
                // Fee
                CFeePayload fee=new CFeePayload(fee_adr,  f);
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
	    if (!this.tip.equals("ID_TRANS_PACKET")) 
	   	throw new Exception("Invalid packet type - CTransPacket.java");
	   	  
	    // Deserialize transaction data
	    CTransPayload dec_payload=(CTransPayload) UTILS.SERIAL.deserialize(payload);
	    dec_payload.check(block);
	      
            // Deserialize payload
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
            // Required fee
            double req_fee=0;
            
            if (dec_payload.cur.equals("MSK"))
            {
                // Required amount
                if (dec_payload.escrower.equals(""))
                    req_fee=dec_payload.amount*0.001;
                else
                    req_fee=dec_payload.amount*0.001+0.003;
                
                // Min fee ?
                if (req_fee<UTILS.CONSTANTS.MIN_FEE_AMOUNT) 
                    req_fee=UTILS.CONSTANTS.MIN_FEE_AMOUNT;
                
                // Fee
                if (fee.amount<req_fee)
                    throw new Exception("Invalid fee - CTransPacket.java");
            }
            else
            {
                // Required amount
                if (dec_payload.escrower.equals(""))
                    req_fee=dec_payload.amount*0.0001;
                else
                    req_fee=dec_payload.amount*0.0001+0.003;
                
                // Min fee ?
                if (req_fee<UTILS.CONSTANTS.MIN_FEE_AMOUNT) 
                    req_fee=UTILS.CONSTANTS.MIN_FEE_AMOUNT;
                
                // Fee
                if (fee.amount<req_fee)
                    throw new Exception("Invalid fee - CTransPacket.java");
            }
	   	  
            // Footprint
            CPackets foot=new CPackets(this);
            foot.add("Source", dec_payload.target_adr);
            foot.add("Recipient", dec_payload.dest);
            foot.add("Amount", UTILS.FORMAT_8.format(dec_payload.amount));
            foot.add("Currency", dec_payload.cur);
            foot.add("Escrower", dec_payload.escrower);
            foot.write();
        }
}