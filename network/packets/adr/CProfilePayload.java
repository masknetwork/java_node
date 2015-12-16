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
   
   // Avatar
   String avatar;
   
   // Description
   String description;
   
   // Website
   String website;
   
   // Facebook
   String facebook;
    
   // Email
   String email;
   
   // Telephone
   String tel;
   
   // Days
   long days;
   
   public CProfilePayload(String target_adr, 
                          String name, 
		          String description,
                          String email, 
                          String tel,
                          String website, 
                          String facebook, 
                          String avatar, 
		          long days)
   {
	   // Superclass
	   super(target_adr);
	   
	   // Target address
	   this.target_adr=target_adr;
 	   
	   // Name
	   this.name=name;
	   
	   // Avatar
	   this.avatar=avatar;
	   
	   // Description
	   this.description=description;
	   
	   // Website
	   this.website=website;
	   
	   // Facebook
	   this.facebook=facebook;
	   
	   // Email
	   this.email=email;
	   
	   // Telephone
	   this.tel=tel;
	    
	   // Days
	   this.days=days;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         name+
 			         avatar+
 			         description+
 			         website+
 			         facebook+
 			         email+
 			         tel+
 			         String.valueOf(days));
           
           // Sign
           this.sign();
   }
   
   public CResult check(CBlockPayload block)
   {
   	  // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	  
           // Sealed address ?
           if (UTILS.BASIC.hasAttr(this.target_adr, "ID_SEALED"))
              return new CResult(false, "Target address is sealed.", "CAddSignPayload", 104);
           
   	  // Name
          if (!this.name.equals(""))
            if (this.name.length()>50)
              return new CResult(false, "Invalid name length", "CProfilePayload", 48);
          
          // Description
          if (this.description.length()>250)
              return new CResult(false, "Invalid description length", "CProfilePayload", 48);
          
          // Email
          if (!UTILS.BASIC.emailValid(this.email))
              return new CResult(false, "Invalid email", "CProfilePayload", 48);
          
          // Tel
          if (!this.tel.equals(""))
             if (this.tel.length()<5 || this.tel.length()>25)    
                return new CResult(false, "Invalid telephone", "CProfilePayload", 48);
          
          // Website
          if (!this.website.equals(""))
             if (!UTILS.BASIC.isLink(this.website))
               return new CResult(false, "Invalid website", "CProfilePayload", 48);
          
          // Facebook
          if (!this.website.equals(""))
             if (!UTILS.BASIC.isLink(this.website))
               return new CResult(false, "Invalid website", "CProfilePayload", 48);
          
          // Avatar
          if (!this.avatar.equals(""))
             if (!UTILS.BASIC.isLink(this.avatar) || this.avatar.indexOf(".jpg")==-1)
               return new CResult(false, "Invalid avatar", "CProfilePayload", 48);
   	 
	   // Check days
 	   if (days<10 || days>36500) 
 		 return new CResult(false, "Invalid period", "CAddEscrowPayload", 64);
   	   
 	    // Check hash
 	    String h=UTILS.BASIC.hash(this.getHash()+
 			              name+
 			              avatar+
 			              description+
 			              website+
 			              facebook+
 			              email+
 			              tel+
 			              String.valueOf(days));
 	    
            if (!this.hash.equals(h)) 
                 return new CResult(false, "Invalid hash", "CPostCommentPayload", 101);
 		  
 	   // Return
 	   return new CResult(true, "Ok", "CAddEscrowPayload", 67);
   }
   
   public CResult commit(CBlockPayload block) 
   {
       CResult res=this.check(block);
       if (res.passed==false) return res;
	  
       // Superclass
       super.commit(block);
       
          
       // Commit
       try
       {
              // Statement
              Statement s=UTILS.DB.getStatement();
   	      
              // Result Set
              ResultSet rs=s.executeQuery("SELECT * "
   	   		                  + "FROM profiles "
   	   		                 + "WHERE adr='"+this.target_adr+"'");
   	      if (UTILS.DB.hasData(rs)==true)
   	      {
   		    rs.next();
   		    
   		    // Obtain expiration time
   		    long expire=rs.getLong("expire");
   		    
   		    // New expiration block
   		    expire=this.block+(this.days*1440);
   		    
   		    // Update
   		    UTILS.DB.executeUpdate("UPDATE profiles "
   		    		            + "SET name='"+UTILS.BASIC.base64_encode(this.name)+"', "
   		    		            + "avatar='"+UTILS.BASIC.base64_encode(this.avatar)+"', "
   		    		            + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
   		    		            + "website='"+UTILS.BASIC.base64_encode(this.website)+"', "
   		    		            + "facebook='"+UTILS.BASIC.base64_encode(this.facebook)+"', "
   		    		            + "email='"+UTILS.BASIC.base64_encode(this.email)+"', "
   		    		            + "tel='"+UTILS.BASIC.base64_encode(this.tel)+"',"
   		    		            + "block='"+this.block+"',"
   		    		            + "expire='"+String.valueOf(expire)+"' "
   		    		   + "WHERE adr='"+this.target_adr+"'");
   	      }
   	      else
   	      {
   	    	  // Expires
   	    	  long expire=this.block+(this.days*1440);
   	    	
   	    	  UTILS.DB.executeUpdate("INSERT INTO profiles(adr, "
   	    	  		                      + "name, "
   	    	  		                      + "avatar, "
   	    	  		                      + "description, "
   	    	  		                      + "website, "
   	    	  		                      + "facebook, "
   	    	  		                      + "email, "
   	    	  		                      + "tel, "
   	    	  		                      + "block, "
   	    	  		                      + "expire) "
   	    	  		          + "VALUES('"+this.target_adr+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.name)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.avatar)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.description)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.website)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.facebook)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.email)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.tel)+"', '"+
   	    	  		                       this.block+"', '"+
   	    	  		                       String.valueOf(expire)+"')");
                  
                  // Image
                  if (!this.avatar.equals(""))
                      UTILS.DB.executeUpdate("INSERT INTO imgs_stack(url) VALUES('"+this.avatar+"')");
   	      }
       }
       catch (SQLException ex) { UTILS.LOG.log("SQLException", ex.getMessage(), "CProfilePayload.java", 249);}
   		   
   	   // Return 
   	   return new CResult(true, "Ok", "CAddEscrowPayload", 82);
    }
  
}
