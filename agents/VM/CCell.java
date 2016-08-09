package wallet.agents.VM;

import java.util.ArrayList;
import wallet.kernel.UTILS;

public class CCell  implements java.io.Serializable
{
    // Data type
    public String type="";

    // Value
    public String val="";
    
    // Name
    public String name="";
    
    // Array
    public ArrayList<CCell> list=new ArrayList<CCell>();
    
    
    static final long serialVersionUID = 8685216892271177917L;
  
    public CCell(String val)
    {
        this.val=val;
        
        if (this.isDouble(val))
            this.type="ID_DOUBLE";
        
        else if (this.isLong(val))
            this.type="ID_LONG";
        
        else this.type="ID_STRING";
    }
    
    public CCell(String val, String name)
    {
        this.val=val;
        this.type="ID_STRING";
        this.name=name;
    }
    
    
    
    public CCell(Double val)
    {
        this.val=UTILS.FORMAT_8.format(val);
        this.type="ID_DOUBLE";
    }
    
    
    public CCell(Long val)
    {
        this.val=String.valueOf(val);
        this.type="ID_LONG";
    }
    
    public CCell(Long val, String name)
    {
        this.val=String.valueOf(val);
        this.type="ID_LONG";
        this.name=name;
    }
    
    public CCell(int val)
    {
        this.val=String.valueOf(val);
        this.type="ID_LONG";
    }
    
    public CCell(int val, String name)
    {
        this.val=String.valueOf(val);
        this.type="ID_LONG";
        this.name=name;
    }
    
    public CCell(ArrayList<CCell> list)
    {
        this.type="ID_LIST";
        this.list=new ArrayList<CCell>(list);
    }
    
    public CCell(CCell cel)
    {
        this.val=cel.val;
        this.type=cel.type;
    }
    
    public CCell(char c)
    {
        this.type="ID_STRING";
        this.val=String.valueOf(c);
    }
    
    public CCell(CCell cel, String name)
    {
        this.val=cel.val;
        this.type=cel.type;
        this.name=cel.name;
        this.list=cel.list;
    }
    
    public void copy(CCell cell)
    {
        this.type=cell.type;
        this.val=cell.val;
        this.list=cell.list;
    }
    
    public void add(CCell cell) throws Exception
    {
        this.combine(cell, "add");
    }
    
    public void sub(CCell cell) throws Exception
    {
        this.combine(cell, "sub");
    }
    
    public void mul(CCell cell) throws Exception
    {
        this.combine(cell, "mul");
    }
    
    public void div(CCell cell) throws Exception
    {
        this.combine(cell, "div");
    }
    
    public void combine(CCell cell, String op) throws Exception
    {
        // this values
        long this_val_long=0;
        double this_val_double=0;
        
        // Cell value
        long cell_val_long=0;
        double cell_val_double=0;
        
        
        // Actual cell is String
        if (this.type.equals("ID_STRING"))
        {
            // Convert local value 
            if (this.isLong(this.val))
                    this_val_long=Long.parseLong(this.val); 
                else if (this.isDouble(this.val))
                    this_val_double=Double.parseDouble(this.val); 
                else throw new Exception("Invalid type");
        }
        else
        {
             // Local value
            if (this.type.equals("ID_LONG")) 
                  this_val_long=Long.parseLong(this.val);
            
            if (this.type.equals("ID_DOUBLE")) 
                this_val_double=Double.parseDouble(this.val);
        }
        
        // Remote cell string
        if (cell.type.equals("ID_STRING")) 
        {
                if (this.isLong(this.val))
                    cell_val_long=Long.parseLong(cell.val); 
                else if (this.isDouble(this.val))
                    cell_val_double=Double.parseDouble(cell.val); 
                else throw new Exception("Invalid type");
        }
        else
        {
            // Remote cell number
            if (cell.type.equals("ID_LONG")) cell_val_long=Long.parseLong(cell.val);
            if (cell.type.equals("ID_DOUBLE")) cell_val_double=Double.parseDouble(cell.val);
        }
           
        // Calculate
        switch (op)
        {
                case "add" : this.val=UTILS.FORMAT_8.format(this_val_long+this_val_double+cell_val_long+cell_val_double);
                             break;
                             
                case "sub" : this.val=UTILS.FORMAT_8.format(this_val_long+this_val_double-cell_val_long-cell_val_double);
                             break;
                             
                case "mul" : this.val=UTILS.FORMAT_8.format((this_val_long+this_val_double)*(cell_val_long+cell_val_double));
                             break;
                             
                case "div" : this.val=UTILS.FORMAT_8.format((this_val_long+this_val_double)/(cell_val_long+cell_val_double));
                             break;
        }
    }
    
