package wallet.agents.VM;

import wallet.agents.VM.instructions.strings.CCONCAT;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.kernel.UTILS;
import wallet.agents.VM.instructions.*;
import wallet.agents.VM.instructions.feeds.*;
import wallet.agents.VM.instructions.flow.*;
import wallet.agents.VM.instructions.general.*;
import wallet.agents.VM.instructions.hash.CHASH;
import wallet.agents.VM.instructions.math.*;
import wallet.agents.VM.instructions.strings.*;
import wallet.agents.VM.instructions.storage.*;
import wallet.agents.VM.instructions.sys_calls.*;
import wallet.agents.VM.instructions.stack.*;

public class COpCode 
{
    // Op codes
    public ArrayList<CInstruction> code;
    
    // Steps
    public long steps=0;
    
    // Line
    public long lines=0;
    
    // Exit
    public boolean exit=false;
    
    // Cost
    public double fee=0;
    
    // Virtual machine
    public VM VM;
    
    public COpCode(VM VM)
    {
       // Create instruction list
       this.code=new ArrayList<CInstruction>();
       
       // VM
       this.VM=VM;
       
       // Add blank line
       this.code.add(new CBLANK(VM));
    }
    
    public CInstruction getIns(String name, ArrayList<CToken> tokens) throws Exception
    {
        CInstruction i=null;
        
        switch (name.toUpperCase())
        {
            case "MOV" : i=new CMOV(VM, tokens); break;
            case "MATH" : i=new CMATH(VM, tokens); break;
            case "ADD" : i=new CADD(VM, tokens); break;
            case "MUL" : i=new CMUL(VM, tokens); break;
            case "DIV" : i=new CDIV(VM, tokens); break;
            case "SUB" : i=new CSUB(VM, tokens); break;
            case "GOTO" : i=new CGOTO(VM, tokens); break;
            case "SIZE" : i=new CSIZE(VM, tokens); break;
            case "LOG" : i=new CLOG(VM, tokens); break;
            case "INDEXOF" : i=new CINDEXOF(VM, tokens); break;
            case "LASTCH" : i=new CLASTCH(VM, tokens); break;
            case "REPLACE" : i=new CREPLACE(VM, tokens); break;
            case "SPLIT" : i=new CSPLIT(VM, tokens); break;
            case "SUBSTR" : i=new CSUBSTR(VM, tokens); break;
            case "TRIM" : i=new CTRIM(VM, tokens); break;
            case "REGEX" : i=new CREGEX(VM, tokens); break;
            case "REJECT" : i=new CREJECT(VM, tokens);  break;
            case "ATPOS" : i=new CATPOS(VM, tokens); break;
            case "IF" : i=new CIF(VM, tokens); break;
            case "HTTP" : i=new CHTTP(VM, tokens); break;
            case "STORAGE" : i=new CSTORAGE (VM, tokens); break; 
            case "API" : i=new CAPI (VM, tokens); break;       
            case "CONCAT" : i=new CCONCAT(VM, tokens); break;
            case "TWEET" : i=new CTWEET(VM, tokens); break;
            case "CALL" : i=new CCALL(VM, tokens); break;
            case "GETFEED" : i=new CGETFEED(VM, tokens); break;
            case "ADDFEED" : i=new CADDFEED(VM, tokens); break;
            case "RET" : i=new CRET(VM, tokens); break;
            case "PUSH" : i=new CPUSH(VM, tokens); break;
            case "POP" : i=new CPOP(VM, tokens); break;
            case "MES" : i=new CMES(VM, tokens); break;      
            case "SEAL" : i=new CSEAL(VM, tokens); break;      
            case "TRANS" : i=new CTRANS(VM, tokens); break;     
            case "LISTPUSH" : i=new CLISTPUSH(VM, tokens); break;     
            case "EXIT" : i=new CEXIT(VM, tokens); break;
            case "BLANK" : i=new CBLANK(VM); break;
            case "TOSTRING" : i=new CTOSTRING(VM, tokens); break;
            case "RAND" : i=new CRAND(VM, tokens); break;
            case "HASH" : i=new CHASH(VM, tokens);  break;
            case "EMAIL" : i=new CEMAIL(VM, tokens); break;
            case "NAMETOADR" : i=new CEMAIL(VM, tokens); break;
            case "ADRVALID" : i=new CEMAIL(VM, tokens); break;
        }
        
        // Return
        return i;
    }
    
    public void push(ArrayList<CToken> tokens) throws Exception
    {
        // Load instruction
        CInstruction i=this.getIns((String)tokens.get(0).ins.toUpperCase(), tokens);
        
        // Add to execution
        this.code.add(i);
    }
    
    public void execute(long start) throws Exception
    {
        // Lines number
        this.lines=this.code.size()-1;
        
        // Exit
        exit=false;
        
        // Balance
        double balance=VM.SYS.AGENT.GENERAL.balance;
        
        try
        {
           // Code index
            if (start==0)
              VM.REGS.RCI=VM.TAGS.getLine("#begin#");
            else
              VM.REGS.RCI=start;
                
           // Instruction
           CInstruction i;
        
           while (VM.REGS.RCI<this.code.size()-1 && 
                  this.steps<10000 &&
                  !exit)
           {
               // Increase instruction pointer
               VM.REGS.RCI++;
               
               // Steps
               this.steps++;
              
               // Load instruction Execute
               i=this.code.get(new Long(VM.REGS.RCI).intValue());
               
               // Execute
               i.execute();
               
               // Fee
               this.fee=this.fee+0.00000001*this.steps;
               
               // Out of funds ?
               if (balance<fee)
                   throw new Exception("App run out of funds");
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Error on line "+VM.REGS.RCI+" ("+ex.getMessage()+")");
        }
        
    }
    
    public void newTrans() throws Exception
    {
        this.execute(VM.TAGS.getLine("#transaction#"));  
    }        
    
    public void exit(CCell cel)
    {
        this.exit=true;
    }
}
