package wallet.agents;

import java.util.ArrayList;

public class CGrammarLine 
{
   // Tokens
   public ArrayList<CGrammarLineToken> tokens=new ArrayList<CGrammarLineToken>();  
    
   public CGrammarLine()
   {
         
   }
   
   public void add(CGrammarLineToken token)
   {
       this.tokens.add(token);
   }
   
   public boolean compare(ArrayList<CToken> tokens) throws Exception
   {
       CToken input_token;
       CGrammarLineToken grammar_token;
       
       // Diffrent length
       if (tokens.size()!=this.tokens.size())
           return false;
       
       for (int a=0; a<=tokens.size()-1; a++)
       {
          // Input token
          input_token=tokens.get(a);
          
          // Grammar token
          grammar_token=this.tokens.get(a);
          
          // Compare
          if (!grammar_token.compare(input_token))
              return false;
       }
           
       return true;
   }
}
