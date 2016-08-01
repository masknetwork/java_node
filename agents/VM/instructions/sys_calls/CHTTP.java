package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.CLoader;
import wallet.kernel.UTILS;

public class CHTTP extends CInstruction 
{
    // URL
    CToken url;
    
    // Par 1
    CToken par_1=null;
    
    // Par 2
    CToken par_2=null;
    
    // Par 3
    CToken par_3=null;
    
    // Par 4
    CToken par_4=null;
    
    // Par 5
    CToken par_5=null;
    
    // Par 6
    CToken par_6=null;
    
    // Par 7
    CToken par_7=null;
    
    // Par 8
    CToken par_8=null;
    
    // Par 9
    CToken par_9=null;
    
    // Par 10
    CToken par_10=null;
    
    
    public CHTTP(VM VM, ArrayList<CToken>tokens) 
    {
        super(VM, "HTTP");
        
        // URL
        this.url=tokens.get(1);
        
        // Par 1
        if (tokens.size()>2) this.par_1=tokens.get(3);
        
        // Par 2
        if (tokens.size()>4) this.par_2=tokens.get(5);
        
        // Par 3
        if (tokens.size()>6) this.par_3=tokens.get(7);
        
        // Par 4
        if (tokens.size()>8) this.par_4=tokens.get(9);
        
        // Par 5
        if (tokens.size()>10) this.par_5=tokens.get(11);
        
        // Par 6
        if (tokens.size()>12) this.par_6=tokens.get(13);
        
        // Par 7
        if (tokens.size()>14) this.par_7=tokens.get(15);
        
        // Par 8
        if (tokens.size()>16) this.par_8=tokens.get(17);
        
        // Par 9
        if (tokens.size()>18) this.par_9=tokens.get(19);
        
        // Par 10
        if (tokens.size()>20) this.par_10=tokens.get(21);
    }  
    
    public void execute() throws Exception
    {
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // Log
        VM.RUNLOG.add(VM.REGS.RCI, "HTTP "+this.url.cel.val+", "+this.par_1.cel.val);
        
         // My address
        if (!UTILS.WALLET.isMine(owner.val)) return;
        
        // Owner password
        CCell pass=VM.SYS.getVar("SYS.AGENT.GENERAL.PASS");
        
        // Create loader
        CLoader loader=new CLoader(this.url.cel.val, pass.val);
        
        // Add params
        if (this.par_1!=null) loader.addParam("par_1", this.par_1.cel.val);
        if (this.par_2!=null) loader.addParam("par_2", this.par_2.cel.val);
        if (this.par_3!=null) loader.addParam("par_3", this.par_3.cel.val);
        if (this.par_4!=null) loader.addParam("par_4", this.par_4.cel.val);
        if (this.par_5!=null) loader.addParam("par_5", this.par_5.cel.val);
        if (this.par_6!=null) loader.addParam("par_6", this.par_6.cel.val);
        if (this.par_7!=null) loader.addParam("par_7", this.par_7.cel.val);
        if (this.par_8!=null) loader.addParam("par_8", this.par_8.cel.val);
        if (this.par_9!=null) loader.addParam("par_9", this.par_9.cel.val);
        if (this.par_10!=null) loader.addParam("par_10", this.par_10.cel.val);
        
        // Launcg loader
        loader.start();
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
