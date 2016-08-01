package wallet.agents.VM.instructions.sys_calls;

import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;
import wallet.network.packets.blocks.CBlockPayload;

public class CTRANS extends CInstruction
{
    // Receiver
    CToken receiver;

    // Amount
    CToken amount;
    
    // Currency
    CToken cur;
    
    // Message
    CToken mes;
    
    // Escrower
    CToken escrower;
    
    public CTRANS(VM VM, ArrayList<CToken>tokens)
    {
        // Constructor
        super(VM, "TRANS");
        
        // Receiver
        this.receiver=tokens.get(1);
        
        // Amount
        this.amount=tokens.get(3);
        
        // Currency
        this.cur=tokens.get(5);
        
        // Message
        this.mes=tokens.get(7);
        
        // Escrower
        this.escrower=tokens.get(9);
    }
    
    public void execute() throws Exception
    {
        VM.RUNLOG.add(VM.REGS.RCI, "TRANS "
                                    +this.receiver.cel.val+", "
                                    +this.amount.cel.val+", "
                                    +this.cur.cel.val+", "
                                    +this.mes.cel.val+", "
                                    +this.escrower.cel.val);
        
        // Owner address
        CCell owner=VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER");
        
        // Receiver
        if (!UTILS.BASIC.adressValid(this.receiver.cel.val))
            throw new Exception("Invalid receiver");
        
        // Receiver
        if (this.receiver.cel.val.length()<30)
            this.receiver.cel.val=UTILS.BASIC.adrFromDomain(this.receiver.cel.val);
        
        // Escrower
        if (this.escrower.cel.val.length()>0)
        {
           // Escrower
           if (!UTILS.BASIC.adressValid(this.escrower.cel.val))
               throw new Exception("Invalid escrower");
        
           // Escrower
           if (this.escrower.cel.val.length()<30)
               this.escrower.cel.val=UTILS.BASIC.adrFromDomain(this.escrower.cel.val);
        }
        
        // Amount
        double amount=Double.parseDouble(this.amount.cel.val);
        
        // Amount
        if (!this.cur.cel.val.equals("MSK"))
        {
           if (amount<0.0001) 
               throw new Exception("Invalid amount");    
        }
        else
        {
            if (amount<0.00000001) 
               throw new Exception("Invalid amount");
        }
        
        // Balance
        if (UTILS.ACC.getBalance(owner.val, this.cur.cel.val)<amount)
            throw new Exception("Insufficient funds");
        
        // Currency
        if (!this.cur.cel.val.equals("MSK"))
           if (!UTILS.BASIC.isAsset(this.cur.cel.val))
               throw new Exception("Invalid currency");
        
        // Hash
        String hash=UTILS.BASIC.hash(owner.val+amount+this.cur.cel.val+UTILS.NET_STAT.last_block+this.receiver.cel.val);
        
        // Sandbox ?
        if (VM.sandbox) 
        {
            System.out.println("Sandboxed transaction : "+owner.val+", "+this.receiver.cel.val+", "+this.amount.cel.val+", "+this.cur.cel.val);
            return;
        }
        
        // Transaction
        UTILS.ACC.newTransfer(owner.val, 
                                this.receiver.cel.val, 
                                Double.parseDouble(this.amount.cel.val), 
                                false,
                                this.cur.cel.val, 
                                "Outgoing transaction", 
                                this.cur.cel.val, 
                                hash, 
                                VM.block,
                                null, 
                                0);
        
        // Message
        UTILS.DB.executeUpdate("UPDATE my_trans "
                                + "SET mes='"+UTILS.BASIC.base64_encode(this.mes.cel.val)+"' "
                              + "WHERE hash='"+hash+"'");
        
        // Clear
        UTILS.ACC.clearTrans(hash, "ID_ALL", UTILS.NET_STAT.last_block+1);
        
        // Fee
        VM.CODE.fee=VM.CODE.fee+0.0001;
    }
}
