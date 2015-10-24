package wallet.network.packets.markets.exchangers;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CNewExchangerPacket extends CBroadcastPacket 
{
   public CNewExchangerPacket(String fee_adr,
		              String adr,
                              String title,
                              String description,
                              String web_page,
                              String ex_tip,
                              String ex_asset,
                              String ex_cur,
                              String ex_price_type,
                              double fixed_price,
                              String feed_symbol,
                              String feed_branch_symbol,
                              double mkt_bid,
                              long mkt_days)
   {
	   // Constructs the broadcast packet
	   super("ID_NEW_EXCHANGER_PACKET");
			  
	   // Builds the payload class
	   CNewExchangerPayload dec_payload=new CNewExchangerPayload(fee_adr,
                                                                     title,
                                                                     description,
                                                                     web_page,
                                                                     ex_tip,
                                                                     ex_asset,
                                                                     ex_cur,
                                                                     ex_price_type,
                                                                     fixed_price,
                                                                     feed_symbol,
                                                                     feed_branch_symbol,
                                                                     mkt_bid,
                                                                     mkt_days);
					
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
		   if (!this.tip.equals("ID_NEW_EXCHANGER_PACKET")) 
		   	  return new CResult(false, "Invalid packet type", "CNewEscrowerPacket", 42);
		   
		   // Deserialize transaction data
		   CNewExchangerPayload ass_payload=(CNewExchangerPayload) UTILS.SERIAL.deserialize(payload);
		   
		
		   	
		   // Return 
		   return new CResult(true, "Ok", "CNewEscrowerPacket", 74);
		}
		   
		public CResult commit(CBlockPayload block)
		{
		   	  // Superclass
		   	  CResult res=super.commit(block);
		   	  if (res.passed==false) return res;
		   	  
		   	  // Deserialize transaction data
		   	  CNewExchangerPayload ass_payload=(CNewExchangerPayload) UTILS.SERIAL.deserialize(payload);

			  // Fee is 0.0001 / day ?
			  res=ass_payload.commit(block);
		      if (res.passed==false) return res;
			  
			  // Return 
		   	  return new CResult(true, "Ok", "CNewEscrowerPacket", 9);
		   }
		}
