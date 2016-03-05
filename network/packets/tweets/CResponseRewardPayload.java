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


public class CResponseRewardPayload extends CPayload 
{
   // Tweet ID
   long resID;
   
   // Tip amount
   double tip;
   
   public CResponseRewardPayload(String adr, 
		                 long resID,
                                 double tip) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Follow address
           this.resID=resID;
           
           // Tip
           this.tip=tip;
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.resID+
                                 this.tip);
 	   
 	   //Sign
 	   this.sign();
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
       try
       {
             // Super class
   	     CResult res=super.check(block);
   	     if (res.passed==false) return res;
   	
   	     // Statement
             Statement s=UTILS.DB.getStatement();
             
             // Response id exist ?
             ResultSet rs=s.executeQuery("SELECT tc.*, tw.budget_cur, tw.budget "
                                         + "FROM tweets_comments AS tc "
                                         + "JOIN tweets AS tw ON tw.tweetID=tc.tweetID "
                                        + "WHERE rowID='"+this.resID+"'");
            
             
             if (!UTILS.DB.hasData(rs))
                 return new CResult(false, "Invalid hash", "CFollowPayload", 157);
             
             // Next
             rs.next();
             
             // Funds
             if (rs.getDouble("budget")<this.tip)
                return new CResult(false, "Invalid hash", "CFollowPayload", 157);
             
             // Transaction
             UTILS.BASIC.newTrans(rs.getString("adr"), 
                                  "", 
                                  this.tip, 
                                  true, 
                                  rs.getString("budget_cur"), 
                                  "You have awarded a response", 
                                  "", 
                                  UTILS.BASIC.hash(String.valueOf(this.resID)), 
                                  this.block,
                                  block,
                                  0);
             
             // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
 			               this.resID+
                                       this.tip);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CFollowPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CFollowPayload", 157);
            
            // Close
            rs.close(); s.close();
       
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CFollowPayload.java", 57);
        }
       
       
 	// Return
 	return new CResult(true, "Ok", "CFollowPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       try
       {
          CResult res=this.check(block);
          if (res.passed==false) return res;
	  
          // Superclass
          super.commit(block);
       
          // Statement
          Statement s=UTILS.DB.getStatement();
          
          // Load response data
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM tweets_comments "
                                    + "WHERE rowID='"+this.resID+"'");
          
          // Next
          rs.next();
          
          // Update tweet balance
          UTILS.DB.executeUpdate("UPDATE tweets "
                                  + "SET budget=budget-"+this.tip+" "
                                + "WHERE tweetID='"+rs.getLong("tweetID")+"'");
          
          // Increase tip
          UTILS.DB.executeUpdate("UPDATE tweets_comments "
                                  + "SET tipped=tipped+"+this.tip+" "
                                + "WHERE rowID='"+this.resID+"'");
          
          // Clear transactions
          UTILS.BASIC.clearTrans(UTILS.BASIC.hash(String.valueOf(this.resID)), "ID_ALL");
       }
       catch (SQLException ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
       }
       catch (Exception ex) 
       {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFollowPayload.java", 57);
       }
       
   	// Return 
   	return new CResult(true, "Ok", "CFollowPayload", 70);
    }
}
