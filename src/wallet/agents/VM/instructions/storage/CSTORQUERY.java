package wallet.agents.VM.instructions.storage;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CSTORQUERY extends CInstruction
{
   // Destination
   CToken dest=null;

   // Table
   CToken query=null;
   
   
    public CSTORQUERY(VM VM, ArrayList<CToken>tokens)
    {
       // Constructor
       super(VM, "STORQUERY");
        
       // Dest
       this.dest=tokens.get(1);
       
       // Table
       this.query=tokens.get(3);
    }
    
    public void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "STORQUERY "+this.query.cel.val);
        
       CCell table=VM.STOR.query(this.query.cel.val);
       this.dest.cel.copy(table);
       
       // Cost
       VM.CODE.fee=VM.CODE.fee+table.rs.lines_no*0.00000001;
    }
}
