// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CSealAdrPayload extends CPayload 
{
	// Address
	String adr;
        
        // Days
        public long days;
	
    public CSealAdrPayload(String adr, long days) throws Exception
    {
	  super(adr);
		
	   // Days
	   this.days=days;
	   
	   // Address
	   this.adr=adr;
	   
	   // Hash
	   hash=UTILS.BASIC.hash(this.getHash()+
			         adr+
	 			 String.valueOf(days));
	 			   
	   // Signature
	   this.sign();
   }
	     
   public CResult check(CBlockPayload block) throws Exception
   {
	 		// Super class
	         CResult res=super.check(block);
	         if (res.passed==false) return res;
	 	
	         // Hash
	         String h=hash=UTILS.BASIC.hash(this.getHash()+
	        		                        this.adr+
	 		                                String.valueOf(this.days));
	         if (!h.equals(this.hash))
	         	return new CResult(false, "Invalid hash", "CSealAdrPayload.java", 49);
	         
	         
	         // Sign 
	         if (this.checkSig()==false)
	        	return new CResult(false, "Invalid hash", "CSealAdrPayload.java", 49);
	         
	         // Return
	 	    return new CResult(true, "Ok", "CSealAdrPayload", 67);
	}
   
	 	
	public CResult commit(CBlockPayload block) throws Exception
	{		
	 		CResult res=this.check(block);
	 		if (res.passed==false) return res;
	 		  
	 	    // Superclass
	 	    super.commit(block);
	 	    
	 	    // Commit
	 	   	UTILS.BASIC.applyAdrAttr(this.target_adr, 
	 	   			                 "ID_SEALED", 
	 	   			                 "", 
	 	   			                 "", 
	 	   			                 "",
	 	   			                 "",
                                                         "",
                                                         "",
                                                         this.days,  
                                                         block.block);
	 	       
	 	   	// Return 
	 	   	return new CResult(true, "Ok", "CSealAdrPayload.java", 149);
	 	}
	 }
