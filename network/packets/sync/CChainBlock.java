package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CResult;

public class CChainBlock implements java.io.Serializable
{
     // Serial
    private static final long serialVersionUID = 100L;
   
    // Prev hash
    public String prev_hash;
    
    // Block
    public long block;
    
    // Block hash
    public String block_hash;
    
    // Signer
    public String signer;
    
    // Signer balance
    public long signer_balance;
    
    // Timestamp
    public long tstamp;
    
    // Nonce
    public long nonce;
    
    // Payload hash
    public String payload_hash;
    
    // Net dif
    public String net_dif;
    
    // Commited
    public long commited;
    
    // Tab 1
    public String tab_1="";
    
    // Tab 2
    public String tab_2="";
    
    // Tab 3
    public String tab_3="";
    
    // Tab 4
    public String tab_4="";
    
    // Tab 5
    public String tab_5="";
    
    // Tab 6
    public String tab_6="";
    
    // Tab 7
    public String tab_7="";
    
    // Tab 8
    public String tab_8="";
    
    // Tab 9
    public String tab_9="";
    
    // Tab 10
    public String tab_10="";
    
    // Tab 11
    public String tab_11="";
    
    // Tab 12
    public String tab_12="";
    
    // Tab 13
    public String tab_13="";
    
    // Tab 14
    public String tab_14="";
    
    // Tab 15
    public String tab_15="";
    
    // Tab 15
    public String tab_16="";
    
    // Tab 15
    public String tab_17="";
    
    // Tab 15
    public String tab_18="";
    
    // Tab 15
    public String tab_19="";
    
    // Tab 15
    public String tab_20="";
    
    // Tab 15
    public String tab_21="";
    
    // Tab 15
    public String tab_22="";
    
    // Tab 15
    public String tab_23="";
    
    // Tab 15
    public String tab_24="";
    
    // Tab 15
    public String tab_25="";
    
    // Tab 15
    public String tab_26="";
    
    // Tab 15
    public String tab_27="";
    
    // Tab 15
    public String tab_28="";
    
    // Tab 15
    public String tab_29="";
    
    // Tab 15
    public String tab_30="";
    
    public CChainBlock(String prev_block_hash,
                       String block_hash,
                       long block, 
                       String signer, 
                       long signer_balance,
                       long tstamp, 
                       long nonce, 
                       String payload_hash,
                       String net_dif,
                       long commited,
                       String tab_1,
                       String tab_2,
                       String tab_3,
                       String tab_4,
                       String tab_5,
                       String tab_6,
                       String tab_7,
                       String tab_8,
                       String tab_9,
                       String tab_10,
                       String tab_11,
                       String tab_12,
                       String tab_13,
                       String tab_14,
                       String tab_15,
                       String tab_16,
                       String tab_17,
                       String tab_18,
                       String tab_19,
                       String tab_20,
                       String tab_21,
                       String tab_22,
                       String tab_23,
                       String tab_24,
                       String tab_25,
                       String tab_26,
                       String tab_27,
                       String tab_28,
                       String tab_29,
                       String tab_30) throws Exception
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
        
        // Commited
        this.commited=commited;
        
        // Tab 1
        this.tab_1=tab_1;
        
        // Tab 2
        this.tab_2=tab_2;
        
        // Tab 3
        this.tab_3=tab_3;
        
        // Tab 4
        this.tab_4=tab_4;
        
        // Tab 5
        this.tab_5=tab_5;
        
        // Tab 6
        this.tab_6=tab_6;
        
        // Tab 7
        this.tab_7=tab_7;
        
        // Tab 8
        this.tab_8=tab_8;
        
        // Tab 9
        this.tab_9=tab_9;
        
        // Tab 10
        this.tab_10=tab_10;
        
        // Tab 11
        this.tab_11=tab_11;
        
        // Tab 12
        this.tab_12=tab_12;
        
        // Tab 13
        this.tab_13=tab_13;
        
        // Tab 14
        this.tab_14=tab_14;
        
        // Tab 14
        this.tab_15=tab_15;
        
        // Tab 14
        this.tab_16=tab_16;
        
        // Tab 14
        this.tab_17=tab_17;
        
        // Tab 14
        this.tab_18=tab_18;
        
        // Tab 14
        this.tab_19=tab_19;
        
        // Tab 14
        this.tab_20=tab_20;
        
        // Tab 14
        this.tab_21=tab_21;
        
        // Tab 14
        this.tab_22=tab_22;
        
        // Tab 14
        this.tab_23=tab_23;
        
        // Tab 14
        this.tab_24=tab_24;
        
        // Tab 14
        this.tab_25=tab_25;
        
        // Tab 14
        this.tab_26=tab_26;
        
        // Tab 14
        this.tab_27=tab_27;
        
        // Tab 14
        this.tab_28=tab_28;
        
        // Tab 14
        this.tab_29=tab_29;
        
        // Tab 14
        this.tab_30=tab_30;
        
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
                                        this.net_dif,
                                        this.tab_1,
                                        this.tab_2, 
                                        this.tab_3, 
                                        this.tab_4, 
                                        this.tab_5, 
                                        this.tab_6,
                                        this.tab_7,
                                        this.tab_8,
                                        this.tab_9,
                                        this.tab_10,
                                        this.tab_11,
                                        this.tab_12,
                                        this.tab_13,
                                        this.tab_14,
                                        this.tab_15,
                                        this.tab_16,
                                        this.tab_17,
                                        this.tab_18,
                                        this.tab_19,
                                        this.tab_20,
                                        this.tab_21,
                                        this.tab_22,
                                        this.tab_23,
                                        this.tab_24,
                                        this.tab_25,
                                        this.tab_26,
                                        this.tab_27,
                                        this.tab_28,
                                        this.tab_29,
                                        this.tab_30))
            return new CResult(true, "Ok", "CChainBlock", 67);
        else
            return new CResult(false, "Invalid block", "CChainBlock", 67);
 
    }
}
