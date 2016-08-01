package wallet.agents.VM.sys.events.assets;
import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class OPEN_MARKET  extends CEvent
{
   // Address
   String ADR;
   
   // Mkt ID
   long ID;
   
   // Asset symbol
   String ASSET;
    
   // Currency symbol
   String CURRENCY;
   
   // Title
   String TITLE;
   
   // Description
   String DESCRIPTION;
   
   // Decimals
   int DECIMALS;
   
   // Days
   long DAYS;
   

   public OPEN_MARKET(String adr,
                     long id,
                     String asset,
                     String cur,
                     String title,
                     String description,
                     int decimals,
                     long days)
    {
        // Sender
        this.ADR=adr;
        
        // ID
        this.ID=id;
        
        // Sender
        this.ASSET=asset;
        
        // Sender
        this.CURRENCY=cur;
        
        // Sender
        this.TITLE=title;
        
        // Sender
        this.DESCRIPTION=description;
        
        // Sender
        this.DECIMALS=decimals;
        
        // Days
        this.DAYS=days;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           // Address
           case "ADR" : c=new CCell(this.ADR); break;
           
           // ID
           case "ID" : c=new CCell(this.ID); break;
           
           // Asset
           case "ASSET" : c=new CCell(this.ASSET); break;
           
           // Currency
           case "CURRENCY" : c=new CCell(this.CURRENCY); break;
           
           // Title
           case "TITLE" : c=new CCell(this.TITLE); break;
           
           // Description
           case "DESCRIPTION" : c=new CCell(this.DESCRIPTION); break;
           
           // Decimals
           case "DECIMALS" : c=new CCell(this.DECIMALS); break;
           
           // Days
           case "DAYS" : c=new CCell(this.DAYS); break;
       }
       
       // Return
       return c;
    }
    
  
}
