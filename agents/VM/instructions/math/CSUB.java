package wallet.agents.VM.instructions.math;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CSUB extends CInstruction 
{
    // From
    CToken from_loc=null;
    
    // To
    CToken to_loc=null;
    
    public CSUB(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "SUB");
        
        // From register
        this.from_loc=tokens.get(1);
        
        // To register
        this.to_loc=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
        // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "SUB "+this.from_loc.cel.val+", "+this.to_loc.cel.val);
        
        // Copy
        this.from_loc.cel.sub(this.to_loc.cel);
    }
}
