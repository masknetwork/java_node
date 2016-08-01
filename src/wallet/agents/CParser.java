package wallet.agents;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.general.CBLANK;
import wallet.kernel.UTILS;

public class CParser 
{
    // Source code
    public CSource src;
    
    // Source code
    public ArrayList<String> scode=new ArrayList<String>();
    
    // Line tokens
    private ArrayList<CToken> tokens=new ArrayList<CToken>();
    
    // Currenct token
    private CToken token;
    
    // Line number
    int line_no=1;
    
    // Grammar
    CGrammar GRAMMAR;
    
    // Virtual machine
    VM VM;
    
    public CParser(CGrammar grammar, VM VM)
    {
        // Init
        this.src=new CSource();
        
        // Blank line
        this.scode.add("");
        
        // Grammar
        this.GRAMMAR=grammar;
        
        // Virtual machine
        this.VM=VM;
    }
    
    public String parse(boolean sandbox) throws Exception
    {
        try
        {
           // Next line
           String next_line;
        
           // Preparse
           while (!this.src.eof)
           {
               // Load tags
               this.loadTags(src.nextLine());
            
               // Line number
               line_no++;
           }
       
           // Reset
           this.src.reset();
        
           // Lin no
           this.line_no=1;
        
           // Parse
           while (!this.src.eof)
           {
              this.parseLine(src.nextLine(), sandbox);
              line_no++;
           }
        
           // Sandbox ?
           if (sandbox)
               UTILS.DB.executeUpdate("UPDATE agents_mine "
                                       + "SET compiler='SURfT0s=' "
                                     + "WHERE ID='"+VM.agentID+"'");
        
           // Ok
           return "ID_OK";
        }
        catch (Exception ex)
        {
            // Sandbox ?
            if (sandbox)
            UTILS.DB.executeUpdate("UPDATE agents_mine "
                                    + "SET compiler='"+UTILS.BASIC.base64_encode(ex.getMessage()+" on line "+line_no)+"' "
                                  + "WHERE ID='"+VM.agentID+"'");
            
            // Throw exception
            throw new Exception(ex.getMessage()+" (line "+line_no+")");
        }
    }
    
    // Preparse
    public void loadTags(String line) throws Exception
    {
        // Val
        String val;
        
        // Trim
        line=line.trim();
        
        // Blank ?
        if (line.length()<2) return;
        
        // Tag ?
        if (line.charAt(0)=='#' && line.charAt(line.length()-1)=='#')
        {
            // Add line
            VM.TAGS.add(line, this.line_no);
            
            // Replace tag
            for (int a=0; a<=src.lines.size()-1; a++)
            {
                if (((String)this.src.lines.get(a)).indexOf(line)>0)
                {
                  val=((String)this.src.lines.get(a)).replace(line, String.valueOf(this.line_no));
                  this.src.lines.set(a, val);
                }
                else if (((String)this.src.lines.get(a)).indexOf(line)==0 && (line_no-1)!=a)
                    throw new Exception ("Duplicated tag found at line "+line_no);
            }
        }
        
    }
    
   
    public void parseLine(String line, boolean sandbox) throws Exception
    {
        // Initial line
        String iLine=line;
        
        // Current word
        String cword="";
        
        // In string ?
        boolean in_string=false;
        boolean in_exp=false;
        
        // Trim
        line=line.trim();
        
        // Blank or comment line
        if (line.length()<3 || line.charAt(0)=='/' || line.charAt(0)=='#')
        {
            // Add a blank line
            this.scode.add("");
            
            // Blank line
            VM.CODE.code.add(new CBLANK(VM));
            
            // Return
            return;
        }
        
        // First word
        String fw[]=line.split(" ");
        
        // First token
        this.tokens.add(new CToken(VM, fw[0]));
        
        // Remove first token
        line=line.substring(fw[0].length());
        
        // Trim
        line=line.trim();
        
        
        for (int a=0; a<=line.length()-1; a++)
        {
            if (in_string==false && in_exp==false)
            {
               if (line.charAt(a)!=',' && 
                   line.charAt(a)!=' ')
               {
                   // Current word
                   cword=cword+line.charAt(a);
                   
                   // String open ?
                   if (line.charAt(a)=='"')
                   {
                       if (in_string==false) 
                           in_string=true;
                       else
                           in_string=false;
                   }
                   
                   if (line.charAt(a)=='(')
                       in_exp=true;
               }
               else
               {
                   // Add if comma
                   if (line.charAt(a)==',')
                   {
                       CToken t=new CToken(VM, cword);
                       if (cword.length()>0) this.tokens.add(t);
                       this.tokens.add(new CToken(VM, ","));
                   }
                   
                   // Add token
                   else if (line.charAt(a)!=' ') 
                       this.tokens.add(new CToken(VM, cword));
                   
                   // reset word
                   cword="";
               }
            }
            else 
            {
                // Current word
                cword=cword+line.charAt(a);
                
                // End of string
                if ((line.charAt(a)=='"' && in_string==true) || 
                    (line.charAt(a)==')' && in_exp==true)) 
                {
                    if (in_string==true) in_string=false;
                    if (in_exp==true) in_exp=false;
                    this.tokens.add(new CToken(VM, cword));
                    cword="";
                }
            }
        }
        
        // Token
        if (in_string==true)
        {
            if (cword.charAt(cword.length()-1)!='"')
               throw new Exception("Invalid string");
            else 
               this.tokens.add(new CToken(VM, cword));
        }
        else
        {
            CToken t=new CToken(VM, cword);
            if (!t.type.equals("")) this.tokens.add(t);
        }
        
        // All tokens recognized
        for (int a=0; a<=this.tokens.size()-1; a++)
        {
            String type=this.tokens.get(a).type;
            
            if (type.equals("ID_ERR"))
                throw new Exception("Invalid token : "+this.tokens.get(a).ins);
        }
        
        // Load instruction
        VM.CODE.push(tokens);
        for (int a=0; a<=this.tokens.size()-1; a++)
        {
            String type=this.tokens.get(a).type;
            System.out.print(type+", ");
        }
        
        System.out.println("");
        
        // Check tokens
        if (!this.GRAMMAR.check(tokens))
            throw new Exception("Syntax error on line "+this.line_no+" "+tokens.get(0).ins);
        
        // Reset tokens
        this.tokens.clear();
        
        // Add to source code
        this.scode.add(iLine);
    }
   
    public void loadCode(long aID, boolean sandbox) throws Exception
    {
       // Send to source
       this.src.loadSource(aID, sandbox);
    }
}
