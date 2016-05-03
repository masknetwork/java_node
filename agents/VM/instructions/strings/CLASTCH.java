package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CLASTCH extends CInstruction
{
    // Destination
    CToken dest;
    
    // Haystack
    CToken haystack=null;
    
    // Characters number
    CToken no=null;
    
    public CLASTCH(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "LASTCH");
        
        // Destination
        this.dest=tokens.get(1);
        
        // To register
        this.haystack=tokens.get(3);
        
        // Characters no
        this.no=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
       // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "LASTCH "+this.dest.cel.name+", "+this.haystack.cel.val+", "+this.no.cel.val);
        
       // Length valid
       int chars=Integer.parseInt(this.no.cel.val);
       
       // Valid ?
       if (this.haystack.cel.val.length()<chars) 
           throw new Exception("Invalid length");
            
       // Start position
       int start=this.haystack.cel.val.length()-chars;
       
       // New cell
       CCell cel=new CCell(this.haystack.cel.val.substring(start));
       
       // Copy
       this.dest.cel.copy(cel);
    }
}