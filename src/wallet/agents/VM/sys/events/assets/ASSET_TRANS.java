package wallet.agents.VM.sys.events.assets;
import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class ASSET_TRANS extends CEvent
{
    // Sender
    String SENDER;
    
    // Receiver
    String RECEIVER;
    
    // Amount
    double AMOUNT;
    
    // Escrower
    String ESCROWER;
    
    // Hash
    String HASH;
    
    // Block
    long BLOCK;
    
    
    public ASSET_TRANS(String sender, 
                      String receiver, 
                      double amount, 
                      String escrower, 
                      String hash,
                      long block)
    {
        // Sender
        this.SENDER=sender;
        
        // Receiver
        this.RECEIVER=receiver;
    
        // Amount
        this.AMOUNT=amount;
    
        // Escrower
        this.ESCROWER=escrower;
    
        // Hash
        this.HASH=hash;
    
        // Block
        this.BLOCK=block;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           // Sender
           case "SENDER" : c=new CCell(this.SENDER); break;
           
           // Receiver
           case "RECEIVER" : c=new CCell(this.RECEIVER); break;
           
           // Amount
           case "AMOUNT" : c=new CCell(this.AMOUNT); break;
           
           // Escrower
           case "ESCROWER" : c=new CCell(this.ESCROWER); break;
           
           // Hash
           case "HASH" : c=new CCell(this.HASH); break;
       }
       
       // Return
       return c;
    }
    
   
}