    public void concat(CCell cell)
    {
        this.val=this.val+cell.val;
    }
    
    public int equals(CCell cell)
    {
        if (this.type.equals(cell.type) && 
            this.val.equals(cell.val))
            return 1;
        else
            return 0;
    }
    
    public int compare(CCell cell) throws Exception
    {
        // Values
        long local_val_long=0;
        double local_val_double=0;
        long cell_val_long=0;
        double cell_val_double=0;
        
         // Compare string to number
         if (this.type.equals("ID_STRING") && 
            (cell.type.equals("ID_LONG") || 
             cell.type.equals("ID_DOUBLE")))
         throw new Exception("Can't compare distinct tokens");
         
         // Compare string to number
         if (cell.type.equals("ID_STRING") && 
            (this.type.equals("ID_LONG") || 
             this.type.equals("ID_DOUBLE")))
         throw new Exception("Can't compare distinct tokens");
         
         // Compare string to string
         if (this.type.equals("ID_STRING") && 
             cell.type.equals("ID_STRING"))
             if (this.val.equals(cell.val))
                 return 0;
             else 
                 return 1;
         
         if ((this.type.equals("ID_LONG") || this.type.equals("ID_DOUBLE")) && 
             (cell.type.equals("ID_LONG") || cell.type.equals("ID_DOUBLE")))
         {
             if (this.type.equals("ID_LONG")) 
                 local_val_long=Long.parseLong(this.val);
             
             if (this.type.equals("ID_DOUBLE")) 
                 local_val_double=Double.parseDouble(this.val);
             
             if (cell.type.equals("ID_LONG")) 
                 cell_val_long=Long.parseLong(cell.val);
             
             if (cell.type.equals("ID_DOUBLE")) 
                 cell_val_double=Double.parseDouble(cell.val);
             
             if ((local_val_long+local_val_double)<(cell_val_long+cell_val_double))
                 return -1;
             else if ((local_val_long+local_val_double)==(cell_val_long+cell_val_double))
                 return 0;
             else if ((local_val_long+local_val_double)>(cell_val_long+cell_val_double))
                 return 1;
         }
         
         return -1;
    }
    
    public CCell getByte(CCell cel) throws Exception
    {
        // Is string ?
        if (!this.type.equals("ID_STRING")) 
            throw new Exception("Invalid type");
        
        // New cell
        CCell c=new CCell(this.val.charAt(Integer.parseInt(cel.val)));
        
        // Return
        return c;
    }
        
    public void makeString() throws Exception
    {
        switch (this.type)
        {
            case "ID_LONG" : this.val=String.valueOf(this.val); 
                             this.type="ID_STRING";
                             break;
                             
            case "ID_DOUBLE" : this.val=String.valueOf(this.val); 
                               this.type="ID_STRING";
                               break;
                    
        }
    }
    
    public void makeDouble() throws Exception
    {
        switch (this.type)
        {
            case "ID_LONG" : this.type="ID_DOUBLE";
                             break;
                             
            case "ID_STRING" : this.val=String.valueOf(Double.parseDouble(this.val)); 
                               this.type="ID_DOUBLE";
                               break;
        }
    }
    
    public void makeLong() throws Exception
    {
        switch (this.type)
        {
            case "ID_DOUBLE" : this.type="ID_LONG";
                             break;
                             
            case "ID_STRING" : this.val=String.valueOf(Long.parseLong(this.val)); 
                               this.type="ID_LONG";
                               break;
        }
    }
    
