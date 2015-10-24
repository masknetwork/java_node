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
   int days;
   
   public CProfilePayload(String target_adr, 
                          String name, 
		          String description,
                          String email, 
                          String tel,
                          String website, 
                          String facebook, 
                          String avatar, 
		          int days)
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
 	   hash=UTILS.BASIC.hash(this.hash+
 			         target_adr+
 			         name+
 			         avatar+
 			         description+
 			         website+
 			         facebook+
 			         email+
 			         tel+
 			         String.valueOf(days)+
 			         String.valueOf(tstamp));
   }
   
   public CResult check(CBlockPayload block)
   {
   	  // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	
   	  // Target address valid
   	  if (UTILS.BASIC.adressValid(this.target_adr)==false) 
   		return new CResult(false, "Invalid target address", "CProfilePayload", 48);
   	
          // Name
          if (this.name.length()>50)
              return new CResult(false, "Invalid name length", "CProfilePayload", 48);
          
          // Description
          if (this.description.length()>250)
              return new CResult(false, "Invalid description length", "CProfilePayload", 48);
          
          // Email
          if (!UTILS.BASIC.emailValid(this.email))
              return new CResult(false, "Invalid email", "CProfilePayload", 48);
          
          // Tel
          // Website
          // Facebook
          // Avatar
   	 
	   // Check days
 	   if (days<10 || days>36500) 
 		 return new CResult(false, "Invalid period", "CAddEscrowPayload", 64);
   	   
 	    // Check hash
 	    String h=UTILS.BASIC.hash(this.hash+
                                      target_adr+
                                      name+
                                      avatar+
                                      description+
                                      website+
                                      facebook+
                                      email+
                                      tel+
                                      String.valueOf(days)+
                                      String.valueOf(tstamp));
 	    
            if (this.hash!=h) 
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
               Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
   	      ResultSet rs=s.executeQuery("SELECT * "
   	   		                         + "FROM profiles "
   	   		                        + "WHERE adr='"+this.target_adr+"'");
   	      if (UTILS.DB.hasData(rs)==true)
   	      {
   		    rs.next();
   		    
   		    // Obtain expiration time
   		    long expire=rs.getLong("expire");
   		    
   		    // New expiration block
   		    expire=expire+(this.days*288);
   		    
   		    // Update
   		    UTILS.DB.executeUpdate("UPDATE profiles "
   		    		            + "SET name='"+this.name+"', "
   		    		            + "avatar='"+this.avatar+"', "
   		    		            + "description='"+this.description+"', "
   		    		            + "website='"+this.website+"', "
   		    		            + "facebook='"+this.facebook+"', "
   		    		            + "email='"+this.email+"', "
   		    		            + "tel='"+this.tel+"',"
   		    		            + "block='"+this.block+"',"
   		    		            + "expire='"+String.valueOf(expire)+"' "
   		    		   + "WHERE adr='"+this.target_adr+"'");
   	      }
   	      else
   	      {
   	    	  // Expires
   	    	  long expire=this.block+(days*288);
   	    	
   	    	  UTILS.DB.executeUpdate("INSERT INTO profiles(adr, "
   	    	  		                      + "name, "
   	    	  		                      + "avatar, "
   	    	  		                      + "description, "
   	    	  		                      + "website, "
   	    	  		                      + "facebook, "
   	    	  		                      + "twitter, "
   	    	  		                      + "ebay, "
   	    	  		                      + "email, "
   	    	  		                      + "tel, "
   	    	  		                      + "country, "
   	    	  		                      + "block, "
   	    	  		                      + "expire) "
   	    	  		          + "VALUES('"+this.target_adr+"', '"+
   	    	  		                       this.name+"', '"+
   	    	  		                       this.avatar+"', '"+
   	    	  		                       this.description+"', '"+
   	    	  		                       this.website+"', '"+
   	    	  		                       this.facebook+"', '"+
   	    	  		                       this.email+"', '"+
   	    	  		                       this.tel+"', '"+
   	    	  		                       this.block+"', '"+
   	    	  		                       String.valueOf(expire)+"')");
   	      }
       }
       catch (SQLException ex) { UTILS.LOG.log("SQLException", ex.getMessage(), "CProfilePayload.java", 249);}
   		   
   	   // Return 
   	   return new CResult(true, "Ok", "CAddEscrowPayload", 82);
    }
  
}
