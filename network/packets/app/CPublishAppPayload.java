package wallet.network.packets.app;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CPublishAppPayload extends CPayload
{
    // App ID
    public long appID;
    
    // Adr
    public String adr;
    
    // Name
    public String name;
    
    // Desc
    public String desc;
    
    // Payment address
    public String pay_adr;
    
    // Website
    public String website;
    
    // Pic
    public String pic;
    
    // Version
    public String ver;
    
    // Price
    public double price;
    
    // Target
    public String target;
    
    // Categ
    public String categ;
    
    
    
    public CPublishAppPayload(String target, 
                              long appID, 
                              String adr, 
                              String categ,
                              String name, 
                              String desc, 
                              String pay_adr, 
                              String website, 
                              String pic, 
                              String ver, 
                              double price) throws Exception
    {
        // Constructor
        super(adr);
        
        // App ID
        this.appID=appID;
    
        // Adr
        this.adr=adr;
    
        // Name
        this.name=name;
    
        // Desc
        this.desc=desc;
        
        // Payment address
        this.pay_adr=pay_adr;
    
        // Website
        this.website=website;
    
        // Pic
        this.pic=pic;
    
        // Version
        this.ver=ver;
    
        // Price
        this.price=price;
    
        // Target
        this.target=target;
        
        // Categ
        this.categ=categ;
       
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.target+
                              this.appID+
                              this.adr+
                              this.categ+
                              this.name+
                              this.desc+
                              this.pay_adr+
                              this.website+
                              this.pic+
                              this.ver+
                              this.price);
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
         // Super class
         CResult res=super.check(block);
         if (res.passed==false) throw new Exception(res.reason);
         
         // Target
         if (!this.target.equals("ID_STORE") &&
             !this.target.equals("ID_DIR"))
         throw new Exception("Invalid target (CPublishAppPayload.java)");
         
         // Statement 
         Statement s=UTILS.DB.getStatement();
         
         // Valid category
         if (!this.categ.equals("ID_BUSINESS") &&
             !this.categ.equals("ID_EDUCATION") &&
             !this.categ.equals("ID_ENTERTAINMENT") &&
             !this.categ.equals("ID_FINANCE") &&
             !this.categ.equals("ID_GAMES") &&
             !this.categ.equals("ID_GAMBLING") &&
             !this.categ.equals("ID_PRODUCTIVITY") &&
             !this.categ.equals("ID_SHOPPING") &&
             !this.categ.equals("ID_TRADING") &&
             !this.categ.equals("ID_UTILITIES"))
         throw new Exception("Invalid application category (CPublishAppPayload.java)");
         
         // Load agent data
         ResultSet rs=s.executeQuery("SELECT * "
                                     + "FROM agents "
                                    + "WHERE aID='"+this.appID+"' "
                                      + "AND adr='"+this.target_adr+"'");
             
         // Exist ?
         if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid application ID (CPublishAppPayload.java)");
             
         // Next
         rs.next();
         
         // Rented Application ?
         if (this.target.equals("ID_STORE") && 
             !rs.getString("owner").equals(rs.getString("adr")))
         throw new Exception("Rented applications can't be published to market (CPublishAppPayload.java)");
             
         // Sealed ?
         if (rs.getLong("sealed")>0)
            throw new Exception("Sealed agent (CPublishAppPayload.java)");
         
         // Name
         this.name=UTILS.BASIC.base64_decode(this.name);
         if (!UTILS.BASIC.titleValid(this.name))
            throw new Exception("Invalid title (CPublishAppPayload.java)");
    
         // Desc
         this.desc=UTILS.BASIC.base64_decode(this.desc);
         if (!UTILS.BASIC.descriptionValid(this.desc))
              throw new Exception("Invalid description (CPublishAppPayload.java)");
    
         // Website
         this.website=UTILS.BASIC.base64_decode(this.website);
         if (this.website.length()>5)
            if (!UTILS.BASIC.isLink(this.website))
               throw new Exception("Invalid website (CPublishAppPayload.java)");
         
         
         // Pic
         this.pic=UTILS.BASIC.base64_decode(this.pic);
         if (this.pic.length()>5)
            if (!UTILS.BASIC.isPic(this.pic))
             throw new Exception("Invalid website (CPublishAppPayload.java)");
         
         
         // Version
         this.ver=UTILS.BASIC.base64_decode(this.ver);
         if (this.ver.length()<1 || ver.length()>20)
             throw new Exception("Invalid website (CPublishAppPayload.java)");
       
         // Price
         if (this.target.equals("ID_STORE"))
         {
            if (price<0.0001)
             throw new Exception("Invalid price (CPublishAppPayload.java)");
        
            // Payment adddress valid ?
            if (!UTILS.BASIC.adressValid(this.pay_adr))
               throw new Exception("Invalid payment address (CPublishAppPayload.java)");
       
         }
         
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
                                    this.target+
                                    this.appID+
                                    this.adr+
                                    this.categ+
                                    UTILS.BASIC.base64_encode(this.name)+
                                    UTILS.BASIC.base64_encode(this.desc)+
                                    this.pay_adr+
                                    UTILS.BASIC.base64_encode(this.website)+
                                    UTILS.BASIC.base64_encode(this.pic)+
                                    UTILS.BASIC.base64_encode(this.ver)+
                                    this.price);
        
          // Hash match ?
          if (!this.hash.equals(h))
            throw new Exception("Invalid hash (CPublishAppPayload.java)");
         
         // Return
  	 return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Commit parent
 	CResult res=super.commit(block);
 	if (res.passed==false) return res;
        
        // Directory ?
        long dir=0;
        if (this.target.equals("ID_STORE"))
            dir=0;
        else
            dir=1000;
            
        // Execute 
        UTILS.DB.executeUpdate("UPDATE agents "
                                + "SET categ='"+this.categ+"', "
                                    + "name='"+this.name+"', "
                                    + "description='"+this.desc+"', "
                                    + "pay_adr='"+this.pay_adr+"', "
                                    + "website='"+this.website+"', "
                                    + "pic='"+this.pic+"', "
                                    + "ver='"+this.ver+"', "
                                    + "price='"+this.price+"', "
                                    + "dir='"+dir+"' "
                              + "WHERE aID='"+this.appID+"'");
        
        // Return
  	return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
    }
}
