package wallet.agents.VM.sys.events.trans;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class TRANS  extends CEvent
{
    // From adr
    String SENDER;
    
    // Amount
    double AMOUNT;
    
    // Currency
    String CUR;
    
    // Message
    String MES;
    
    // Escrower
    String ESCROWER;
    
    // Hash
    String HASH;
    
    // Aproved
    public boolean aproved=true;
    
    public TRANS(String sender, 
                 double amount, 
                 String cur, 
                 String mes, 
                 String escrower, 
                 String hash)
    {
        // From adr
        this.SENDER=sender;
    
        // Amount
        this.AMOUNT=amount;
    
         // Currency
         this.CUR=cur;
    
         // Message
         this.MES=mes;
    
         // Escrower
         this.ESCROWER=escrower;
    
         // Hash
         this.HASH=hash;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           case "SENDER" : c=new CCell(this.SENDER); break;
           case "AMOUNT" : c=new CCell(this.AMOUNT); break;
           case "CUR" : c=new CCell(this.CUR); break;
           case "MES" : c=new CCell(this.MES); break;
           case "ESCROWER" : c=new CCell(this.ESCROWER); break;
           case "HASH" : c=new CCell(this.HASH); break;
       }
       
       // Return
       return c;
    }
    
  
}
