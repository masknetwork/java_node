package wallet.agents.VM.storage;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;

public class CStorage implements java.io.Serializable
{
    // Storage tables
    ArrayList<CStorageTable> tables=new ArrayList<CStorageTable>();
    
    // Virtual machine
    VM VM;
    
    static final long serialVersionUID = 750134661831883175L;
    
    public CStorage(VM VM, boolean sandbox) throws Exception
    {
        // Machine
        this.VM=VM;
        
        // Load storage
        Statement s=UTILS.DB.getStatement();
        
        // Result set
        ResultSet rs;
        
        // Load storage
        if (sandbox==true)
        rs=s.executeQuery("SELECT * "
                          + "FROM agents_mine "
                         + "WHERE ID='"+VM.agentID+"'");
        else
        rs=s.executeQuery("SELECT * "
                          + "FROM agents "
                         + "WHERE aID='"+VM.agentID+"'");
        
        if (UTILS.DB.hasData(rs))
        {
           // Next
           rs.next();
        
           // Storage initialized ?
           if (rs.getString("storage").length()>5)
              fromJSON(UTILS.BASIC.base64_decode(rs.getString("storage")));
        }
        else throw new Exception("Invalid agent ID");
    }
    
    // Set a storage location
    public void set(CCell table, 
                    CCell col, 
                    CCell line, 
                    CCell cel) throws Exception
    {
        // Table exist ?
        if (!this.tableExist(table.val)) 
            this.addTable(table.val);
        
        // Set column
        CStorageTable tab=this.getTable(table.val);        
        
        // Set
        tab.set(col.val, Long.parseLong(line.val), cel);
    }
    
    // Set a storage location
    public void setLast(String table, 
                        String col, 
                        CCell cel) throws Exception
    {
        // Table exist ?
        if (!this.tableExist(table)) this.addTable(table);
        
        // Set column
        CStorageTable tab=this.getTable(table);
        
        // Set
        tab.setLast(col, cel);
    }
    
    // Get a storage location
    public CCell get(CCell table, 
                     CCell col, 
                     CCell line) throws Exception
    {
        // Table exist ?
        if (!this.tableExist(table.val)) 
            throw new Exception("Table doesn't exist - "+table);
        
        // Get table
        CStorageTable tab=this.getTable(table.val);
        
        // Return
        return tab.get(col.val, Long.parseLong(line.val));
    }
    
    // Add a new line
    public void addLine(CCell table) throws Exception
    {
        // Table exist ?
        if (!this.tableExist(table.val)) 
            this.addTable(table.val);
        
        // Get table
        CStorageTable tab=this.getTable(table.val);
        
        // New line
        tab.addLine();
        
    }
    
    // Add a storage location
    public void add(String table, 
                    String col, 
                    CCell cel) throws Exception
    {
        // Table exist ?
        if (!this.tableExist(table)) this.addTable(table);
        
        // Get table
        CStorageTable tab=this.getTable(table);
        
        // Column exist
        if (!tab.colExist(col))
            tab.addCol(col);
        
        // Add
        tab.addLine();
        
        // Set 
        tab.setLast(col, cel);
    }
    
    public boolean tableExist(String table) throws Exception
    {
       // Search table
       for (int a=0; a<=this.tables.size()-1; a++)
           if (((CStorageTable)this.tables.get(a)).name.toLowerCase().equals(table.toLowerCase()))
              return true;
       
       // return
       return false;
   }
    
    public CStorageTable getTable(String name) throws Exception
    {
         for (int a=0; a<=this.tables.size()-1; a++)
           if (((CStorageTable)this.tables.get(a)).name.toLowerCase().equals(name.toLowerCase()))
              return this.tables.get(a);  
         
         throw new Exception("Table "+name+" does not exist");
    }
    
    public CStorageTable addTable(String table) throws Exception
    {
        // New table
        CStorageTable tab=new CStorageTable(table);
        
        if (!this.tableExist(table))
           this.tables.add(tab);
        
        return tab;
    }
    
