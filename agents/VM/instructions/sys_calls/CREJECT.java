package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CREJECT extends CInstruction
{
    // Type
    CToken target=null;
    
    public CREJECT(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "REFUND");
        
        // Target
        this.target=tokens.get(1);
    }
   
    public  void execute() throws Exception
    {
        // Log 
        VM.RUNLOG.add(VM.REGS.RCI, "REJECT");
        
        // Target
        if (!this.target.cel.val.toUpperCase().equals("NEW_TRANS") && 
            !this.target.cel.val.toUpperCase().equals("NEW_ASSET_TRANS") && 
            !this.target.cel.val.toUpperCase().equals("NEW_ASSET_MARKET") && 
            !this.target.cel.val.toUpperCase().equals("NEW_ASSET_MARKET_ORDER") && 
            !this.target.cel.val.toUpperCase().equals("NEW_ASSET_CLOSE_ORDER"))
        throw new Exception("Invalid event");
        
        // Refund
        switch (this.target.cel.val)
        {
            // Reject transaction
            case "NEW_TRANS" : VM.SYS.EVENT.TRANS.reject(); break;
            
            // Reject asset transaction
            case "NEW_ASSET_TRANS" : VM.SYS.EVENT.ASSET_TRANS.reject(); break;
            
            // Reject open market
            case "NEW_ASSET_MARKET" : VM.SYS.EVENT.OPEN_MARKET.reject(); break;
            
            // Reject open order
            case "NEW_ASSET_MARKET_ORDER" : VM.SYS.EVENT.OPEN_ORDER.reject(); break;
            
            // Reject close order
            case "ASSET_MKT_CLOSE_ORDER" : VM.SYS.EVENT.CLOSE_ORDER.reject(); break;
            
            // Reject bet open
            case "NEW_BET" : VM.SYS.EVENT.BET_OPEN.reject(); break;
            
            // Reject bet buy
            case "NEW_NET_BUY" : VM.SYS.EVENT.BET_BUY.reject(); break;
        }
        
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
   }
}