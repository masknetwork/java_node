package wallet.agents.VM.instructions.flow;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;

public class CIF extends CInstruction
{
    // Expression
    CToken exp;
    
    // Instruction
    CToken ins;
    
    // Instruction par 1
    CToken par_1=null;
    
    // Instruction par 2
    CToken par_2=null;
    
    // Instruction par 3
    CToken par_3=null;
    
    // Instruction par 4
    CToken par_4=null;
    
    // Instruction par 5
    CToken par_5=null;
    
    // Comp
    String comp="";
    
    public CIF(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "IF");
        
        // Compare expresion
        this.exp=tokens.get(1);
        
        // Instruction
        this.ins=tokens.get(3);
        
        // Instruction par 1
        if (tokens.size()>5) this.par_1=tokens.get(5);
        
        // Instruction par 2
        if (tokens.size()>7) this.par_2=tokens.get(7);
        
        // Instruction par 3
        if (tokens.size()>9) this.par_3=tokens.get(9);
        
        // Instruction par 4
        if (tokens.size()>11) this.par_4=tokens.get(11);
        
        // Instruction par 5
        if (tokens.size()>13) this.par_5=tokens.get(13);
        
     
        
        // Expression value
        String val=this.exp.cel.val;
        
        // Exp array
        String[] ea=null;
        
