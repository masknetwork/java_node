package wallet.agents.VM.sys.events.assets;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class CLOSE_ORDER  extends CEvent
{
    // Sender
    long orderID;
    
    // Type
    String type;
    
    // Amount
    double amount;
    
    // Price
    double price;
    
    public CLOSE_ORDER(long orderID, 
                      String type, 
                      double amount, 
                      double price)
    {
        // Order ID
        this.orderID=orderID;
        
        // Type
        this.type=type;
        
        // Amount
        this.amount=amount;
        
        // Price
        this.price=price;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           // Order ID
           case "ORDERID" : c=new CCell(this.orderID); break;
           
           // Type
           case "TYPE" : c=new CCell(this.type); break;
           
           // Amount
           case "AMOUNT" : c=new CCell(this.amount); break;
           
           // Price
           case "PRICE" : c=new CCell(this.price); break;
       }
       
       // Return
       return c;
    }
  
}

