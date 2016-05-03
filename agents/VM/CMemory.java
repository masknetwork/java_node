package wallet.agents.VM;

import java.util.ArrayList;

public class CMemory 
{
    // Memory
    ArrayList<CCell> mem=new ArrayList<CCell>();
    
    // Virtual machine
    VM VM;

    public CMemory(VM VM)
    {
        // MAchine
        this.VM=VM;
    }
    
    public CCell getVar(String var)
    {
        // Var
        var=var.replace("$", "");
                
        // Cell
        CCell c;
        
        // Var exist ?
        for (int a=0; a<=this.mem.size()-1; a++)
        {
            // Get cell
            c=this.mem.get(a);
            
            // Check
            if (c.name.equals(var)) return c;
        }
        
        // Create new cell
        c=new CCell("", var);
        
        // Add cell
        this.mem.add(c);
        
        // Return
        return c;
    }
    
    public boolean varExist(String var)
    {
        CCell c;
        
         // Var
         var=var.replace("$", "");
        
        // Var exist ?
        for (int a=0; a<=this.mem.size()-1; a++)
        {
            // Get cell
            c=this.mem.get(a);
            
            // Check
            if (c.name.equals(var)) 
                return true; 
            else 
                return false;
        }
        
        // Return
        return false;
    }
    
    public void dump()
    {
        System.out.println();
        System.err.print("Memory trace : ");
        
        for (int a=0; a<=this.mem.size()-1; a++)
            System.err.print(this.mem.get(a).val+", ");
    }
}
