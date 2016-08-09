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
 			         this.market_bid+
                                 this.hours);
 	   
 	   //Sign
           this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
           // Super class
   	  super.check(block);
   	  
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
   	 
 	
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
       // Superclass
       super.commit(block);
       
       // Commit
       UTILS.DB.executeUpdate("INSERT INTO ads "
                                    + "SET adr='"+this.target_adr+"', "
   		   		        + "title='"+UTILS.BASIC.base64_encode(this.title)+"', "
   		                        + "message='"+UTILS.BASIC.base64_encode(this.mes)+"', "
   		   	                + "link='"+UTILS.BASIC.base64_encode(this.link)+"', "
                                        + "country='"+this.country+"', "
   		      	                + "mkt_bid='"+UTILS.FORMAT_4.format(this.market_bid)+"', "
   		   	                + "expire='"+(this.block+(this.hours*60))+"', "
   		      	                + "block='"+this.block+"'");
    }
}
