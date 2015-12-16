package wallet.network.packets.trans;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.crypto.SealedObject;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.details.*;

public class CTransPayload extends CPayload 
{
	// Source
	public String src="";
	
	// Destination
	public String dest="";
	
	// Amount
	public double amount=0.0001;
	
	// Currency
	public String cur="MSK";
	
	// Escrower
	public String escrower="";
        
        // Message
        public CMesDetails mes=null;
        
        // Additional data
        public CMiscDetails data=null;
        
        // OTP old pass
        public String otp_old_pass="";
        
        // OTP new hash
        public String otp_new_hash="";
       
        
	public CTransPayload(String src, 
                             String dest, 
                             double amount, 
                             String cur, 
                             String mes,
                             String escrower, 
                             String otp_old_pass, 
                             String otp_new_hash,
                             String req_field_1,
                             String req_field_2,
                             String req_field_3,
                             String req_field_4,
                             String req_field_5,
                             long cartID)
        {
           // Constructo
           super(src);
           
	   // Source
	   this.src=src;
		
           // Destination
	   this.dest=dest;
		
	   // Amount
	   this.amount=amount;
           
           // Min fee ?
           if (this.dest=="default" && this.amount<0.0001) this.amount=0.0001;
		
	   // Currency
	   this.cur=cur;
                
           // Escrower
           this.escrower=escrower;
           
           // OTP old password
           this.otp_old_pass=otp_old_pass;
           
           // OTP new hash
           this.otp_new_hash=otp_new_hash;
           
           // Digest
           String d=this.getHash()+
		    this.src+
                    this.dest+
                    UTILS.FORMAT.format(this.amount)+
                    this.cur+
                    this.escrower+
                    this.otp_old_pass+
                    this.otp_new_hash;
           
           // Message 
           if (!mes.equals("")) 
           {
                this.mes=new CMesDetails(mes, this.dest);
                d=d+this.mes.hash;
           }
           
           // Data
           if (!req_field_1.equals("")) 
           {
               this.data=new CMiscDetails(req_field_1, 
                                          req_field_2, 
                                          req_field_3, 
                                          req_field_4, 
                                          req_field_5, 
                                          this.dest);
               d=d+this.data.hash;
           }
           
	   hash=UTILS.BASIC.hash(d);
            
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
	    
	    // Check source
	    if (!UTILS.BASIC.addressExist(this.src))
	    	return new CResult(false, "Invalid source address.", "CTransPayload", 77);
	    
            // Source frozen ?
            if (UTILS.BASIC.hasAttr(this.src, "ID_FROZEN"))
               return new CResult(false, "Source address is frozen.", "CTransPayload", 77);
            
	    // Check dest
	    if (!UTILS.BASIC.adressValid(this.dest))
	    	return new CResult(false, "Invalid dest address.", "CTransPayload", 77);
	    
	    // Check amount
            if (this.cur.equals("MSK"))
            {
	      if (this.amount<0.0001)
	      	  return new CResult(false, "Invalid amount.", "CTransPayload", 77);
            }
            else
            {
                if (this.amount<0.00000001)
	      	  return new CResult(false, "Invalid amount.", "CTransPayload", 77);
            }
            
            // Message included ?
            if (this.mes!=null)
            {
                res=this.mes.check();
                if (!res.passed) return new CResult(false, "Invalid message.", "CTransPayload", 77);
            }
            
            // Data included ?
            if (this.data!=null)
            {
                res=this.data.check(this.dest);
                if (!res.passed) return new CResult(false, "Invalid data.", "CTransPayload", 77);
            }
            
           // Digest
           String d=this.getHash()+
		    this.src+
                    this.dest+
                    UTILS.FORMAT.format(this.amount)+
                    this.cur+
                    this.escrower+
                    this.otp_old_pass+
                    this.otp_new_hash;
           
           // Message 
           if (this.mes!=null) d=d+this.mes.hash;
           
           // Data
           if (this.data!=null) d=d+this.data.hash;
           
           // Hash
           String h=UTILS.BASIC.hash(d);
            
           // Check hash
           if (!this.hash.equals(h))
               return new CResult(false, "Invalid hash", "CTransPayload", 77);
            
            // Statement
            Statement s=UTILS.DB.getStatement();
                   
	    // Check cur
	    if (!this.cur.equals("MSK"))
            {
               // Symbol valid ?
               if (!UTILS.BASIC.symbolValid(this.cur))
                  return new CResult(false, "Invalid currency", "CTransPayload", 77);
            }
            
            // Check escrower
	    if (!this.escrower.equals(""))
	    {
	        if (!UTILS.BASIC.adressValid(this.escrower))
	        	return new CResult(false, "Invalid escrower.", "CTransPayload", 77);
	    }
	    
             // Restricted recipients ?
            if (UTILS.BASIC.hasAttr(this.src, "ID_RESTRICT_REC"))
            {
                // Load data
                ResultSet rs=s.executeQuery("SELECT * "
                                            + "FROM adr_options "
                                           + "WHERE adr='"+this.src+"' "
                                             + "AND op_type='ID_RESTRICT_REC'");
                
                // Next
                rs.next();
                
                // Recipient 1
                String rec_1=rs.getString("par_1");
                
                // Recipient 2
                String rec_2=rs.getString("par_2");
                
                // Recipient 3
                String rec_3=rs.getString("par_3");
                
                // Recipient 4
                String rec_4=rs.getString("par_4");
                
                // Recipient 5
                String rec_5=rs.getString("par_5");
                
                // Check recipient
                if (!this.dest.equals(rec_1) && 
                    !this.dest.equals(rec_2) && 
                    !this.dest.equals(rec_3) && 
                    !this.dest.equals(rec_4) && 
                    !this.dest.equals(rec_5))
                return new CResult(false, "Invalid recipient.", "CTransPayload", 240);
            }
            
            // OTP ?
            if (UTILS.BASIC.hasAttr(this.src, "ID_OTP"))
            {
                // Load data
                ResultSet rs=s.executeQuery("SELECT * "
                                            + "FROM adr_options "
                                           + "WHERE adr='"+this.src+"' "
                                             + "AND op_type='ID_OTP'");
                
                // Next
                rs.next();
                
                // Old hash
                String old_hash=rs.getString("par_1");
                
                // Emergency address
                String em_adr=rs.getString("par_2");
                
                // Destination not emergency address ?
                if (!this.dest.equals(em_adr))
                {
                    // Password hash
                    String pass_hash=UTILS.BASIC.hash(this.otp_old_pass);
                    
                    // Match ?
                    if (!pass_hash.equals(old_hash))
                        return new CResult(false, "Invalid OTP pass", "CTransPayload", 77);
                }   
            }
            
	    // Check source balance
            double balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.src, this.cur);
	    if (balance<this.amount)
	    	return new CResult(false, "Insufficient funds to execute transaction ("+this.amount+")", "CTransPayload", 77);
	    
