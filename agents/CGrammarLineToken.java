package wallet.agents;

public class CGrammarLineToken 
{
    // Type
    public String type;
    
    // Val
    public String val;
    
    public CGrammarLineToken(String type, String val)
    {
        // Type
        this.type=type;
    
        // Val
        this.val=val;
    }
    
    public boolean compare(CToken token) throws Exception
    {
        if (this.type.equals("ID_INS"))
        {
            if (this.type.equals(token.type) && 
               this.val.equals(token.ins.toUpperCase()))
               return true;
            else
               return false;
        } 
        else if (this.type.indexOf("-")>0)
        {
            String[] v=this.type.split("-");
            
            if (v[0].equals(token.type) && 
               v[1].equals("GLOBAL"))
               return true;
            else
               return false;
        }
        else
        {
            if (this.type.equals(token.type))
               return true;
            else
               return false;
        }
    }
}
