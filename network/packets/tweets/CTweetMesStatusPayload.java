// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CTweetMesStatusPayload extends CPayload 
{
   // Tweet ID
   long mesID;
   
   // Message
   String new_status;
   	
   public CTweetMesStatusPayload(String adr, 
		                 long mesID, 
		                 String new_status,
                                 String sig) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Target wall
           this.mesID=mesID;
   
           // Message
           this.new_status=new_status;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.target_adr+
                                 String.valueOf(this.mesID)+
                                 this.new_status);
 	   
 	   // Sign
 	   this.sign(sig);
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
      // Super class
   	     CResult res=super.check(block);
   	     if (res.passed==false) return res;
   	
   	     // Message status
             if (!this.new_status.equals("ID_APROVE") && 
                 !this.new_status.equals("ID_REJECT"))
             return new CResult(false, "Invalid status.", "CTweetMesStatusPayload", 77);
             
             // Statement
             Statement s=UTILS.DB.getStatement();
            
             // Check mes ID
             ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM tweets_comments "
                                       + "WHERE rowID='"+this.mesID+"'");
            
             // Tweet exist ?
             if (!UTILS.DB.hasData(rs))
                 return new CResult(false, "Invalid tweetID.", "CTweetMesStatusPayload", 77);
             
             // Next
             rs.next();
             
             // Load tweet data
             rs=s.executeQuery("SELECT * "
                              + "FROM tweets "
                             + "WHERE tweetID='"+rs.getLong("tweetID")+"'");
            
             // Next
             rs.next();
             
             // Tweet address
             if (!this.target_adr.equals(rs.getString("adr")))
                return new CResult(false, "Invalid tweet address.", "CTweetMesStatusPayload", 77);
            
	     // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
 			               this.target_adr+
                                       String.valueOf(this.mesID)+
                                       this.new_status);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CTweetMesStatusPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CTweetMesStatusPayload", 157);
       
       
 	// Return
 	return new CResult(true, "Ok", "CTweetMesStatusPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
        // Superclass
          super.commit(block);
       
          // Status
          String status;
       
          // Commit
          if (this.new_status.equals("ID_APROVE"))
          {
             // Approve
             UTILS.DB.executeUpdate("UPDATE tweets_comments "
                                  + "SET status='"+this.new_status+"' "
                                + "WHERE rowID='"+this.mesID+"'");
             
             // Load tweetID 
             Statement s=UTILS.DB.getStatement();
             
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM tweets_comments "
                                        + "WHERE rowID='"+this.mesID+"'");
             
             // Next
             rs.next();
             
             // Increase comments number
             UTILS.DB.executeUpdate("UPDATE tweets "
                                     + "SET comments=comments+1 "
                                   + "WHERE tweetID='"+rs.getLong("tweetID")+"'");
          }
          else
          {
              UTILS.DB.executeUpdate("DELETE FROM tweets_comments "
                                      + "WHERE rowID='"+this.mesID+"'");
          }
      
       
   	// Return 
   	return new CResult(true, "Ok", "CTweetMesStatusPayload", 70);
    }
}
