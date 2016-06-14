package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CEMAIL extends CInstruction
{
    // From address
    CToken from;
    
    // Recipient
    CToken rec;
    
    // Subject
    CToken subj;
    
    // Message
    CToken mes;
    
    public CEMAIL(VM VM, ArrayList<CToken>tokens) 
    {
        // Constructor
        super(VM, "EMAIL");
        
        // From
        this.from=tokens.get(1);
        
        // Recipient
        this.rec=tokens.get(3);
        
        // Subject
        this.subj=tokens.get(5);
    
        // Message
        this.mes=tokens.get(7);
    }
    
    public void execute() throws Exception
    {
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // My address
        if (!UTILS.WALLET.isMine(owner.val)) return;
        
        // Email valid
        if (!UTILS.BASIC.emailValid(this.rec.cel.val))
            throw new Exception("Invalid email");
            
        // Message valid
        if (this.mes.cel.val.length()<2 || this.mes.cel.val.length()>2500)
            throw new Exception("Invalid message length");
        
        // Subject valid
        if (this.subj.cel.val.length()<2 || this.subj.cel.val.length()>100)
            throw new Exception("Invalid message length");
        
        // Insert email
        UTILS.DB.executeUpdate("INSERT INTO out_emails "
                                     + "SET format='ID_MES', "
                                         + "from='"+this.from.cel.val+"', "
                                         + "dest='"+this.rec.cel.val+"', "
                                         + "subject='"+UTILS.BASIC.base64_encode(this.subj.cel.val)+"', "
                                         + "message='"+UTILS.BASIC.base64_encode(this.mes.cel.val)+"', "
                                         + "adr='"+owner+"', "
                                         + "tstamp='"+UTILS.BASIC.tstamp()+"', "
                                         + "status='ID_PENDING'");
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
