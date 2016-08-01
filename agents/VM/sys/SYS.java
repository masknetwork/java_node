package wallet.agents.VM.sys;

import wallet.agents.VM.sys.agent.AGENT;
import java.sql.ResultSet;
import java.sql.Statement;
import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.net.NET;
import wallet.agents.VM.sys.events.EVENT;
import wallet.agents.VM.sys.events.trans.TRANS;
import wallet.kernel.UTILS;

public class SYS 
{
    // Agent data
    public AGENT AGENT;
    
    // Network
    public NET NET;
    
    // Operation
    public EVENT EVENT;
    
    public SYS(long agentID, boolean sandbox) throws Exception
    {
        // Agent
        AGENT=new AGENT(agentID, sandbox);
        
        // Network
        NET=new NET(agentID);
        
        // Operation
        EVENT=new EVENT(agentID, sandbox);
    }
    
    public CCell getVar(String var) throws Exception
    {
        CCell c=null;
        
        // Uppercase
        var=var.toUpperCase();
        
        // Split
        String[] v=var.split("\\.");
        
        // Valid ?
        if (!v[0].equals("SYS"))
            throw new Exception("Invalid variable "+var);
        
        // Select
        switch (v[1])
        {
            case "AGENT" :  c=AGENT.getField(var); break;
            case "NET" :  c=NET.getField(v[2]); break;
            case "EVENT" :  c=EVENT.getField(var); break;
        }
        
        // Return exception
        if (c==null) 
            throw new Exception("Invalid variable "+var);
        
        // Return
        return c;
    }
    
}
