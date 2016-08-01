package wallet.agents.VM.instructions.flow;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CRET extends CInstruction
{
   public CRET(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "CRET");
    }
   
    public  void execute() throws Exception
    {
       // Line
       long line=0;
        
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "RET ");
       
       // Get function line
       line=(long)VM.HEAP.get(VM.HEAP.size()-1);
       
       // Next line
       line++;
       
       // Removes line
       VM.HEAP.remove(VM.HEAP.size()-1);
       
       // Jump
       VM.REGS.setCodeIndex(line);
    }
}