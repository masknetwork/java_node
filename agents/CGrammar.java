package wallet.agents;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CGrammar 
{
   // Lines
   public ArrayList<CGrammarLine> lines=new ArrayList<CGrammarLine>();
    
   public CGrammar() throws Exception
   {
       loadGrammar();
   }
   
   public boolean check(ArrayList<CToken> tokens) throws Exception
   {
       CGrammarLine line;
       
       for (int a=0; a<=this.lines.size()-1; a++)
       {
          line=this.lines.get(a);
          if (line.compare(tokens)) return true;
       }
           
       return false;
   }
   
   public void loadGrammar() throws Exception
   {
       CGrammarLine line;
       String strLine;
       
       // Open the file
       InputStream fstream = getClass().getResourceAsStream("grammar.txt");
       BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
       
       while ((strLine = br.readLine()) != null)   
       {
           if (strLine.length()>2)
           {
              // Trim
              strLine=strLine.trim();
           
              // Replace whitespaces
              strLine=strLine.replaceAll("\\s+","");
           
              // Explode
              String s[]=strLine.split(",");
           
              // Instruction 
              String i[]=s[0].split("-");
           
              // Line
              line=new CGrammarLine();
           
              // Add instruction
              line.add(new CGrammarLineToken("ID_INS", i[1]));
           
              // Params
              if (s.length>1) 
                 for (int a=1; a<=s.length-1; a++)
                     line.add(new CGrammarLineToken(s[a], ""));
           
              // Load line
              this.lines.add(line);
           }
       }

       //Close the input stream
       br.close();
      
   }
}
