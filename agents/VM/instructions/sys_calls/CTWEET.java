package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CTWEET extends CInstruction 
{
    // Tweet
    CToken tweet;
    
    // Pic 1
    CToken pic_1=null;
    
    // Pic 2
    CToken pic_2=null;

    // Pic 3
    CToken pic_3=null;

    // Pic 4
    CToken pic_4=null;

    // Pic 5
    CToken pic_5=null;
    
    
    public CTWEET(VM VM, ArrayList<CToken>tokens) 
    {
        super(VM, "TWEET");
        
        // Tweet
        this.tweet=tokens.get(1);
        
        // Pic 1
        if (tokens.size()>3) 
            this.pic_1=tokens.get(3);
        
        // Pic 2
        if (tokens.size()>5) 
            this.pic_2=tokens.get(5);
        
        // Pic 3
        if (tokens.size()>7) 
            this.pic_3=tokens.get(7);
        
        // Pic 4
        if (tokens.size()>9) 
            this.pic_4=tokens.get(9);
        
        // Pic 5
        if (tokens.size()>11) 
            this.pic_5=tokens.get(11);
    }  
    
    public void execute() throws Exception
    {
       // Owner address
       CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
       
       // Pic 1
       String pic_1="";
       if (this.pic_1!=null) pic_1=this.pic_1.cel.val;
       
        // Pic 2
       String pic_2="";
       if (this.pic_2!=null) pic_2=this.pic_2.cel.val;
       
        // Pic 3
       String pic_3="";
       if (this.pic_3!=null) pic_3=this.pic_3.cel.val;
       
        // Pic 1
       String pic_4="";
       if (this.pic_4!=null) pic_4=this.pic_4.cel.val;
       
        // Pic 1
       String pic_5="";
       if (this.pic_5!=null) pic_5=this.pic_5.cel.val;
       
        // Tweet
       VM.RUNLOG.add(VM.REGS.RCI, "TWEET "+
                                  this.tweet.cel.val+", "
                                  +pic_1+", "
                                  +pic_2+", "
                                  +pic_3+", "
                                  +pic_4+", "
                                  +pic_5);
        
       // Insert tweet
       UTILS.DB.executeUpdate("INSERT INTO tweets "
                                    + "SET adr='"+owner.val+"', "
                                        + "target_adr='"+owner.val+"', "
                                        + "mes='"+UTILS.BASIC.base64_encode(this.tweet.cel.val)+"', "
                                        + "pic_1='"+UTILS.BASIC.base64_encode(pic_1)+"', "
                                        + "pic_2='"+UTILS.BASIC.base64_encode(pic_2)+"', "
                                        + "pic_3='"+UTILS.BASIC.base64_encode(pic_3)+"', "
                                        + "pic_4='"+UTILS.BASIC.base64_encode(pic_4)+"', "
                                        + "pic_5='"+UTILS.BASIC.base64_encode(pic_5)+"', "
                                        + "block='"+VM.block+"', "
                                        + "received='"+UTILS.BASIC.tstamp()+"'");
       
       // Fee
       VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
