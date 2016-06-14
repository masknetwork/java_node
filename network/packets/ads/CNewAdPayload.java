// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.ads;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CNewAdPayload extends CPayload 
{
   // Country
   public String country;
	
   // Title
   public String title;
	
   // Message
   public String mes;
	
   // Link
   public String link;
   
   // Market bid
   public double market_bid;
   
   // Expires
   public long hours;
	
   public CNewAdPayload(String adr,
		        String country, 
		        long hours, 
		        double market_bid, 
		        String title, 
		        String mes, 
		        String link,
                        String sig) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Country
	   this.country=country;
		
	   // Hours
	   this.hours=hours;
		
	   // Title
	   this.title=title;
		
	   // Message
	   this.mes=mes;
		
	   // Link
	   this.link=link;
	   
	   // Market bid
	   this.market_bid=market_bid;
	   
	   // Expires
	   this.hours=hours;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.country+
 			         this.title+
 			         this.mes+
 			         this.link+
 			         this.market_bid+
                                 this.hours);
 	   
 	   //Sign
           this.sign(sig);
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
           // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	
   	  // Target address valid
   	  if (!UTILS.BASIC.canSpend(this.target_adr)) 
   		throw new Exception("Target address can't spend funds - CNewAdPayload.java");
   	  
   	  // Check country
   	  if (this.country.length()==2 || this.country.equals("XX"))
   	  {
   		  if (UTILS.BASIC.countryExist(this.country)==false)
   			throw new Exception("Invalid country - CNewAdPayload.java");
   	  }
   	  else
   		throw new Exception("Invalid country - CNewAdPayload.java");
   	  
          // Check hours
  	  if (this.hours<1) 
  	    throw new Exception("Invalid period - CNewAdPayload.java");
   	   
          // Bid
          if (this.market_bid<0.0001)
             throw new Exception("Invalid bid - CNewAdPayload.java");
          
  	  // Check Title 
          if (!UTILS.BASIC.isString(this.title))
            throw new Exception("Invalid title - CNewAdPayload.java");
            
          // Check title length
          if (this.title.length()<5 || this.title.length()>30)
              throw new Exception("Invalid title - CNewAdPayload.java");
   	  
          // Check message 
          if (!UTILS.BASIC.isString(this.mes))
            throw new Exception("Invalid message - CNewAdPayload.java");
            
          // Check message length
          if (this.mes.length()<50 || this.mes.length()>70)
              throw new Exception("Invalid message - CNewAdPayload.java");
          
	  // Check link
	  if (!UTILS.BASIC.isLink(this.link))
	     throw new Exception("Invalid link - CNewAdPayload.java");
          
          // Contract
          if (UTILS.BASIC.isContractAdr(this.target_adr))
               throw new Exception("Contract address - CNewAdPayload.java");
          
	   // Check Hash
	   String h=UTILS.BASIC.hash(this.getHash()+
 			             this.country+
 			             this.title+
 			             this.mes+
 			             this.link+
 			             this.market_bid+
                                     this.hours);
	  
          // Hash
   	  if (!h.equals(this.hash)) 
             throw new Exception("Invalid hash - CNewAdPayload.java");
   	 
 	  // Return
 	  return new CResult(true, "Ok", "CNewAdPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       // Superclass
       super.commit(block);
       
       // Commit
       UTILS.DB.executeUpdate("INSERT INTO ads(adr, "
   		   		            + "title, "
   		   		            + "message, "
   		   		            + "link, "
                                            + "country, "
   		   		            + "mkt_bid, "
   		   		            + "expire, "
   		   		            + "block)"
   		   		            + "VALUES('"+
                                            this.target_adr+"', '"+
   		   		            UTILS.BASIC.base64_encode(this.title)+"', '"+
   		   		            UTILS.BASIC.base64_encode(this.mes)+"', '"+
   		   		            UTILS.BASIC.base64_encode(this.link)+"', '"+
                                            this.country+"', '"+
   		   		            UTILS.FORMAT.format(this.market_bid)+"', '"+
   		   		            (this.block+(this.hours*60))+"', '"+
   		   		            this.block+"')");
   	  
          
   	// Return 
   	return new CResult(true, "Ok", "CReqDataPayload", 70);
    }
}
