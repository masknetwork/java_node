package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CINDEXOF extends CInstruction
{
    // Destination
    CToken dest;
    
    // String
    CToken haystack=null;
    
    // Needle
    CToken niddle=null;
    
    public CINDEXOF(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "INDEXOF");
        
        // Dest
        this.dest=tokens.get(1);
        
        // From register
        this.haystack=tokens.get(3);
        
        // To register
        this.niddle=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
       // Log
       VM.RUNLOG.add(VM.REGS.RCI, "INDEXOF "+this.dest.cel.name+", '"+this.haystack.cel.val+"', '"+this.niddle.cel.val+"'");
       
       // Niddle val
       String niddle=this.niddle.cel.val;
       
       // Position
       int pos=this.haystack.cel.val.indexOf(niddle);
       
       // Write position
       this.dest.cel.copy(new CCell(pos));
    }
}