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


public class CTweetMesPayload extends CPayload 
{
   // Tweet ID
   long tweetID;
   
   // Comment ID
   long comID;
   
   // Message
   String mes;
   
   // Row ID
   long rowID;
   	
   public CTweetMesPayload(String adr, 
		           long tweetID,
                           long comID,
		           String mes) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Target wall
           this.tweetID=tweetID;
   
           // Message
           this.mes=mes;
           
           // Comment ID
           this.comID=comID;
           
           // Row ID
           this.rowID=Math.round(Math.random()*1000000000+this.block);
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.target_adr+
                                 this.tweetID+
                                 this.comID+
                                 this.mes+
                                 this.rowID);
 	   
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
             
             // Decode
             this.mes=UTILS.BASIC.base64_decode(this.mes);
   	
   	     // Check Message
  	     if (this.mes.length()<5 || this.mes.length()>1000)
               return new CResult(false, "Invalid message.", "CTweetMesPayload", 77);
	  
            // Statement
            Statement s=UTILS.DB.getStatement();
            
            // Comment ID exist ?
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM tweets_comments "
                                       + "WHERE rowID='"+this.rowID+"'");
            
            if (UTILS.DB.hasData(rs))
               return new CResult(false, "Message ID already exist.", "CTweetMesPayload", 77);
            
            // Content valid
            boolean found=false;
            
            // Tweet ID ?
            if (this.tweetID>0)
            {
               // Check tweetID
               rs=s.executeQuery("SELECT * "
                                 + "FROM tweets "
                                + "WHERE tweetID='"+this.tweetID+"'");
            
               // Tweet exist ?
               if (UTILS.DB.hasData(rs)) found=true;
            }
            
            // Comment ID ? 
            if (this.comID>0)
            {
               // Check tweetID
               rs=s.executeQuery("SELECT * "
                                 + "FROM tweets_comments "
                                + "WHERE rowID='"+this.comID+"'");
            
               // Tweet exist ?
               if (UTILS.DB.hasData(rs)) found=true;
            }
            
            // No comment Id / tweet ID
            if (!found)
               return new CResult(false, "Invalid tweet ID / comment ID", "CTweetMesPayload", 157);
            
	     // Check Hash
	     String h=UTILS.BASIC.hash(this.getHash()+
 			               this.target_adr+
                                       this.tweetID+
                                       this.comID+
                                       UTILS.BASIC.base64_encode(this.mes)+
                                       this.rowID);
	  
   	    if (!h.equals(this.hash)) 
   		return new CResult(false, "Invalid hash", "CTweetMesPayload", 157);
   	  
   	    // Check signature
   	    if (this.checkSig()==false)
   		return new CResult(false, "Invalid signature", "CTweetMesPayload", 157);
       
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesPayload.java", 57);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CTweetMesPayload.java", 57);
        }
       
 	// Return
 	return new CResult(true, "Ok", "CTweetMesPayload", 164);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       CResult res=this.check(block);
       if (res.passed==false) return res;
	  
       // Superclass
       super.commit(block);
       
       // Status
       String status;
       
       // Commit
       UTILS.DB.executeUpdate("INSERT INTO tweets_comments(adr,"
                                                         + "tweetID, "
                                                         + "comID, "
                                                         + "rowID, "
   		   		                         + "mes, "
   		   		                         + "block, "
                                                         + "status)"
   		   		                         + "VALUES('"+
                                                         this.target_adr+"', '"+
                                                         this.tweetID+"', '"
                                                         +this.comID+"', '"
                                                         +this.rowID+"', '"
                                                         +UTILS.BASIC.base64_encode(UTILS.BASIC.clean(this.mes))+"', '"+
                                                         +this.block+"', "
                                                         +"'ID_PENDING')");
       
       
   	// Return 
   	return new CResult(true, "Ok", "CTweetMesPayload", 70);
    }
}
