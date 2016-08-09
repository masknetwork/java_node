package wallet.agents.VM.sys.events.vote;

import wallet.agents.VM.CCell;

public class VOTE 
{
    // Address
    String ADR;
    
     // Type
    String TYPE;
    
    // Power
    double POWER;
    
    public VOTE(String adr,
                String type,
                double power)
    {
        // Sender
        this.ADR=adr;
    
        // Subject
        this.TYPE=type;
        
        // Message
        this.POWER=power;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           case "ADR" : c=new CCell(this.ADR); break;
           case "TYPE" : c=new CCell(this.TYPE); break;
           case "POWER" : c=new CCell(this.POWER); break;
       }
       
       // Return
       return c;
    }
}
