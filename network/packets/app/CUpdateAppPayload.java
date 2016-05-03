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
     
     public CResult check(CBlockPayload block) throws Exception
     {
        // Commit parent
 	CResult res=super.check(block);
 	if (res.passed==false) return res;
        
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // App ID
        ResultSet rs=s.executeQuery("SELECT * "
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
            !this.op.equals("ID_FROZE") &&
            !this.op.equals("ID_SEAL") &&
            !this.op.equals("ID_REMOVE_STORE") &&
            !this.op.equals("ID_REMOVE_DIR"))
        throw new Exception ("Invalid operation (CUpdateAppPayload.java)");
        
        // Sealed agent ?
        if (rs.getLong("sealed")>0)
            throw new Exception ("Sealed application (CUpdateAppPayload.java)");
        
        // Days
        if (this.op.equals("ID_SEAL"))
        {
            if (this.days<1)
                throw new Exception ("Sealed days (CUpdateAppPayload.java)");
        }
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                    this.appID+
                                    this.op+
                                    this.days);
        
        // Hash match
        if (!h.equals(this.hash))
            throw new Exception ("Invalid hash (CUpdateAppPayload.java)");
        
        // Return
  	 return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
    }
     
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Commit parent
 	CResult res=super.commit(block);
 	if (res.passed==false) return res;
        
        switch (this.op)
        {
            case "ID_UNINSTALL" : UTILS.DB.executeUpdate("DELETE FROM agents "
                                                             + "WHERE aID='"+this.appID+"'"); 
                                  break;
                                  
            case "ID_FROZE" : UTILS.DB.executeUpdate("UPDATE agents "
                                                      + "SET status='ID_FROZEN' "
                                                    + "WHERE aID='"+this.appID+"'"); 
                                  break;
                                  
            case "ID_SEAL" : UTILS.DB.executeUpdate("UPDATE agents "
                                                      + "SET sealed='"+UTILS.BASIC.blocskFromDays(days)+"' "
                                                    + "WHERE aID='"+this.appID+"'"); 
                                  break;
                                  
            case "ID_REMOVE_STORE" : UTILS.DB.executeUpdate("UPDATE agents "
                                                           + "SET price='0' "
                                                         + "WHERE aID='"+this.appID+"'"); 
                                  break;
                                  
            case "ID_REMOVE_DIR" : UTILS.DB.executeUpdate("UPDATE agents "
                                                           + "SET dir='0' "
                                                         + "WHERE aID='"+this.appID+"'"); 
                                  break;
        }
        
         // Return
  	 return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
    }
}
