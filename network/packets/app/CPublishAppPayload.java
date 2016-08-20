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
                              UTILS.BASIC.base64_encode(this.name)+
                              UTILS.BASIC.base64_encode(this.desc)+
                              this.pay_adr+
                              UTILS.BASIC.base64_encode(this.website)+
                              UTILS.BASIC.base64_encode(this.pic)+
                              UTILS.BASIC.base64_encode(this.ver)+
                              this.price);
        
        // Sign
        this.sign();
    }
    
    public void check(CBlockPayload block) throws Exception
    {
         // Super class
         super.check(block);
         
         // Target
         if (!this.target.equals("ID_STORE") &&
             !this.target.equals("ID_DIR"))
         throw new Exception("Invalid target (CPublishAppPayload.java)");
         
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
         ResultSet rs=UTILS.DB.executeQuery("SELECT * "
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
             
         // Name
         if (!UTILS.BASIC.isTitle(this.name))
            throw new Exception("Invalid title (CPublishAppPayload.java)");
    
         // Desc
         if (!UTILS.BASIC.isDesc(this.desc))
              throw new Exception("Invalid description (CPublishAppPayload.java)");
    
         // Website
         if (this.website.length()>5)
            if (!UTILS.BASIC.isLink(this.website))
               throw new Exception("Invalid website (CPublishAppPayload.java)");
         
         // Pic
         if (this.pic.length()>5)
            if (!UTILS.BASIC.isPic(this.pic))
             throw new Exception("Invalid website (CPublishAppPayload.java)");
         
         
         // Version
         if (this.ver.length()<1 || ver.length()>20)
             throw new Exception("Invalid website (CPublishAppPayload.java)");
       
         // Price
         if (this.target.equals("ID_STORE"))
         {
            // Payment adddress valid ?
            if (!UTILS.BASIC.isAdr(this.pay_adr))
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
         
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Commit parent
 	super.commit(block);
 	
        // Directory ?
        long dir=0;
        long app_store;
        if (this.target.equals("ID_STORE"))
        {
            dir=0;
            app_store=this.block;
        }
        else
        {
            dir=1000;
            app_store=0;
        }
        
        // Execute 
        UTILS.DB.executeUpdate("UPDATE agents "
                                + "SET categ='"+this.categ+"', "
                                    + "name='"+UTILS.BASIC.base64_encode(this.name)+"', "
                                    + "description='"+UTILS.BASIC.base64_encode(this.desc)+"', "
                                    + "pay_adr='"+this.pay_adr+"', "
                                    + "website='"+UTILS.BASIC.base64_encode(this.website)+"', "
                                    + "pic='"+UTILS.BASIC.base64_encode(this.pic)+"', "
                                    + "ver='"+UTILS.BASIC.base64_encode(this.ver)+"', "
                                    + "price='"+this.price+"', "
                                    + "dir='"+dir+"', "
                                    + "app_store='"+app_store+"' "
                              + "WHERE aID='"+this.appID+"'");
       
    }
}