    public CCell getByCol(String table, 
                         String get_col, 
                         String by_col, 
                         CCell by_cell) throws Exception
   {
       // Get column
       CStorageTable tab=this.getTable(table);
       
       // Set
       return tab.getByCol(get_col, by_col, by_cell);
   }
    
    public void setByCol(String table, 
                         String set_col, 
                         CCell set_val, 
                         String by_col, 
                         CCell by_cell) throws Exception
   {
       // Get column
       CStorageTable tab=this.getTable(table);
       
       // Set
       tab.setByCol(set_col, set_val, by_col, by_cell);
   }
   
   public void removeByCol(CCell table, 
                           CCell by_col,
                           CCell op,
                           CCell by_cell) throws Exception
   {
       // Get column
       CStorageTable tab=this.getTable(table.val);
       
       // Set
       tab.removeByCol(by_col.val, op.val, by_cell);
   }
   
   public void remove(CCell table, CCell pos) throws Exception
   {
       // Get column
       CStorageTable tab=this.getTable(table.val);
       
       // Set
       tab.remove(Long.parseLong(pos.val));
   }
   
   public void removeTable(CCell table) throws Exception
   {
       // Get column
       CStorageTable tab=this.getTable(table.val);
       
       // Set
       tab.removeAll();
       
       // Remove table
       this.tables.remove(tab);
   }
    
   public void flush(boolean sandbox) throws Exception
   {
       // Serialize
       String data=UTILS.BASIC.base64_encode(this.toJSON());
       System.out.println(this.toJSON());
       //if (0==0) return;
       
       // Write
       if (sandbox==true)
       UTILS.DB.executeUpdate("UPDATE agents_mine "
                               + "SET storage='"+data+"' "
                             + "WHERE ID='"+this.VM.agentID+"'");
       else
       UTILS.DB.executeUpdate("UPDATE agents "
                               + "SET storage='"+data+"' "
                             + "WHERE aID='"+this.VM.agentID+"'");
   }
   
   public CCell search (String table, String col, CCell val) throws Exception
   {
       // Load table
       CStorageTable tab=this.getTable(table);
       
       // Return
       return new CCell(tab.search(col, val));
   }
   
   public void trace()
    {
        for (int a=0; a<=this.tables.size()-1; a++)
            ((CStorageTable)this.tables.get(a)).trace();
    }
   
   
   
   public CCell getLine(String table, long pos) throws Exception
   {
       // New cell
       CCell c=new CCell("");
       c.type="ID_LIST";
       
       // Table
       if (!this.tableExist(table))
           throw new Exception("Invalid table");
       
       // Get table
       CStorageTable tab=this.getTable(table);
       
       // Position
       if (pos<0 || pos>tab.lines_no)
          throw new Exception("Invalid position");
       
       // Load line
       for (int a=0; a<=tab.columns.size()-1; a++)
       {
           CStorageColumn col=tab.columns.get(a);
           CCell cel=new CCell(col.lines.get((int)pos));
           c.addCell(cel);
       }
       
       // Return
       return c;
   }
   
   public CCell runQuery(String table, 
                         CCell col_1, CCell comp_1, CCell val_1,
                         CCell col_2, CCell comp_2, CCell val_2,
                         CCell col_3, CCell comp_3, CCell val_3,
                         CCell col_4, CCell comp_4, CCell val_4,
                         CCell col_5, CCell comp_5, CCell val_5) throws Exception
   {
       // Load table
       CStorageTable tab=this.getTable(table);
       
       // Compare
       CCell res=tab.query(col_1, comp_1, val_1,
                           col_2, comp_2, val_2,
                           col_3, comp_3, val_3,
                           col_4, comp_4, val_4,
                           col_5, comp_5, val_5);
       
       // Return
       return res;
   }
   
