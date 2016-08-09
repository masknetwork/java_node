package wallet.agents.VM.sys.events;

import wallet.agents.VM.CCell;
import wallet.agents.VM.sys.bets.*;
import wallet.agents.VM.sys.events.block.BLOCK;
import wallet.agents.VM.sys.events.message.MESSAGE;
import wallet.agents.VM.sys.events.signal.SIG;
import wallet.agents.VM.sys.events.trans.TRANS;
import wallet.agents.VM.sys.events.assets.*;
import wallet.agents.VM.sys.events.vote.VOTE;

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
    
    // New asset transaction
    public ASSET_TRANS ASSET_TRANS=null;
    
    // Close order
    public CLOSE_ORDER CLOSE_ORDER=null;
    
    // Open market
    public OPEN_MARKET OPEN_MARKET=null;
    
    // New order
    public OPEN_ORDER OPEN_ORDER=null;
    
    // Open bet
    public BET_OPEN BET_OPEN=null;
    
    // Buy bet
    public BET_BUY BET_BUY=null;
    
    // Vote
    public VOTE VOTE=null;
    
    public EVENT(long agentID, boolean sandbox) throws Exception
    {
        // Agent ID
        this.agentID=agentID;
    }
    
    // Add transaction
    public void loadTrans(String sender, 
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
    public void loadMessage(String sender, 
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
    public void loadBlock(String hash, 
                         long no, 
                         long nonce)
    {
        this.BLOCK=new BLOCK(hash,
                             no,
                             nonce);
    }
    
    // New asset trans
    public void loadAssetTrans(String sender, 
                              String receiver, 
                              double amount, 
                              String escrower, 
                              String hash,
                              long block)
    {
        this.ASSET_TRANS=new ASSET_TRANS(sender, 
                                       receiver, 
                                       amount, 
                                       escrower, 
                                       hash,
                                       block);
    }
    
    // New asset trans
    public void loadOpenAssetMarket(String adr,
                              long id,
                              String asset,
                              String cur,
                              String title,
                              String description,
                              int decimals,
                              long days)
    {
        this.OPEN_MARKET=new OPEN_MARKET(adr,
                                       id,
                                       asset,
                                       cur,
                                       title,
                                       description,
                                       decimals,
                                       days);
    }
    
    // Add open order
    public void loadOpenAssetOrder(long orderID,
                                   String type, 
                                   double amount,
                                   double price)
    {
        this.OPEN_ORDER=new OPEN_ORDER(orderID,
                                     type, 
                                     amount,
                                     price);
    }
    
    // Add close order
    public void loadCloseAssetOrder(long orderID, 
                                    String type, 
                                    double amount, 
                                    double price)
    {
        this.CLOSE_ORDER=new CLOSE_ORDER(orderID, 
                                       type, 
                                       amount, 
                                       price);
    }
    
    // Vote
    public void loadVote(String adr,
                         String type,
                         double power)
    {
        this.VOTE=new VOTE(adr,
                           type,
                           power);
    }
    
    
    public void loadBetOpen(long mktID,
                            String feed_1,
                            String branch_1,
                            String feed_2,
                            String branch_2,
                            String feed_3,
                            String branch_3,
                            String tip,
                            double val_1,
                            double val_2,
                            String title,
                            String description,
                            double budget,
                            double win_multiplier,
                            long start_block,
                            long end_block,
                            long accept_block,
                            String cur,
                            String hash,
                            long block)
    {
        this.BET_OPEN=new BET_OPEN(mktID,
                                   feed_1, branch_1,
                                   feed_2, branch_2,
                                   feed_3, branch_3,
                                   tip,
                                   val_1, val_2,
                                   title, description,
                                   budget,
                                   win_multiplier,
                                   start_block, end_block, accept_block,
                                   cur,
                                   hash,
                                   block);
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
            // New transaction
            case "TRANS" : if (TRANS!=null) c=this.TRANS.getVal(v[3]); else c=new CCell(0); break;
            
            // New message
            case "MES" : if (MES!=null) c=this.MES.getVal(v[3]); else c=new CCell(0); break;
            
            // New block
            case "BLOCK" : if (BLOCK!=null) c=this.BLOCK.getVal(v[3]); else c=new CCell(0); break;
            
            // New asset transaction
            case "ASSET_TRANS" : if (ASSET_TRANS!=null) c=this.ASSET_TRANS.getVal(v[3]); else c=new CCell(0); break;
            
            // Open asset market
            case "OPEN_ASSET_MARKET" : if (OPEN_MARKET!=null) c=this.OPEN_MARKET.getVal(v[3]); else c=new CCell(0); break;
            
            // Open asset order
            case "OPEN_ASSET_ORDER" : if (OPEN_ORDER!=null) c=this.OPEN_ORDER.getVal(v[3]); else c=new CCell(0); break;
            
            // Close asset order
            case "CLOSE_ASSET_ORDER" : if (CLOSE_ORDER!=null) c=this.CLOSE_ORDER.getVal(v[3]); else c=new CCell(0); break;
            
            // Open speculative market
            case "BET_OPEN" : if (this.BET_OPEN!=null) c=this.BET_OPEN.getVal(v[3]); else c=new CCell(0); break;
            
            // Open speculative order
            case "BET_BUY" : if (BET_BUY!=null) c=this.BET_BUY.getVal(v[3]); else c=new CCell(0); break;
        }
        
        // Load
        return c;
    }
}
