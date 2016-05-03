package wallet.agents.VM.instructions.math;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CADD extends CInstruction 
{
    // From
    CToken from_loc=null;
    
    // To
    CToken to_loc=null;
    
    public CADD(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "ADD");
        
        // From register
        this.to_loc=tokens.get(1);
        
        // To register
        this.from_loc=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "ADD "+this.to_loc.cel.val+", "+this.from_loc.cel.val);
       
       // Copy
       this.to_loc.cel.add(this.from_loc.cel);
    }
}
