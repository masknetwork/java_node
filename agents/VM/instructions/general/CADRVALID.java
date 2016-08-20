package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CADRVALID  extends CInstruction 
{
     // Result
    CToken dest=null;
    
    // Address
    CToken adr=null;
    
    // Address
    CToken adr_type=null;
    
    public CADRVALID(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "ADRVALID");
        
        // Dest
        this.dest=tokens.get(1);
        
        // Adress
        this.adr=tokens.get(3);
        
        // Adress type
        this.adr_type=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
        // Record 
        VM.RUNLOG.add(VM.REGS.RCI, "ADRVALID "+this.dest.cel.name+", "+this.adr.cel.val+", "+this.adr_type.cel.val);
        
        // Valid
        if (!this.adr_type.cel.val.equals("BTC") &&  
            !this.adr_type.cel.val.equals("MSK"))
        throw new Exception("Invalid address type");
        
        // Valid BTC address ?
        if (this.adr_type.equals("BTC"))
        {
            if (UTILS.BASIC.isBitcoinAdr(this.adr.cel.val))
                this.dest.cel.copy(new CCell(1));
            else
                this.dest.cel.copy(new CCell(0));
        }
        
        // Valid MSK address ?
        if (this.adr_type.equals("MSK"))
        {
            if (UTILS.BASIC.isAdr(this.adr.cel.val))
                this.dest.cel.copy(new CCell(1));
            else
                this.dest.cel.copy(new CCell(0));
        }
    }
}
