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
		        String link) throws Exception
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
 			         UTILS.FORMAT.format(this.market_bid)+
                                 String.valueOf(this.hours));
 	   
 	   //Sign
 	   this.sign();
           
           System.out.print(this.block);
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
           // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	
   	  // Target address valid
   	  if (UTILS.BASIC.adressValid(this.target_adr)==false) 
   		return new CResult(false, "Invalid target address", "CNewAdPayload", 51);
   	  
   	  // Check country
   	  if (this.country.length()==2 || this.country.equals("XX"))
   	  {
   		  if (UTILS.BASIC.countryExist(this.country)==false)
   			return new CResult(false, "Invalid target country", "CNewAdPayload", 51); 
   	  }
   	  else
   		return new CResult(false, "Invalid target country", "CNewAdPayload", 51);  
   	  
          // Check hours
  	  if (this.hours<1) 
  	    return new CResult(false, "Invalid period.", "CNewAdPayload", 77);
   	  
      
  	  // Check Title 
          if (this.title.length()<5 || title.length()>30)
            return new CResult(false, "Invalid title.", "CNewAdPayload", 77);
            
            // Check letters
            if (!this.title.matches("[A-Za-z0-9\\.,?!$%'\\s]+"))
                return new CResult(false, "Invalid title.", "CNewAdPayload", 77);
   	  
          // Check Message
  	  if (this.mes.length()<50 || this.mes.length()>70)
            return new CResult(false, "Invalid message.", "CNewAdPayload", 77);
	  
          if (!this.mes.matches("[A-Za-z0-9\\.,?!$%'\\s]+"))
               return new CResult(false, "Invalid message.", "CNewAdPayload", 77);
          
	  // Check link
	  if (!UTILS.BASIC.isLink(this.link))
	     return new CResult(false, "Invalid link.", "CNewAdPayload", 77);
          
	    // Check Hash
	   String h=UTILS.BASIC.hash(this.getHash()+
 			             this.country+
 			             this.title+
 			             this.mes+
 			             this.link+
 			             UTILS.FORMAT.format(this.market_bid)+
                                     String.valueOf(this.hours));
	  
   	  if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CNewAdPayload", 157);
   	  
   	  // Check signature
   	  if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CNewAdPayload", 157);
 		  
 	  // Return
 	  return new CResult(true, "Ok", "CNewAdPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       CResult res=this.check(block);
       if (res.passed==false) return res;
	  
       // Superclass
       super.commit(block);
       
       // Commit
   	   UTILS.DB.executeUpdate("INSERT INTO ads(adr, "
   		   		                + "title, "
   		   		                + "message, "
   		   		                + "link, "
                                                + "country, "
   		   		                + "mkt_bid, "
   		   		                + "expires, "
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
