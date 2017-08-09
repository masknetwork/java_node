// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.adr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;

public class CProfilePayload extends CPayload 
{
   // Target address
   String target_adr;

   // Name
   String name;
   
   // Pic back
   String pic_back;
   
   // Pic
   String pic;
   
   // Description
   String description;
   
   // Website
   String website;
   
   // Email
   String email;
   
   // Days
   long days;
   
   // Serial
   private static final long serialVersionUID = 100L;
   
   public CProfilePayload(String target_adr, 
                          String name, 
		          String description,
                          String email, 
                          String website, 
                          String pic_back, 
                          String pic, 
		          long days) throws Exception
   {
	   // Superclass
	   super(target_adr);
	   
	   // Name
	   this.name=name;
	   
	   // Pic back
	   this.pic_back=pic_back;
           
           // Pic 
	   this.pic=pic;
	   
	   // Description
	   this.description=description;
	   
	   // Website
	   this.website=website;
	   
	   // Email
	   this.email=email;
	   
	   // Days
	   this.days=days;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         name+
 			         pic_back+
                                 pic+
 			         description+
 			         website+
 			         email+
 			         days);
           
           // Sign
           this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
   	  // Super class
   	  super.check(block);
   	  
          // Name
          if (!UTILS.BASIC.isTitle(this.name))
              throw new Exception("Invalid name - CProfilePayload.java");
          
          // Description
          if (!UTILS.BASIC.isDesc(this.description))
              throw new Exception("Invalid description - CProfilePayload.java");
          
          // Email
          if (!this.email.equals(""))
            if (!UTILS.BASIC.isEmail(this.email))
              throw new Exception("Invalid email - CProfilePayload.java");
          
          // Website
          if (!this.website.equals(""))
             if (!UTILS.BASIC.isLink(this.website))
               throw new Exception("Invalid website - CProfilePayload.java");
         
          // Pic back
          if (!this.pic_back.equals(""))
             if (!UTILS.BASIC.isPic(this.pic_back))
               throw new Exception("Invalid pic - CProfilePayload.java");
          
          // Pic 
          if (!this.pic.equals(""))
             if (!UTILS.BASIC.isPic(this.pic))
               throw new Exception("Invalid pic - CProfilePayload.java");
          
           // Check days
 	   if (days<1) 
 	      throw new Exception("Invalid days - CProfilePayload.java");
   	       
 	    // Check hash
 	    String h=UTILS.BASIC.hash(this.getHash()+
 			              name+
 			              pic_back+
                                      pic+
 			              description+
 			              website+
 			              email+
 			              days);
 	    
            if (!this.hash.equals(h)) 
                 throw new Exception("Invalid hash - CProfilePayload.java");
 	
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
       // Superclass
       super.commit(block);
       
       // Profile exist ?
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM profiles "
                                         + "WHERE adr='"+this.target_adr+"'");
        
       // Has data ?
        if (UTILS.DB.hasData(rs))
            UTILS.DB.executeUpdate("UPDATE profiles "
                                    + "SET name='"+UTILS.BASIC.base64_encode(this.name)+"', "
   	    	                        + "pic='"+UTILS.BASIC.base64_encode(this.pic_back)+"', "
                                        + "pic_back='"+UTILS.BASIC.base64_encode(this.pic)+"', "
   	    	                        + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
   	       	                        + "website='"+UTILS.BASIC.base64_encode(this.website)+"', "
   	    	                        + "email='"+UTILS.BASIC.base64_encode(this.email)+"', "
   	       	                        + "block='"+this.block+"' "
                                  + "WHERE adr='"+this.target_adr+"'");
   	
        else 
            UTILS.DB.executeUpdate("INSERT INTO profiles "
                                    + "SET adr='"+this.target_adr+"', "
   	                                + "name='"+UTILS.BASIC.base64_encode(this.name)+"', "
   	    	                        + "pic='"+UTILS.BASIC.base64_encode(this.pic_back)+"', "
                                        + "pic_back='"+UTILS.BASIC.base64_encode(this.pic)+"', "
   	    	                        + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
   	       	                        + "website='"+UTILS.BASIC.base64_encode(this.website)+"', "
   	    	                        + "email='"+UTILS.BASIC.base64_encode(this.email)+"', "
   	       	                        + "block='"+this.block+"', "
   	    	                        + "expire='"+(this.block+(this.days*1440))+"'");
   	    	  		        
    }
  
}
