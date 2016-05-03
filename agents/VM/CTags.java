package wallet.agents.VM;

import java.util.ArrayList;

public class CTags 
{
    // Tags
    public ArrayList<CTag> tags=new ArrayList<CTag>();    
    
    public CTags()
    {
       
    }
    
    public void add(String t, long line)
    {
         // New tag
        CTag tag=new CTag(t, line);
        
        // Add tag
        this.tags.add(tag);
    }
    
    public long getLine(String tag)
    {
        // Line
        long line=-1;
        
        // Find tag
        for (int a=0; a<=this.tags.size()-1; a++)
            if (this.tags.get(a).tag.equals(tag))
                return this.tags.get(a).line;
        
        // Return 
        return line;
    }
    
    public boolean hasTag(String tag)
    {
        for (int a=0; a<=this.tags.size()-1; a++)
        {
            // Load tag
            CTag t=this.tags.get(a);
            
            // Equals ?
            if (t.tag.equals(tag)) 
                return true;
        }
        
        return false;
    }
}
