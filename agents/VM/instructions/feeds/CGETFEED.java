package wallet.agents.VM.instructions.feeds;

import java.sql.ResultSet;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CGETFEED extends CInstruction
{
    // Dest
    CToken dest;
    
    // Feed
    CToken feed;

    // Branch
    CToken branch;
    
   
    public CGETFEED(VM VM, ArrayList<CToken>tokens)
    {
        // Constructor
        super(VM, "GETFEED");
        
        // Dest
        this.dest=tokens.get(1);
        
        // Feed
        this.feed=tokens.get(3);
        
        // Branch
        this.branch=tokens.get(5);
    }
    
    public void execute() throws Exception
    {
        VM.RUNLOG.add(VM.REGS.RCI, "GETFEED "
                                    +this.feed.cel.val+", "
                                    +this.branch.cel.val);
        
        // Owner address
        CCell aID=VM.SYS.getVar("SYS.AGENT.GENERAL.AID");
        
        // Check feed and branch
        if (!UTILS.BASIC.isBranch(this.feed.cel.val, this.branch.cel.val))
            throw new Exception("Invalid feed");
        
        // Subscribed ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM agents_feeds "
                                          + "WHERE agentID='"+aID+"' "
                                            + "AND feed='"+this.feed+"' "
                                            + "AND branch='"+this.branch+"'");
        
        // Has data
        if (!UTILS.DB.hasData(rs) && !VM.sandbox)
            throw new Exception("Unsubscribed agent");
        
        // Load data
        this.dest.cel.copy(new CCell(rs.getDouble("val")));
    }
}

