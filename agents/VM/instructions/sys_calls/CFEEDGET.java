package wallet.agents.VM.instructions.sys_calls;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.kernel.UTILS;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CFEEDGET extends CInstruction 
{
    // Dest
    CToken dest;
    
    // Feed
    CToken feed;
            
    // Branch
    CToken branch;
    
    public CFEEDGET(VM VM, ArrayList<CToken>tokens) 
    {
        // Constructor
        super(VM, "FEEDGET");
        
        // Dest
        this.dest=tokens.get(1);
        
        // Feed
        this.feed=tokens.get(3);
        
        // Branch
        this.feed=tokens.get(5);
    }  
    
    public void execute() throws Exception
    {
        // Check feed
        if (!UTILS.BASIC.feedValid(this.feed.cel.val, this.branch.cel.val))
            throw new Exception("Invalid data feed");
        
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load feed
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM feeds_branches "
                                   + "WHERE feed_symbol='"+this.feed.cel.val+"' "
                                     + "AND symbol='"+this.feed.cel+"'");
        
        // Value
        this.dest.cel=new CCell(rs.getDouble("val"));
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
