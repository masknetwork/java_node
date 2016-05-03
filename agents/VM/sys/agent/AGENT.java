package wallet.agents.VM.sys.agent;

import wallet.agents.VM.sys.agent.general.GENERAL;
import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.agent.vars.VARS;

public class AGENT 
{
    // Agent ID
    long agentID;
    
    // General data
    public GENERAL GENERAL;
    
    // Global variables
    public VARS VARS;
    
    public AGENT(long agentID, boolean sandbox) throws Exception
    { 
       // Agent ID
       this.agentID=agentID;
       
       // General data
       this.GENERAL=new GENERAL(agentID, sandbox);
       
       // Variables
       this.VARS=new VARS(agentID, sandbox);
    }
    
    public CCell getField(String field) throws Exception
    {
        // Cell
        CCell c=null;
        
        // Lowercase
        field=field.toUpperCase();
        
        // Split
        String[] v=field.split("\\.");
        
        // Valid ?
        if (!v[2].equals("GENERAL") && 
            !v[2].equals("VARS"))
        throw new Exception("Invalid field "+field);
        
        // Branch
        switch (v[2])
        {
            case "GENERAL" : c=this.GENERAL.getData(v[3]); break;
            case "VARS" : c=this.VARS.getData(v[3]); break;
        }
        
        // Exception
        if (c==null) throw new Exception("Invalid field");
        
        // Return 
        return c;
    }
}
