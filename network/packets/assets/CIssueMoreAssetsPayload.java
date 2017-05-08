package wallet.network.packets.assets;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CIssueMoreAssetsPayload extends CPayload
{
    // Symbol
    public String symbol;
    
    // Qty
    public long qty;
    
    public CIssueMoreAssetsPayload(String adr, 
                                   String symbol, 
                                   long qty, 
                                   String payload_sign) throws Exception
    {
        // Constructor
        super(adr);
        
        // Symbol
        this.symbol=symbol;
        
        // Qty
        this.qty=qty;
        
        // Hash
 	hash=UTILS.BASIC.hash(this.getHash()+
                              this.symbol+
                              this.qty);
        
        // Sign
        this.sign(payload_sign);
    }
    
     public void check(CBlockPayload block) throws Exception
    {
        // Super class
        super.check(block);
        
        // Check symbol
        if (!UTILS.BASIC.isSymbol(symbol))
            throw new Exception("Invalid symbol - CIssueMoreAssetsPayload.java");
        
        
        // Symbol valid
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM assets "
                                          + "WHERE symbol='"+this.symbol+"' "
                                            + "AND adr='"+this.target_adr+"'");
        
        // Not found
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid symbol - CIssueMoreAssetsPayload.java");
        
        
        // Qty
        if (qty<1000 || qty>1000000000000L)
            throw new Exception("Invalid qty - CIssueMoreAssetsPayload.java");
        
        // Hash
 	hash=UTILS.BASIC.hash(this.getHash()+
                              this.symbol+
                              this.qty);
        
        // Check hash
        if (!hash.equals(this.hash))
           throw new Exception("Invalid hash - CIssueMoreAssetsPayload.java");
       
    }
     
      public void commit(CBlockPayload block) throws Exception
    {
 	    // Superclass
           super.commit(block);
 	   
           // Update assets
           UTILS.DB.executeUpdate("UPDATE assets "
                                   + "SET qty=qty+"+this.qty+", "
                                       + "block='"+this.block+"' "
                                 + "WHERE symbol='"+this.symbol+"'");
           
           // Update assets owners
           UTILS.DB.executeUpdate("UPDATE assets_owners "
                                   + "SET qty=qty+"+this.qty+", block='"+this.block+"' "
                                 + "WHERE symbol='"+this.symbol+"' "
                                   + "AND owner='"+this.target_adr+"'");
  
     }
}
