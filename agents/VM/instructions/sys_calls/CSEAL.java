package wallet.agents.VM.instructions.sys_calls;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CSEAL extends CInstruction
{
    // Receiver
    CToken days;
    
    public CSEAL(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "SEAL");
        
        // Destination
        this.days=tokens.get(1);
        
    }
   
    public  void execute() throws Exception
    {
        // Register
        VM.RUNLOG.add(VM.REGS.RCI, 
                      "SEAL "+
                      this.days.cel.val);
        
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // Fee
        double fee=0.0001*days.cel.getLong();
        
        // Funds ?
        if (UTILS.ACC.getBalance(owner.val, "MSK")<fee)
            throw new Exception("Insufficient funds to execute transaction");
        
        // Statement
        
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM adr "
                                   + "WHERE adr='"+owner.val+"'");
        
        // Next
        rs.next();
        
        // Sealed
        long sealed=rs.getLong("sealed");
        
        // Already sealed ?
        if (sealed>0)
            sealed=sealed+(this.days.cel.getLong()*1440);
        else
            sealed=VM.block+(this.days.cel.getLong()*1440);
        
        // Sandbox ?
        if (VM.sandbox) 
        {
            VM.RUNLOG.add(VM.REGS.RCI, 
                      "Sandboxed seal : "+
                      this.days.cel.val);
            return;
        }
        
        // Seal
        UTILS.DB.executeUpdate("UPDATE adr "
                                + "SET sealed='"+sealed+"' "
                              + "WHERE adr='"+owner.val+"'");
            
        // Close
        
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+(0.0001*days.cel.getLong());
    }
}