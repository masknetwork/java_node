package wallet.agents.VM.instructions.general;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CATPOS extends CInstruction 
{
    // To
    CToken to=null;
    
    // From
    CToken pos=null;
    
    // To
    CToken var=null;
    
    public CATPOS(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "ATPOS");
        
        // To register
        this.to=tokens.get(1);
        
        // String register
        this.var=tokens.get(3);
        
        // At position
        this.pos=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
        CCell p=null;
       
       // Position
       if (this.pos.cel.type.equals("ID_LONG"))
       {
          long pos=Long.parseLong(this.pos.cel.val);
          p=this.var.cel.atPos(pos);
       }
       
       // Position
       if (this.pos.cel.type.equals("ID_STRING"))
         p=this.var.cel.atPos(this.pos.cel.val);
       
       // Position
       if (this.pos.cel.type.equals("ID_LIST"))
         p=this.var.cel.list.get(Integer.parseInt(this.pos.cel.val));
       
       // Null ?
       if (p==null) throw new Exception("Invalid position");
       
       // Copy
       this.to.cel.copy(p);
       
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "ATPOS "+this.to.cel.name+", "+this.var.cel.name+", "+this.pos.cel.val);
    }
}