   public CCell query(String query) throws Exception
   {
       // Trim
       query=query.trim();
       
       // Comma
       query=query.replace(",", " ");
       
       // Double spaces
       query=query.replace("  ", " ");
       
       // Split
       String[] v=query.split(" ");
       
       // Size 
       if (v.length<3 || v.length>15)
           throw new Exception("Invalid query string length");
       
       // result
       CCell res=null;
       
       // Instruction
       switch (v[0].toUpperCase())
       {
           case "INSERT" : res=this.runInsert(v); break;
           case "DELETE" : res=this.runDelete(v); break;
           case "SELECT" : res=this.runSelect(v); break;
           case "UPDATE" : res=this.runUpdate(v); break;
           case "DROP" : res=this.runDrop(v); break;
       }
       
       return res;
   }
   
   public CCell runDrop(String[] tokens) throws Exception
   {
          // Check syntax
          if (!tokens[1].toUpperCase().equals("TABLE")) 
              throw new Exception("Query string syntax error");
          
          // Table
          String table=tokens[2];
       
          // Add table if doesn't exist
          if (!this.tableExist(table))
              throw new Exception("Invalid table");
          
          // Drop
          this.removeTable(new CCell(table));
          
          // Return 
          return new CCell(0);
   }
   
   public CCell runInsert(String[] tokens) throws Exception
   {
       CStorageTable sTable=null;
       
       try
       {
          // Check syntax
          if (!tokens[1].toUpperCase().equals("INTO")) 
              throw new Exception("Query string syntax error");
       
          // Table
          String table=tokens[2];
       
          // Add table if doesn't exist
          if (!this.tableExist(table))
              this.addTable(table);
       
          // Load table
          sTable=this.getTable(table);
       
          // Add line ?
          sTable.addLine();
       
          // Set
          if (!tokens[3].toUpperCase().equals("SET")) 
              throw new Exception("Query string syntax error");
       
          // Expressions
          for (int a=4; a<=tokens.length-1; a++)
          {
              // Split
              String[] v=tokens[a].split("=");
           
              // Column
              String col=v[0];
           
              // Value
              CCell val=this.evalExp(v[1]);
           
              // Set
              sTable.setLast(col, val);
          }
       }
       catch (Exception ex)
       {
           // Remove last line
           sTable.removeLastLine();
           
           // Throws exception
           throw new Exception(ex.getMessage());
       }
           
       return new CCell(sTable.lastID);
   }
   
