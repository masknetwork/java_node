package wallet.agents.VM.sys.bets;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.CEvent;

public class BET_OPEN extends CEvent
{
    // Market ID
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
    
    // Tip
    String TIP;
    
    // Val 1
    double VAL_1;
    
    // Val 2
    double VAL_2;
    
    // Title
    String TITLE;
    
    // Description
    String DESCRIPTION;
    
    // Budget
    double BUDGET;
    
    // Win multiplier
    double WIN_MULTIPLIER;
    
    // Start block
    long START_BLOCK;
    
    // End block
    long END_BLOCK;
    
    // Accept block
    long ACCEPT_BLOCK;
        
    // Currency
    String CUR;
    
    // Hash
    String HASH;
    
    // Block
    long BLOCK;
    
    public BET_OPEN(long mktID,
                    String feed_1,
                    String branch_1,
                    String feed_2,
                    String branch_2,
                    String feed_3,
                    String branch_3,
                    String tip,
                    double val_1,
                    double val_2,
                    String title,
                    String description,
                    double budget,
                    double win_multiplier,
                    long start_block,
                    long end_block,
                    long accept_block,
                    String cur,
                    String hash,
                    long block)
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
    
        // Tip
        this.TIP=tip;
    
        // Val 1
        this.VAL_1=val_1;
    
        // Val 2
        this.VAL_2=val_2;
    
        // Title
        this.TITLE=title;
    
        // Description
        this.DESCRIPTION=description;
    
        // Budget
        this.BUDGET=budget;
    
        // Win multiplier
        this.WIN_MULTIPLIER=win_multiplier;
    
        // Start block
        this.START_BLOCK=start_block;
    
        // End block
        this.END_BLOCK=end_block;
    
        // Accept block
        this.ACCEPT_BLOCK=accept_block;
        
        // Currency
        this.CUR=cur;
        
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
        
           // Tip
           case "TIP" : c=new CCell(this.CUR); break;
                          
           // Val 1
           case "VAL_1" : c=new CCell(this.VAL_1); break;
        
           // Val 2
           case "VAL_2" : c=new CCell(this.VAL_2); break;
        
           // Title
           case "TITLE" : c=new CCell(this.TITLE); break;
        
           // Description
           case "DESCRIPTION" : c=new CCell(this.DESCRIPTION); break;
                          
           // Budget
           case "BUDGET" : c=new CCell(this.BUDGET); break;
        
           // Win multiplier
           case "WIN_MULTIPLIER" : c=new CCell(this.WIN_MULTIPLIER); break;
           
           // Start block
           case "START_BLOCK" : c=new CCell(this.START_BLOCK); break;
           
           // Ends block
           case "END_BLOCK" : c=new CCell(this.END_BLOCK); break;
           
           // Accept block
           case "ACCEPT_BLOCK" : c=new CCell(this.ACCEPT_BLOCK); break;
           
           // Currency
           case "CUR" : c=new CCell(this.CUR); break;
       }
       
       // Return
       return c;
    }
        
}
