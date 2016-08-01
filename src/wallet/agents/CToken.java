package wallet.agents;

import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;

public class CToken 
{
    // Token type
    public String type="";
    
    // Value
    public String ins="";
    
    // Token type
    public CCell cel=null;
    
    // Virtual machine
    VM VM;
    
    public  CToken(VM VM, String token) throws Exception
    {
         // Trim
         token.trim();
         
         // Length
         if (token.length()==0) return;
                 
         // Register
        if (VM.vm_utils.isRegister(token))
        {
            this.type="ID_REG";
            this.cel=VM.REGS.getCell(token);
        }
        
        // Instruction
        else if (VM.vm_utils.isInstruction(token))
        {
            this.type="ID_INS";
            this.ins=token;
        }
        
        // Long
        else if (VM.vm_utils.isLong(token))
        {
            this.type="ID_LONG";
            this.cel=new CCell(Long.parseLong(token));
        }
        
        // Douuble
        else if (VM.vm_utils.isDouble(token))
        {
            this.type="ID_DOUBLE";
            this.cel=new CCell(Double.parseDouble(token));
        }
        
        // Comma ?
        else if (token.equals(","))
            this.type="ID_COMMA";
        
        // String ?
        else if (token.charAt(0)=='"')
        {
            this.type="ID_STRING";
            this.cel=new CCell(token.replace("\"", ""));
            this.cel.type="ID_STRING";
        }
        
        // Asterix
        else if (token.equals("*"))
        {
               this.type="ID_ASTERIX";
               this.cel=new CCell(token);
        }
        
        // Complex variable
        else if (token.indexOf(".")>0 && 
                 token.indexOf("[")>0 && 
                 token.indexOf("]")>0)
        {
               this.type="ID_COMPLEX_VAR";
               this.cel=new CCell(token);
        }
        
        else if (token.charAt(0)=='@')
        {
            this.type="ID_GLOBAL";
            this.cel=VM.SYS.AGENT.VARS.getData(token.replace("@", ""));
        }
        
        else if (token.charAt(0)=='$')
        {
            this.type="ID_VAR";
            this.cel=VM.MEM.getVar(token);
        }
        
        // Comparator
        else if (token.equals("<") || 
                 token.equals("<=") || 
                 token.equals("==") || 
                 token.equals(">") || 
                 token.equals(">=") ||
                 token.equals("!="))
        {
            this.type="ID_COMPARE";
            this.cel=new CCell(token);
        }
        
        // Compare expression ?
        else if (token.charAt(0)=='(' && token.endsWith(")"))
        {
            if (token.indexOf("<")>0 || 
                 token.indexOf("<=")>0 || 
                 token.indexOf("==")>0 || 
                 token.indexOf(">")>0 || 
                 token.indexOf(">=")>0 ||
                 token.indexOf("!=")>0)
           {
              this.type="ID_COMPARE_EXP";
              token=token.replace("(", "");
              token=token.replace(")", "");
              this.cel=new CCell(token);
           }
            else throw new Exception("Invalid expression "+token);
        }
        
        // Size bigger than 4 ?
        else if (token.length()>4)
        {
            if (token.substring(0, 4).equals("SYS."))
            {
                   this.type="ID_SYS";
                   this.cel=new CCell(VM.SYS.getVar(token));
            }
            else throw new Exception ("Undefined symbol "+token);
        }
        
        else 
            throw new Exception ("Undefined symbol "+token);
        }
}
