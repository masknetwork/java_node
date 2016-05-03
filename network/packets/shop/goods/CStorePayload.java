package wallet.network.packets.shop.goods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CStorePayload extends CPayload
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
    
    public CStorePayload(String adr, 
                         String name, 
                         String desc, 
                         String website, 
                         String pic, 
                         long days) throws Exception
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
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash()+
                                   this.name+
                                   this.desc+
                                   this.website+
                                   this.pic+
                                   this.days+
                                   UID);
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
            // Super class
            CResult res=super.check(block);
            if (res.passed==false) return res;
      
            // Title
            this.name=UTILS.BASIC.base64_decode(this.name);
            if (!UTILS.BASIC.titleValid(this.name))
             return new CResult(false, "Invalid title", "CNewEscrowerPayload.java", 69);
      
            // Description
            this.desc=UTILS.BASIC.base64_decode(this.desc);
            if (!UTILS.BASIC.titleValid(this.desc))
             return new CResult(false, "Invalid description", "CNewEscrowerPayload.java", 69);
      
            // Web page
            this.website=UTILS.BASIC.base64_decode(this.website);
            if (!UTILS.BASIC.isLink(this.website))
               return new CResult(false, "Invalid web page", "CNewEscrowerPayload.java", 69);
            
            // Pic
            this.pic=UTILS.BASIC.base64_decode(this.pic);
            if (!UTILS.BASIC.isLink(this.pic))
               return new CResult(false, "Invalid web page", "CNewEscrowerPayload.java", 69);
        
            // Days
            if (days<100)
              return new CResult(false, "Invalid days", "CNewEscrowerPayload.java", 69);
     
            // Another offer already exist ?
            Statement s=UTILS.DB.getStatement();
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM shop_stores "
                                       + "WHERE UID='"+this.UID+"'");
            if (UTILS.DB.hasData(rs))
               return new CResult(false, "Another store with the same UID already exist", "CNewEscrowerPayload.java", 69);
      
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
                                      UTILS.BASIC.base64_encode(this.name)+
                                      UTILS.BASIC.base64_encode(this.desc)+
                                      UTILS.BASIC.base64_encode(this.website)+
                                      UTILS.BASIC.base64_encode(this.pic)+
                                      this.days+
                                      this.UID);
      
           if (!h.equals(this.hash))
    	      return new CResult(false, "Invalid hash", "CNewEscrowerPayload.java", 74);
      }
      catch (Exception ex) 
      {  
       	  UTILS.LOG.log("Exception", ex.getMessage(), "CNewEscrowerPayload.java", 57);
      }

      
      // Return
      return new CResult(true, "Ok", "CNewEscrowerPayload", 77);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       try
       {
	   // Super class
	   CResult res=super.check(block);
	   if (res.passed==false) return res;
                
           // Check
           res=this.check(block);
	   if (res.passed==false) return res;
	  
           // Superclass
           super.commit(block);
       
           // Insert
           UTILS.DB.executeUpdate("INSERT INTO shop_stores(adr, "
                                                        + "name, "
                                                        + "description, "
                                                        + "website, "
                                                        + "pic, "
                                                        + "expire, "
                                                        + "block, "
                                                        + "UID) VALUES('"
                                                        +this.target_adr+"', '"
                                                        +UTILS.BASIC.base64_encode(this.name)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.desc)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.website)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.pic)+"', '"
                                                        +UTILS.BASIC.getExpireBlock(this.days)+"', '"
                                                        +this.block+"', '"
                                                        +this.UID+"')");
           
           // Insert default category
           UTILS.DB.executeUpdate("INSERT INTO shop_stores_categs(storeID, "
                                                               + "parent, "
                                                               + "name) "
                                      + "VALUES("
                                                               +this.UID+", "
                                                               + "0, "
                                                               + "'default')");
           
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CNewEscrowerPayload.java", 57);
        }

           
   	 // Return 
   	 return new CResult(true, "Ok", "CNewEscrowerPayload.java", 149);
    }
}
