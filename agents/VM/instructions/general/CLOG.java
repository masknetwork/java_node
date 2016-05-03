package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CLOG extends CInstruction
{
    // From
    CToken target=null;
  
    
    public CLOG(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "LOG");
        
        // From register
        this.target=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "LOG "+this.target.cel.val);
       
       // Copy
       System.out.println("Logger : " + this.target.cel.val);
    }
}