  public CCell runUpdate(String[] tokens) throws Exception
   {
       // Cells
       CCell col_1=null;
       CCell comp_1=null;
       CCell val_1=null;
       
       CCell col_2=null;
       CCell comp_2=null;
       CCell val_2=null;
       
       CCell col_3=null;
       CCell comp_3=null;
       CCell val_3=null;
       
       CCell col_4=null;
       CCell comp_4=null;
       CCell val_4=null;
       
       CCell col_5=null;
       CCell comp_5=null;
       CCell val_5=null;
       
       // Actions
       String act_1=null;
       String act_2=null;
       String act_3=null;
       String act_4=null;
       String act_5=null;
       
       // Conditions
       String cond_1=null;
       String cond_2=null;
       String cond_3=null;
       String cond_4=null;
       String cond_5=null;
       
       // Table
       String table=tokens[1];
       
       // Add table if doesn't exist
       if (!this.tableExist(table))
          throw new Exception("Table "+table+" doesn't exist");
       
       // Load table
       CStorageTable sTable=this.getTable(table);
       
       // Set
       if (!tokens[2].toUpperCase().equals("SET"))
          throw new Exception("Query string syntax error");
       
       // Current state
       String state="ID_ACT";
       
       int index=1;
       while (state.equals("ID_ACT"))
       {
           if (!tokens[index+2].toUpperCase().equals("WHERE"))
           {
              switch (index)
              {
                  case 1 : act_1=tokens[index+2]; break;
                  case 2 : act_2=tokens[index+2]; break;
                  case 3 : act_3=tokens[index+2]; break;
                  case 4 : act_4=tokens[index+2]; break;
                  case 5 : act_5=tokens[index+2]; break;
              }
              
              // Index
              index++;
           }
           else state="ID_COND";
       }
       
       // Index
       index=index+3;
       
       // Index 2
       int index_2=1;
       
       // Condition index
       int cond=1;
       
       for (int a=index; a<=tokens.length-1; a++)
       {
           if (index_2%2==0 && 
               !tokens[a].toUpperCase().equals("AND"))
           {
               throw new Exception("Query string syntax error");
           }
           
           if (index_2%2!=0)
           {
              switch (cond)
              {
                  case 1 : cond_1=tokens[a]; break;
                  case 2 : cond_2=tokens[a]; break;
                  case 3 : cond_3=tokens[a]; break;
                  case 4 : cond_4=tokens[a]; break;
                  case 5 : cond_5=tokens[a]; break;
              }
              
              cond++;
           }
           
           // Index
           index_2++;
       }
       
       // Array 1
       String[] a1=this.getElements(sTable, cond_1);
       col_1=new CCell(a1[0]);
       comp_1=new CCell(a1[1]);
       val_1=new CCell(a1[2]);
       
       if (cond_2!=null)
       {
           String[] a2=this.getElements(sTable, cond_2);
           col_2=new CCell(a2[0]);
           comp_2=new CCell(a2[1]);
           val_2=new CCell(a2[2]);
       }
       
       if (cond_3!=null)
       {
           String[] a3=this.getElements(sTable, cond_3);
           col_3=new CCell(a3[0]);
           comp_3=new CCell(a3[1]);
           val_3=new CCell(a3[2]);
       }
       
       if (cond_4!=null)
       {
           String[] a4=this.getElements(sTable, cond_4);
           col_4=new CCell(a4[0]);
           comp_4=new CCell(a4[1]);
           val_4=new CCell(a4[2]);
       }
       
       if (cond_5!=null)
       {
           String[] a5=this.getElements(sTable, cond_5);
           col_5=new CCell(a5[0]);
           comp_5=new CCell(a5[1]);
           val_5=new CCell(a5[2]);
       }
       
       // Load results
       CCell res=this.runQuery(table, 
                                   col_1, comp_1, val_1, 
                                   col_2, comp_2, val_2, 
                                   col_3, comp_3, val_3, 
                                   col_4, comp_4, val_4, 
                                   col_5, comp_5, val_5);
                
        // Has results
        if (res.list.size()>0)
        {
            int delta=0;
            long pos=0;
            
            for (int a=0; a<=res.list.size()-1; a++)
            {
                pos=res.list.get(a).getLong()-delta;
                
                // Action 1
                applyAction(sTable, act_1, pos);
                
                // Action 2
                if (act_2!=null) applyAction(sTable, act_2, pos);
                
                // Action 3
                if (act_3!=null) applyAction(sTable, act_3, pos);
                
                // Action 4
                if (act_4!=null) applyAction(sTable, act_4, pos);
                
                // Action 5
                if (act_5!=null) applyAction(sTable, act_5, pos);
            }
        }
        
  
       return new CCell(res.list.size());
   
   }
  
   public void applyAction(CStorageTable table, String act, long pos) throws Exception
   {
       // Vector
       String[] v=act.split("=");
       
       // Size ok ?
       if (v.length!=2)
           throw new Exception("Query string syntax error");
       
       // Column
       String col=v[0];
       
       // Column ?
       if (!table.colExist(col))
            throw new Exception("Invalid column name "+col);
       
       // Value
       CCell val=this.evalExp(v[1]);
       
       // Set
       table.set(col, pos, val);
   }
   
