package wallet.network.packets.mes;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			           String mes) 
	{
		 super(sender_adr);
			
		 // Receiver
		 this.receiver_adr=receiver_adr;
		
		 // Subject
		 this.subj=subj;
		 
		 // Message
		 this.mes=mes;
		 
		 // Generates a key
		 SecureRandom random = new SecureRandom();
		 byte key[] = new byte[20];
		 random.nextBytes(key);
		 this.key=new String(key);
		 
		 // Message
		 try
		 {
		    this.subj=UTILS.AES.encrypt(subj, this.key);
		    this.mes=UTILS.AES.encrypt(mes, this.key);
		    
		    // Encrypt key
		    CECC ecc=new CECC(receiver_adr);
	            this.key=ecc.encrypt(this.key);
		 }
		 catch (Exception ex) 
		 {
			UTILS.LOG.log("Exception", ex.getMessage(), "CMesPayload.java", 47); 
		 }
		 
		 // Hash
		 hash=UTILS.BASIC.hash(this.getHash()+
				       this.receiver_adr+
				       this.subj+
				       this.mes+
				       this.key);
		 
		 // Sign
		 this.sign();
	}
	
	public CResult check(CBlockPayload block)
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
    	   		                                   + "tstamp)"
    	   		                                   + "VALUES ('"+
    	   		                                   this.target_adr+"', '"+
    	   		                                   this.receiver_adr+"', '"+
    	   		                                   UTILS.BASIC.base64_encode(dec_subject)+"', '"+
    	   		                                   UTILS.BASIC.base64_encode(dec_mes)+"', '"+
                                                           "0', '"+
    	   		                                   String.valueOf(UTILS.BASIC.tstamp())+"')");
    	      
                 // Statement
                 Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    	          
                 // Finds the user
                 ResultSet rs=s.executeQuery("SELECT * "
                                             + "FROM my_adr "
                                            + "WHERE adr='"+this.receiver_adr+"'");
                 rs.next();
                 long userID=rs.getLong("userID");
                 
                 // Increase unread messages
                 UTILS.DB.executeUpdate("UPDATE web_users "
                                         + "SET unread_mes=unread_mes+1 "
                                       + "WHERE ID='"+userID+"'");
                 
       
            }
	   }
    	   catch (SQLException ex)
    	   {
    	       UTILS.LOG.log("SQLException", ex.getMessage(), "CMesPayload.java", 158);
    	   }
            
	 	// Return
	 	return new CResult(true, "Ok", "CNewAssetPayload", 67);
	   }
	   
	   public CResult commit(CBlockPayload block)
	   {
	       // Superclass
	       super.commit(block);
	         
	       // Return 
	       return new CResult(true, "Ok", "CMesPayload.java", 149);
	    }
	}

