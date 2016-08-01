package wallet.agents.VM.instructions.stack;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CPOP extends CInstruction 
{
    // From
    CToken dest=null;
    
    
    public CPOP(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "PUSH");
        
        // From register
        this.dest=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "POP "+this.dest.cel.name);
        
       // Copy
       VM.STACK.pop(this.dest.cel);
    }
}
