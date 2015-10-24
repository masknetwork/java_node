package wallet.network.packets.mes;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CMessage 
{
	// Subject
	public String subject;
	
	// Message
	public String mes;
	
	public CMessage(String subject, String mes) 
	{
	    // Subject
            this.subject=subject;
		
	    // Message
	    this.mes=mes;
	}
	
	public CResult check()
	{
            // Check subject
            if (this.subject.length()<5 || this.subject.length()>50)
                return new CResult(false, "Invalid message.", "CNewAdPayload", 77);
	  
            // Check message
            if (this.mes.length()<5 || this.mes.length()>50)
                return new CResult(false, "Invalid message.", "CNewAdPayload", 77);
		
	    // Return
	    return new CResult(true, "Ok", "CMessage.java", 67);
	}
}
