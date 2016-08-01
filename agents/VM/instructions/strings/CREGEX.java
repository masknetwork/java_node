package wallet.agents.VM.instructions.strings;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CREGEX extends CInstruction
{
    // Destination
    CToken dest;
    
    // Haystack
    CToken regex=null;
    
    // Pattern
    CToken pattern=null;
    
    public CREGEX(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "REGEX");
        
        // Destination
        this.dest=tokens.get(1);
        
        // Regular expression
        this.regex=tokens.get(3);
        
        // String
        this.pattern=tokens.get(5);
    }
   
    public  void execute() throws Exception
    {
        // Load regular expression
        Pattern pat = Pattern.compile(this.regex.cel.val);
        
        // Pattern
        Matcher matcher = pat.matcher(this.pattern.cel.val);
        
        // Match ?
        if (matcher.find())
        {
            this.dest.cel.copy(new CCell(1));
            
            // Out 
            VM.RUNLOG.add(VM.REGS.RCI, "REGEX "+this.dest.cel.name+", 1");
        }
        else
        {
            this.dest.cel.copy(new CCell(0));
            
            // Out 
            VM.RUNLOG.add(VM.REGS.RCI, "REGEX "+this.dest.cel.name+", 0");
        }
    }
}