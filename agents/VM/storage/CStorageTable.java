package wallet.agents.VM.storage;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;

public class CStorageTable  implements java.io.Serializable
{
   // Name
    String name;
    
   // Storage lines
   ArrayList<CStorageColumn> columns=new ArrayList<CStorageColumn>();
   
   // Lines number
   public long lines_no=0;
   
   // Last ID
   long lastID=0;
   
   static final long serialVersionUID = 750134661831883175L;
    
   public CStorageTable(String name)
   {
       // Name
       this.name=name;
      
    }
   
   public CStorageColumn addCol(String column) throws Exception
   {
       // No columns ?
       if (this.columns.size()==0 && !column.equals("ID"))
       {
           CStorageColumn col=new CStorageColumn("ID", column, this.lines_no);
           col.set(this.lines_no, new CCell(1));
       }
       
       // New column
       CStorageColumn col=new CStorageColumn(this.name, column, this.lines_no);
       
       // Add column
       this.columns.add(col);
       
       // Return 
       return col;
   }
   
   public CCell get(String column, long pos) throws Exception
   {
       // Storage column
       CStorageColumn cur;
       
       // Columns exist ?
       if (!this.colExist(column)) throw new Exception("Column does not exist - "+column);
       
       // Get column
       CStorageColumn col=this.getColumn(column);
       
       // Set
       return col.get(pos);
   }
   
   public void set(String column, long pos, CCell cel) throws Exception
   {
       // Storage column
       CStorageColumn cur;
       
       // Columns exist ?
       if (!this.colExist(column)) this.addCol(column);
       
       // Get column
       CStorageColumn col=this.getColumn(column);
       
      
       // Set
       col.set(pos, cel);
   }
   
   public CCell getLast(String column) throws Exception
   {
       return this.get(column, (this.lines_no-1));
   }
   
   public void setLast(String col, CCell cel) throws Exception
   {
       this.set(col, this.lines_no-1, cel);
   }
   
   public void removeLastLine() throws Exception
   {
       this.remove(this.lines_no-1);
   }
   
   public void addLine() throws Exception
   {
       if (this.lines_no==0) this.addCol("ID");
           
       // Add a new line to all columns
       for (int a=0; a<=this.columns.size()-1; a++)
           ((CStorageColumn)this.columns.get(a)).add();
       
       // Set ID
       this.set("ID", this.lines_no, new CCell(this.lastID));
       
       // Last ID
       this.lastID++;
       
       // Lines number
       this.lines_no++;
   }
   
  
   public boolean colExist(String col) throws Exception
   {
        for (int a=0; a<=this.columns.size()-1; a++)
           if (((CStorageColumn)this.columns.get(a)).name.toLowerCase().equals(col.toLowerCase()))
              return true;
              
        return false;
   }
   
   public CStorageColumn getColumn(String name) throws Exception
   {
        // Column exist
        if (!this.colExist(name)) 
            throw new Exception("Invalid column");
            
        // Get column
        for (int a=0; a<=this.columns.size()-1; a++)
           if (((CStorageColumn)this.columns.get(a)).name.equals(name))
              return this.columns.get(a);
        
        // Return
        return null;
   }
   
   public CStorageColumn getColumn(long pos) throws Exception
   {
        return (CStorageColumn)this.columns.get((int)pos);
   }
   
   public CCell getByCol(String get_col, 
                        String by_col, 
                        CCell by_cell) throws Exception
   {
       // Get column
       CStorageColumn col=this.getColumn(by_col);
       
       // Loop
       for (int a=0; a<=this.lines_no-1; a++)
       {
           // Load cell
           CCell c=col.get(a);
           
           // Compare
           if (c.equals(by_cell)==1) 
               return this.get(get_col, a);
       }
       
       // Null
       throw new Exception("Value not found");
   }
   
   public void setByCol(String set_col, 
                        CCell set_val, 
                        String by_col, 
                        CCell by_cell) throws Exception
   {
       // Get column
       CStorageColumn col=this.getColumn(by_col);
       
       // Loop
       for (int a=0; a<=this.lines_no-1; a++)
       {
           // Lod cell
           CCell c=col.get(a);
           
           // Compare
           if (c.equals(by_cell)==1) 
               this.set(set_col, a, set_val);
       }
   }
   
   public void removeByCol(String by_col, 
                           String op, 
                           CCell by_cell) throws Exception
   {
       // Get column
       CStorageColumn col=this.getColumn(by_col);
       
       // Loop
       for (int a=0; a<=this.lines_no-1; a++)
       {
           // Lod cell
           CCell c=col.get(a);
           
           // Compare
           if (c.compare(by_cell)==-1 && op.equals("<") || 
               c.compare(by_cell)==0 && op.equals("=") || 
               c.compare(by_cell)==1 && op.equals(">")) 
           {
               // Removes
               col.remove(a);
               
               // New lines number
               this.lines_no=col.lines.size();
               
               // Return
               return;
           }
       }
   }
   
