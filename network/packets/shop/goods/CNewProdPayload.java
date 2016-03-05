// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.shop.goods;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

public class CNewProdPayload extends CPayload 
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
    
    // Pic 1
    String pic_1;
    
    // Pic 2
    String pic_2;
    
    // Pic 3        
    String pic_3;
    
    // Pic 4
    String pic_4;
    
    // Pic 5
    String pic_5;
    
    // Escrower 1
    String esc_1;
    
    // Escrower 2
    String esc_2;
    
    // Escrower 3        
    String esc_3;
    
    // Escrower 4
    String esc_4;
    
    // Escrower 5
    String esc_5;
    
    // Location town
    String prod_location_town;
    
    // Location country
    String prod_location_country;
    
    // Ships to
    String ships_to;
    
    // Ships exceptions
    String ships_exceptions;
    
    // Condition
    String condition;
    
    // Delivery
    String delivery;
    
    // Buyer protection
    String accept_escrowers;
    
    // Return policy
    String return_policy;
    
    // Postage
    double postage;
    
    // Carrier
    String carrier;
    
    // Market Bid
    double mkt_bid;
    
    // Market expires
    long mkt_days;
    
    // Price
    double price;
                         
    // Categ
    String categ;
    
    // Subcateg
    String sub_categ;
	
	
   public CNewProdPayload(String adr,
                          String section,
                          String section_country,
                          String title,
                          String description,
                          String internalID,
                          String web_page,
                          String pic_1, String pic_2, String pic_3, String pic_4, String pic_5,
                          String desc_1, String desc_2, String desc_3, String desc_4, String desc_5,
                          String prod_location_town,
                          String prod_location_country,
                          String ships_to,
                          String ships_exceptions,
                          String condition,
                          String delivery,
                          String accept_escrowers,
                          String return_policy,
                          double postage,
                          String carrier,
                          double mkt_bid,
                          long mkt_days,
                          double price,
                          String categ,
                          String sub_categ) throws Exception
   {
	  // Constructor
	  super(adr); 
	  
	  // Product location town
	  this.prod_location_town=prod_location_town;
	  
	  // Product location country
	  this.prod_location_country=prod_location_country;
	  
	  // Ships to
	  this.ships_to=ships_to;
	  
	  // Ships exception
	  this.ships_exceptions=ships_exceptions;
	  
	  // Condition
	  this.condition=condition;
	  
	  // Delivery
	  this.delivery=delivery;
	  
	  // Buyer protection
	  this.accept_escrowers=accept_escrowers;
	  
	  // Return policy
	  this.return_policy=return_policy;
	  
	  // Postage
	  this.postage=postage;
	  
	  // Carrier
	  this.carrier=carrier;
	  
	   // Hash
	   hash=UTILS.BASIC.hash(this.getHash());
	   
	   // Column hash
	   String colHash;
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
    	// Super class
    	CResult res=super.check(block);
    	if (res.passed==false) return res;
    	
        // Return
  	    return new CResult(true, "Ok", "CAddSignPayload", 61);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       // Commit
       CResult res=this.check(block);
       if (res.passed==false) return res;
      
       // Superclass
       super.commit(block);
       
       
       // Return
 	   return new CResult(true, "Ok", "CNewPhysProdPacket.java", 169);
   }
   
  
}
