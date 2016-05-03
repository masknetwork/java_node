package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.kernel.UTILS;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CMOV extends CInstruction 
{
    // From
    CToken from_loc=null;
    
    // To
    CToken to_loc=null;
    
    public CMOV(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "MOV");
        
        // From register
        this.to_loc=tokens.get(1);
        
        // To register
        this.from_loc=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
       // Copy
       if (!from_loc.type.equals("ID_COMPLEX_VAR"))
       {
          this.to_loc.cel.copy(this.from_loc.cel);
          
          // Out 
          VM.RUNLOG.add(VM.REGS.RCI, "MOV "+this.to_loc.cel.name+", "+this.from_loc.cel.val);
       }
       else
       {
          String cv=this.from_loc.cel.val;
          String[] v=cv.split("\\.");
          cv=v[0];
          CCell cvar=null;
          
          // Check variable
          if (VM.vm_utils.isRegister(cv))
             cvar=VM.REGS.getCell(cv);
          if (VM.MEM.varExist(cv))
              cvar=VM.MEM.getVar(cv);
          else throw new Exception("Could not find token "+cv);
          
          CCell res=cvar.evalComplexVar(this.from_loc.cel.val, VM);
          this.to_loc.cel.copy(res);
          
          // Out 
          VM.RUNLOG.add(VM.REGS.RCI, "MOV "+this.to_loc.cel.name+", "+res.val);
       }
    }
}
