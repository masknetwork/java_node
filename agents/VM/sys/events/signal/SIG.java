package wallet.agents.VM.sys.events.signal;

public class SIG 
{
    // Signals
    CSignals sig;
    
    public SIG(long agentID, boolean sandbox) throws Exception
    {
        this.sig=new CSignals(agentID, sandbox);
    }
    
    
}
