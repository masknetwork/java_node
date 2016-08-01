package wallet.agents.VM.sys.events.signal;

public class CSignalParam 
{
    // ID
    String ID;
    
    // Data type
    String data_type;
    
    // Min val, length
    double min_val;
    
    // Max val
    double max_val;
    
    public CSignalParam(String ID, 
                        String data_type, 
                        double min, 
                        double max)
    {
        // ID
        this.ID=ID;
    
        // Data type
        this.data_type=data_type;
    
        // Min val, length
        this.min_val=min;
    
        // Max val
        this.max_val=max;
    }
    
    public boolean checkParam(String val) throws Exception
    {
        if (val.length()<this.min_val || 
            val.length()>this.min_val) 
            return false;
        else
            return true;
    }
    
    public boolean checkParam(double val) throws Exception
    {
       if (val<this.min_val || 
            val>this.min_val) 
            return false;
        else
            return true; 
    }
}
