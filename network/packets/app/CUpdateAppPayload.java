package wallet.network.packets.app;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CUpdateAppPayload extends CPayload
{
    // AppID
    long appID;

    // Operation
    String op;

    // Days
    long days;
    
     public CUpdateAppPayload(String adr, 
                              long appID, 
                              String op, 
                              long days) throws Exception
     {
         // Constructor
         super(adr);
         
         // App ID
         this.appID=appID;
         
         // Operation
         this.op=op;
         
         // Days
         this.days=days;
         
         // Hash
         this.hash=UTILS.BASIC.hash(this.getHash()+
                                    this.appID+
                                    this.op+
                                    this.days);
         
         // Sign
         this.sign();
     }
     
     public void check(CBlockPayload block) throws Exception
     {
        // Commit parent
 	super.check(block);
 	
        
        // App ID
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM agents "
                                   + "WHERE aID='"+this.appID+"' "
                                     + "AND adr='"+this.target_adr+"'");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
            throw new Exception ("Invalid app ID (CUpdateAppPayload.java)");
        
        // Load agent data
        rs.next();
        
        // Operation
        if (!this.op.equals("ID_UNINSTALL") &&
            !this.op.equals("ID_REMOVE_STORE") &&
            !this.op.equals("ID_REMOVE_DIR"))
        throw new Exception ("Invalid operation (CUpdateAppPayload.java)");
        
        // Sealed agent ?
        if (UTILS.BASIC.isSealed(rs.getString("adr")))
            throw new Exception ("Sealed application (CUpdateAppPayload.java)");
        
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                    this.appID+
                                    this.op+
                                    this.days);
        
        // Hash match
        if (!h.equals(this.hash))
            throw new Exception ("Invalid hash (CUpdateAppPayload.java)");
    }
     
    public void commit(CBlockPayload block) throws Exception
    {
        // Commit parent
 	super.commit(block);
 	
        switch (this.op)
        {
            case "ID_UNINSTALL" : UTILS.DB.executeUpdate("DELETE FROM agents "
                                                             + "WHERE aID='"+this.appID+"'"); 
            
                                  UTILS.DB.executeUpdate("DELETE FROM storage "
                                                             + "WHERE aID='"+this.appID+"'");
                                  break;
                                  
            case "ID_REMOVE_STORE" : UTILS.DB.executeUpdate("UPDATE agents "
                                                           + "SET app_store='0' "
                                                         + "WHERE aID='"+this.appID+"'"); 
                                  break;
                                  
            case "ID_REMOVE_DIR" : UTILS.DB.executeUpdate("UPDATE agents "
                                                           + "SET dir='0' "
                                                         + "WHERE aID='"+this.appID+"'"); 
                                  break;
        }
        
       
    }
}
