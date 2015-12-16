package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CRestrictRecipientsPayload extends CPayload 
{
	// Address 1
	public String adr_1;
	
	// Address 2
	public String adr_2;
		
        // Address 3
	public String adr_3;
		
	// Address 4
	public String adr_4;
        
        // Address 5
	public String adr_5;
        
        // Days
        public long days;
	
   public CRestrictRecipientsPayload(String target_adr, 
		                     String adr_1, 
		                     String adr_2, 
		                     String adr_3, 
		                     String adr_4, 
                                     String adr_5, 
		                     int days)
   {
	   // Superclass
  	   super(target_adr);
  	   
  	   // Target address
  	   this.target_adr=target_adr;
  	   
  	   // Address 1
  	   this.adr_1=adr_1;
  	   
  	   // Address 2
  	   this.adr_2=adr_2;
  	   
  	   // Address 3
  	   this.adr_3=adr_3;
  	   
  	   // Address 4
  	   this.adr_4=adr_4;
           
           // Address 5
  	   this.adr_5=adr_5;
  	   
  	   // Expires
  	   this.days=days;
  	   
  	   // Hash
   	   hash=UTILS.BASIC.hash(this.hash+
   			         adr_1+
   			         adr_2+
   			         adr_3+
   			         adr_4+
                                 adr_5);
   	   
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
   		return new CResult(false, "Invalid target address", "CRestrictRecipientsPayload", 70);
   	  
          
   	  // At least one target address
   	  if (this.adr_1=="" && this.adr_2=="" && this.adr_3=="" && this.adr_4=="")
   		return new CResult(false, "Invalid restricted recipients", "CRemoveEscrowerPayload", 74);
   	  
          // Recipient valid
   	  if (!this.adr_1.equals(""))
   	    if (UTILS.BASIC.adressValid(this.adr_1)==false) 
   		 return new CResult(false, "Invalid recipient", "CRestrictRecipientsPayload", 79);
   	  
          // Recipient valid
   	  if (!this.adr_2.equals(""))
   	    if (UTILS.BASIC.adressValid(this.adr_2)==false) 
   		 return new CResult(false, "Invalid recipient", "CRestrictRecipientsPayload", 84);
   	  
          // Recipient valid
   	  if (!this.adr_3.equals(""))
   	    if (UTILS.BASIC.adressValid(this.adr_3)==false) 
   		 return new CResult(false, "Invalid recipient", "CRestrictRecipientsPayload", 89);
   	  
          // Recipient valid
   	  if (!this.adr_4.equals(""))
   	    if (UTILS.BASIC.adressValid(this.adr_4)==false) 
   		 return new CResult(false, "Invalid recipient", "CRestrictRecipientsPayload", 94);
          
          // Recipient valid
   	  if (!this.adr_5.equals(""))
   	    if (UTILS.BASIC.adressValid(this.adr_5)==false) 
   		 return new CResult(false, "Invalid recipient", "CRestrictRecipientsPayload", 94);
      
          // Hash
  	  String h=UTILS.BASIC.hash(this.getHash()+
  			                    adr_1+
  			                    adr_2+
  			                    adr_3+
  			                    adr_4+
                                            adr_5);
  	  if (!this.hash.equals(h))
  		return new CResult(false, "Invalid hash", "CRestrictRecipientsPayload", 107);
  	  
  	  // Signature
  	  if (this.checkSig()==false)
  		return new CResult(false, "Invalid signature", "CRestrictRecipientsPayload", 107);
  	  
      // Return
 	  return new CResult(true, "Ok", "CRestrictRecipientsPayload", 105);
   }
   
   public CResult commit(CBlockPayload block)
   {
	   CResult res=this.check(block);
	   if (res.passed==false) return res;
		  
	    // Superclass
	    super.commit(block);
	    
	    // Commit
	   	UTILS.BASIC.applyAdrAttr(this.target_adr, 
	   			                 "ID_RESTRICT_REC", 
	   			                 this.adr_1, 
	   			                 this.adr_2, 
	   			                 this.adr_3,
	   			                 this.adr_4,
                                                 this.adr_5,
                                                 "",
                                                 days, 
                                                 block.block);
	       
	   	// Return 
	   	return new CResult(true, "Ok", "CRestrictRecipientsPayload.java", 149);
   }
}