        // Check expression
        if (val.indexOf("<")==-1 && 
            val.indexOf(">")==-1 && 
            val.indexOf("==")==-1 && 
            val.indexOf("!=")==-1 && 
            val.indexOf("<=")==-1 && 
            val.indexOf(">=")==-1)
        throw new Exception ("Invalid compare expression"); 
       
    }
   
    public  void execute() throws Exception
    {
        // Line
        long line=VM.REGS.RCI;
        
        // Matches
        boolean match=false;
        boolean match_1=false;
        boolean match_2=false;
        boolean match_3=false;
        boolean match_4=false;
        boolean match_5=false;
        
        // Expressions
        String exp_1="";
        String exp_2="";
        String exp_3="";
        String exp_4="";
        String exp_5="";
        
        this.comp="";
        
        // Evaluate expression 1
        String v[];
        String c="";
        
        if (this.exp.cel.val.indexOf("&&")>0) 
        {
            v=this.exp.cel.val.split("&&");
            c="&&";
        }
        else if (this.exp.cel.val.indexOf("||")>0) 
        {
            v=this.exp.cel.val.split("\\|\\|");
            c="||";
        }
        else 
        {
            v=new String[1];
            v[0]=this.exp.cel.val;
            c="&&";
        }
            
        
        // Load expressions
        if (v.length==0) throw new Exception("Invalid expression");
        if (v.length>0) exp_1=v[0];
        if (v.length>1) exp_2=v[1];
        if (v.length>2) exp_3=v[2];
        if (v.length>3) exp_4=v[3];
        if (v.length>4) exp_5=v[4];
        
        // Evaluate
        if (exp_1!="") match_1=this.eval(exp_1);
        if (exp_2!="") match_2=this.eval(exp_2);
        if (exp_3!="") match_3=this.eval(exp_3);
        if (exp_4!="") match_4=this.eval(exp_4);
        if (exp_5!="") match_5=this.eval(exp_5);
        
        if (c.equals("&&"))
        {
           // Match ?
           if (exp_1!="" && exp_2=="" && exp_3=="" && exp_4=="" && exp_5=="" && 
               match_1==true) 
           match=true; 
        
           if (exp_1!="" && exp_2!="" && exp_3=="" && exp_4=="" && exp_5=="" && 
               match_1==true && match_2==true) 
           match=true;
        
           if (exp_1!="" && exp_2!="" && exp_3!="" && exp_4=="" && exp_5=="" && 
            match_1==true && match_2==true && match_3==true) 
           match=true;
        
           if (exp_1!="" && exp_2!="" && exp_3!="" && exp_4!="" && exp_5=="" && 
               match_1==true && match_2==true && match_3==true && match_4==true) 
           match=true;
        
           if (exp_1!="" && exp_2!="" && exp_3!="" && exp_4!="" && exp_5!="" && 
               match_1==true && match_2==true && match_3==true && match_4==true && match_5==true) 
           match=true; 
        }
        else
        {
            // Match ?
           if (exp_1!="" && exp_2=="" && exp_3=="" && exp_4=="" && exp_5=="" && 
               match_1==true) 
           match=true; 
        
           if (exp_1!="" && exp_2!="" && exp_3=="" && exp_4=="" && exp_5=="" && 
               match_1==true || match_2==true) 
           match=true;
        
           if (exp_1!="" && exp_2!="" && exp_3!="" && exp_4=="" && exp_5=="" && 
            match_1==true || match_2==true || match_3==true) 
           match=true;
        
           if (exp_1!="" && exp_2!="" && exp_3!="" && exp_4!="" && exp_5=="" && 
               match_1==true || match_2==true || match_3==true || match_4==true) 
           match=true;
        
           if (exp_1!="" && exp_2!="" && exp_3!="" && exp_4!="" && exp_5!="" && 
               match_1==true || match_2==true || match_3==true || match_4==true || match_5==true) 
           match=true; 
        }
        
        
        
        // Execute
        if (match)
        {
            // Instruction parameters
            ArrayList<CToken>itokens=new ArrayList<CToken>();
            
            // Blank
            itokens.add(null);
            
            // Parameter 1
            if (this.par_1!=null) itokens.add(this.par_1);
            
            // Parameter 2
            if (this.par_2!=null) { itokens.add(null); itokens.add(this.par_2); }
            
            // Parameter 3
            if (this.par_3!=null) { itokens.add(null); itokens.add(this.par_3); }
            
            // Parameter 4
            if (this.par_4!=null) { itokens.add(null); itokens.add(this.par_4); }
            
            // Parameter 5
            if (this.par_5!=null) { itokens.add(null); itokens.add(this.par_5); }
            
            // Get instruction
            CInstruction i=VM.CODE.getIns(this.ins.ins, itokens);
            
            // Execute
            i.execute();
        }
        
        
    }
    
    public boolean eval(String val) throws Exception
    {
        // Exp array
        String[] ea=null;
        
        // Operator
        String op="";
        
        // Check expression
        if (val.indexOf(">=")>0)
        {
            ea=val.split(">=");
            op=">=";
        }
        else if (val.indexOf("<=")>0)
        {
            ea=val.split("<=");
            op="<=";
        }
        else if (val.indexOf("<")>0)
        {
            ea=val.split("<");
            op="<";
        }
        else if (val.indexOf(">")>0)
        {
            ea=val.split(">");
            op=">";
        }
        else if (val.indexOf("==")>0)
        {
            ea=val.split("==");
            op="==";
        }
        else if (val.indexOf("!=")>0)
        {
            ea=val.split("!=");
            op="!=";
        }
        
        // Operator 1
        String op_1=ea[0];
        
        // Operator 2
        String op_2=ea[1];
        
        // Trim
        op_1=op_1.trim();
        op_2=op_2.trim();
        
        // Cells
        CCell c1;
        CCell c2;
        
        
        // Operator 1 type
        if (VM.vm_utils.isDouble(op_1) || 
            VM.vm_utils.isLong(op_1) || 
            (op_1.startsWith("\"") && op_1.endsWith("\"")))
            c1=new CCell(op_1.replace("\"", ""));
        
        else if (VM.vm_utils.isRegister(op_1))
            c1=new CCell(VM.REGS.getCell(op_1));
        
        else c1=new CCell(VM.MEM.getVar(op_1));
        
        // Operator 2 type
        if (VM.vm_utils.isDouble(op_2) || 
            VM.vm_utils.isLong(op_2) ||
            (op_2.startsWith("\"") && op_2.endsWith("\"")))
            c2=new CCell(op_2.replace("\"", ""));
        
        else if (VM.vm_utils.isRegister(op_2))
            c2=new CCell(VM.REGS.getCell(op_2));
        
        else c2=new CCell(VM.MEM.getVar(op_2));
        
        // Comp
        this.comp=this.comp+c1.val+op+c2.val+" ";
        
        // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "IF "+this.comp);
        
        // Compare
        int res=c1.compare(c2);
        
        // Match
        boolean match=false;
        
        // Compare
        switch (op)
        {
            case "<" : if (res==-1) match=true; 
                       break;
                       
            case ">" : if (res==1) match=true; 
                       break;
                       
            case "==" : if (res==0) match=true; 
                       break;
                       
            case "<=" : if (res==-1 || res==0) match=true; 
                       break;
                       
            case ">=" : if (res==1 || res==0) match=true; 
                       break;
                       
            case "!=" : if (res==-1 || res==1) match=true; 
                       break;
        }
        
        return match;
    }

}
