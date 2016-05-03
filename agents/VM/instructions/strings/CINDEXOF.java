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
    CToken str=null;
    
    // Needle
    CToken niddle=null;
    
    public CINDEXOF(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "INDEXOF");
        
        // Dest
        this.dest=tokens.get(1);
        
        // From register
        this.str=tokens.get(3);
        
        // To register
        this.niddle=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
       // Niddle val
       String niddle=this.niddle.cel.val;
       
       // Position
       int pos=this.str.cel.val.indexOf(niddle);
       
       // Write position
       this.dest.cel.copy(new CCell(pos));
    }
}