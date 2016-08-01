package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CREPLACE extends CInstruction
{
    // Destination
    CToken dest=null;
    
    // From
    CToken haystack=null;
    
    // Niddle
    CToken niddle=null;
    
    // Replace
    CToken replace=null;
    
    public CREPLACE(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "REPLACE");
        
        // Destination
        this.dest=tokens.get(1);
        
        // Haystack
        this.haystack=tokens.get(3);
        
        // Niddle
        this.niddle=tokens.get(5);
        
        // Replace
        this.replace=tokens.get(7);
    }
   
    public  void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, 
                     "REPLACE "
                     +this.dest.cel.name+", "
                     +this.haystack.cel.val+", "
                     +this.niddle.cel.val+", "
                     +this.replace.cel.val);
        
       // Reaplace
       String r=this.haystack.cel.val.replace(this.niddle.cel.val, this.replace.cel.val);
       
       // Copy
       this.dest.cel.copy(new CCell(r));
    }
}