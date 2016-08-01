package wallet.agents.VM.instructions.stack;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CPUSH extends CInstruction 
{
    // From
    CToken val=null;
    
    
    public CPUSH(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "PUSH");
        
        // From register
        this.val=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "SUB "+this.val.cel.val);
        
       // Copy
       VM.STACK.push(this.val.cel);
    }
}