package wallet.network.packets.assets;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CTrustAssetPayload extends CPayload
{
    // Symbol
    public String asset;
    
    // Days
    public long days;
    
    public CTrustAssetPayload(String adr, 
                              String asset, 
                              long days) throws Exception
    {
        // Constructor
        super(adr);
        
        // Asset
        this.asset=asset;
        
        // Days
        this.days=days;
        
        // Hash
 	hash=UTILS.BASIC.hash(this.getHash()+
                              this.asset+
                              this.days);
        
        // Sign
        this.sign();
    }
    
     public void check(CBlockPayload block) throws Exception
    {
        // Super class
        super.check(block);
        
        // Asset valid
        if (!UTILS.BASIC.isAsset(asset))
            throw new Exception("Invalid symbol - CTrustAssetPayload.java");
        
        // Days
        if (this.days<1)
            throw new Exception("Invalid symbol - CTrustAssetPayload.java");
        
        // Already trust ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM adr_attr "
                                          + "WHERE adr='"+this.target_adr+"' "
                                            + "AND attr='ID_TRUST_ASSET' "
                                            + "AND s1='"+this.asset+"'");
        
        // Has data ?
        if (UTILS.DB.hasData(rs))
            throw new Exception("Already trusted - CTrustAssetPayload.java");
        
        // Hash
 	hash=UTILS.BASIC.hash(this.getHash()+
                              this.asset+
                              this.days);
        
        // Check hash
        if (!hash.equals(this.hash))
           throw new Exception("Invalid hash - CTrustAssetPayload.java");
       
    }
     
      public void commit(CBlockPayload block) throws Exception
    {
 	    // Update assets
           UTILS.DB.executeUpdate("INSERT INTO adr_attr "
                                        + "SET adr='"+this.target_adr+"', "
                                            + "attr='ID_TRUST_ASSET', "
                                            + "s1='"+this.asset+"'");
     }
}