            // Insert pending transaction
	    UTILS.BASIC.newTrans(this.src, 
                                 this.dest,
                                 -this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction to another address "+this.src, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block);
                
            // To destination
            if (this.escrower.equals("") && !UTILS.BASIC.hasAttr(this.src, "ID_MULTISIG"))
            UTILS.BASIC.newTrans(this.dest, 
                                 this.src,
                                 this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction from address "+this.src, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block);
            
            if (UTILS.WALLET.isMine(this.dest))
            {
               // Message
               if (this.mes!=null && block==null) this.mes.getMessage(this.dest, hash);
               
               // Data
               if (this.data!=null && block==null) this.data.getData(this.dest, hash);
               
               // IPN
              this.checkIPN("unconfirmed");
            }
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 57);
            }
              
	    // Return
	    return new CResult(true, "Ok", "CTransPayload", 164);
        }
         
         public void checkIPN(String status)
         {
             try
             {
                // Load details
                Statement s=UTILS.DB.getStatement();
                    
                 // IPN
                 ResultSet rs=s.executeQuery("SELECT * FROM ipn WHERE adr='"+this.dest+"'");
                 
                 if (UTILS.DB.hasData(rs) && UTILS.WALLET.isMine(this.dest))
                 {
                   // Next
                   rs.next();
                   
                   // creates loader thread
                   CLoader loader=new CLoader(rs.getString("web_link"), rs.getString("web_pass"));
                   
                   // Status
                   loader.addParam("status", status);
                   
                   // Source
                   loader.addParam("src", this.src);
                   
                   // Dest
                   loader.addParam("dest", this.dest);
                   
                   // Amount
                   loader.addParam("amount", String.valueOf(this.amount));
                   
                   // Currency
                   loader.addParam("currency", this.cur);
                   
                   // transaction hash
                   loader.addParam("tx_hash", this.hash);
                   
                   // Block
                   loader.addParam("block", String.valueOf(this.block));
                   
                   // Start
                   loader.start();
                 }
             }
             catch (SQLException ex) 
       	     {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 367);
             }
         }
         
         public CResult commit(CBlockPayload block)
	 { 
             try
             {
                 CResult res=this.check(block);
                 if (!res.passed) return res;
                     
                 // Commit parent
                 super.commit(block);
                 
                // Take coins
                UTILS.BASIC.clearTrans(hash, "ID_SEND");
                
                 // IPN
                 this.checkIPN("confirmed");
              
                // Multisig address ?
                if (UTILS.BASIC.hasAttr(this.src, "ID_MULTISIG"))
                {
                     // Load details
                     Statement s=UTILS.DB.getStatement();
                
                    // Load data
                    ResultSet rs=s.executeQuery("SELECT * "
                                                + "FROM adr_options "
                                               + "WHERE adr='"+this.src+"' "
                                                 + "AND op_type='ID_MULTISIG'");
                    
                    // Next 
                    rs.next();
                    
                    // Signers
                    String signer_1=rs.getString("par_1");
                    String signer_2=rs.getString("par_2");
                    String signer_3=rs.getString("par_3");
                    String signer_4=rs.getString("par_4");
                    String signer_5=rs.getString("par_5");
                    
                    // Minimum signers
                    int min=rs.getInt("par_6");
                    
                    // Insert escrowed
                    UTILS.DB.executeUpdate("INSERT INTO multisig(trans_hash, "
                                                              + "sender_adr, "
                                                              + "rec_adr, "
                                                              + "signer_1, "
                                                              + "sign_1, "
                                                              + "signer_2, "
                                                              + "sign_2, "
                                                              + "signer_3, "
                                                              + "sign_3, "
                                                              + "signer_4, "
                                                              + "sign_4, "
                                                              + "signer_5, "
                                                              + "sign_5, "
                                                              + "amount, "
                                                              + "cur, "
                                                              + "required, "
                                                              + "block) VALUES('"
                                                              +this.hash+"', '"
                                                              +this.src+"', '"
                                                              +this.dest+"', '"
                                                              +signer_1+"', '', '"
                                                              +signer_2+"', '', '"
                                                              +signer_3+"', '', '"
                                                              +signer_4+"', '', '"
                                                              +signer_5+"', '', '"
                                                              +this.amount+"', '"
                                                              +this.cur+"', '"
                                                              +min+"', '"
                                                              +this.block+"')");
                   
                }
                else
                {
                   // Deposit coins
                   if (this.escrower.equals(""))
                   {
                      // Clear transaction for receiver
                      UTILS.BASIC.clearTrans(hash, "ID_RECEIVE");
                
                      // OTP
                      if (!this.otp_new_hash.equals(""))
                          UTILS.DB.executeUpdate("UPDATE adr_options "
                                                  + "SET par_1='"+this.otp_new_hash+"' "
                                                + "WHERE adr='"+this.src+"' "
                                                  + "AND op_type='ID_OTP'");
                   }
                   else
                   {
                      UTILS.DB.executeUpdate("INSERT INTO escrowed(trans_hash, "
                                                                + "sender_adr, "
                                                                + "rec_adr, "
                                                                + "escrower, "
                                                                + "amount, "
                                                                + "cur, "
                                                                + "block) VALUES ('"
                                                                +this.hash+"', '"
                                                                +this.src+"', '"
                                                                +this.dest+"', '"
                                                                +this.escrower+"', '"
                                                                +this.amount+"', '"
                                                                +this.cur+"', '"
                                                                +this.block+"')");
                   
                      // Source my address ?
                      if (UTILS.WALLET.isMine(this.src))
                      {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_esc=unread_esc+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.src)+"' ");
                      }
                   
                      // Destination my address ?
                      if (UTILS.WALLET.isMine(this.dest))
                      {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_esc=unread_esc+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.dest)+"' ");
                      }
                   
                      // Escrower my address ?
                      if (UTILS.WALLET.isMine(this.escrower))
                      {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_esc=unread_esc+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.escrower)+"' ");
                      }
                   }
                }
             }
             catch (SQLException ex) 
       	     {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 559);
             }
             
             // Ok
	     return new CResult(true, "Ok", "CTransPayload", 563);
	}
	 
}
