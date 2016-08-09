package wallet.network.packets.adr;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.agents.CAgent;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CSealPayload extends CPayload
{
    // Days
    long days;
    
    public CSealPayload(String adr, 
                        long days) throws Exception
    {
        // Constructor
        super(adr);
        
        // UID
        this.days=days;
          
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.days);
          
        // Sign
        this.sign();
    }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Parent
       super.check(block);
       
       // Sealed ?
       if (UTILS.BASIC.isSealed(this.target_adr))
           throw new Exception("Sealed address - CSealPayload.java");
           
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.days);
        
        // Check hash
        if (!h.equals(this.hash))
            throw new Exception("Invalid hash - CSealPayload.java");
         
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Constructor
        super.commit(block);
       
        
        // Seal
        UTILS.DB.executeUpdate("UPDATE adr "
                                + "SET sealed='"+(this.block+this.block*1440)+"', "
                                    + "block='"+this.block+"' "
                              + "WHERE adr='"+this.target_adr+"'");
       
       
    }        
}
