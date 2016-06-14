package wallet.network.packets.shop.goods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewStorePayload extends CPayload
{
    // Name
    public String name;
    
    // Description
    public String desc;
    
    // Website
    public String website;
    
    // Pic
    public String pic;
    
    // Days
    public long days;
    
    // UID
    public long UID;
    
    // Escrower policy
    String esc_policy;
    
    public CNewStorePayload(String adr, 
                            String name, 
                            String desc, 
                            String website, 
                            String pic, 
                            String esc_policy,
                            long days,
                            String sign) throws Exception
    {
        // Constructor
        super(adr);
        
        // Name
        this.name=name;
    
        // Description
        this.desc=desc;
    
        // Website
        this.website=website;
    
        // Pic
        this.pic=pic;
    
        // Days
        this.days=days;
        
        // UID
        this.UID=UTILS.BASIC.getID();
        
        // Escrower policy
        this.esc_policy=esc_policy;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash()+
                                   this.name+
                                   this.desc+
                                   this.website+
                                   this.pic+
                                   this.days+
                                   this.esc_policy+
                                   UID);
        
        // Sign
        if (!sign.equals(""))
            this.sign=sign;
        else
            this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        // Super class
        CResult res=super.check(block);
        if (res.passed==false) return res;
        
        // Title
        if (!UTILS.BASIC.isTitle(this.name))
           throw new Exception("Invalid name - CStorePayload.java");
      
        // Description
        if (!UTILS.BASIC.isDesc(this.desc))
           throw new Exception("Invalid description - CStorePayload.java");
      
        // Web page
        if (!UTILS.BASIC.isLink(this.website))
           throw new Exception("Invalid website - CStorePayload.java");
            
        // Pic
        if (!UTILS.BASIC.isPic(this.pic))
           throw new Exception("Invalid pic - CStorePayload.java");
        
        // Days
        if (days<10)
           throw new Exception("Invalid days - CStorePayload.java");
        
        // Escrowers policy
        if (this.esc_policy.equals("ID_NONE") && 
            this.esc_policy.equals("ID_SPECIFIED") && 
            this.esc_policy.equals("ID_ANY"))
        throw new Exception("Invalid escrower - CStorePayload.java");
            
        // Another offer already exist ?
        Statement s=UTILS.DB.getStatement();
        ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM shop_stores "
                                       + "WHERE UID='"+this.UID+"'");
        if (UTILS.DB.hasData(rs))
           throw new Exception("Invalid uid - CStorePayload.java");
      
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.name+
                                  this.desc+
                                  this.website+
                                  this.pic+
                                  this.days+
                                  this.esc_policy+
                                  this.UID);
      
        if (!h.equals(this.hash))
    	   return new CResult(false, "Invalid hash", "CNewEscrowerPayload.java", 74);
      
      
        // Return
        return new CResult(true, "Ok", "CNewEscrowerPayload", 77);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       // Superclass
       super.commit(block);
       
        // Insert
        UTILS.DB.executeUpdate("INSERT INTO shop_stores(adr, "
                                                        + "title, "
                                                        + "description, "
                                                        + "website, "
                                                        + "pic, "
                                                        + "expire, "
                                                        + "block, "
                                                        + "UID) VALUES('"
                                                        +this.target_adr+"', '"
                                                        +UTILS.BASIC.base64_encode(this.name)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.desc)+"', '"
                                                        +this.website+"', '"
                                                        +this.pic+"', '"
                                                        +UTILS.BASIC.getExpireBlock(this.days)+"', '"
                                                        +this.block+"', '"
                                                        +this.UID+"')");
           
        // Insert default category
        UTILS.DB.executeUpdate("INSERT INTO shop_stores_categs(storeID, "
                                                               + "parent, "
                                                               + "name) "
                                      + "VALUES('"
                                                               +this.UID+"', "
                                                               + "'', "
                                                               + "'default')");
        
        UTILS.DB.executeUpdate("INSERT INTO shop_stores_categs(storeID, "
                                                               + "parent, "
                                                               + "name) "
                                      + "VALUES("
                                                               +this.UID+", "
                                                               + "'default', "
                                                               + "'default')");
           
       
   	 // Return 
   	 return new CResult(true, "Ok", "CNewEscrowerPayload.java", 149);
    }
}
