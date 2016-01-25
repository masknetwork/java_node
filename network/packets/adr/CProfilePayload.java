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
		          long days)
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
          
          // Website
          if (!this.website.equals(""))
             if (!UTILS.BASIC.isLink(this.website))
               return new CResult(false, "Invalid website", "CProfilePayload", 48);
          
         
          // Pic back
          if (!this.pic_back.equals(""))
             if (!UTILS.BASIC.isLink(this.pic_back))
               return new CResult(false, "Invalid avatar", "CProfilePayload", 48);
          
          // Pic 
          if (!this.pic.equals(""))
             if (!UTILS.BASIC.isLink(this.pic))
               return new CResult(false, "Invalid avatar", "CProfilePayload", 48);
   	 
	   // Check days
 	   if (days<10 || days>36500) 
 		 return new CResult(false, "Invalid period", "CAddEscrowPayload", 64);
   	   
 	    // Check hash
 	    String h=UTILS.BASIC.hash(this.getHash()+
 			              name+
 			              pic_back+
                                      pic+
 			              description+
 			              website+
 			              email+
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
   		    		            + "pic='"+UTILS.BASIC.base64_encode(this.pic)+"', "
                                            + "pic_back='"+UTILS.BASIC.base64_encode(this.pic_back)+"', "
   		    		            + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
   		    		            + "website='"+UTILS.BASIC.base64_encode(this.website)+"', "
   		    		            + "email='"+UTILS.BASIC.base64_encode(this.email)+"', "
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
   	    	  		                      + "pic, "
                                                      + "pic_back, "
   	    	  		                      + "description, "
   	    	  		                      + "website, "
   	    	  		                      + "email, "
   	    	  		                      + "block, "
   	    	  		                      + "expire) "
   	    	  		          + "VALUES('"+this.target_adr+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.name)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.pic_back)+"', '"+
                                                       UTILS.BASIC.base64_encode(this.pic)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.description)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.website)+"', '"+
   	    	  		                       UTILS.BASIC.base64_encode(this.email)+"', '"+
   	    	  		                       this.block+"', '"+
   	    	  		                       String.valueOf(expire)+"')");
                
   	      }
       }
       catch (SQLException ex) 
       { 
           UTILS.LOG.log("SQLException", ex.getMessage(), "CProfilePayload.java", 249);
       }
   		   
   	   // Return 
   	   return new CResult(true, "Ok", "CAddEscrowPayload", 82);
    }
  
}
