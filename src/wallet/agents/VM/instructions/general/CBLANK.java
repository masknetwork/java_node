package wallet.agents.VM.instructions.general;

import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CBLANK extends CInstruction
{
    public CBLANK(VM VM)
    {
        super(VM, "ID_BLANK");
    }
    
    public void execute()
    {
        VM.CODE.steps--;
    }
}
