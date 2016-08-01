package wallet.agents.VM.sys.bets;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class BET_BUY extends CEvent
{
    // Market ID
    long BETID;
        
    // Feed 1
    String ADR;
    
    // Branch 1
    double AMOUNT;
    
    // Feed 2
    long BLOCK;
    
    public BET_BUY(long betID,
                    String adr,
                    double amount,
                    long block)
    {
        // Bet ID
        this.BETID=betID;
        
        // Adr
        this.ADR=adr;
        
        // Amount
        this.AMOUNT=amount;
        
        // Block
        this.BLOCK=block;     
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           // Bet ID
           case "BETID" : c=new CCell(this.BETID); break;
        
           // Adr
           case "ADR" : c=new CCell(this.ADR); break;
    
           // Amount
           case "AMOUNT" : c=new CCell(this.AMOUNT); break;
    
           // Block
           case "BLOCK" : c=new CCell(this.BLOCK); break;
        }
       
        // Return
        return c;
    }
        
}

