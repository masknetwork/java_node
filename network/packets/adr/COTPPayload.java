package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class COTPPayload extends CPayload 
{
	// Target Address
	public String target_adr; 
	
	// Next hash
	public String next_hash;
	
	// Default address
	public String def_address;
	
	// Days
	public long days;
	
   public COTPPayload(String target_adr, 
		      String next_hash,
		      String def_address,
		      long days)
   {
	   // Super class
	   super(target_adr);
	   
	    // Days
            this.days=days;
	   
	   // Default address
	   this.def_address=def_address;
	   
	   // Target adddress
	   this.target_adr=target_adr;
	   
	   // Next hash
	   this.next_hash=next_hash;
	   
	   // Days
	   this.days=days;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         next_hash+
 			         def_address);	   
 	   
 	   // Signature
 	   this.sign();
   }
   
   public CResult check(CBlockPayload block)
   {
	  // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	
   	  // Target address valid
   	  if (UTILS.BASIC.adressValid(this.target_adr)==false)
   		return new CResult(false, "Invalid target address", "COTPPayload", 50);
   	
   	  // Next password valid
   	  if (UTILS.BASIC.isSHA256(this.next_hash)==false) 
   		return new CResult(false, "Invalid next hash", "COTPPayload", 54);
      
	  // Check hash
	  String h=UTILS.BASIC.hash(this.getHash()+
			            next_hash+
			            def_address);
	  if (!this.hash.equals(h)) 
		  return new CResult(false, "Invalid hash", "COTPPayload", 62);
	  
	  // Check signature
	  if (this.checkSig()==false)
		  return new CResult(false, "Invalid signature", "COTPPayload", 62);
	  
	  // Return
 	  return new CResult(true, "Ok", "COTPPayload", 68);
   }
   
   public CResult commit(CBlockPayload block)
   {
	   // Superclass
	    CResult res=super.commit(block);
	    if (res.passed==false) return res;
	    
	    // Apply changes if possible
	    if (UTILS.STATUS.last_tables_block<this.block)
	    UTILS.BASIC.applyAdrAttr(this.target_adr, 
	  	   	             "ID_OTP", 
	  	   	             this.next_hash, 
	  	   		     this.def_address, 
	  	   	             "",
	  	   		     "",
                                     "",
                                     "",
                                     block.tstamp+(this.days*86400), 
                                     block.block);
	   
            // Return 
	    return new CResult(true, "Ok", "COTPPayload.java", 149);
   }
}
