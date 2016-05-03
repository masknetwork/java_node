package wallet.agents.VM.instructions.flow;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CGOTO  extends CInstruction
{
   // Destination
   CToken line=null;
        
    public CGOTO(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "GOTO");
        
        // Line
        this.line=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "GOTO "+this.line.cel.getLong());
       
       // Jump
       VM.REGS.setCodeIndex(this.line.cel.getLong());
    }
}
