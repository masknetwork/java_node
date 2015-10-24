package wallet.network.packets.markets.escrowers;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CNewEscrowerPayload extends CPayload 
{
    // Section
    String section;
    
    // Section country
    String section_country;
    
    // Title
    String title;
    
    // Description
    String description;
    
    // Internal ID
    String internalID;
    
    // Web page
    String web_page;
    
    // Market bid
    double mkt_bid;
    
    // Market days
    long mkt_days;
    
    // Fee
    double fee;  
                                      
   public CNewEscrowerPayload(String adr,
                              String section,
                              String section_country,
                              String title,
                              String description,
                              String internalID,
                              String web_page,
                              double mkt_bid,
                              long mkt_days,
                              double fee)
   {
	   super(adr);
	   
	   // Qty
	   this.fee=fee;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+String.valueOf(fee));
   }
   
   public CResult check(CBlockPayload block)
   {
      // Super class
      CResult res=super.check(block);
      if (res.passed==false) return res;
   	
      // Fee
      if (this.fee<0.1 || fee>10)
    	  return new CResult(false, "Invalid fee", "CNewEscrowerPayload.java", 69);
     
      // Hash
      String h=UTILS.BASIC.hash(this.getHash()+String.valueOf(fee));
      if (!h.equals(this.hash))
    	  return new CResult(false, "Invalid hash", "CNewEscrowerPayload.java", 74);
      
 	   // Return
 	   return new CResult(true, "Ok", "CNewEscrowerPayload", 77);
   }
   
   public CResult commit(CBlockPayload block)
   {
	   CResult res=this.check(block);
	   if (res.passed==false) return res;
	  
       // Superclass
       super.commit(block);
       
     
       
   	// Return 
   	return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
    }
}
