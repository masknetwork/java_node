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
    }
   
    public  void execute() throws Exception
    {
        // Register
        VM.RUNLOG.add(VM.REGS.RCI, 
                      "SEAL "+
                      this.days.cel.val);
        
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load data
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM adr "
                                   + "WHERE adr='"+owner+"'");
        
        // Next
        rs.next();
        
        // Sealed
        long sealed=rs.getLong("sealed");
        
        // Already sealed ?
        if (sealed>0)
            sealed=sealed+(this.days.cel.getLong()*1440);
        else
            sealed=VM.block+(this.days.cel.getLong()*1440);
        
        // Seal
        UTILS.DB.executeUpdate("UPDATE adr "
                                + "SET sealed='"+sealed+"' "
                              + "WHERE adr='"+this.days.cel.val+"'");
            
        // Close
        s.close();
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+(0.0001*days.cel.getLong());
    }
}