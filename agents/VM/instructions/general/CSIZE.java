package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CSIZE extends CInstruction
{
    // Variable 
    CToken size=null;
    
    // Value
    CToken var=null;
    
    public CSIZE(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "SIZE");
        
        // Variable to set
        this.size=tokens.get(1);
            
        // Value
        this.var=tokens.get(3);
    }
    
    public void execute() throws Exception
    {
        long s=this.var.cel.size();
        this.size.cel.copy(new CCell(s));
        
        // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "SIZE "+this.size.cel.name+", "+s);
    }
}