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
	
        try
        {
	   // Days
	   this.days=days;
		   
	   // Hash
	   hash=UTILS.BASIC.hash(this.getHash()+
			         String.valueOf(days));
		   
	   // Sign
	   this.sign();
        }
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CFrozeAdrPayload.java", 36); 
        }
    }
	   
    public CResult check(CBlockPayload block) 
    {
	try
        {
            // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
	    
             // Sealed address ?
             if (UTILS.BASIC.hasAttr(this.target_adr, "ID_SEALED"))
              return new CResult(false, "Target address is sealed.", "CAddSignPayload", 104);
           
	    // Hash
	    String h=UTILS.BASIC.hash(this.getHash()+
	  		                    String.valueOf(this.days));
	    
            // Hash
            if (!h.equals(this.hash))
	       return new CResult(false, "Invalid hash", "CFrozeAdrPayload.java", 54);
	          
	    // Check expire block
	    if (this.days<1)
	       return new CResult(false, "Invalid expire block", "CFrozeAdrPayload.java", 58);
	          
	    // Signature
	    if (this.checkSig()==false)
	        return new CResult(false, "Invalid hash", "CFrozeAdrPayload.java", 62);
	          
	}
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CFrozeAdrPayload.java", 67); 
        }
        
        // Return
	return new CResult(true, "Ok", "CFrozeAdrPayload", 71);
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
                                                 this.days, 
                                                 block.block);
	  	       
	  	   	// Return 
	  	   	return new CResult(true, "Ok", "CFrozeAdrPayload.java", 92);
	  	}
	 }
