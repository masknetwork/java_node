// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CAddSignPayload extends CPayload 
{
	// Adr
	String target_adr;
	
	// Signer
	String signer_1;
	
	// Signer
	String signer_2;
	
        // Signer
	String signer_3;
	
        // Signer
	String signer_4;
	
        // Signer
	String signer_5;
        
        // Minimum signers
        int min;
       
        // Days
        long days;
	
    public CAddSignPayload(String target_adr, 
                           String signer_1, 
                           String signer_2, 
                           String signer_3,
                           String signer_4, 
                           String signer_5,
                           int min,
                           long days,
                           String signature) throws Exception
    {
    	// Constructor
    	super(target_adr);
    	
        try
        {
	    // Target address
    	    this.target_adr=target_adr;
    	
    	    // Signer
    	    this.signer_1=signer_1;
    	
    	    // Signer
    	    this.signer_2=signer_2;
    	
            // Signer
    	    this.signer_3=signer_3;
    	
            // Signer
    	    this.signer_4=signer_4;
    	
            // Signer
    	    this.signer_5=signer_5;
    	
            // Min
            this.min=min;
        
           // Days 
    	   this.days=days;
    	
    	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         target_adr+
 			         signer_1+
                                 signer_2+
                                 signer_3+
                                 signer_4+
                                 signer_5+
 			         min+
                                 days);
 	   
 	   // Signature
           if (signature.equals("")) 
               this.sign();
           else
               this.sign=signature;
        }
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CAddSignPacket.java", 90); 
        }
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
    	   // Super class
    	   CResult res=super.check(block);
    	   if (res.passed==false) return res;
    	   
           // Sealed address ?
           if (UTILS.BASIC.hasAttr(this.target_adr, "ID_SEALED"))
              return new CResult(false, "Target address is sealed.", "CAddSignPayload", 104);
           
    	   // Target adsress valid
    	   if (UTILS.BASIC.adressValid(this.target_adr)==false) 
    		return new CResult(false, "Invalid signer.", "CAddSignPayload", 104);
    	
    	   // Signer 1 valid
           if (!this.signer_1.equals(""))
    	     if (UTILS.BASIC.adressValid(this.signer_1)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 109);
        
           // Signer 2 valid
           if (!this.signer_2.equals(""))
    	       if (UTILS.BASIC.adressValid(this.signer_2)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 114);
        
           // Signer 3 valid
           if (!this.signer_3.equals(""))
    	      if (UTILS.BASIC.adressValid(this.signer_3)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 119);
        
           // Signer 4 valid
           if (!this.signer_4.equals(""))
    	      if (UTILS.BASIC.adressValid(this.signer_4)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 124);
        
           // Signer 5 valid
           if (!this.signer_5.equals(""))
    	      if (UTILS.BASIC.adressValid(this.signer_5)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 129);
           
           // Min
           if (this.min<1)
              return new CResult(false, "Invalid minimum signers", "CAddSignPayload", 133);
        
 	   // Check hash
   	   String h=UTILS.BASIC.hash(this.getHash()+
 			             target_adr+
 			             signer_1+
                                     signer_2+
                                     signer_3+
                                     signer_4+
                                     signer_5+
 			             min+
                                     days);
   	    
           if (!this.hash.equals(h))
   	      return new CResult(false, "Invalid hash code", "CAddSignPayload", 147);
   	    
   	   // Signature
   	   if (this.checkSig()==false)
   	       return new CResult(false, "Invalid signature", "CAddSignPayload", 151);
   	 }
         catch (Exception ex) 
         { 
	       UTILS.LOG.log("Exception", ex.getMessage(), "CAddSignPacket.java", 155); 
         }
        
        // Return
  	return new CResult(true, "Ok", "CAddSignPayload", 160);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        try
        {
           // Superclass
           super.commit(block);
                 
           // Commit
    	   UTILS.BASIC.applyAdrAttr(this.target_adr, 
			            "ID_MULTISIG", 
			            this.signer_1, 
			            this.signer_2, 
			            this.signer_3,
			            this.signer_4,
                                    this.signer_5,
                                    String.valueOf(this.min),
                                    this.days, 
                                    block.block);
    	}
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CAddSignPacket.java", 155); 
        }
        
    	// Return
  	return new CResult(true, "Ok", "CAddSignPayload", 77);
    }
}