    public void makeCell(CCell cell) throws Exception
    {
        switch (cell.type)
        {
            case "ID_STRING" : this.makeString(); break;
            case "ID_LONG" : this.makeLong(); break;
            case "ID_DOUBLE" : this.makeDouble(); break;
        }
    }
    
    public boolean isDouble(String str)
    {
        if (str.indexOf('.')>0)
        {
            try
            {
               double l=Double.parseDouble(str);
            }
            catch (Exception ex)
            {
                return false;
            }
        } else return false;
        
        return true;
    }
    
    public boolean isLong(String str)
    {
        try
        {
           long l=Long.parseLong(str);
        }
        catch (Exception ex)
        {
            return false;
        }
        
        return true;
    }
    
    public double round(double val)
    {
        return (double) Math.round(val*100000000)/100000000;
    }
    
    public long getLong()
    {
        return Long.parseLong(val);
    }
    
    public String getString()
    {
        return val;
    }
    
    public double getDouble()
    {
        return Double.parseDouble(val);
    }
    
    public void addCell(CCell cel) throws Exception
    {
        // Change type
        this.type="ID_LIST";
        
        // Add
        this.list.add(cel);
    }
    
    public long size()
    {
        // Size
        long size=0;
        
        switch (this.type)
        {
            case "ID_STRING" : size=this.val.length(); break;
            case "ID_LIST" : size=this.list.size(); break;
        }
        
        // Return
        return size;
    }
    
    public CCell atPos(long pos) throws Exception
    {
        // Size
        CCell el=null;
        
        switch (this.type)
        {
            case "ID_STRING" : el=new CCell(this.val.charAt((int)pos)); break;
            case "ID_LIST" : el=this.list.get((int)pos); break;
        }
        
        // Return
        if (el==null) 
            throw new Exception("Invalid position");
        else
            return el;
    }
    
    public CCell lastPos() throws Exception
    {
       return this.atPos(this.list.size()-1);
    }
    
    public CCell atPos(String name) throws Exception
    {
        // Size
        CCell el=null;
        
        if (this.type.equals("ID_LIST"))
            throw new Exception("Invalid data type");
        
        for (int a=0; a<=this.list.size()-1; a++)
        {
            // Load cell
            CCell c=this.list.get(a);
            
            // Found ?
            if (c.name.equals(name))
                 el=new CCell(c);
        }
        
        // Return
        if (el==null) 
            throw new Exception("Invalid position");
        else
            return el;
    }
    
    public void loadArray(ArrayList<Integer> array) throws Exception
    {
        for (int a=0; a<=array.size()-1; a++)
            this.addCell(new CCell(array.get(a)));
    }
    
     public void toDouble()
     {
         // Try to convert
         double d=Double.parseDouble(this.val);
         
         // Convert
         this.type="ID_DOUBLE";
     }
     
     public CCell evalComplexVar(String var, VM VM) throws Exception
     {
         if (this.list==null)
             throw new Exception("Couldn't evaluate complex variable");
         
         // Vector
         String[] v=var.split("\\.");
         
         // Load column
         v=v[1].split("\\[");
         
         // Column
         String col=v[0];
         
         // Position
         String pos=v[1].replace("]", "");
         
         long p=0;
             
         // Finds position
         if (this.isLong(pos)) 
            p=Long.parseLong(pos);
         else if (VM.vm_utils.isRegister(pos))
            p=VM.REGS.getCell(pos).getLong();
         else if (VM.MEM.varExist(pos))
            p=VM.MEM.getVar(pos).getLong();
         else
            throw new Exception("Could not evaluate complex expression "+var);
         
        
            return listGet(col, p);
     }
     
     public CCell listGet(String col_name, long pos) throws Exception
     {
         for (int a=0; a<=this.list.size()-1; a++)
         {
             CCell cel=this.list.get(a);
             
             if (cel.name.equals(col_name))
                 return cel.list.get((int)pos);
         }
         
         throw new Exception("Coudn't find column "+col_name);
     }
     
     public void listAdd(CCell cel)
     {
         // Add
         this.list.add(cel);
         
         // Change type
         this.type="ID_LIST";
     }
}
