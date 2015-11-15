package wallet.network.packets.trans;

import java.text.DecimalFormat;

import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.kernel.*;
        
public class CTransPacket extends CBroadcastPacket 
{		
	public CTransPacket(String fee_adr, 
			    String src, 
			    String dest, 
			    double amount, 
			    String cur,
			    String mes,
                            String escrower,
                            String otp_old_pass,
                            String otp_new_pass,
                            String req_field_1,
                            String req_field_2,
                            String req_field_3,
                            String req_field_4,
                            String req_field_5,
                            long cartID)
	{
		// Super class
		super("ID_TRANS_PACKET");
		   
		CTransPayload dec_payload;
                dec_payload = new CTransPayload(src, 
                                                dest,
                                                amount,
                                                cur,
                                                mes,
                                                escrower, 
                                                otp_old_pass, 
                                                otp_new_pass,
                                                req_field_1,
                                                req_field_2, 
                                                req_field_3, 
                                                req_field_4, 
                                                req_field_5, 
                                                0);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(dec_payload);
				
		// Network fee
                if (cur.equals("MSK"))
		  fee=new CFeePayload(fee_adr,  amount*0.001);
		else
                  fee=new CFeePayload(fee_adr,  0.0001);
                
		// Sign packet
		this.sign();
	}
	
	
	
	 // Check 
	   public CResult check(CBlockPayload block)
	   {
	      // Super class
	   	  CResult res=super.check(block);
	   	  if (res.passed==false) return res;
	   	
	   	  // Check type
	   	  if (!this.tip.equals("ID_TRANS_PACKET")) 
	   		return new CResult(false, "Invalid packet type", "CTransPacket", 39);
	   	  
	   	  // Check sig
	   	  if (this.checkSign()==false)
	   		return new CResult(false, "Invalid signature", "CTransPacket", 39);
	   	  
	          // Deserialize transaction data
	   	  CTransPayload dec_payload=(CTransPayload) UTILS.SERIAL.deserialize(payload);
	   	  res=dec_payload.check(block);
	   	  if (res.passed==false) return res;
                  
                  if (dec_payload.cur.equals("MSK"))
                  {
                     if (this.fee.amount<(dec_payload.amount*0.001))
                        return new CResult(false, "Invalid fee", "CTransPacket", 39);
                  }
                  else
                  {
                     if (this.fee.amount<(dec_payload.amount*0.0001))
                        return new CResult(false, "Invalid fee", "CTransPacket", 39); 
                  }
	   	  
                  // Footprint
                  CFootprint foot=new CFootprint("ID_TRANS_PACKET", 
                                                this.hash, 
                                                dec_payload.hash, 
                                                this.fee.src, 
                                                this.fee.amount, 
                                                this.fee.hash,
                                                this.block);
                  
                 foot.add("Source", dec_payload.src);
                 foot.add("Recipient", dec_payload.dest);
                 foot.add("Amount", String.valueOf(dec_payload.amount));
                 foot.add("Currency", dec_payload.cur);
                 foot.add("Escrower", dec_payload.escrower);
                 foot.write();
          
	   	  // Return 
	   	  return new CResult(true, "Ok", "CNewAdPacket", 45);
	   }
	   
	   public CResult commit(CBlockPayload block)
	   {
	   	  // Superclass
	   	  CResult res=super.commit(block);
	   	  if (res.passed==false) return res;
	   	  
	   	  // Deserialize transaction data
	   	  CTransPayload dec_payload=(CTransPayload) UTILS.SERIAL.deserialize(payload);

		  // Fee is 0.01% ?
                  if (Double.parseDouble(UTILS.FORMAT.format(this.fee.amount))>=Double.parseDouble(UTILS.FORMAT.format(this.fee.amount))) 
		  {
			  res=dec_payload.commit(block);
			  if (res.passed==false) return res;
		  }
		  else return new CResult(false, "Invalid fee amount", "CTransPacket", 39); 
		  
		  // Return 
	   	  return new CResult(true, "Ok", "CTransPacket", 62);
	   }
}