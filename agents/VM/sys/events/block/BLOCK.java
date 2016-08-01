package wallet.agents.VM.sys.events.block;

import wallet.agents.VM.CCell;

public class BLOCK 
{
   // Sender
    String HASH;
    
     // Message
    long NO;
    
    // Message
    long NONCE;
    
    public BLOCK(String hash,
                 long no,
                 long nonce)
    {
        // Sender
        this.HASH=hash;
    
        // Subject
        this.NO=no;
        
        // Message
        this.NONCE=nonce;
    
        // Hash
        this.HASH=hash;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           case "HASH" : c=new CCell(this.HASH); break;
           case "NO" : c=new CCell(this.NO); break;
           case "NONCE" : c=new CCell(this.NONCE); break;
       }
       
       // Return
       return c;
    }
}