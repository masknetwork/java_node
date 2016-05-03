package wallet.agents.VM.instructions.sys_calls;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CFEEDSET extends CInstruction 
{
    // Feed
    CToken feed;
            
    // Branch
    CToken branch;
    
    // Source
    CToken src;
    
     
    public CFEEDSET(VM VM, ArrayList<CToken>tokens) 
    {
        // Constructor
        super(VM, "FEEDGET");
        
        // Feed
        this.feed=tokens.get(1);
        
        // Branch
        this.feed=tokens.get(3);
        
        // Source
        this.src=tokens.get(5);
    }  
    
    public void execute() throws Exception
    {
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // Check feed
        if (!UTILS.BASIC.feedValid(this.feed.cel.val, this.branch.cel.val))
            throw new Exception("Invalid data feed");
        
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load feed
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM feeds_branches "
                                   + "WHERE feed_symbol='"+this.feed.cel.val+"' "
                                     + "AND symbol='"+this.feed.cel+"' "
                                     + "AND adr='"+owner+"'");
        
        // Feed exist ?
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid data feed");
        
        // Value
        double val=Double.parseDouble(this.src.cel.val);
        
        // Set data feed
        UTILS.DB.executeUpdate("UPDATE feeds_branches "
                                + "SET val='"+val+"' "
                              + "WHERE feed_symbol='"+this.feed.cel.val+"' "
                                + "AND symbol='"+this.feed.cel+"' "
                                + "AND adr='"+owner+"'");
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