   public CCell runDelete(String[] tokens) throws Exception
   {
       // Cells
       CCell col_1=null;
       CCell comp_1=null;
       CCell val_1=null;
       
       CCell col_2=null;
       CCell comp_2=null;
       CCell val_2=null;
       
       CCell col_3=null;
       CCell comp_3=null;
       CCell val_3=null;
       
       CCell col_4=null;
       CCell comp_4=null;
       CCell val_4=null;
       
       CCell col_5=null;
       CCell comp_5=null;
       CCell val_5=null;
       
       CStorageTable sTable=null;
       
       // Check syntax
       if (!tokens[1].toUpperCase().equals("FROM")) 
          throw new Exception("Query string syntax error");
       
       // Table
       String table=tokens[2];
       
       // Add table if doesn't exist
       if (!this.tableExist(table))
          this.addTable(table);
       
       // Load table
       sTable=this.getTable(table);
       
       // Set
       if (tokens.length>3)
       {
           if (!tokens[3].toUpperCase().equals("WHERE")) 
              throw new Exception("Query string syntax error");
       
           // Expression 4
           if (tokens[4]==null)
              throw new Exception("Query syntax error");
                
           // Array 1
           String[] a1=this.getElements(sTable, tokens[4]);
           col_1=new CCell(a1[0]);
           comp_1=new CCell(a1[1]);
           val_1=new CCell(a1[2]);
           
           // And
           if (tokens.length>5)
           {
              if (!tokens[5].toUpperCase().equals("AND") && 
                  !tokens[5].toUpperCase().equals("OR"))
                  throw new Exception("Query syntax error");
           }
           
           if (tokens.length>6)
           {
              // Array 2
              String[] a2=this.getElements(sTable, tokens[6]);
              col_2=new CCell(a2[0]);
              comp_2=new CCell(a2[1]);
              val_2=new CCell(a2[2]);
           }
           
           // And
           if (tokens.length>7)
           {
               if (!tokens[7].toUpperCase().equals("AND") && 
                   !tokens[7].toUpperCase().equals("OR"))
                   throw new Exception("Query syntax error");
           }
           
           if (tokens.length>8)
           {
              // Array 2
              String[] a3=this.getElements(sTable, tokens[8]);
              col_3=new CCell(a3[0]);
              comp_3=new CCell(a3[1]);
              val_3=new CCell(a3[2]);
           }
           
           // And
           if (tokens.length>9)
           {
               if (!tokens[9].toUpperCase().equals("AND") && 
                   !tokens[9].toUpperCase().equals("OR"))
                   throw new Exception("Query syntax error");
           }
           
           if (tokens.length>10)
           {
              // Array 2
              String[] a4=this.getElements(sTable, tokens[10]);
              col_4=new CCell(a4[0]);
              comp_4=new CCell(a4[1]);
              val_4=new CCell(a4[2]);
           }
           
           // And
           if (tokens.length>11)
           {
               if (!tokens[11].toUpperCase().equals("AND") && 
                   !tokens[11].toUpperCase().equals("OR"))
                   throw new Exception("Query syntax error");
           }
           
           if (tokens.length>12)
           {
              // Array 2
              String[] a5=this.getElements(sTable, tokens[12]);
              col_5=new CCell(a5[0]);
              comp_5=new CCell(a5[1]);
              val_5=new CCell(a5[2]);
           }
           
           // Load results
           CCell res=this.runQuery(table, 
                                   col_1, comp_1, val_1, 
                                   col_2, comp_2, val_2, 
                                   col_3, comp_3, val_3, 
                                   col_4, comp_4, val_4, 
                                   col_5, comp_5, val_5);
                
            // Has results
            if (res.list.size()>0)
            {
                int delta=0;
                long pos=0;
                for (int a=0; a<=res.list.size()-1; a++)
                {
                   pos=res.list.get(a).getLong()-delta;
                   this.remove(new CCell(table), new CCell(pos));
                   delta++;
                }
            }
        }
      
       return new CCell(sTable.lastID);
   }
   
