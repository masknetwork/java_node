package wallet.agents.VM.instructions.sys_calls;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.kernel.UTILS;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CDBQUERY extends CInstruction
{
   // Destination
   CToken dest=null;

   // Table
   CToken table=null;
   
   // Column 1
   CToken col_1=null;
   
   // Comparator 1
   CToken comp_1=null;
   
   // Value 1
   CToken val_1=null;
   
   // Column 2
   CToken col_2=null;
   
   // Comparator 2
   CToken comp_2=null;
   
   // Value 2
   CToken val_2=null;
   
   // Column 3
   CToken col_3=null;
   
   // Comparator 3
   CToken comp_3=null;
   
   // Value 3
   CToken val_3=null;
    
    public CDBQUERY(VM VM, ArrayList<CToken>tokens) 
    {
        super(VM, "DBQUERY");
        
        // Dest
        this.dest=tokens.get(1);
       
        // Table
        this.table=tokens.get(3);
       
        // Col 1
        this.col_1=tokens.get(5);
       
        // Comp 1
        this.comp_1=tokens.get(7);
       
        // Val 1
        this.val_1=tokens.get(9);
       
        if (tokens.size()>11)
        {
           // Col 2
           this.col_2=tokens.get(11);
       
           // Comp 2
           this.comp_2=tokens.get(13);
       
           // Val 2
           this.val_2=tokens.get(15);
           
           if (tokens.size()>17)
           {
               // Col 3
               this.col_3=tokens.get(17);
       
               // Comp 3
               this.comp_3=tokens.get(19);
       
               // Val 3
               this.val_3=tokens.get(21);
           }
       }
    }
    
    public void execute() throws Exception
    {
        // Table
        switch (this.table.cel.val)
        {
            case "adr" : executeADR(); break;    
        }
    }
    
    public void executeADR() throws Exception
    {
        // Result
        CCell res=new CCell("");
        res.type="ID_LIST";
        
        // Check cols
        if (this.col_1.cel.val.equals("adr") && 
            this.col_1.cel.val.equals("created") && 
            this.col_1.cel.val.equals("balance"))
        throw new Exception("Invalid column");
        
        // Check col 2
        if (this.col_2!=null)
           if (this.col_2.cel.val.equals("adr") && 
               this.col_2.cel.val.equals("created") && 
               this.col_2.cel.val.equals("balance"))
           throw new Exception("Invalid column");
        
        // Check col 3
        if (this.col_3!=null)
           if (this.col_3.cel.val.equals("adr") && 
               this.col_3.cel.val.equals("created") && 
               this.col_3.cel.val.equals("balance"))
           throw new Exception("Invalid column");
        
        // Query
        String q="SELECT adr, created, balance FROM adr WHERE ";
        
        // Column 1
        if (this.comp_1.cel.val.equals("="))
            q=q+this.col_1.cel.val+this.comp_1.cel.val+"'"+this.val_1.cel.val+"'";
        else
            q=q+this.col_1.cel.val+this.comp_1.cel.val+this.val_1.cel.val;
        
        // Column 2
        if (this.col_2!=null)
        {
            if (this.comp_2.cel.val.equals("="))
               q=q+" AND "+this.col_2.cel.val+this.comp_2.cel.val+"'"+this.val_2.cel.val+"'";
            else
               q=q+" AND "+this.col_2.cel.val+this.comp_2.cel.val+this.val_2.cel.val;
        }
        
        // Column 3
        if (this.col_3!=null)
        {
            if (this.comp_3.cel.val.equals("="))
               q=q+" AND "+this.col_3.cel.val+this.comp_3.cel.val+"'"+this.val_3.cel.val+"'";
            else
               q=q+" AND "+this.col_3.cel.val+this.comp_3.cel.val+this.val_3.cel.val;
        }
        
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Execute
        ResultSet rs=s.executeQuery(q);
        
        // Empty
        if (!UTILS.DB.hasData(rs)) res=new CCell(0);
        
        // Load rows
        while (rs.next())
        {
            // Row
            CCell row=new CCell("");
            
            // Row type
            row.type="ID_LIST";
            
            // Address
            CCell adr=new CCell(rs.getString("adr"));
            adr.name="adr";
            row.addCell(adr);
            
            // Balance
            CCell balance=new CCell(rs.getDouble("balance"));
            balance.name="balance";
            row.addCell(balance);
            
            // Block
            CCell created=new CCell(rs.getLong("created"));
            balance.name="created";
            row.addCell(created);
            
            // Add row
            res.addCell(row);
            
            // Fee
            VM.CODE.fee=VM.CODE.fee+0.0001;
        }
        
        // Return
        this.dest.cel.copy(res);
    }
}
