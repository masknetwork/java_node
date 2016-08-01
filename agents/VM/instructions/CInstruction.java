package wallet.agents.VM.instructions;

import wallet.agents.VM.VM;

public class CInstruction 
{
    // Type
    public String type;
    
    // Virtual machine
    public VM VM;
    
    public CInstruction(VM VM, String type)
    {
       // Virtual machine
       this.VM=VM; 
        
       // Type
       this.type=type;
    }
    
    public void execute() throws Exception
    {
        
    }
}
