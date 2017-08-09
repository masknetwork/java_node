// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import wallet.kernel.UTILS;
import wallet.network.*;
import wallet.network.packets.blocks.*;

public class CFeePayload extends CTransPayload 
{
    // Serial
    private static final long serialVersionUID = 100L;
   
   	  
    public  CFeePayload(String src, double amount) throws Exception
    {
         // Constructor
         super(src, 
    	       "default", 
    	       amount,
               "MSK",
               "",
    	       ""); 
         
          // No fees ?
          if (amount<=0)
              throw new Exception("Invalid fee - CFeePayload.java, line 28");
          
          // Amount
          if (amount<UTILS.SETTINGS.min_fee)
          {
             // Set fee
             this.amount=UTILS.SETTINGS.min_fee;
            
             // Digest
             this.hash=UTILS.BASIC.hash(this.getHash()+
		                        this.dest+
                                        this.amount+
                                        this.cur+
                                        this.escrower); 
           
             // Sign
             this.sign();
          }
    }
    
    public void check(CBlockPayload block) throws Exception
    {
    	 // Super
    	super.check(block);
    	    
    	// Destination
    	if (!this.dest.equals("default"))
           throw new Exception("Invalid recipient - CFeePayload.java");
    	
    	// Amount
    	if (amount<UTILS.CONSTANTS.MIN_FEE_AMOUNT) 
    	   throw new Exception("Invalid fee amount - CFeePayload.java");
        
        // Currency
    	if (!this.cur.equals("MSK")) 
           throw new Exception("Invalid currency - CFeePayload.java");
        
        // Can spend ?
        if (!UTILS.BASIC.canSpend(this.target_adr))
            throw new Exception("Network fee address can't spend funds - CFeePayload.java");
    }
}