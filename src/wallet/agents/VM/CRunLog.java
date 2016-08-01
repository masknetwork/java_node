package wallet.agents.VM;

import wallet.kernel.UTILS;

public class CRunLog 
{
    // Result
    String res;
    
    // Sandbox
    boolean sandbox;
    
    // Steps
    long steps=0;
    
    // Virtual machine
    VM VM;
    
    // Last cost
    double last_cost=0;
    
    public CRunLog(VM VM, boolean sandbox)
    {
        // Result
        res="{\"log\" : [";
        
        // Sandbox
        this.sandbox=sandbox;
        
        // VM
        this.VM=VM;
    }
    
    public void add(long line, String ins) throws Exception
    {
        // Step
        this.steps++;
        
        // Cost
        double cost=VM.CODE.fee-this.last_cost;
        
        // Add
        if (steps>1)
           this.res=this.res+",{\"line\" : "+line+", "
                              + "\"ins\" : \""+ins+"\", "
                              + "\"last_cost\" : \""+UTILS.FORMAT_8.format(this.last_cost)+"\", "
                              + "\"total_cost\" : \""+UTILS.FORMAT_8.format(VM.CODE.fee)+"\"}";
        else
           this.res=this.res+"{\"line\" : "+line+", "
                              + "\"ins\" : \""+ins+"\", "
                              + "\"last_cost\" : \""+UTILS.FORMAT_8.format(this.last_cost)+"\", "
                              + "\"total_cost\" : \""+UTILS.FORMAT_8.format(VM.CODE.fee)+"\"}";
        
        // Update cost
        this.last_cost=VM.CODE.fee;
    }
    
    public void flush() throws Exception
    {
        res=res+"]}";
        
        if (this.sandbox)
        UTILS.DB.executeUpdate("UPDATE agents_mine "
                                + "SET exec_log='"+UTILS.BASIC.base64_encode(res)+"' "
                              + "WHERE ID='"+this.VM.agentID+"'");
        else
        UTILS.DB.executeUpdate("UPDATE agents "
                                + "SET exec_log='"+UTILS.BASIC.base64_encode(res)+"' "
                              + "WHERE aID='"+this.VM.agentID+"'");
    }
}
