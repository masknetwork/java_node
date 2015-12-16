package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CShareAdrPayload extends CPayload 
{
	// Encrypted private key
	String priv_key;
	
	// Encrypted public key
	String pub_key;
	
	// Target address
	String share_adr;
	
   public CShareAdrPayload(String pub_key, String priv_key, String share_adr)
   {
	   // Superclass
  	   super(pub_key);
  	   
  	   // Shar with
  	   this.share_adr=share_adr;
  	   
  	   // Encrypted public key
  	   this.pub_key=pub_key;
  	   
  	   // Encrypted private key
  	   CECC ecc=new CECC(share_adr);
  	   this.priv_key=ecc.encrypt(priv_key);
  	   
  	   // Hash
   	   hash=UTILS.BASIC.hash(this.hash+			           
   			                 share_adr+
   			                 pub_key+
   			                 this.priv_key);   
   	   
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
	  
          // Check hash
	  String h=UTILS.BASIC.hash(this.getHash()+
			            share_adr+
                                    pub_key+
                                    this.priv_key);
	  if (!this.hash.equals(h)) 
		  return new CResult(false, "Invalid hash", "CShareAdrPayload", 101);
	  
	  // Signature
	  if (this.checkSig()==false)
		  return new CResult(false, "Invalid signature", "CShareAdrPayload", 101);
	    
	   // Return
 	  return new CResult(true, "Ok", "CShareAdrPayload", 46);
   }
   
   public CResult commit(CBlockPayload block)
   {
       CResult res=this.check(block);
       if (res.passed==false) return res;
	  
       // Superclass
       super.commit(block);
       
       // My address as target ?
       if (UTILS.WALLET.isMine(this.share_adr))
       {
    	   // Decrypt private key
    	   CAddress adr=UTILS.WALLET.getAddress(this.share_adr);
    	   this.priv_key=adr.decrypt(this.priv_key);
    			   
          // Insert the pending address
          UTILS.DB.executeUpdate("INSERT INTO pending_adr("
        		                 + "share_adr, "
        		                 + "pub_key, "
       		                     + "priv_key) "
       		                     + "VALUES ('"+
       		                     this.share_adr+"', '"+
       		                     this.pub_key+"', '"+
       		                     this.priv_key+"')");
          
          // User ID
          long userID=UTILS.BASIC.getAdrUserID(this.share_adr);
          
          // Update
          UTILS.DB.executeUpdate("UPDATE web_users "
                                  + "SET pending_adr=pending_adr+1 "
                                + "WHERE ID='"+userID+"'");
       }
       
       // Return 
   	   return new CResult(true, "Ok", "CRemoveEscrowerPayload", 82);
   }
}
