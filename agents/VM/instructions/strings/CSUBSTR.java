package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CSUBSTR extends CInstruction
{
    // Destination 
    CToken dest=null;
    
    // String
    CToken str=null;
    
    // Start
    CToken start=null;
    
    // End
    CToken end=null;
    
    public CSUBSTR(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "SUBSTR");
        
        // Destination
        this.dest=tokens.get(1);
        
        // String
        this.str=tokens.get(3);
        
        // Start
        this.start=tokens.get(5);
        
        // End
        this.end=tokens.get(7);
    }
   
    public  void execute() throws Exception
    {
       // Start
       int start=Integer.parseInt(this.start.cel.val);
       
       // End
       int end=Integer.parseInt(this.end.cel.val);
       
       // Substring
       String substr=this.str.cel.val.substring(start, end);
       
       // Copy
       this.dest.cel.copy(new CCell(substr));
    }
}