   public void remove(long pos) throws Exception
   {
       for (int a=0; a<=this.columns.size()-1; a++)
       {
           // Load column
           CStorageColumn col=this.getColumn(a);
           
           // Remove
           col.remove(pos);
           
           // Lines
           this.lines_no=col.lines.size();
       }
   }
   
   public void removeAll() throws Exception
   {
       for (int a=0; a<=this.columns.size()-1; a++)
       {
           // Load column
           CStorageColumn col=this.getColumn(a);
           
           // Remove
           col.removeAll();
       }   
    
       // Lines
       this.lines_no=0;
   }
   
   public long search(String col, CCell search) throws Exception
   {
       // Column
       CStorageColumn c=this.getColumn(col);
       
       // Return
       return c.search(search);
   }
   
   public void trace()
    {
        System.out.println();
        System.out.println(this.name);
        System.out.println("--------------------------------------------");
        
        for (int a=0; a<=this.columns.size()-1; a++)
        {
             ((CStorageColumn)this.columns.get(a)).trace();
             System.out.println("");
        }  
    }
   
   public boolean exist(ArrayList<Integer> array, int search)
   {
       for (int a=0; a<=array.size()-1; a++)
           if (array.get(a)==search) return true;
       
       return false;
   }
   
   public ArrayList<Integer> intersect(ArrayList<Integer> a1, ArrayList<Integer> a2)
   {
       ArrayList<Integer> res=new ArrayList<Integer>();
       int size; 
       
       // One of sizes 0
       if (a1.size()==0 || a2.size()==0) return res;
       
       // Smaller size
       if (a1.size()<a2.size())
       {
           for (int a=0; a<=a1.size()-1; a++)
               if (this.exist(a2, a1.get(a)))
                   res.add(a1.get(a));
       }
       else 
       {
           for (int a=0; a<=a2.size()-1; a++)
               if (this.exist(a1, a2.get(a)))
                   res.add(a2.get(a));
       }
       
       // Return
       return res;
   }
   
   public CCell query(CCell col_1, CCell comp_1, CCell val_1,
                      CCell col_2, CCell comp_2, CCell val_2,
                      CCell col_3, CCell comp_3, CCell val_3,
                      CCell col_4, CCell comp_4, CCell val_4,
                      CCell col_5, CCell comp_5, CCell val_5) throws Exception
   {
       CCell result=new CCell(new ArrayList<CCell>());
       
       CStorageColumn c_1=this.getColumn(col_1.val);
       ArrayList<Integer> res_1=c_1.query(comp_1, val_1);
       
       if (col_2!=null)
       {
          CStorageColumn c_2=this.getColumn(col_2.val);
          ArrayList<Integer> res_2=c_2.query(comp_2, val_2);
          
          // Intersect arrays
          ArrayList<Integer> i=this.intersect(res_1, res_2);
          
          if (col_3!=null)
          {
              CStorageColumn c_3=this.getColumn(col_3.val);
              ArrayList<Integer> res_3=c_3.query(comp_3, val_3);
          
              // Intersect arrays
              ArrayList<Integer> i2=this.intersect(i, res_3);
              
              if (col_4!=null)
              {
                 CStorageColumn c_4=this.getColumn(col_4.val);
                 ArrayList<Integer> res_4=c_4.query(comp_4, val_4);
          
                 // Intersect arrays
                 ArrayList<Integer> i3=this.intersect(i, res_4);
                 
                 if (col_5!=null)
                 {
                     CStorageColumn c_5=this.getColumn(col_5.val);
                     ArrayList<Integer> res_5=c_5.query(comp_5, val_5);
          
                     // Intersect arrays
                     ArrayList<Integer> i4=this.intersect(i, res_5);
              
                     // Return 
                     result.loadArray(i4);
                 }
              
                 // Return 
                 result.loadArray(i3);
              }
              
              // Return 
              result.loadArray(i2);
          }
          else result.loadArray(i);
       }
       else result.loadArray(res_1);
       
       return result;
   }
   
   public String toJSON()
   {
       String res="{\"name\":\""+this.name+"\",\"lastID\":\""+this.lastID+"\",\"columns\":[";
       
       for (int a=0; a<=this.columns.size()-1; a++)
         if (a==0)
           res=res+this.columns.get(a).toJSON();
         else
           res=res+","+this.columns.get(a).toJSON();
       
       res=res+"]}";
       
       return res;
   }
  
}
