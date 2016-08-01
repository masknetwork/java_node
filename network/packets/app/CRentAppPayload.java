package wallet.network.packets.app;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CRentAppPayload extends CPayload
{
    // AppID
    long appID;
    
    // Days
    long days;
    
    // Price
    double price;
    
    // New appID
    long new_appID;
    
    // Transaction
    CTransPayload payment=null;
    
    public CRentAppPayload(String adr, 
                          long appID,
                          long days) throws Exception
    {
       // Constructor
       super(adr);   
       
       // App ID
       this.appID=appID;
       
       // Days
       this.days=days;
       
       // New appID
       this.new_appID=UTILS.BASIC.getID();
       
       // Statement
       
       
       // Load app data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM agents "
                                         + "WHERE aID='"+this.appID+"'");
       
       // Next
       rs.next();
       
       // Price
       this.price=rs.getDouble("price");
       
       // Payment to owner
       this.payment=new CTransPayload(this.target_adr, 
                                      rs.getString("pay_adr"), 
                                      price*days, 
                                      "MSK", 
                                      "",
                                      ""); 
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.getHash()+
                                  this.appID+
                                  this.price+
                                  this.days+
                                  this.payment.hash);
       
       // Sign
       this.sign();
    }
    
     public void check(CBlockPayload block) throws Exception
     {
        // Commit parent
 	super.check(block);
 	
        // Days
        if (this.days<1)
            throw new Exception("Invalid agent ID (CRentAppPayload.java)");
        
        // New App ID
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM agents "
                                          + "WHERE aID='"+this.new_appID+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
           throw new Exception("Invalid agent new ID (CRentAppPayload.java)");
        
        // Already an agent installed on address ?
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM agents "
                                + "WHERE adr='"+this.target_adr+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
           throw new Exception("An agent is already installed (CRentAppPayload.java)");
        
        // Load data
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM agents "
                                + "WHERE aID='"+this.appID+"' "
                                  + "AND price>0");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid agent ID (CRentAppPayload.java)");
        
        // Next
        rs.next();
        
        // Price
        double price=this.days*rs.getDouble("price");
        
        // Balance
        if (UTILS.ACC.getBalance(this.target_adr, "MSK", block)<price)
            throw new Exception("Innsufficient funds (CRentAppPayload.java)");
        
        // Check payload destination and price
        if (this.payment.amount<price || 
            !this.payment.cur.equals("MSK") || 
            !this.payment.dest.equals(rs.getString("pay_adr")))
        throw new Exception("Innsufficient payment (CRentAppPayload.java)");
        
        // Check payment
        this.payment.check(block);
        
       
     }
     
     public void commit(CBlockPayload block) throws Exception
     {
        // Commit parent
 	super.commit(block);
 	
        // Statement
        
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM agents "
                                    + "WHERE aID='"+this.appID+"'");
        
        // Next
        rs.next();
        
        // Commit payment
        this.payment.commit(block);
        
        // Install
        UTILS.DB.executeUpdate("INSERT INTO agents (owner, "
                                                 + "adr, "
                                                 + "aID, "
                                                 + "categ, "
                                                 + "name, "
                                                 + "description, "
                                                 + "globals, "
                                                 + "interface, "
                                                 + "signals, "
                                                 + "code, "
                                                 + "exec_log, "
                                                 + "pay_adr, "
                                                 + "storage, "
                                                 + "run_period, "
                                                 + "expire, "
                                                 + "block) VALUES('"
                                                 +rs.getString("owner")+"', '"
                                                 +this.target_adr+"', '"
                                                 +this.new_appID+"', '"
                                                 +rs.getString("categ")+"', '"
                                                 +rs.getString("name")+"', '"
                                                 +rs.getString("description")+"', '"
                                                 +rs.getString("globals")+"', '"
                                                 +rs.getString("interface")+"', '"
                                                 +rs.getString("signals")+"', '"
                                                 +rs.getString("code")+"', '', '', "
                                                 +"'', '"
                                                 +rs.getLong("run_period")+"', '"
                                                 +(this.block+this.days*1440)+"', '"
                                                 +this.block+"')");

     }
}
