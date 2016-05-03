// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.mes;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.agents.CAgent;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CMesPayload extends CPayload 
{
    // Receiver
	String receiver_adr;
	
	// Subj
	String subj;
	
	// Mes
	String mes;
	
	// Key
	String key;
	
	public CMesPayload(String sender_adr, 
			           String receiver_adr, 
			           String subj, 
			           String mes)  throws Exception
	{
            super(sender_adr);
	         
            try
            {
		 // Receiver
		 this.receiver_adr=receiver_adr;
		
		 // Subject
		 this.subj=subj;
		 
		 // Message
		 this.mes=mes;
		 
                 // Statement 
                 Statement s=UTILS.DB.getStatement();
                 
                 // Contract ?
                 ResultSet rs=s.executeQuery("SELECT * "
                                             + "FROM agents "
                                            + "WHERE adr='"+receiver_adr+"'");
                 
                 // Has data
                 if (UTILS.DB.hasData(rs))
                 {
                     this.subj=subj;
                     this.mes=mes;
                 }
                 else
                 {
		    // Generates a key
		    String k=UTILS.BASIC.randString(25);
                 
		    this.subj=UTILS.AES.encrypt(subj, k);
		    this.mes=UTILS.AES.encrypt(mes, k);
		    
		    // Encrypt key
		    CECC ecc=new CECC(receiver_adr);
	            this.key=ecc.encrypt(k);
		 }
                 
                 // Close
                 s.close();
                 
                 // Hash
		 hash=UTILS.BASIC.hash(this.getHash()+
				       this.receiver_adr+
				       this.subj+
				       this.mes+
				       this.key);
                    
		 // Sign
		 this.sign();
            }
            catch (Exception ex) 
            { 
	         UTILS.LOG.log("Exception", ex.getMessage(), "CMesPacket.java", 67); 
            }
        }
	
	
	public CResult check(CBlockPayload block) throws Exception
	{
            try
    	    {       
               // Super class
	       CResult res=super.check(block);
	       if (res.passed==false) return res;
	   	
	       // Check receiver ?
	       if (UTILS.BASIC.adressValid(this.receiver_adr)==false)
	   	   return new CResult(false, "Invalid address", "CMesPayload.java", 79);
	    
	       // Check hash
	       String h=UTILS.BASIC.hash(this.getHash()+
		                         this.receiver_adr+
		                         this.subj+
		                         this.mes+
		                         this.key);
	        if (!this.hash.equals(h))
		   return new CResult(false, "Invalid hash", "CMesPayload.java", 79);
	   
	        // Check sig
	        if (!this.checkSig())
		   return new CResult(false, "Invalid signature", "CMesPayload.java", 79);
	        
                // Statement 
                 Statement s=UTILS.DB.getStatement();
                 
                 // Contract ?
                 ResultSet rs=s.executeQuery("SELECT * "
                                             + "FROM agents "
                                            + "WHERE adr='"+receiver_adr+"'");
                 
                 // Has data
                 if (!UTILS.DB.hasData(rs))
                 {
	             // Insert message
                     if (UTILS.WALLET.isMine(this.receiver_adr)==true && block==null)
                     {
    	                 // Decrypt key
    	                 CAddress adr=UTILS.WALLET.getAddress(this.receiver_adr);
    	                 String dec_key=adr.decrypt(this.key);
    	   
    	                 // Decrypt subject
    	                 String dec_subject=UTILS.AES.decrypt(subj, dec_key);
		   
    	                  // Decrypt message
    	                  String dec_mes=UTILS.AES.decrypt(mes, dec_key);
    	   
    	                  UTILS.DB.executeUpdate("INSERT INTO mes(from_adr, "
    	   		                                       + "to_adr, "
    	   		                                       + "subject, "
    	   		                                       + "mes, "
    	   		                                       + "status, "
    	   		                                       + "tstamp, "
                                                               + "tgt)"
    	   		                                       + "VALUES ('"+
    	   		                                         this.target_adr+"', '"+
    	   		                                         this.receiver_adr+"', '"+
    	   		                                         UTILS.BASIC.base64_encode(dec_subject)+"', '"+
    	   		                                         UTILS.BASIC.base64_encode(dec_mes)+"', '"+
                                                                 "0', '"+
    	   		                                         String.valueOf(UTILS.BASIC.tstamp())+"', "
                                                                 + "'0')");
                     }
    	          }
           }
	   catch (Exception ex)
    	   {
    	       UTILS.LOG.log("SQLException", ex.getMessage(), "CNewMesPayload.java", 152);
               return new CResult(false, "SQLException", "CNewMesPayload", 153);
    	   }
            
	   // Return
	   return new CResult(true, "Ok", "CNewAssetPayload", 67);
	}
	   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
	    super.commit(block);
            
            // Statement 
            Statement s=UTILS.DB.getStatement();
                 
            // Contract ?
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM agents "
                                       + "WHERE adr='"+receiver_adr+"'");
            
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                // Next
                rs.next();
                     
                // Load message
                CAgent AGENT=new CAgent(rs.getLong("aID"), false);
                    
                // Set Message 
                AGENT.loadMes(this.target_adr,
                              this.subj,
                              this.mes, 
                              this.hash);
                    
                // Execute
                AGENT.execute("#message#", false);
            }
            
	    // Return 
	    return new CResult(true, "Ok", "CMesPayload.java", 149);
	}
    }

