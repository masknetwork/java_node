package wallet.agents.VM;

import wallet.kernel.UTILS;

public class CRegisters 
{
    // Long value registers
    public CCell R1=new CCell(0, "R1");
    public CCell R2=new CCell(0, "R2");
    public CCell R3=new CCell(0, "R3");
    public CCell R4=new CCell(0, "R4");
    public CCell R5=new CCell(0, "R5");
    public CCell R6=new CCell(0, "R6");
    public CCell R7=new CCell(0, "R7");
    public CCell R8=new CCell(0, "R8");
    public CCell R9=new CCell(0, "R9");
    public CCell R10=new CCell(0, "R10");
    
    // Code index
    public long RCI=0;
    
    public CRegisters()
    {
        
    }
    
    public CCell getCell(String reg)
    {
        // Null cell
        CCell r=null;
           
        // Register
        reg=reg.toUpperCase();
        
        switch (reg)
        {
            case "R1" : r=this.R1; break;
            case "R2" : r=this.R2; break;
            case "R3" : r=this.R3; break;
            case "R4" : r=this.R4; break;
            case "R5" : r=this.R5; break;
            case "R6" : r=this.R6; break;
            case "R7" : r=this.R7; break;
            case "R8" : r=this.R8; break;
            case "R9" : r=this.R9; break;
            case "R10" : r=this.R10; break;
        }
        
        return r;
    }
    
    public void trace()
    {
        System.out.println();
        System.err.print("Registers : ");
        System.err.print("RL1 : "+this.R1.val+", ");
        System.err.print("RL2 : "+this.R2.val+", ");
        System.err.print("RL3 : "+this.R3.val+", ");
        System.err.print("RL4 : "+this.R4.val+", ");
        System.err.print("RL5 : "+this.R5.val+", ");
        System.err.print("RL6 : "+this.R6.val+", ");
        System.err.print("RL7 : "+this.R7.val+", ");
        System.err.print("RL8 : "+this.R8.val+", ");
        System.err.print("RL9 : "+this.R9.val+", ");
        System.err.print("RL10 : "+this.R10.val+", ");
    }
    
    public void setCodeIndex(long line) throws Exception
    {
        // New index
        long new_index=0;
        
        // New index
        new_index=line-1;
        
        // Outside ?
        if (new_index<0) 
            throw new Exception("Invalid code index ("+new_index+")");
        
        // Set code index
        this.RCI=new_index;
    }
    
    public void moveCodeIndex(long delta) throws Exception
    {
        // New index
        long new_index=0;
        
        // New index
        new_index=this.RCI+delta;
        
        // Outside ?
        if (new_index<0) 
            throw new Exception("Invalid code index ("+new_index+")");
        
        // Set code index
        this.RCI=new_index;
    }
}
