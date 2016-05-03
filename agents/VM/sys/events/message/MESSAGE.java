package wallet.agents.VM.sys.events.message;

import wallet.agents.VM.CCell;

public class MESSAGE 
{
    // Sender
    String SENDER;
    
     // Message
    String SUBJECT;
    
    // Message
    String MESSAGE;
    
    // Hash
    String HASH;
    
    public MESSAGE(String sender,
                   String subject,
                   String message,
                   String hash)
    {
        // Sender
        this.SENDER=sender;
    
        // Subject
        this.SUBJECT=subject;
        
        // Message
        this.MESSAGE=message;
    
        // Hash
        this.HASH=hash;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           case "SENDER" : c=new CCell(this.SENDER); break;
           case "SUBJECT" : c=new CCell(this.SUBJECT); break;
           case "MESSAGE" : c=new CCell(this.MESSAGE); break;
           case "HASH" : c=new CCell(this.HASH); break;
       }
       
       // Return
       return c;
    }
}
