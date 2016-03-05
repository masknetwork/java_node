package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CResult;

public class CChainBlock 
{
    // Prev hash
    String prev_hash;
    
    // Block
    long block;
    
    // Block hash
    String block_hash;
    
    // Signer
    String signer;
    
    // Signer balance
    long signer_balance;
    
    // Timestamp
    long tstamp;
    
    // Nonce
    long nonce;
    
    // Payload hash
    String payload_hash;
    
    // Net dif
    long net_dif;
    
    public CChainBlock(String prev_block_hash,
                       String block_hash,
                       long block, 
                       String signer, 
                       long signer_balance,
                       long tstamp, 
                       long nonce, 
                       String payload_hash,
                       long net_dif) throws Exception
    {
        // Prev hash
        this.prev_hash=prev_block_hash;
    
        // Block
        this.block=block;
    
        // Block hash
        this.block_hash=block_hash;
    
        // Signer
        this.signer=signer;
        
        // Signer balance
        this.signer_balance=signer_balance;
    
        // Timestamp
        this.tstamp=tstamp;
    
        // Nonce
        this.nonce=nonce;
    
        // Payload hash
        this.payload_hash=payload_hash;
        
        // Net dif
        this.net_dif=net_dif;
    }
    
    public CResult check() throws Exception
    {
        if (UTILS.MINER_UTILS.checkHash(this.prev_hash, 
                                        this.block, 
                                        this.payload_hash, 
                                        this.signer,
                                        this.signer_balance, 
                                        this.tstamp, 
                                        this.nonce,
                                        this.block_hash,
                                        this.net_dif))
            return new CResult(true, "Ok", "CChainBlock", 67);
        else
            return new CResult(false, "Invalid block", "CChainBlock", 67);
 
    }
}
