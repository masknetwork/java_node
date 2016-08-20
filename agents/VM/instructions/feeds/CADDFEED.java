package wallet.agents.VM.instructions.feeds;

import java.sql.ResultSet;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CADDFEED extends CInstruction
{
    // Feed
    CToken feed;

    // Branch
    CToken branch;
    
    // Days
    CToken days;
    
    
    public CADDFEED(VM VM, ArrayList<CToken>tokens)
    {
        // Constructor
        super(VM, "ADDFEED");
        
        // Feed
        this.feed=tokens.get(1);
        
        // Branch
        this.branch=tokens.get(3);
        
        // Days
        this.days=tokens.get(5);
    }
    
    public void execute() throws Exception
    {
        VM.RUNLOG.add(VM.REGS.RCI, "ADDFEED "
                                    +this.feed.cel.val+", "
                                    +this.branch.cel.val+", "
                                    +this.days.cel.val+"");
        
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
         // Agent ID
        CCell aID=VM.SYS.getVar("SYS.AGENT.GENERAL.AID");
        
        // Check feed and branch
        if (!UTILS.BASIC.isBranch(this.feed.cel.val, this.branch.cel.val))
            throw new Exception("Invalid feed");
        
        // Days
        if (this.days.cel.getLong()<10)
            throw new Exception("Invalid days");
        
        // Load branch
        ResultSet rs=UTILS.DB.executeQuery("SELECT fb.*, feeds.adr "
                                           + "FROM feeds_branches AS fb "
                                           + "JOIN feeds ON feeds.symbol=fb.feed_symbol "
                                          + "WHERE fb.feed_symbol='"+this.feed.cel.val+"' "
                                            + "AND fb.symbol='"+this.branch.cel.val+"'");
        
        // Next
        rs.next();
        
        // Fee
        double fee=this.days.cel.getLong()*rs.getDouble("fee");
        
        // Funds
        if (UTILS.ACC.getBalance(owner.val, "MSK")<fee)
            throw new Exception("Insufficient funds");
        
        // Hash
        String hash=UTILS.BASIC.hash(owner.val+this.feed+this.branch+this.days);
        
        // Sandbox ?
        if (VM.sandbox) 
        {
            System.out.println("Sandboxed subscribe contract to feed");
            return;
        }
        
        // Transaction
        UTILS.ACC.newTransfer(owner.val, 
                              rs.getString("adr"), 
                              fee, 
                              false,
                              "MSK", 
                              "Subscribe agent to data feed", 
                              "MSK", 
                              hash, 
                              VM.block,
                              null, 
                              0);
        
        // Clear
        UTILS.ACC.clearTrans(hash, "ID_ALL", UTILS.NET_STAT.last_block+1);
        
        // Already added ?
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM agents_feeds "
                                + "WHERE agentID='"+aID.val+"' "
                                  + "AND feed='"+this.feed+"' "
                                  + "AND branch='"+this.branch+"'");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
            UTILS.DB.executeUpdate("INSERT INTO agents_feeds "
                                     + "SET agentID='"+aID.val+"', "
                                         + "feed='"+this.feed.cel.val+"', "
                                         + "branch='"+this.branch.cel.val+"', "
                                         + "expire='"+(VM.block+this.days.cel.getLong()*1440)+"',"
                                         + "block='"+VM.block+"'");
        else
            UTILS.DB.executeUpdate("UPDATE agents_feeds "
                                    + "SET expire=expire+"+(rs.getLong("expire")+this.days.cel.getLong()*1440)+", "
                                        + "block='"+VM.block+"' "
                                  + "WHERE agentID='"+aID.val+"' "
                                    + "AND feed='"+this.feed.cel.val+"' "
                                    + "AND branch='"+this.branch.cel.val+"'");
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}

