package wallet.agents.VM.sys.net;

import wallet.agents.VM.CCell;
import wallet.kernel.UTILS;

public class NET 
{
    // Agent ID
    long agentID;
        
    public NET(long agentID)
    {
        // Agent ID
        this.agentID=agentID;
    }
    
    public CCell getField(String field) throws Exception
    {
        CCell res=null;
        
        // Valid field ?
        if (!field.equals("LAST_BLOCK") && 
            !field.equals("LAST_BLOCK_HASH") && 
            !field.equals("NET_DIF"))
        throw new Exception("Invalid field");
        
        switch (field)
        {
            case "LAST_BLOCK" : res=new CCell(UTILS.NET_STAT.last_block); break;
            case "LAST_BLOCK_HASH" : res=new CCell(UTILS.NET_STAT.last_block_hash); break;
            case "NET_DIF" : res=new CCell(UTILS.NET_STAT.net_dif); break;
        }
        
        // Return
        return res;
    }
}