   public CCell runSelect(String[] tokens) throws Exception
   {
       // Cells
       CCell col_1=null;
       CCell comp_1=null;
       CCell val_1=null;
       
       CCell col_2=null;
       CCell comp_2=null;
       CCell val_2=null;
       
       CCell col_3=null;
       CCell comp_3=null;
       CCell val_3=null;
       
       CCell col_4=null;
       CCell comp_4=null;
       CCell val_4=null;
       
       CCell col_5=null;
       CCell comp_5=null;
       CCell val_5=null;
       
       CStorageTable sTable=null;
       
       // Check syntax
       if (!tokens[1].toUpperCase().equals("*")) 
          throw new Exception("Query string syntax error");
       
       // From
       if (!tokens[2].toUpperCase().equals("FROM")) 
          throw new Exception("Query string syntax error");
       
       // Table
       String table=tokens[3];
       
       // Add table if doesn't exist
       if (!this.tableExist(table))
          this.addTable(table);
       
       // Load table
       sTable=this.getTable(table);
       
       // Result set
       CStorageTable rs=new CStorageTable(table);
       
       
       // Set
       if (tokens.length>4)
       {
           if (!tokens[4].toUpperCase().equals("WHERE")) 
              throw new Exception("Query string syntax error");
       
           // Expression 4
           if (tokens[5]==null)
              throw new Exception("Query syntax error");
                
           // Array 1
           String[] a1=this.getElements(sTable, tokens[5]);
           col_1=new CCell(a1[0]);
           comp_1=new CCell(a1[1]);
           val_1=new CCell(a1[2]);
           
           // And
           if (tokens.length>6)
           {
              if (!tokens[6].toUpperCase().equals("AND") && 
                  !tokens[6].toUpperCase().equals("OR"))
                  throw new Exception("Query syntax error");
           }
           
           if (tokens.length>7)
           {
              // Array 2
              String[] a2=this.getElements(sTable, tokens[7]);
              col_2=new CCell(a2[0]);
              comp_2=new CCell(a2[1]);
              val_2=new CCell(a2[2]);
           }
           
           // And
           if (tokens.length>8)
           {
               if (!tokens[8].toUpperCase().equals("AND") && 
                   !tokens[8].toUpperCase().equals("OR"))
                   throw new Exception("Query syntax error");
           }
           
           if (tokens.length>9)
           {
              // Array 2
              String[] a3=this.getElements(sTable, tokens[9]);
              col_3=new CCell(a3[0]);
              comp_3=new CCell(a3[1]);
              val_3=new CCell(a3[2]);
           }
           
           // And
           if (tokens.length>10)
           {
               if (!tokens[10].toUpperCase().equals("AND") && 
                   !tokens[10].toUpperCase().equals("OR"))
                   throw new Exception("Query syntax error");
           }
           
           if (tokens.length>11)
           {
              // Array 2
              String[] a4=this.getElements(sTable, tokens[11]);
              col_4=new CCell(a4[0]);
              comp_4=new CCell(a4[1]);
              val_4=new CCell(a4[2]);
           }
           
           // And
           if (tokens.length>12)
           {
               if (!tokens[12].toUpperCase().equals("AND") && 
                   !tokens[12].toUpperCase().equals("OR"))
                   throw new Exception("Query syntax error");
           }
           
           if (tokens.length>13)
           {
              // Array 2
              String[] a5=this.getElements(sTable, tokens[13]);
              col_5=new CCell(a5[0]);
              comp_5=new CCell(a5[1]);
              val_5=new CCell(a5[2]);
           }
           
           // Load results
           CCell res=this.runQuery(table, 
                                   col_1, comp_1, val_1, 
                                   col_2, comp_2, val_2, 
                                   col_3, comp_3, val_3, 
                                   col_4, comp_4, val_4, 
                                   col_5, comp_5, val_5);
                
            // Has results
            if (res.list.size()>0)
            {
                // Line
                long line=0;
                
                // Creates lines
                for (int a=0; a<=res.list.size()-1; a++) 
                    rs.addLine();
                    
                for (int a=0; a<=res.list.size()-1; a++)
                {
                   // Load line
                   line=res.list.get(a).getLong();
                   
                   // Load Data
                   for (int b=0; b<=sTable.columns.size()-1; b++)
                   {
                       // Column
                       CStorageColumn col=sTable.getColumn(b);
                       
                       // Columns name
                       String col_name=col.name;
                       
                       // Val
                       CCell val=col.get(line);
                       
                       // Set column
                       rs.set(col_name, a, val);
                   }
                }
            }
        }
      
       return new CCell(rs);
   }
   
