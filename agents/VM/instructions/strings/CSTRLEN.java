package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CSTRLEN extends CInstruction
{
    // Destination
    CToken dest=null;
    
    // Str
    CToken str=null;
    
    
    public CSTRLEN(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "STRLEN");
        
        // Destination
        this.dest=tokens.get(1);
        
        // String
        this.str=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
       // Copy
       this.dest.cel.copy(new CCell(this.str.cel.val.length()));
    }
}