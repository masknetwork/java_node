// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CAddress;
import wallet.kernel.CECC;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CEscrowedTransSignPayload extends CPayload
{
     // Transaction hash
    String trans_hash;
	
    // Type
    String type;
	
    public CEscrowedTransSignPayload(String signer, String trans_hash, String type)  throws Exception
    {
        super(signer);
			
        // Receiver
	this.trans_hash=trans_hash;
		
	// Subject
	this.type=type;
		 
        // Hash
	hash=UTILS.BASIC.hash(this.getHash()+
		              this.trans_hash+
			      this.type);
		 
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
                
                // Check
                res=this.check(block);
	   	if (res.passed==false) return res;
                
	        // Check type ?
	        if (!this.type.equals("ID_RELEASE") && !this.type.equals("ID_RETURN"))
	            return new CResult(false, "Invalid signture type", "CEscrowedTransSignPayload.java", 79);
	   
                // Load transaction data
                Statement s=UTILS.DB.getStatement();
    	          
                // Finds the user
                ResultSet rs=s.executeQuery("SELECT * "
                                            + "FROM escrowed "
                                           + "WHERE trans_hash='"+this.trans_hash+"'");
           
                // Has data
                if (!UTILS.DB.hasData(rs))
                    return new CResult(false, "Invalid transaction hash", "CEscrowedTransSignPayload.java", 79);
           
                // Next
                rs.next();
                
                // Sender
                String sender=rs.getString("sender_adr");
                
                // Receiver
                String receiver=rs.getString("rec_adr");
           
                // Escrower
                String escrower=rs.getString("escrower");
                
                // Amount
                float amount=rs.getFloat("amount");
                
                // Currency
                String cur=rs.getString("cur");
                
                // Check signer for releasing funds
                if (this.type.equals("ID_RELEASE"))
                   if (!this.target_adr.equals(sender) && 
                       !this.target_adr.equals(escrower)) 
                           return new CResult(false, "Invalid signer", "CEscrowedTransSignPayload.java", 79);
                
                // Check signer for returning funds
                if (this.type.equals("ID_RETURN"))
                   if (!this.target_adr.equals(receiver) && 
                      !this.target_adr.equals(escrower)) 
                          return new CResult(false, "Invalid signer", "CEscrowedTransSignPayload.java", 79);
           
	         // Check hash
	         String h=UTILS.BASIC.hash(this.getHash()+
		                           this.trans_hash+
			                   this.type);
	         
                 // Invalid hash
                 if (!this.hash.equals(h))
	             return new CResult(false, "Invalid hash", "CMesPayload.java", 79);
                 
                 // Release ?
                 if (this.type.equals("ID_RELEASE"))
                     UTILS.BASIC.newTrans(receiver, 
                                          sender,
                                          amount,
                                          true,
                                          cur, 
                                          "Funds have been released by escrower / sender", 
                                          escrower, 
                                          this.hash, 
                                          this.block,
                                          block,
                                          0);
                 
                 // Return ?
                 if (this.type.equals("ID_RETURN"))
                     UTILS.BASIC.newTrans(sender, 
                                          receiver,
                                          amount,
                                          true,
                                          cur, 
                                          "Funds have been returned to you by escrower / sender", 
                                          escrower, 
                                          this.hash, 
                                          this.block,
                                          block,
                                          0);
                 
                 // Close
                 rs.close(); s.close();
            }
    	   catch (SQLException ex)
    	   {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CMesPayload.java", 158);
               return new CResult(false, "Ok", "CNewAssetPayload", 67);
           }
           
           // Return
	   return new CResult(true, "Ok", "CNewAssetPayload", 67);
	}
	   
	   public CResult commit(CBlockPayload block) throws Exception
	   {
               try
               {
                   // Check payload
                   CResult res=this.check(block);
                   if (!res.passed) return res;
           
	            // Superclass
	            super.commit(block);
               
                    // Clear transactions
                    UTILS.BASIC.clearTrans(hash, "ID_ALL", this.block);
               
                    // Delete transaction
                    UTILS.DB.executeUpdate("DELETE FROM escrowed "
                                          + "WHERE trans_hash='"+this.trans_hash+"'");
               }
               catch (Exception ex)
               {
                   UTILS.LOG.log("SQLException", ex.getMessage(), "CMesPayload.java", 158);
                   return new CResult(false, "Ok", "CMesPayload.java", 149);
               }
	         
	       // Return 
	       return new CResult(true, "Ok", "CMesPayload.java", 149);
	    }
	}


