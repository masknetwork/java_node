package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CTOSTRING extends CInstruction
{
    // Variable 
    CToken target=null;
    
     public CTOSTRING(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "TOSTRING");
        
        // To register
        this.target=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
        // Change type
        this.target.cel.type="ID_STRING";
        
        // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "TOSTRING "+this.target.cel.val);
    }
}
