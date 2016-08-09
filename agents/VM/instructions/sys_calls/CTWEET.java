package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CTWEET extends CInstruction 
{
    // Title
    CToken title;
    
    // Content
    CToken mes;
    
    // Pic
    CToken pic=null;
    
    
    public CTWEET(VM VM, ArrayList<CToken>tokens) 
    {
        super(VM, "TWEET");
        
        // Tweet
        this.title=tokens.get(1);
        
        // Pic 1
        this.mes=tokens.get(3);
        
        // Pic 2
        if (tokens.size()>5) 
          this.pic=tokens.get(5);
    }  
    
    public void execute() throws Exception
    {
       // Owner address
       CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
       
       // Pic 1
       String pic="";
       if (this.pic!=null) pic=this.pic.cel.val;
       
       // Tweet
       VM.RUNLOG.add(VM.REGS.RCI, "TWEET "+
                                  this.title.cel.val+", "+
                                  this.mes.cel.val+", "+
                                  this.pic.cel.val);
       
       // Sandbox ?
        if (VM.sandbox) 
        {
            System.out.println("Sandboxed tweet : "+owner.val+", "
                               +this.title.cel.val+", "
                               +this.mes.cel.val+", "
                               +this.pic.cel.val);
            return;
        }
        
        // Check title
        if (!UTILS.BASIC.isTitle(this.title.cel.val))
            throw new Exception ("Invalid title - CNewTweetPayload.java");
        
   	// Check Message
        if (!UTILS.BASIC.isDesc(this.mes.cel.val, 10000))
          throw new Exception ("Invalid message - CNewTweetPayload.java");
           
        // Pic
        if (this.pic!=null)
          if (!UTILS.BASIC.isPic(this.pic.cel.val))
              throw new Exception ("Invalid pic - CNewTweetPayload.java");
        
       // Insert tweet
       UTILS.DB.executeUpdate("INSERT INTO tweets "
                                    + "SET adr='"+owner.val+"', "
                                        + "title='"+UTILS.BASIC.base64_encode(this.title.cel.val)+"', "
                                        + "mes='"+UTILS.BASIC.base64_encode(this.mes.cel.val)+"', "
                                        + "pic='"+UTILS.BASIC.base64_encode(pic)+"', "
                                        + "block='"+VM.block+"'");
       
       // Fee
       VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
