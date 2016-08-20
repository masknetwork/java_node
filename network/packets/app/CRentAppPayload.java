package wallet.network.packets.app;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.agents.CAgent;
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
   
    
    public CRentAppPayload(String adr, 
                          long appID,
                          long days) throws Exception
    {
       // Constructor
       super(adr);   
       
       // App ID
       this.appID=appID;
       
       // New appID
       this.new_appID=UTILS.BASIC.getID();
       
       // Days
       this.days=days;
       
        // Hash
       this.hash=UTILS.BASIC.hash(this.getHash()+
                                  this.appID+
                                  this.new_appID+
                                  this.days);
       
       // Sign
       this.sign();
    }
    
     public void check(CBlockPayload block) throws Exception
     {
        // Commit parent
 	super.check(block);
 	
        // Days
        if (this.days<1)
            throw new Exception("Invalid days (CRentAppPayload.java)");
        
        // New App ID
        if (!UTILS.BASIC.validID(this.new_appID))
            throw new Exception("Invalid agent ID (CRentAppPayload.java)");
        
        // Sealed address ?
        if (UTILS.BASIC.isSealed(this.target_adr))
           throw new Exception("Sealed address (CRentAppPayload.java)");
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM agents "
                                          + "WHERE aID='"+this.appID+"' "
                                            + "AND app_store>0");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid agent ID (CRentAppPayload.java)");
        
        // Next
        rs.next();
        
        // Price
        double price=this.days*rs.getDouble("price");
        
        // Non free ?
        if (price>0)
        {
            // Balance
            if (UTILS.ACC.getBalance(this.target_adr, "MSK", block)<price)
               throw new Exception("Innsufficient funds (CRentAppPayload.java)");
        
           // Transfer
           UTILS.ACC.newTransfer(this.target_adr,
                                 rs.getString("pay_adr"), 
                                 price,
                                 true,
                                 "MSK", 
                                 "App payment", 
                                 "", 
                                 this.hash, 
                                 this.block,
                                 block,
                                 0);
        }
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.appID+
                                  this.new_appID+
                                  this.days);
        
        if (!this.hash.equals(h))   
            throw new Exception("Invalid hash (CRentAppPayload.java)");
     }
     
     public void commit(CBlockPayload block) throws Exception
     {
        // Commit parent
 	super.commit(block);
 	 
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM agents "
                                          + "WHERE aID='"+this.appID+"'");
        
        // Next
        rs.next();
        
        // Install
        UTILS.DB.executeUpdate("INSERT INTO agents "
                                     + "SET owner='"+rs.getString("owner")+"', "
                                          + "adr='"+this.target_adr+"', "
                                          + "aID='"+this.new_appID+"', "
                                          + "categ='"+rs.getString("categ")+"', "
                                          + "name='"+rs.getString("name")+"', "
                                          + "description='"+rs.getString("description")+"', "
                                          + "globals='"+rs.getString("globals")+"', "
                                          + "interface='"+rs.getString("interface")+"', "
                                          + "signals='"+rs.getString("signals")+"', "
                                          + "code='"+rs.getString("code")+"', "
                                          + "exec_log='', "
                                          + "pay_adr='', "
                                          + "run_period='"+rs.getLong("run_period")+"', "
                                          + "expire='"+(this.block+this.days*1440)+"', "
                                          + "block='"+this.block+"'");
        
        // Clear
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
        
        // Load VM
        CAgent AGENT=new CAgent(UTILS.BASIC.getAgentID(this.target_adr), false, this.block);
                    
        // Execute
        AGENT.execute("#install#", false, this.block);
     }
}
