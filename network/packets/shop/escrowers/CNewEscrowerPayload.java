// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.shop.escrowers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CNewEscrowerPayload extends CPayload 
{
    // Title
    String title;
    
    // Description
    String description;
    
    // Web page
    String web_page;
    
    // Market days
    long days;
    
    // Fee
    double fee;  
                                      
   public CNewEscrowerPayload(String adr,
                              String title,
                              String description,
                              String web_page,
                              long days,
                              double fee) throws Exception
   {
	   super(adr);
	   
	   // Qty
	   this.title=title;
           
           // Description
           this.description=description;
           
           // Web page
           this.web_page=web_page;
           
           // Market days
           this.days=days;
           
           // Fee
           this.fee=fee;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
                                 title+
                                 description+
                                 web_page+
                                 days+
                                 fee);
           
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
          this.description=UTILS.BASIC.base64_decode(this.description);
          if (!UTILS.BASIC.titleValid(this.description))
             return new CResult(false, "Invalid description", "CNewEscrowerPayload.java", 69);
      
          // Web page
          this.web_page=UTILS.BASIC.base64_decode(this.web_page);
          if (!UTILS.BASIC.isLink(this.web_page))
             return new CResult(false, "Invalid web page", "CNewEscrowerPayload.java", 69);
      
          // Fee
          if (this.fee<0.01 || fee>10)
    	      return new CResult(false, "Invalid fee", "CNewEscrowerPayload.java", 69);
      
          // Days
          if (days<10)
              return new CResult(false, "Invalid days", "CNewEscrowerPayload.java", 69);
     
          // Another offer already exist ?
          Statement s=UTILS.DB.getStatement();
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM escrowers "
                                     + "WHERE adr='"+this.target_adr+"'");
          if (UTILS.DB.hasData(rs))
             return new CResult(false, "Another offer already exist", "CNewEscrowerPayload.java", 69);
      
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    UTILS.BASIC.base64_encode(title)+
                                    UTILS.BASIC.base64_encode(description)+
                                    UTILS.BASIC.base64_encode(web_page)+
                                    days+
                                    fee);
      
          if (!h.equals(this.hash))
    	      return new CResult(false, "Invalid hash", "CNewEscrowerPayload.java", 74);
      }
      catch (SQLException ex) 
      {  
       	  UTILS.LOG.log("SQLException", ex.getMessage(), "CNewEscrowerPayload.java", 57);
      }
      catch (Exception ex) 
      {  
       	  UTILS.LOG.log("Exception", ex.getMessage(), "CNewEscrowerPayload.java", 57);
      }

      
      // Return
      return new CResult(true, "Ok", "CNewEscrowerPayload", 77);
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
       
           // Insert
           UTILS.DB.executeUpdate("INSERT INTO escrowers(adr, "
                                                   + "title, "
                                                   + "description, "
                                                   + "web_page, "
                                                   + "fee, "
                                                   + "expire, "
                                                   + "block) VALUES('"
                                                   +this.target_adr+"', '"
                                                   +UTILS.BASIC.base64_encode(this.title)+"', '"
                                                   +UTILS.BASIC.base64_encode(this.description)+"', '"
                                                   +UTILS.BASIC.base64_encode(this.web_page)+"', '"
                                                   +this.fee+"', '"
                                                   +UTILS.BASIC.getExpireBlock(this.days)+"', '"
                                                   +this.block+"')");
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CNewEscrowerPayload.java", 57);
        }

           
   	 // Return 
   	 return new CResult(true, "Ok", "CNewEscrowerPayload.java", 149);
    }
}
