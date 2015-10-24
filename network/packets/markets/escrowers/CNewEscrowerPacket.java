package wallet.network.packets.markets.escrowers;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CNewEscrowerPacket extends CBroadcastPacket 
{
   public CNewEscrowerPacket(String fee_adr,
		             String adr,
                             String section,
                             String section_country,
                             String title,
                             String description,
                             String internalID,
                             String web_page,
                             double mkt_bid,
                             long mkt_days,
                             double esc_fee)    
   {
	   // Constructs the broadcast packet
	   super("ID_NEW_ESCROWER_PACKET");
			  
	   // Builds the payload class
	   CNewEscrowerPayload dec_payload=new CNewEscrowerPayload(adr,
                                                               section,
                                                               section_country,
                                                               title,
                                                               description,
                                                               internalID,
                                                               web_page,
                                                               mkt_bid,
                                                               mkt_days,
                                                               esc_fee);
					
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	   // Network fee
	   fee=new CFeePayload(fee_adr, mkt_days*mkt_bid);
			   
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
		   if (!this.tip.equals("ID_NEW_ESCROWER_PACKET")) 
		   	  return new CResult(false, "Invalid packet type", "CNewEscrowerPacket", 42);
		   
		   // Deserialize transaction data
		   CNewEscrowerPayload ass_payload=(CNewEscrowerPayload) UTILS.SERIAL.deserialize(payload);
		   
		   
		   // Return 
		   return new CResult(true, "Ok", "CNewEscrowerPacket", 74);
		}
		   
		public CResult commit(CBlockPayload block)
		{
		   	  // Superclass
		   	  CResult res=super.commit(block);
		   	  if (res.passed==false) return res;
		   	  
		   	  // Deserialize transaction data
		   	  CNewEscrowerPayload ass_payload=(CNewEscrowerPayload) UTILS.SERIAL.deserialize(payload);

			  // Fee is 0.0001 / day ?
			  res=ass_payload.commit(block);
		      if (res.passed==false) return res;
			  
			  // Return 
		   	  return new CResult(true, "Ok", "CNewEscrowerPacket", 9);
		   }
		}
