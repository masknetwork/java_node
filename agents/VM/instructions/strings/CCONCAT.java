package wallet.agents.VM.instructions.strings;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CCONCAT extends CInstruction 
{
    // From
    CToken from_loc=null;
    
    // To
    CToken to_loc=null;
    
    public CCONCAT(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "CONCAT");
        
        // From register
        this.to_loc=tokens.get(1);
        
        // To register
        this.from_loc=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
       // Copy
       this.to_loc.cel.concat(this.from_loc.cel);
    }
}