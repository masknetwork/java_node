package wallet.agents;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.kernel.UTILS;

public class CSource 
{
    // Index
    int index=-1;
    
    // Line number
    long line_no=0;
    
    // EOF
    boolean eof=false;
    
    // Buffered reader
    BufferedReader br;
    
    // Lines
    ArrayList lines=new ArrayList();
    
    public CSource()
    {
       
    }
    
    public String nextLine() throws Exception
    {
        // Index
        index++;
        
        // EOF
        if (index==this.lines.size()) 
        {
            this.eof=true;
            return "";
        }
        
        // New line
        String line = this.lines.get(index).toString();
        
        // Blank line ?
        if (line.trim().length()<3) return " ";
        
        // Return
        return line;
    }
    
    public void reset()
    {
       index=-1;
       this.eof=false;
    }
    
    public void loadSource(long aID, boolean sandbox) throws Exception
    {
         // Line 
       String strLine="";
       
       // Code
       String code="";
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Result set
       ResultSet rs;
       
       // Load code
       if (sandbox==true)
           rs=s.executeQuery("SELECT * "
                             + "FROM agents_mine "
                            + "WHERE ID='"+aID+"'");
       else
           rs=s.executeQuery("SELECT * "
                             + "FROM agents "
                            + "WHERE aID='"+aID+"'");
       
       // Data ?
       if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid agent");
       
       // Next
       rs.next();
       
       // Code
       String c=UTILS.BASIC.base64_decode(rs.getString("code"));
       
       // Open the file
       InputStream fstream = getClass().getResourceAsStream("asm.txt");
       br = new BufferedReader(new StringReader(c));
      
       while (strLine!=null)
       {
           // Load line
           strLine=br.readLine();
           
           // Add line
           if (strLine!=null) 
               this.lines.add(strLine);
       }
    }
    
    public void dump()
    {
        for (int a=0; a<=this.lines.size()-1; a++)
            System.out.println(this.lines.get(a));
    }
}
