// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.adr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wallet.kernel.*;
import wallet.network.CResult;
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
   
   public CProfilePayload(String target_adr, 
                          String name, 
		          String description,
                          String email, 
                          String website, 
                          String pic_back, 
                          String pic, 
		          long days,
                          String sig) throws Exception
   {
	   // Superclass
	   super(target_adr);
	   
	   // Target address
	   this.target_adr=target_adr;
 	   
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
           this.sign(sig);
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
   	  // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	  
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
          
          // Profile already exist ?
          Statement s=UTILS.DB.getStatement();
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM profiles "
                                     + "WHERE adr='"+this.target_adr+"'");
          
          if (UTILS.DB.hasData(rs))
              throw new Exception("Profile already exist - CProfilePayload.java");
   	 
	   // Check days
 	   if (days<1) 
 	      throw new Exception("Invalid days - CProfilePayload.java");
   	   
           // Can spend
           if (!UTILS.BASIC.canSpend(this.target_adr))
               throw new Exception("Can't spend from this address - CProfilePayload.java");
               
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
                 return new CResult(false, "Invalid hash", "CPostCommentPayload", 101);
 		  
 	   // Return
 	   return new CResult(true, "Ok", "CAddEscrowPayload", 67);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       CResult res=this.check(block);
       if (res.passed==false) return res;
	  
       // Superclass
       super.commit(block);
          
       // Statement
       Statement s=UTILS.DB.getStatement();
   	    
       // Insert
       UTILS.DB.executeUpdate("INSERT INTO profiles(adr, "
   	    	  		                      + "name, "
   	    	  		                      + "pic, "
                                                      + "pic_back, "
   	    	  		                      + "description, "
   	    	  		                      + "website, "
   	    	  		                      + "email, "
   	    	  		                      + "block, "
   	    	  		                      + "expire) "
   	    	  		          + "VALUES('"+this.target_adr+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.name)+"', '"+
   	    	  		                       this.pic_back+"', '"+
                                                       this.pic+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.description)+"', '"+
   	    	  		                       this.website+"', '"+
   	    	  		                       this.email+"', '"+
   	    	  		                       this.block+"', '"+
   	    	  		                       UTILS.BASIC.getExpireBlock(days)+"')");
                
   	
   		   
        // Return 
   	return new CResult(true, "Ok", "CAddEscrowPayload", 82);
    }
  
}
