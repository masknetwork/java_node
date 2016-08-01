package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import javafx.scene.control.Cell;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.CAddress;
import wallet.kernel.UTILS;

public class CMES extends CInstruction
{
    // Destination
    CToken dest;
    
    // Subject
    CToken subject;
    
    // Message
    CToken mes;
    
    public CMES(VM VM, ArrayList<CToken>tokens)
    {
        // Constructor
        super(VM, "MES");
        
        // Destination
        this.dest=tokens.get(1);
                
        // Subject
        this.subject=tokens.get(3);
       
        // Mes
        this.mes=tokens.get(5);
    }
    
    public void execute() throws Exception
    {
        // Out 
        VM.RUNLOG.add(VM.REGS.RCI, "MES "
                                   +this.dest.cel.val+", "
                                   +this.subject.cel.val+", "
                                   +this.mes.cel.val);
            
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // Subject
        if (this.subject.cel.val.length()<2 || this.subject.cel.val.length()>250)
            throw new Exception("Invalid subject length");
        
        // Message
        if (this.mes.cel.val.length()<2 || this.mes.cel.val.length()>1000)
            throw new Exception("Invalid message length");
        
         // Sandbox ?
        if (VM.sandbox) 
        {
            System.out.println("Sandboxed message : "+this.subject.cel.val+", "+this.mes.cel.val);
            return;
        }
        
        // Insert message
        if (UTILS.WALLET.isMine(this.dest.cel.val)==true)
             UTILS.DB.executeUpdate("INSERT INTO mes(from_adr, "
    	   		                          + "to_adr, "
    	   		                          + "subject, "
    	   		                          + "mes, "
    	   		                          + "status, "
    	   		                          + "tstamp, "
                                                  + "tgt)"
    	   		                       + "VALUES ('"+
    	   		                             owner.val+"', '"+
    	   		                             this.dest.cel.val+"', '"+
    	   		                             UTILS.BASIC.base64_encode(this.subject.cel.val)+"', '"+
    	   		                             UTILS.BASIC.base64_encode(this.mes.cel.val)+"', '"+
                                                     "0', '"+
    	   		                             String.valueOf(UTILS.BASIC.tstamp())+"', "
                                                     + "'0')");
        
        // Cost
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
