package wallet.agents.VM.sys.events.speculative;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class SPEC_MKT_ORDER_OPEN extends CEvent
{
    // Market symbol
    long MKTID; 
    
    // Position symbol
    long POSID;
    
    // Address
    String ADR;
    
    // Open
    double OPEN;
    
    // SL
    double SL;
    
    // TP
    double TP;
    
    // Leverage
    long LEVERAGE;
    
    // Qty
    double QTY;
    
    // Tip
    String TIP;
    
    // Margin
    double MARGIN;
    
    // Days
    long DAYS;
    
    // Hash
    String HASH;
    
    // Block
    long BLOCK;
    
    
    public SPEC_MKT_ORDER_OPEN(long mktID,
                               long posID,
                               String adr,
                               double open,
                               double sl,
                               double tp,
                               long leverage,
                               double qty,
                               String tip,
                               double margin,
                               long days,
                               String hash,
                               long block)
    {
        // Market ID
        this.MKTID=mktID;
        
        // Position ID
        this.POSID=posID;
    
        // Address
        this.ADR=adr;
    
        // Open
        this.OPEN=open;
    
        // Stop loss
        this.SL=sl;
        
        // Take profit
        this.TP=tp;
    
        // Leverage
        this.LEVERAGE=leverage;
        
        // Qty
        this.QTY=qty;
                          
        // Tip
        this.TIP=tip;
        
        // Margin
        this.MARGIN=margin;
        
        // Days
        this.DAYS=days;
                          
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
           // Market ID
           case "MKTID" : c=new CCell(this.MKTID); break;
        
           // Position ID
           case "POSID" : c=new CCell(this.POSID); break;
    
           // Address
           case "ADR" : c=new CCell(this.ADR); break;
    
           // Open
           case "OPEN" : c=new CCell(this.OPEN); break;
    
           // Stop loss
           case "SL" : c=new CCell(this.SL); break;
        
           // Take profit
           case "TP" : c=new CCell(this.TP); break;
    
           // Leverage
           case "LEVERAGE" : c=new CCell(this.LEVERAGE); break;
        
           // Qty
           case "QTY" : c=new CCell(this.QTY); break;
                          
           // Tip
           case "TIP" : c=new CCell(this.TIP); break;
        
           // Margin
           case "MARGIN" : c=new CCell(this.MARGIN); break;
        
           // Days
           case "DAYS" : c=new CCell(this.DAYS); break;
                          
           // Hash
           case "HASH" : c=new CCell(this.HASH); break;
        
           // Block
           case "BLOCK" : c=new CCell(this.BLOCK); break;
       }
       
       // Return
       return c;
    }
    
}
