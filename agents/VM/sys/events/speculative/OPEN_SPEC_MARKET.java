package wallet.agents.VM.sys.events.speculative;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;
import wallet.agents.VM.sys.events.EVENT;

public class OPEN_SPEC_MARKET  extends CEvent
{
    // Symbol
    long MKTID; 
    
    // Feed 1
    String FEED_1; 
    
    // Branch 1
    String BRANCH_1; 
    
    // Feed 2
    String FEED_2;
    
    // Branch 2
    String BRANCH_2; 
    
    // Feed 3
    String FEED_3;
    
    // Branch 3
    String BRANCH_3; 
    
    // Currency
    String CUR; 
    
    // Max leverage
    long MAX_LEVERAGE;
    
    // Spread
    double SPREAD;
    
    // String 
    String REAL_SYMBOL;
    
    // Title
    String TITLE;
    
    // Description
    String DESCRIPTION;
    
    // Max position size
    double MAX_MARGIN;
    
    // Days
    long DAYS;
    
    // Hash
    String HASH;
    
    // Block
    long BLOCK;
    
    
    public OPEN_SPEC_MARKET(long mktID,
                          String feed_1, 
                          String branch_1, 
                          String feed_2, 
                          String branch_2, 
                          String feed_3, 
                          String branch_3, 
                          String cur,
                          long max_leverage,
                          double spread,
                          String real_symbol,
                          String title,
                          String desc,
                          double max_margin,
                          long days)
    {
        // Market ID
        this.MKTID=mktID;
        
        // Feed 1
        this.FEED_1=feed_1;
    
        // Branch 1
        this.BRANCH_1=branch_1;
    
        // Feed 2
        this.FEED_2=feed_2;
    
        // Branch 2
        this.BRANCH_2=branch_2;
        
        // Feed 3
        this.FEED_3=feed_3;
    
        // Branch 3
        this.BRANCH_3=branch_3;
        
        // Currency
        this.CUR=cur;
                          
        // Max leverage
        this.MAX_LEVERAGE=max_leverage;
        
        // Spread
        this.SPREAD=spread;
        
        // Real symbol
        this.REAL_SYMBOL=real_symbol;
        
        // Title
        this.TITLE=title;
                          
        // Description
        this.DESCRIPTION=desc;
        
        // Max margin
        this.MAX_MARGIN=max_margin;
        
        // Days
        this.DAYS=days;
    }
    
    public CCell getVal(String val)
    {
       CCell c=null;
       
       switch (val)
       {
           // Market ID
           case "MKTID" : c=new CCell(this.MKTID); break;
        
           // Feed 1
           case "FEED_1" : c=new CCell(this.FEED_1); break;
    
           // Branch 1
           case "BRANCH_1" : c=new CCell(this.BRANCH_1); break;
    
           // Feed 2
           case "FEED_2" : c=new CCell(this.FEED_2); break;
    
           // Branch 2
           case "BRANCH_2" : c=new CCell(this.BRANCH_2); break;
        
           // Feed 3
           case "FEED_3" : c=new CCell(this.FEED_3); break;
    
           // Branch 3
           case "BRANCH_3" : c=new CCell(this.BRANCH_3); break;
        
           // Currency
           case "CUR" : c=new CCell(this.CUR); break;
                          
           // Max leverage
           case "MAX_LEVERAGE" : c=new CCell(this.MAX_LEVERAGE); break;
        
           // Spread
           case "SPREAD" : c=new CCell(this.SPREAD); break;
        
           // Real symbol
           case "REAL_SYMBOL" : c=new CCell(this.REAL_SYMBOL); break;
        
           // Title
           case "TITLE" : c=new CCell(this.TITLE); break;
                          
           // Description
           case "DESCRIPTION" : c=new CCell(this.DESCRIPTION); break;
        
           // Max margin
           case "MAX_MARGIN" : c=new CCell(this.MAX_MARGIN); break;
        
           // Days
           case "DAYS" : c=new CCell(this.DAYS); break;
       }
       
       // Return
       return c;
    }
    
       
}
