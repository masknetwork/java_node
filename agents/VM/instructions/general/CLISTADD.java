package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CLISTADD  extends CInstruction
{
    // Dest
    CToken dest=null;
   
    // Src
    CToken src=null;
    
    public CLISTADD(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "LISTADD");
        
        // Array
        this.dest=tokens.get(1);
        
        // Value to add
        this.src=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "LISTADD "+this.dest.cel.name+", "+this.src.cel.val);
       
       // Copy
       this.dest.cel.listAdd(this.src.cel);
    }    
}
