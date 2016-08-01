package wallet.agents.VM.instructions.flow;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CCALL extends CInstruction
{
    // To
    CToken function=null;
   
    public CCALL(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "CALL");
        
        // To register
        this.function=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Line
       long line=0;
        
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "CALL "+this.function.cel.val);
       
       // Push actual line number to heap
       VM.HEAP.add(VM.REGS.RCI);
       
       // Jump
       VM.REGS.setCodeIndex(this.function.cel.getLong());
    }
}
