package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CREFUND extends CInstruction
{
    public CREFUND(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "REFUND");
    }
   
    public  void execute() throws Exception
    {
        // Refund
        VM.SYS.OP.TRANS.refund();
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}