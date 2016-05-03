// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.exchangers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

public class CNewExchangerPayload extends CPayload 
{
    // Title
    public String title;
                                
    // Description
    public String desc;
                                
    // Web page
    public String webpage;
                                
    // Position type
    public String pos_type;
                                
    // Asset
    public String asset;
                                
    // Currency
    public String cur;
                                
    // Pay method
    public String pay_method;
                                
    // Payment details
    public String pay_details;
                                
    // Price type
    public String price_type;
                                
    // Price
    public double price;
                                
    // Price feed
    public String price_feed;
                                
    // Price branch
    public String price_branch;
                                
    // Price margin
    public double price_margin;
                                
    // Country
    public String country;
                   
    // Town type
    public String town_type;
                                
    // Town
    public String town;
                                
    // Escrowers
    public String escrowers;
		                
    // Days
    public long days;
	
    public CNewExchangerPayload(String adr,
                                String title,
                                String desc,
                                String webpage,
                                String pos_type,
                                String asset,
                                String cur,
                                String pay_method,
                                String pay_details,
                                String price_type,
                                double price,
                                String price_feed,
                                String price_branch,
                                double price_margin,
                                String country,
                                String town_type,
                                String town,
                                String escrowers,
		                long days) throws Exception
  {
      // Constructor
      super(adr);
	  
      // Title
      this.title=title;
                                
      // Description
      this.desc=desc;
                                
      // Web page
      this.webpage=webpage;
                                
      // Position type
      this.pos_type=pos_type;
                                
      // Asset
      this.asset=asset;
                                
      // Currency
      this.cur=cur;
                                
      // Pay method
      this.pay_method=pay_method;
                                
      // Payment details
      this.pay_details=pay_details;
                                
      // Price type
      this.price_type=price_type;
                                
      // Price
      this.price=price;
                                
      // Price feed
      this.price_feed=price_feed;
                                
      // Price branch
      this.price_branch=price_branch;
                                
      // Price margin
      this.price_margin=price_margin;
                                
      // Country
      this.country=country;
                   
      // Town type
      this.town_type=town_type;
                                
      // Town
      this.town=town;
                                
      // Escrowers
      this.escrowers=escrowers;
		                
      // Days
      this.days=days;
      
      // Hash
      this.hash=UTILS.BASIC.hash(this.getHash()+
                                 title+
                                 desc+
                                 webpage+
                                 pos_type+
                                 asset+
                                 cur+
                                 pay_method+
                                 pay_details+
                                 price_type+
                                 price+
                                 price_feed+
                                 price_branch+
                                 price_margin+
                                 country+
                                 town_type+
                                 town+
                                 escrowers+
		                 days);
      
      // Sign
      this.sign();
  }
  
