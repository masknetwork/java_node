package wallet.agents.VM.instructions.math;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CMATH extends CInstruction
{
     // From
    CToken dest=null;
    
    // Value to be rounded
    CToken func=null;
    
    // Param 1
    CToken par_1=null;
    
    // Param 2
    CToken par_2=null;
    
    // Param 3
    CToken par_3=null;
    
    // Param 4
    CToken par_4=null;
    
    // Param 5
    CToken par_5=null;
    
    
    public CMATH(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "MATH");
        
        // From register
        this.dest=tokens.get(1);
        
        // Target
        this.func=tokens.get(3);
        
        // Parameter 1
        if (tokens.size()>5) this.par_1=tokens.get(5);
        
        // Parameter 2
        if (tokens.size()>7) this.par_2=tokens.get(7);
        
        // Parameter 3
        if (tokens.size()>9) this.par_3=tokens.get(9);
        
        // Parameter 4
        if (tokens.size()>11) this.par_4=tokens.get(11);
        
        // Parameter 5
        if (tokens.size()>13) this.par_5=tokens.get(13);
    }
   
    public  void execute() throws Exception
    {
        // Rounded
        double r=0;
        
        // Out 
        if (this.par_2!=null)
            VM.RUNLOG.add(VM.REGS.RCI, "MATH "+this.dest.cel.name+", '"+this.func.cel.val+"', "+this.par_1.cel.val+", "+this.par_2.cel.val);
        else
            VM.RUNLOG.add(VM.REGS.RCI, "MATH "+this.dest.cel.name+", '"+this.func.cel.val+"', "+this.par_1.cel.val);
        
        // Select function
        switch (this.func.cel.val.toUpperCase())
        {
            // Floor
            case "FLOOR" : r=Math.floor(this.par_1.cel.getDouble()); break; 
            
            // Ceil
            case "CEIL" : r=Math.ceil(this.par_1.cel.getDouble()); break; 
            
            // Round
            case "ROUND" : r=Math.round(this.par_1.cel.getDouble()); break; 
            
            // Modulo
            case "MOD" : r=this.par_1.cel.getDouble()%this.par_1.cel.getDouble(); break; 
            
            // Absolute
            case "ABS" : r=Math.abs(this.par_1.cel.getDouble()); break; 
            
            // Sin
            case "SIN" : r=Math.sin(this.par_1.cel.getDouble()); break; 
            
            // Cos
            case "COS" : r=Math.cos(this.par_1.cel.getDouble()); break; 
            
            // Tan
            case "TAN" : r=Math.tan(this.par_1.cel.getDouble()); break; 
            
            // Max
            case "MAX" : r=Math.max(this.par_1.cel.getDouble(), this.par_2.cel.getDouble()); break; 
            
            // Min
            case "MIN" : r=Math.min(this.par_1.cel.getDouble(), this.par_2.cel.getDouble()); break; 
            
            // Average
            case "AVG" : r=Math.abs((this.par_1.cel.getDouble()+this.par_2.cel.getDouble())/2); break; 
            
            // Exp
            case "EXP" : r=Math.exp(this.par_1.cel.getDouble()); break; 
            
            // Log
            case "LOG" : r=Math.log(this.par_1.cel.getDouble()); break; 
            
            // Cube root
            case "CBRT" : r=Math.cbrt(this.par_1.cel.getDouble()); break; 
            
            // POW
            case "POW" : r=Math.pow(this.par_1.cel.getDouble(), this.par_2.cel.getDouble()); break; 
            
            // To Radians
            case "TORADIANS" : r=Math.toRadians(this.par_1.cel.getDouble()); break; 
            
            // To Degrees
            case "TODEGREES" : r=Math.toRadians(this.par_1.cel.getDouble()); break; 
            
            // Digits round
            case "DROUND" : if (this.par_2.cel.val.equals("0")) 
                            {
                                r=Math.round(Double.parseDouble(this.par_1.cel.val));
                            }
                            else
                            {
                                // Digits
                                long d=Long.parseLong(this.par_2.cel.val);
            
                                 // 10 power
                                 long p=Math.round(Math.pow(10, d));
            
                                 // Result
                                 long l=Math.round(Double.parseDouble(this.par_1.cel.val)*p);
                                 r=(double)l/p;
                            } 
                            break; 
        }
        
        // Copy
        this.dest.cel.copy(new CCell(r));
    }
}
