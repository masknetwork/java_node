package wallet.agents.VM.instructions.general;

import java.sql.ResultSet;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CNAMETOADR extends CInstruction
{
    // Result
    CToken adr=null;
    
    // Address
    CToken name=null;
    
    public CNAMETOADR(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "NAMETOADR");
        
        // Dest
        this.adr=tokens.get(1);
        
        // Adress
        this.name=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
        // Record 
        VM.RUNLOG.add(VM.REGS.RCI, "NAMETOADR "+this.adr.cel.name+", "+this.name.cel.val);
        
        // Is address ?
        if (UTILS.BASIC.isAdr(this.name.cel.val))
        {
            this.adr.cel.copy(this.adr.cel);
        }
        else if (UTILS.BASIC.isDomain(this.name.cel.val))
        {
            // Load data
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM domains "
                                              + "WHERE domain='"+this.name.cel.val+"'");
            
            // Copy
            this.adr.cel.copy(new CCell(rs.getString("adr")));
        }
        else throw new Exception("Invalid name");
    }
}
