// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import wallet.network.*;
import wallet.network.packets.blocks.*;

public class CFeePayload extends CTransPayload 
{
	
	public  CFeePayload(String src) throws Exception
        {
	  super(src, 
		"default", 
		0.0001, 
                "MSK",
                "",
		"", ""); 
        }
	  
    public  CFeePayload(String src, double amount) throws Exception
    {
         super(src, 
    	       "default", 
    	       amount,
               "MSK",
               "",
    	       "", ""); 
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
    	 // Super
    	CResult res=super.check(block);
    	if (!res.passed) 
            return new CResult(false, res.reason, "CFeePayload.java", 36);
            
    	// Destination
    	if (!this.dest.equals("default"))
    		 return new CResult(true, "Invalid recipient", "CFeePayload.java", 36);
    	
    	// Amount
    	if (amount<0.0001) 
    		 return new CResult(true, "Invalid Amount", "CFeePayload.java", 40);
        
        // Currency
    	if (!this.cur.equals("MSK")) 
           return new CResult(true, "Invalid Currency", "CFeePayload.java", 40);
    	
    	 // Return 
   	     return new CResult(true, "Ok", "CFeePayload.java", 43);
    }
}