   public String[] getElements(CStorageTable table, String exp) throws Exception
   {
       // Separator
       String sep="";
       
       // Get separator
       if (exp.indexOf("=")>0) sep="=";
       if (exp.indexOf("<")>0) sep="<";
       if (exp.indexOf(">")>0) sep=">";
       if (exp.indexOf("<>")>0) sep="<>";
       if (exp.indexOf("<=")>0) sep="<=";
       if (exp.indexOf(">=")>0) sep=">=";
       
       // Split
       String rv[]=new String[4];
       String[] v=exp.split(sep);
       
       // Column exist ?
       if (!table.colExist(v[0]))
           throw new Exception("Column not found "+v[0]);
       
       // Format
       rv[0]=v[0];
       rv[1]=sep;
       rv[2]=this.evalExp(v[1]).val;
       
       // Return
       return rv;
   }
   
   public CCell evalExp(String exp) throws Exception
   {
       if (exp.charAt(0)=='\'' && exp.endsWith("'"))
       {
           // Removes 
           exp=exp.replace("'", "");
           
           // New cell
           CCell c=new CCell(exp);
           
           // Set type
           c.type="ID_STRING";
           
           // Return
           return c;
       }
       else if (VM.vm_utils.isRegister(exp))
       {
           return new CCell(VM.REGS.getCell(exp));
       }
       else if (VM.MEM.varExist(exp))
       {
           return new CCell(VM.MEM.getVar(exp));
       }
       else if (VM.vm_utils.isDouble(exp) || VM.vm_utils.isLong(exp))
       {
           return new CCell(exp);
       }
       else throw new Exception("Could not evaluate "+exp);
   }
   
   public String toJSON()
   {
       // Init result
       String res="{\"tables\":[";
       
       // Load tables
       for (int a=0; a<=this.tables.size()-1; a++)
           if (a==0)
               res=res+this.tables.get(a).toJSON();
           else
               res=res+","+this.tables.get(a).toJSON();
       
       res=res+"]}";
       
       // Return
       return res;
   }
   
   public void fromJSON(String data) throws Exception
   {
       // Object
       JSONObject obj = new JSONObject(data); 
       System.out.println(obj);
           
       // Response
       JSONArray jtables = obj.getJSONArray("tables");  
       
       for (int a=0; a<=jtables.length()-1; a++)
       {
           // Table name
           JSONObject jtable=jtables.getJSONObject(a);
           String tab_name=jtable.getString("name");
           
           // Add table
           CStorageTable table=this.addTable(tab_name);
           
           // Last ID
           table.lastID=jtable.getLong("lastID");
           
           // Load columns
           JSONArray jcolumns=jtable.getJSONArray("columns"); 
           
           // Lines no
           long lines_no=0;
           
           // Parse columns
           for (int b=0; b<=jcolumns.length()-1; b++)
           {
               // Column name
               JSONObject jcol=jcolumns.getJSONObject(b);
               String col_name=jcol.getString("name");
               
               // Add clumn
               CStorageColumn column=table.addCol(col_name);
           
               // Load lines
               JSONArray jlines=jcol.getJSONArray("data"); 
               
               // Parse lines
               for (int c=0; c<=jlines.length()-1; c++)
               {
                  // Add column
                  column.add();
                  
                  // Set last
                  column.setLast(new CCell(jlines.getString(c)));
               }
               
               // Lines
               lines_no=jlines.length();
            }
           
           // Table lines
           table.lines_no=lines_no;
      }
   }
}
