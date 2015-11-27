package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CFrozeAdrPayload extends CPayload 
{
	// Days
    String adr;
    
    // Days
    long days;
			
    public CFrozeAdrPayload(String adr, int days)
    {
	// Block address
	super(adr);
		   
	// Days
	this.days=days;
		   
	// Hash
	hash=UTILS.BASIC.hash(this.getHash()+
			      String.valueOf(days));
		   
	// Sign
	this.sign();   
    }
	   
    public CResult check(CBlockPayload block) 
    {
	  		  // Super class
	          CResult res=super.check(block);
	          if (res.passed==false) return res;
	  	
	          // Hash
	          String h=UTILS.BASIC.hash(this.getHash()+
	  		                    String.valueOf(this.days));
	          
                  if (!h.equals(this.hash))
	          	return new CResult(false, "Invalid hash", "CBlockAdrPayload.java", 49);
	          
	          // Check expire block
	          if (this.days<1)
	          	return new CResult(false, "Invalid expire block", "CBlockAdrPayload.java", 49);
	          
	          // Signature
	          if (this.checkSig()==false)
	        	  return new CResult(false, "Invalid hash", "CBlockAdrPayload.java", 49);
	          
	          // Return
	  	      return new CResult(true, "Ok", "CBlockAdrPayload", 67);
	  	}
	  	
	  	public CResult commit(CBlockPayload block)
	  	{		
	  		// Superclass
	  	    super.commit(block);
	  	    
	  	    // Commit
	  	   	UTILS.BASIC.applyAdrAttr(this.target_adr, 
	  	   			         "ID_FROZEN", 
	  	   			         "", 
	  	   			         "", 
	  	   			         "",
	  	   			         "",
                                                 "",
                                                 "",
                                                 block.tstamp+(this.days*86400), 
                                                 block.block);
	  	       
	  	   	// Return 
	  	   	return new CResult(true, "Ok", "CBlockAdrPayload.java", 149);
	  	}
	 }
