package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CTRIM extends CInstruction
{
    // Destination 
    CToken dest=null;
    
    public CTRIM(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "SUBSTR");
        
        // Destination
        this.dest=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
       // Copy
       this.dest.cel.copy(new CCell(this.dest.cel.val.trim()));
    }
}