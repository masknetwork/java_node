package wallet.agents.VM.instructions.storage;

import java.sql.ResultSet;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CSTORAGE extends CInstruction
{
   // Destination
   CToken dest=null;

   // Table
   CToken query=null;
   
   
    public CSTORAGE(VM VM, ArrayList<CToken>tokens)
    {
       // Constructor
       super(VM, "STORAGE");
        
       // Dest
       this.dest=tokens.get(1);
       
       // Table
       this.query=tokens.get(3);
    }
    
    public void execute() throws Exception
    {
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "STORAGE "+this.query.cel.val);
        
       // Parse query
       this.parseQuery(this.query.cel.val);
    }
    
    public void parseQuery(String query) throws Exception
    {
        // No brachets
        query=query.replace("(", "");
        query=query.replace(")", "");
        
        // Split
        ArrayList tokens=this.parse(query);
        
        // Instruction
        String ins=tokens.get(0).toString();
        
        // Instruction 
        if (!ins.equals("SELECT") &&
            !ins.equals("INSERT") &&
            !ins.equals("UPDATE") &&
            !ins.equals("DELETE"))
        throw new Exception("Query syntax error");
        
        switch (ins)
        {
            // Select
            case "SELECT" : this.executeSelect(tokens); break;
            
            // Insert
            case "INSERT" : this.executeInsert(tokens); break;
            
            // Update
            case "UPDATE" : this.executeUpdate(tokens); break;
            
            // Delete
            case "DELETE" : this.executeDelete(tokens); break;
        }
    }
    
    public ArrayList parse(String query) throws Exception
    {
       String token="";
       boolean inString=false;
       ArrayList tokens=new ArrayList();
       
       for (int a=0; a<=query.length()-1; a++)
       {
           // No separator char
           if (query.charAt(a)!=',' && query.charAt(a)!=' ')
           {
               // Add char
               token=token+query.charAt(a);
           }
           
           // In string ?
           if (query.charAt(a)=='\'')
           {
               if (!inString) 
                  inString=true;
               else
                  inString=false;
               
               // Add char
               token=token+query.charAt(a);
           }
           
           // Separator ?
           if (query.charAt(a)==',' || query.charAt(a)==' ')
           {
               // In string ?
               if (inString)
               {
                    // Add char
                    token=token+query.charAt(a);
               }
               else
               {
                   if (!token.equals(""))
                   {
                      tokens.add(token);
                      token="";
                   }
               }
           }
       }
       
       // Last token
       if (!token.equals(""))
       {
         tokens.add(token);
         token="";
       }
       
       // Return 
       return tokens;
    }
    
    public void executeSelect(ArrayList tokens) throws Exception
    {
        // Size ?
        if (tokens.size()<4)
           throw new Exception("Query string snytax");
        
        // Delete ?
        if (!tokens.get(0).toString().equals("SELECT"))
            throw new Exception("Query string snytax");
        
        // * ?
        if (!tokens.get(1).toString().equals("*"))
            throw new Exception("Query string snytax");
        
        // From ?
        if (!tokens.get(2).toString().equals("FROM"))
            throw new Exception("Query string snytax");
        
        // Table
        String tab=tokens.get(3).toString();
        
        // Valid
        if (!this.isTableName(tab))
           throw new Exception("Query string snytax error");
        
        // Sandbox ?
        String table="storage";
        if (VM.sandbox)
            table="storage_local";
        
       String query="SELECT * FROM "+table+" WHERE aID='"+VM.SYS.AGENT.GENERAL.aID+"' AND tab='"+tab+"'";
       if (tokens.size()==4)
       {
          // Execute
          ResultSet rs=UTILS.DB.executeQuery(query);
        
          // Load data
          this.dest.cel.copy(this.loadData(rs));
        
          return;
       }
       
       // From ?
       if (!tokens.get(4).toString().equals("WHERE"))
            throw new Exception("Query string snytax");
        
       // Columns
        String ops="";
        int a=5;
        while (a<=tokens.size()-1)
        {
           // Token
           String token=tokens.get(a).toString();
           
           if (!token.equals("AND") && !token.equals("OR")) 
               ops=ops+this.formatSelectOperator(token)+" ";
           else
               ops=ops+token+" ";
           
           a++;
        }
        
        // Query
        query=query+" AND "+ops;
        
        // Execute
        ResultSet rs=UTILS.DB.executeQuery(query);
        
        // Load data
        this.dest.cel.copy(this.loadData(rs));
    }
    
    public void executeInsert(ArrayList tokens) throws Exception
    {
        // Days
        if (this.dest.cel.getLong()<1)
            throw new Exception("Invalid days");
        
        // Size ?
        if (tokens.size()<5)
           throw new Exception("Query string snytax");
        
        // Insert ?
        if (!tokens.get(0).toString().equals("INSERT"))
            throw new Exception("Query string snytax");
        
        // Into ?
        if (!tokens.get(1).toString().equals("INTO"))
            throw new Exception("Query string snytax");
        
        // Table
        String tab=tokens.get(2).toString();
        
        // Valid
        if (!this.isTableName(tab))
           throw new Exception("Query string snytax");
        
        // Sandbox ?
        String table="storage";
        if (VM.sandbox)
            table="storage_local";
        
        // Set ?
        if (!tokens.get(3).toString().equals("SET"))
            throw new Exception("Query string snytax");
        
        // Columns
        String ops="";
        for (int a=4; a<=tokens.size()-1; a++)
           ops=ops+this.formatInsertOperator(tokens.get(a).toString())+", ";
        
        // Last comma
        ops=ops.substring(0, ops.length()-2);
        
        // Query
        String query="INSERT INTO "+table+" "
                           + "SET aID='"+VM.SYS.AGENT.GENERAL.aID+"', "
                               + "tab='"+tab+"', "
                               + "expire='"+(VM.block+this.dest.cel.getLong()*1440)+"', "
                               + "block='"+VM.block+"', "
                               + "rowhash='', ";
        
        // Attach operators
        query=query+ops;
        
         // Fee
         VM.CODE.fee=VM.CODE.fee+(0.00000001*query.length()*this.dest.cel.getLong());
        
        // Execute
        UTILS.DB.executeUpdate(query);
    }
    
    public void executeUpdate(ArrayList tokens) throws Exception
    {
        // Size ?
        if (tokens.size()<4)
           throw new Exception("Query string snytax");
        
        // Insert ?
        if (!tokens.get(0).toString().equals("UPDATE"))
            throw new Exception("Query string snytax");
        
        // Table
        String tab=tokens.get(1).toString();
        
        // Valid
        if (!this.isTableName(tab))
           throw new Exception("Query string snytax");
        
        // Sandbox ?
        String table="storage";
        if (VM.sandbox)
            table="storage_local";
        
        // Set ?
        if (!tokens.get(2).toString().equals("SET"))
            throw new Exception("Query string snytax");
        
        // Columns
        String ops="";
        int a=3;
        while (!tokens.get(a).toString().equals("WHERE") && a<=tokens.size()-1)
        {
           ops=ops+this.formatInsertOperator(tokens.get(a).toString())+", ";
           a++;
        }
        
        // Last comma
        ops=ops.substring(0, ops.length()-2);
        
        String query;
        
        // End ?
        if (a==tokens.size()-1)
        {
            query="UPDATE "+table+" SET "+ops+" WHERE aID='"+VM.SYS.AGENT.GENERAL.aID+"' AND tab='"+tab;
            UTILS.DB.executeUpdate(query);
            return;
        }
        
       query="UPDATE "+table+" SET "+ops+" WHERE aID='"+VM.SYS.AGENT.GENERAL.aID+"' AND tab='"+tab+"' AND ";
       
       // Columns
        ops="";
        int b=a+1;
        while (b<=tokens.size()-1)
        {
           String token=tokens.get(b).toString();
           if (!token.equals("AND") && !token.equals("OR")) 
               ops=ops+this.formatSelectOperator(token)+" ";
           else
               ops=ops+token+" ";
           b++;
        }
        
        // Query
        query=query+ops;
        
        // Execute
        UTILS.DB.executeUpdate(query);
    }
    
    public void executeDelete(ArrayList tokens) throws Exception
    {
        // Size ?
        if (tokens.size()<3)
           throw new Exception("Query string snytax");
        
        // Delete ?
        if (!tokens.get(0).toString().equals("DELETE"))
            throw new Exception("Query string snytax");
        
        // From ?
        if (!tokens.get(1).toString().equals("FROM"))
            throw new Exception("Query string snytax");
        
        // Table
        String tab=tokens.get(2).toString();
        
        // Valid
        if (!this.isTableName(tab))
           throw new Exception("Query string snytax");
        
        // Sandbox ?
        String table="storage";
        if (VM.sandbox)
            table="storage_local";
        
       String query="DELETE FROM "+table+" WHERE aID='"+VM.SYS.AGENT.GENERAL.aID+"' AND tab='"+tab+"'";
       if (tokens.size()==3)
       {
          UTILS.DB.executeUpdate(query);
          return;
       }
       
       // From ?
       if (!tokens.get(3).toString().equals("WHERE"))
            throw new Exception("Query string snytax");
       
       // Columns
        String ops="";
        int a=4;
        while (a<=tokens.size()-1)
        {
           // Token
           String token=tokens.get(a).toString();
           
           if (!token.equals("AND") && !token.equals("OR")) 
               ops=ops+this.formatSelectOperator(token)+" ";
           else
               ops=ops+token+" ";
           
           a++;
        }
        
        // Query
        query=query+" AND "+ops;
        
        // Execute
        UTILS.DB.executeUpdate(query);
    }
    
    public String formatInsertOperator(String token) throws Exception
    {
        // Comparator
        String comp;
        
        // First occurence of =
        int pos=token.indexOf("=");
        
        // Valid
        if (pos==-1) 
            throw new Exception("Query string snytax");
        
        // Column
        String col=token.substring(0, pos);
        
        // Column valid
        if (!col.equals("s1") && 
            !col.equals("s2") && 
            !col.equals("s3") && 
            !col.equals("s4") && 
            !col.equals("s5") &&
            !col.equals("s6") && 
            !col.equals("s7") && 
            !col.equals("s8") && 
            !col.equals("s9") && 
            !col.equals("s10") &&
            !col.equals("s11") && 
            !col.equals("s12") && 
            !col.equals("s13") && 
            !col.equals("s14") && 
            !col.equals("s15") &&
            !col.equals("s16") && 
            !col.equals("s17") && 
            !col.equals("s18") && 
            !col.equals("s19") && 
            !col.equals("s20") &&
            !col.equals("d1") && 
            !col.equals("d2") && 
            !col.equals("d3") && 
            !col.equals("d4") && 
            !col.equals("d5") && 
            !col.equals("d6") && 
            !col.equals("d7") && 
            !col.equals("d8") && 
            !col.equals("d9") && 
            !col.equals("d10") &&
            !col.equals("d11") && 
            !col.equals("d12") && 
            !col.equals("d13") && 
            !col.equals("d14") && 
            !col.equals("d15") &&
            !col.equals("d16") &&
            !col.equals("d17") && 
            !col.equals("d18") && 
            !col.equals("d19") && 
            !col.equals("d20"))
        throw new Exception("Query string snytax");
        
        // String
        String str=token.substring(pos+1, token.length()-1);
        
        // Is string 
        if (!this.isString(str))
            throw new Exception("Query string snytax");
        
        if (col.equals("s1") || 
            col.equals("s2") || 
            col.equals("s3") || 
            col.equals("s4") || 
            col.equals("s5") ||
            col.equals("s6") || 
            col.equals("s7") || 
            col.equals("s8") || 
            col.equals("s9") || 
            col.equals("s10") ||
            col.equals("s11") || 
            col.equals("s12") || 
            col.equals("s13") || 
            col.equals("s14") || 
            col.equals("s15") ||
            col.equals("s16") || 
            col.equals("s17") || 
            col.equals("s18") || 
            col.equals("s19") || 
            col.equals("s20"))
        {
            String s=str.replace("'", "");
            str=col+"='"+UTILS.BASIC.base64_encode(s)+"'";
        }
        
        else if (col.equals("d1") || 
                 col.equals("d2") || 
                 col.equals("d3") || 
                 col.equals("d4") || 
                 col.equals("d5") ||
                 col.equals("d6") || 
                 col.equals("d7") || 
                 col.equals("d8") || 
                 col.equals("d9") || 
                 col.equals("d10") ||
                 col.equals("d11") || 
                 col.equals("d12") || 
                 col.equals("d13") || 
                 col.equals("d14") || 
                 col.equals("d15") ||
                 col.equals("d16") || 
                 col.equals("d17") || 
                 col.equals("d18") || 
                 col.equals("d19") || 
                 col.equals("d20"))
        {
            String s=str.replace("'", "");
            
            if (!UTILS.BASIC.isNumber(s))
                throw new Exception("Query string snytax");
            
            str=col+"='"+s+"'";
        }
        
        return str;
    }
    
    public String formatSelectOperator(String token) throws Exception
    {
        // Comparator
        String comp;
        
        // Valid separator ?
        if (token.indexOf("=")==-1 && 
            token.indexOf("<=")==-1 && 
            token.indexOf(">=")==-1 && 
            token.indexOf("<>")==-1)
        throw new Exception("Query string snytax");
        
        // First occurence
        int pos=0;
        String sep="";
        
        if (token.indexOf("<=")!=-1)
        {
            pos=token.indexOf("<=");
            sep="<=";
        }
        else if (token.indexOf(">=")!=-1)
        {
            pos=token.indexOf(">=");
            sep=">=";
        }
        else if (token.indexOf("<>")!=-1)
        {
            pos=token.indexOf("<>");
            sep="<>";
        }
        else  if (token.indexOf("=")!=-1)
        {
            pos=token.indexOf("=");
            sep="=";
        }
        
        // Column
        String col=token.substring(0, pos);
        
        // String
        String str="";
        if (sep.equals("<>") || 
            sep.equals("<=") || 
            sep.equals(">="))
            str=token.substring(pos+2, token.length());
        else
            str=token.substring(pos+1, token.length());
        
        // Column valid
        if (!col.equals("s1") && 
            !col.equals("s2") && 
            !col.equals("s3") && 
            !col.equals("s4") && 
            !col.equals("s5") &&
            !col.equals("s6") && 
            !col.equals("s7") && 
            !col.equals("s8") && 
            !col.equals("s9") && 
            !col.equals("s10") &&
            !col.equals("s11") && 
            !col.equals("s12") && 
            !col.equals("s13") && 
            !col.equals("s14") && 
            !col.equals("s15") &&
            !col.equals("s16") && 
            !col.equals("s17") && 
            !col.equals("s18") && 
            !col.equals("s19") && 
            !col.equals("s20") &&
            !col.equals("d1") && 
            !col.equals("d2") && 
            !col.equals("d3") && 
            !col.equals("d4") && 
            !col.equals("d5") &&
            !col.equals("d6") && 
            !col.equals("d7") && 
            !col.equals("d8") && 
            !col.equals("d9") && 
            !col.equals("d10") &&
            !col.equals("d11") && 
            !col.equals("d12") && 
            !col.equals("d13") && 
            !col.equals("d14") && 
            !col.equals("d15") &&
            !col.equals("d16") && 
            !col.equals("d17") && 
            !col.equals("d18") && 
            !col.equals("d19") && 
            !col.equals("d20"))
        throw new Exception("Query string snytax");
        
        if (col.equals("s1") ||
            col.equals("s2") ||
            col.equals("s3") ||
            col.equals("s4") ||
            col.equals("s5") ||
            col.equals("s6") ||
            col.equals("s7") ||
            col.equals("s8") ||
            col.equals("s9") ||
            col.equals("s10") ||
            col.equals("s11") ||
            col.equals("s12") ||
            col.equals("s13") ||
            col.equals("s14") ||
            col.equals("s15") ||
            col.equals("s16") ||
            col.equals("s17") ||
            col.equals("s18") ||
            col.equals("s19") ||
            col.equals("s20"))
        {
            if (sep.equals("<=") ||
                sep.equals(">="))
            throw new Exception("Query string snytax");
            
            if (!this.isString(str))
                throw new Exception("Query string snytax");
        }
        
        if (col.equals("s1") ||
            col.equals("s2") ||
            col.equals("s3") ||
            col.equals("s4") ||
            col.equals("s5") ||
            col.equals("s6") ||
            col.equals("s7") ||
            col.equals("s8") ||
            col.equals("s9") ||
            col.equals("s10") ||
            col.equals("s11") ||
            col.equals("s12") ||
            col.equals("s13") ||
            col.equals("s14") ||
            col.equals("s15") ||
            col.equals("s16") ||
            col.equals("s17") ||
            col.equals("s18") ||
            col.equals("s19") ||
            col.equals("s20"))
        {
            String s=str.replace("'", "");
            str=col+sep+"'"+UTILS.BASIC.base64_encode(s)+"'";
        }
        
        else if (col.equals("d1") ||
                 col.equals("d2") ||
                 col.equals("d3") ||
                 col.equals("d4") ||
                 col.equals("d5") ||
                 col.equals("d6") ||
                 col.equals("d7") ||
                 col.equals("d8") ||
                 col.equals("d9") ||
                 col.equals("d10") ||
                 col.equals("d11") ||
                 col.equals("d12") ||
                 col.equals("d13") ||
                 col.equals("d14") ||
                 col.equals("d15") ||
                 col.equals("d16") ||
                 col.equals("d17") ||
                 col.equals("d18") ||
                 col.equals("d19") ||
                 col.equals("d20"))
        {
            String s=str.replace("'", "");
            
            if (!UTILS.BASIC.isNumber(s))
                throw new Exception("Query string snytax");
            
            str=col+sep+s;
        }
        
        return str;
    }
    
    public boolean isString(String str)
    {
        if (str.charAt(0)!='\'' || 
            str.charAt(str.length()-1)!='\'')
            return false;
        else
            return true;
    }
    
    public boolean isTableName(String table)
    {
       if (!table.matches("^[a-z0-9]{0,30}$"))
            return false;
	else 
	    return true;
    }
    
    public CCell loadData(ResultSet rs) throws Exception
    {
       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // aID
       CCell colAID=new CCell("");
       colAID.name="aID";

       // Tab
       CCell colTab=new CCell("");
       colTab.name="tab";

       // S1...S10
       CCell colS1=new CCell("");
       colS1.name="s1";
       
       CCell colS2=new CCell("");
       colS2.name="s2";
       
       CCell colS3=new CCell("");
       colS3.name="s3";
       
       CCell colS4=new CCell("");
       colS4.name="s4";
       
       CCell colS5=new CCell("");
       colS5.name="s5";
       
       CCell colS6=new CCell("");
       colS6.name="s6";
       
       CCell colS7=new CCell("");
       colS7.name="s7";
       
       CCell colS8=new CCell("");
       colS8.name="s8";
       
       CCell colS9=new CCell("");
       colS9.name="s9";
       
       CCell colS10=new CCell("");
       colS10.name="s10";
       
       CCell colS11=new CCell("");
       colS11.name="s11";
       
       CCell colS12=new CCell("");
       colS12.name="s12";
       
       CCell colS13=new CCell("");
       colS13.name="s13";
       
       CCell colS14=new CCell("");
       colS14.name="s14";
       
       CCell colS15=new CCell("");
       colS15.name="s15";
       
       CCell colS16=new CCell("");
       colS16.name="s16";
       
       CCell colS17=new CCell("");
       colS17.name="s17";
       
       CCell colS18=new CCell("");
       colS18.name="s18";
       
       CCell colS19=new CCell("");
       colS19.name="s19";
       
       CCell colS20=new CCell("");
       colS20.name="s20";
       
       // D1...D10
       CCell colD1=new CCell("");
       colD1.name="d1";
       
       CCell colD2=new CCell("");
       colD2.name="d2";
       
       CCell colD3=new CCell("");
       colD3.name="d3";
       
       CCell colD4=new CCell("");
       colD4.name="d4";
       
       CCell colD5=new CCell("");
       colD5.name="d5";
       
       CCell colD6=new CCell("");
       colD6.name="d6";
       
       CCell colD7=new CCell("");
       colD7.name="d7";
       
       CCell colD8=new CCell("");
       colD8.name="d8";
       
       CCell colD9=new CCell("");
       colD9.name="d9";
       
       CCell colD10=new CCell("");
       colD10.name="d10";
       
       CCell colD11=new CCell("");
       colD11.name="d11";
       
       CCell colD12=new CCell("");
       colD12.name="d12";
       
       CCell colD13=new CCell("");
       colD13.name="d13";
       
       CCell colD14=new CCell("");
       colD14.name="d14";
       
       CCell colD15=new CCell("");
       colD15.name="d15";
       
       CCell colD16=new CCell("");
       colD16.name="d16";
       
       CCell colD17=new CCell("");
       colD17.name="d17";
       
       CCell colD18=new CCell("");
       colD18.name="d18";
       
       CCell colD19=new CCell("");
       colD19.name="d19";
       
       CCell colD20=new CCell("");
       colD20.name="d20";
       
       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getLong("ID")));

                     // AID
                     colAID.addCell(new CCell(rs.getLong("aID")));

                     // Tab
                     colTab.addCell(new CCell(rs.getString("tab")));

                     // S1...S10
                     colS1.addCell(new CCell(rs.getString("s1")));
                     colS2.addCell(new CCell(rs.getString("s2")));
                     colS3.addCell(new CCell(rs.getString("s3")));
                     colS4.addCell(new CCell(rs.getString("s4")));
                     colS5.addCell(new CCell(rs.getString("s5")));
                     colS6.addCell(new CCell(rs.getString("s6")));
                     colS7.addCell(new CCell(rs.getString("s7")));
                     colS8.addCell(new CCell(rs.getString("s8")));
                     colS9.addCell(new CCell(rs.getString("s9")));
                     colS10.addCell(new CCell(rs.getString("s10")));
                     colS11.addCell(new CCell(rs.getString("s11")));
                     colS12.addCell(new CCell(rs.getString("s12")));
                     colS13.addCell(new CCell(rs.getString("s13")));
                     colS14.addCell(new CCell(rs.getString("s14")));
                     colS15.addCell(new CCell(rs.getString("s15")));
                     colS16.addCell(new CCell(rs.getString("s16")));
                     colS17.addCell(new CCell(rs.getString("s17")));
                     colS18.addCell(new CCell(rs.getString("s18")));
                     colS19.addCell(new CCell(rs.getString("s19")));
                     colS20.addCell(new CCell(rs.getString("s20")));
                     
                     // D1...D10
                     colD1.addCell(new CCell(rs.getDouble("d1")));
                     colD2.addCell(new CCell(rs.getDouble("d2")));
                     colD3.addCell(new CCell(rs.getDouble("d3")));
                     colD4.addCell(new CCell(rs.getDouble("d4")));
                     colD5.addCell(new CCell(rs.getDouble("d5")));
                     colD6.addCell(new CCell(rs.getDouble("d6")));
                     colD7.addCell(new CCell(rs.getDouble("d7")));
                     colD8.addCell(new CCell(rs.getDouble("d8")));
                     colD9.addCell(new CCell(rs.getDouble("d9")));
                     colD10.addCell(new CCell(rs.getDouble("d10")));
                     colD11.addCell(new CCell(rs.getDouble("d11")));
                     colD12.addCell(new CCell(rs.getDouble("d12")));
                     colD13.addCell(new CCell(rs.getDouble("d13")));
                     colD14.addCell(new CCell(rs.getDouble("d14")));
                     colD15.addCell(new CCell(rs.getDouble("d15")));
                     colD16.addCell(new CCell(rs.getDouble("d16")));
                     colD17.addCell(new CCell(rs.getDouble("d17")));
                     colD18.addCell(new CCell(rs.getDouble("d18")));
                     colD19.addCell(new CCell(rs.getDouble("d19")));
                     colD20.addCell(new CCell(rs.getDouble("d20")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // betID
       c.addCell(colAID);

       // Tab
       c.addCell(colTab);

       // S1....S10
       c.addCell(colS1);
       c.addCell(colS2);
       c.addCell(colS3);
       c.addCell(colS4);
       c.addCell(colS5);
       c.addCell(colS6);
       c.addCell(colS7);
       c.addCell(colS8);
       c.addCell(colS9);
       c.addCell(colS10);
       c.addCell(colS11);
       c.addCell(colS12);
       c.addCell(colS13);
       c.addCell(colS14);
       c.addCell(colS15);
       c.addCell(colS16);
       c.addCell(colS17);
       c.addCell(colS18);
       c.addCell(colS19);
       c.addCell(colS20);
       
       // D1....D10
       c.addCell(colD1);
       c.addCell(colD2);
       c.addCell(colD3);
       c.addCell(colD4);
       c.addCell(colD5);
       c.addCell(colD6);
       c.addCell(colD7);
       c.addCell(colD8);
       c.addCell(colD9);
       c.addCell(colD10);
       c.addCell(colD11);
       c.addCell(colD12);
       c.addCell(colD13);
       c.addCell(colD14);
       c.addCell(colD15);
       c.addCell(colD16);
       c.addCell(colD17);
       c.addCell(colD18);
       c.addCell(colD19);
       c.addCell(colD20);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }
    
    
}
