package wallet.agents.VM.sys.events.signal;

import java.util.ArrayList;


public class CSignal 
{
    // Signal ID
    String ID;
    
    // Params
    ArrayList<CSignalParam> params=new  ArrayList<CSignalParam>();
    
    public CSignal(String ID)
    {
        this.ID=ID;
    }
    
    public void addParam(CSignalParam param)
    {
        this.params.add(param);
    }
}
