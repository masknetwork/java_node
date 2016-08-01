package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CEXIT  extends CInstruction
{
    // From
    CToken code=null;
    
   
    public CEXIT(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "EXIT");
        
        
        // From register
        this.code=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "EXIT "+this.code.cel.val);
       
       // Copy
       VM.CODE.exit(this.code.cel);
    }    
}
