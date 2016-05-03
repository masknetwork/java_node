package wallet.agents.VM.instructions.math;

import java.util.ArrayList;
import java.util.Random;
import wallet.kernel.UTILS;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CRAND extends CInstruction
{
     // From
    CToken dest=null;
    
    // Minimum
    CToken min=null;
    
    // Maximum
    CToken max=null;
    
    // Seed
    CToken seed=null;
    
    public CRAND(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "RAND");
        
        // Destination
        this.dest=tokens.get(1);
        
        // Min val
        this.min=tokens.get(3);
        
        // Max val
        this.max=tokens.get(5);
        
        // Seed ?
        if (tokens.size()>7) this.seed=tokens.get(7);
    }
   
    public  void execute() throws Exception
    {
        // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "RAND "+this.dest.cel.name+", "+this.min.cel.val+", "+this.max.cel.val);
        
        // Seed
        long seed=0;
        
        // Min
        int min=(int)this.min.cel.getLong();
        
        // Max
        int max=(int)this.max.cel.getLong();
        
        // Seed
        if (this.seed!=null)
        {
            if (this.seed.cel.type.equals("ID_LONG")) 
                seed=this.seed.cel.getLong();
            else if (this.seed.cel.type.equals("ID_STRING")) 
                seed=this.extractSeed(this.seed.cel.val);
            else throw new Exception("Invalid seed format");
        }
        else
        {
            // Load transaction hash
            CCell trans_hash=VM.SYS.getVar("SYS.EVENT.TRANS.HASH");
            
            // Load block hash
            CCell block_hash=VM.SYS.getVar("SYS.NET.LAST_BLOCK_HASH");
            
            // Seed hash
            String h=trans_hash.val+block_hash.val;
            
            // Hash
            h=UTILS.BASIC.hash(h);
            
            // Seed
            seed=this.extractSeed(h);
        }
        
        // Random
        Random r=new Random();
        
        // Set seed
        r.setSeed(seed);
        
        // Generate random
        long rand=r.nextInt(max)+min;
        
        // Destination
        this.dest.cel.copy(new CCell(rand));
    }
    
    public long extractSeed(String seed) throws Exception
    {
        // Seed
        String s="";
        
        // Compute seed from hash
        for (int a=0; a<=seed.length()-1; a++)
        {
            if (seed.charAt(a)=='0' || 
                seed.charAt(a)=='1' || 
                seed.charAt(a)=='2' ||
                seed.charAt(a)=='3' || 
                seed.charAt(a)=='4' || 
                seed.charAt(a)=='5' || 
                seed.charAt(a)=='6' || 
                seed.charAt(a)=='7' || 
                seed.charAt(a)=='8' || 
                seed.charAt(a)=='9')
            if (s.length()<10) 
                s=s+seed.charAt(a);
                
        }
        
        return Long.parseLong(s);
    }
}