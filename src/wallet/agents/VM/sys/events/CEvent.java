package wallet.agents.VM.sys.events;

public class CEvent 
{
    // Aproved
    public boolean APPROVED;
    
    public CEvent()
    {
        
    }
    
    public void reject() throws Exception
    {
        this.APPROVED=false;
    }    
}