  public CResult check(CBlockPayload block) throws Exception
  {
      try
      {
          // Super class
          CResult res=super.check(block);
          if (res.passed==false) return res;
     
          // Title
          this.title=UTILS.BASIC.base64_decode(this.title);
          if (!UTILS.BASIC.titleValid(this.title))
             return new CResult(false, "Invalid title", "CNewEscrowerPayload.java", 69);
     
          // Description
          this.desc=UTILS.BASIC.base64_decode(this.desc);
          if (!UTILS.BASIC.titleValid(this.desc))
             return new CResult(false, "Invalid description", "CNewEscrowerPayload.java", 69);
     
          // Webpage
          if (!this.webpage.equals(""))
          {
             // Web page
             this.webpage=UTILS.BASIC.base64_decode(this.webpage);
             
             // Web page
             if (!UTILS.BASIC.isLink(this.webpage))
                 return new CResult(false, "Invalid webpage", "CNewEscrowerPayload.java", 69);
          }
          
          // Position type
          if (!this.pos_type.equals("ID_BUY") && 
              !this.pos_type.equals("ID_SELL"))
           return new CResult(false, "Invalid position type", "CNewEscrowerPayload.java", 69);
     
          // Asset
          if (!this.asset.equals("MSK"))
          {
              // Asset valid
              if (!UTILS.BASIC.isSymbol(this.cur))
                 return new CResult(false, "Invalid asset symbol", "CNewEscrowerPayload.java", 69);
         
              // Search asset
              Statement s=UTILS.DB.getStatement();
              ResultSet rs=s.executeQuery("SELECT * "
                                          + "FROM assets "
                                         + "WHERE symbol='"+this.cur+"'");
          }
      
          // Pay method
          if (this.pay_method.equals("ID_LOCAL_BANK") && 
              this.pay_method.equals("ID_SEPA") && 
              this.pay_method.equals("ID_WIRE") && 
              this.pay_method.equals("ID_WU") && 
              this.pay_method.equals("ID_PAYPAL") && 
              this.pay_method.equals("ID_SKRILL") && 
              this.pay_method.equals("ID_BITCOIN") && 
              this.pay_method.equals("ID_LITECOIN") && 
              this.pay_method.equals("ID_OTHER"))
          return new CResult(false, "Invalid payment method", "CNewEscrowerPayload.java", 69);
         
          // Payment details
          this.pay_details=UTILS.BASIC.base64_decode(this.pay_details);
          if (!UTILS.BASIC.descriptionValid(this.pay_details))
             return new CResult(false, "Invalid payment details", "CNewEscrowerPayload.java", 69);
     
          // Price type
          if (!this.price_type.equals("ID_FIXED") &&
              !this.price_type.equals("ID_MOBILE"))
          return new CResult(false, "Invalid price type", "CNewEscrowerPayload.java", 69);
     
          // Mobile price
          if (this.price_type.equals("ID_MOBILE"))
          {
              // Price feed and branch
              if (!UTILS.BASIC.feedValid(this.price_feed, this.price_branch))
               return new CResult(false, "Invalid feed", "CNewEscrowerPayload.java", 69);
         
              // Price margin
              if (this.price_margin<0 || this.price_margin>25)
                  return new CResult(false, "Invalid price margin", "CNewEscrowerPayload.java", 69);
          }
     
          // Country
          if (this.country.length()!=2)
              return new CResult(false, "Invalid country", "CNewEscrowerPayload.java", 69);
     
          // Town type
          if (!this.town_type.equals("ID_ALL") && 
              !this.town_type.equals("ID_SPECIFY"))
         return new CResult(false, "Invalid town", "CNewEscrowerPayload.java", 69);
     
          // Specific town
          if (this.town_type.equals("ID_SPECIFY"))
          {
             this.town=UTILS.BASIC.base64_decode(this.town);
              
             if (this.town.length()<3 || this.town.length()>50)
                  return new CResult(false, "Invalid town", "CNewEscrowerPayload.java", 69);
          }
          
          // Escrowers
          this.escrowers=UTILS.BASIC.base64_decode(this.escrowers);
          if (!UTILS.BASIC.descriptionValid(this.escrowers))
              return new CResult(false, "Invalid escrowers", "CNewEscrowerPayload.java", 69);
          
          // Days
          if (this.days<10)
              return new CResult(false, "Invalid days", "CNewEscrowerPayload.java", 69);
          
          // Hash
         String h=UTILS.BASIC.hash(this.getHash()+
                                   UTILS.BASIC.base64_encode(title)+
                                   UTILS.BASIC.base64_encode(desc)+
                                   UTILS.BASIC.base64_encode(webpage)+
                                   pos_type+
                                   asset+
                                   cur+
                                   pay_method+
                                   UTILS.BASIC.base64_encode(pay_details)+
                                   price_type+
                                   price+
                                   price_feed+
                                   price_branch+
                                   price_margin+
                                   country+
                                   town_type+
                                   UTILS.BASIC.base64_encode(town)+
                                   UTILS.BASIC.base64_encode(escrowers)+
		                   days);
         
         if (!this.hash.equals(h))
             return new CResult(false, "Invalid hash", "CNewEscrowerPayload.java", 69);
     }
     catch (SQLException ex) 
     {  
       	UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
     }

        
     // Return 
     return new CResult(true, "Ok", "CNewExchangerPayload", 77);
  }
  
  public CResult commit(CBlockPayload block) throws Exception
  {
	try
       {
	   // Super class
	   CResult res=super.check(block);
	   if (res.passed==false) return res;
                
           // Check
           res=this.check(block);
	   if (res.passed==false) return res;
	  
           // Superclass
           super.commit(block);
           
           // Insert exchanger
           UTILS.DB.executeUpdate("INSERT INTO exchangers(adr, "
                                                        + "title, "
                                                        + "description, "
                                                        + "webpage, "
                                                        + "type, "
                                                        + "asset, "
                                                        + "cur, "
                                                        + "pay_method, "
                                                        + "pay_details, "
                                                        + "price_type, "
                                                        + "price, "
                                                        + "price_feed, "
                                                        + "price_branch, "
                                                        + "price_margin, "
                                                        + "country, "
                                                        + "town_type, "
                                                        + "town, "
                                                        + "escrowers, "
                                                        + "block, "
                                                        + "expire) VALUES ('"
                                                        +this.target_adr+"', '"
                                                        +UTILS.BASIC.base64_encode(this.title)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.desc)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.webpage)+"', '"
                                                        +this.pos_type+"', '"
                                                        +this.asset+"', '"
                                                        +this.cur+"', '"
                                                        +this.pay_method+"', '"
                                                        +UTILS.BASIC.base64_encode(this.pay_details)+"', '"
                                                        +this.price_type+"', '"
                                                        +this.price+"', '"
                                                        +this.price_feed+"', '"
                                                        +this.price_branch+"', '"
                                                        +this.price_margin+"', '"
                                                        +this.country+"', '"
                                                        +this.town_type+"', '"
                                                        +UTILS.BASIC.base64_encode(this.town)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.escrowers)+"', '"
                                                        +this.block+"', '"
                                                        +UTILS.BASIC.getExpireBlock(this.days)+"')");
         
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CNewEscrowerPayload.java", 57);
        }
        
        return new CResult(true, "Ok", "CNewExchangerPayload", 77);
   }
}
