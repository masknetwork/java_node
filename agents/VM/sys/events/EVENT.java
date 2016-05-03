package wallet.agents.VM.sys.events;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.events.block.BLOCK;
import wallet.agents.VM.sys.events.message.MESSAGE;
import wallet.agents.VM.sys.events.signal.SIG;
import wallet.agents.VM.sys.events.trans.TRANS;

public class EVENT 
{
    // Agent ID
    long agentID;
    
    // Signals
    public SIG SIG=null;
    
    // Transaction
    public TRANS TRANS=null;
    
    // Message
    public MESSAGE MES=null;
    
    // Block
    public BLOCK BLOCK=null;
    
    public EVENT(long agentID, boolean sandbox) throws Exception
    {
        // Agent ID
        this.agentID=agentID;
    }
    
    // Add transaction
    public void addTrans(String sender, 
                         double amount, 
                         String cur, 
                         String mes, 
                         String escrower, 
                         String hash)
    {
        this.TRANS=new TRANS(sender, 
                             amount, 
                             cur, 
                             mes, 
                             escrower, 
                             hash);
    }
    
    // Add transaction
    public void addMessage(String sender, 
                           String subject, 
                           String mes, 
                           String hash)
    {
        this.MES=new MESSAGE(sender,
                             subject,
                             mes,
                             hash);
    }
    
    // Add transaction
    public void addBlock(String hash, 
                         long no, 
                         long nonce)
    {
        this.BLOCK=new BLOCK(hash,
                             no,
                             nonce);
    }
    
    public CCell getField(String var) throws Exception
    {
        // Cell
        CCell c=new CCell("");
        
        // Transaction null
        if (this.TRANS==null && 
            this.SIG==null && 
            this.MES==null &&
            this.BLOCK==null)
        return new CCell("");
        
        // Split
        String[] v=var.split("\\.");
        
        // Check
        if (!v[1].equals("EVENT"))
            throw new Exception("Invalid query string");
        
        // Select
        switch (v[2])
        {
            case "TRANS" : if (TRANS!=null) c=this.TRANS.getVal(v[3]); break;
            case "MES" : if (MES!=null) c=this.MES.getVal(v[3]); break;
            case "BLOCK" : if (BLOCK!=null) c=this.BLOCK.getVal(v[3]); break;
        }
        
        // Load
        return c;
    }
}
