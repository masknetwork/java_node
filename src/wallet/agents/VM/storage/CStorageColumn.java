package wallet.agents.VM.storage;

import java.util.ArrayList;
import wallet.agents.VM.CCell;

public class CStorageColumn  implements java.io.Serializable
{
    // Table
    String table;
    
    // Name
    String name;
    
    // Lines
    ArrayList<CCell> lines=new ArrayList<CCell>();
    
    static final long serialVersionUID = -7834521716746266274L;
    
    // Cosntructor
    public CStorageColumn(String table, String name, long lines_no)
    {
        // Table
        this.table=table;
                
        // Name
        this.name=name;
        
        // Add lines
        for (int a=0; a<=lines_no-1; a++)
            this.lines.add(new CCell(""));
    }
    
    public void add()
    {
        // New line
        CCell c=new CCell("");
        
        // Name
        c.name=name;
        
        // Add value
        this.lines.add(c);
    }
    
    public long search(CCell val) throws Exception
    {
        for (int a=0; a<=this.lines.size()-1; a++)
        {
            CCell c=this.lines.get(a);
            if (c.equals(val)==1) return a;
        }
        
        return -1;
    }
     
    public boolean checkLocation(long pos)
    {
        // Empty storage
        if (this.lines.size()==0) return false;
        
        // Bounds ?
        if (pos<0 || pos>=this.lines.size()) return false;
        
        // Return 
        return true;
    }
    
    public CCell get(long pos) throws Exception
    {
        // Check location
        if (!this.checkLocation(pos))
            throw new Exception("Invalid storage location (Table : "+this.table+", Column : "+this.name+", Position : "+pos+")");
        
        // Return
        return new CCell(this.lines.get((int)pos));
    }
    
    public void set(long pos, CCell cel) throws Exception
    {
        // Check location
        if (!this.checkLocation(pos))
            throw new Exception("Invalid storage location (Table : "+this.table+", Column : "+this.name+", Position : "+pos+")");
        
        // Copy
        ((CCell)this.lines.get((int)pos)).copy(cel);
    }
    
    public void setLast(CCell cel) throws Exception
    {
        // Copy
        ((CCell)this.lines.get((int)this.lines.size()-1)).copy(cel);
    }
    
    public void remove(long pos) throws Exception
    {
        this.lines.remove((int)pos);
    }
    
    public void removeAll() throws Exception
    {
        for (int a=0; a<=this.lines.size()-1; a++)
            this.remove(a);
    }
    
    public void trace()
    {
        System.out.print(this.name+" -> ");
        
        for (int a=0; a<=this.lines.size()-1; a++)
            System.out.print(((CCell)this.lines.get(a)).val+", ");
           
    }
    
    public ArrayList query(CCell comp, CCell val) throws Exception
    {
        // Result
        ArrayList<Integer> res=new ArrayList<Integer>();
        
        // Cell
        CCell c;
        
        // Index
        int i=0;
        
        for (int a=0; a<=this.lines.size()-1; a++)
        {
            // Get cell
            c=this.lines.get(a);
            
            // Compare
            long r=c.compare(val);
            
           if (comp.val.equals("<") && r==-1) 
           {
               // Add position
               res.add(a);
               
               // Increase
               i++;
           }
           
           if (comp.val.equals("<=") && (r==-1 || r==0)) 
           {
               // Add position
               res.add(a);
               
               // Increase
               i++;
           }
           
           if (comp.val.equals("=") && r==0) 
           {
               // Add position
               res.add(a);
               
               // Increase
               i++;
           }
           
           if (comp.val.equals(">") && r==1) 
           {
               // Add position
               res.add(a);
               
               // Increase
               i++;
           }
           
           if (comp.val.equals(">=") && (r==1 || r==0)) 
           {
               // Add position
               res.add(a);
               
               // Increase
               i++;
           }
        }
        
        return res;
    }
    
   public String toJSON()
   {
       String res="{\"name\":\""+this.name+"\",\"data\":[";
       
       for (int a=0; a<=this.lines.size()-1; a++)
         if (a==0)
           res=res+"\""+this.lines.get(a).val+"\"";
         else
           res=res+",\""+this.lines.get(a).val+"\"";
       
       res=res+"]}";
       
       return res;
   }
   
   public long max()
   {
       return 0;
   }
}
