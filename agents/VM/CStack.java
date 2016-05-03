package wallet.agents.VM;

import java.util.ArrayList;

public class CStack 
{
    // Stack
    ArrayList<CCell> stack=new ArrayList<CCell>();    
    
    // Virtual machine
    public VM VM;
    
    public CStack(VM VM)
    {
        
    }
    
    public void push(CCell cel) throws Exception
    {
        this.stack.add(new CCell(cel));
    }
    
    public void pop(CCell cel) throws Exception
    {
        // Empty ?
        if (this.stack.size()==0) throw new Exception("Stack is empty");
                
        // Copy cell
        CCell c=this.stack.get(this.stack.size()-1);
        
        // Copy
        cel.copy(c);
        
        // Remove from stack
        this.stack.remove(c);
    }
    
    public void dump()
    {
        System.out.println();
        System.err.println("Stack trace : ");
        
        for (int a=0; a<=this.stack.size()-1; a++)
            System.err.print(this.stack.get(a).val+", ");
    }
}
