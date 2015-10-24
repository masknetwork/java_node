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
                           long days)
    {
    	// Constructor
    	super(target_adr);
    	
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
 	   this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {
    	// Super class
    	CResult res=super.check(block);
    	if (res.passed==false) return res;
    	
    	// Target adsress valid
    	if (UTILS.BASIC.adressValid(this.target_adr)==false) 
    		return new CResult(false, "Invalid signer.", "CAddSignPayload", 60);
    	
    	// Signer 1 valid
        if (!this.signer_1.equals(""))
    	  if (UTILS.BASIC.adressValid(this.signer_1)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 50);
        
        // Signer 2 valid
        if (!this.signer_2.equals(""))
    	  if (UTILS.BASIC.adressValid(this.signer_2)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 50);
        
        // Signer 3 valid
        if (!this.signer_3.equals(""))
    	  if (UTILS.BASIC.adressValid(this.signer_3)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 50);
        
        // Signer 4 valid
        if (!this.signer_4.equals(""))
    	  if (UTILS.BASIC.adressValid(this.signer_4)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 50);
        
        // Signer 5 valid
        if (!this.signer_5.equals(""))
    	  if (UTILS.BASIC.adressValid(this.signer_5)==false) 
    		return new CResult(false, "Invalid target address", "CAddSignPayload", 50);
        
        // Min
        if (this.min<1)
           return new CResult(false, "Invalid minimum signers", "CAddSignPayload", 50);
        
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
   	   return new CResult(false, "Invalid hash code", "CAddSignPayload", 65);
   	    
   	// Signature
   	if (this.checkSig()==false)
   	    return new CResult(false, "Invalid signature", "CAddSignPayload", 65);
   	    	
        // Return
  	return new CResult(true, "Ok", "CAddSignPayload", 61);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Superclass
        super.commit(block);
           
        // Commit
    	UTILS.BASIC.applyAdrAttr(this.target_adr, 
			         "ID_MULTISIG", 
			         this.days*1440+this.block, 
                                 UTILS.BASIC.block(),
			         this.signer_1, 
			         this.signer_2, 
			         this.signer_3,
			         this.signer_4,
                                 this.signer_5,
                                 String.valueOf(this.min));
    	
    	// Return
  	    return new CResult(true, "Ok", "CAddSignPayload", 77);
    }
}
