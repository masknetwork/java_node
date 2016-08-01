package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CSPLIT extends CInstruction
{
    // Destination
    CToken dest=null;
    
    // From
    CToken haystack=null;
    
    // Niddle
    CToken sep=null;
   
    
    public CSPLIT(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "SPLIT");
        
        // Destination
        this.dest=tokens.get(1);
        
        // Haystack
        this.haystack=tokens.get(3);
        
        // Separator
        this.sep=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
       // Log 
       VM.RUNLOG.add(VM.REGS.RCI, "SPLIT "+this.dest.cel.name+", "+this.haystack.cel.val+", "+this.sep.cel.val);
       
       // Cell
       CCell cel=new CCell("");
        
       // Replace
       String[] v=this.haystack.cel.val.split(this.sep.cel.val);
       
       // New cell
       for (int a=0; a<=v.length-1; a++)
           cel.addCell(new CCell(v[a]));
       
       // Copy
       this.dest.cel.copy(cel);
    }
}