package wallet.network.packets.trans.details;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CAddress;
import wallet.kernel.CECC;
import wallet.kernel.UTILS;
import wallet.network.CResult;


public class CMiscDetails extends CPayDetails 
{
    // Encryption key
    String key;
    
    // Req field 1
    String req_field_1;
    
    // Req field 2
    String req_field_2;
    
    // Req field 3
    String req_field_3;
    
    // Req field 4
    String req_field_4;
    
    // Req field 5
    String req_field_5;
    
    public CMiscDetails(String req_field_1, 
                        String req_field_2, 
                        String req_field_3,
                        String req_field_4, 
                        String req_field_5,
                        String adr) 
    {
	// Secure random
       String k=UTILS.BASIC.randString(25);
          
       // Encrypt message
       this.req_field_1=UTILS.AES.encrypt(req_field_1, k);
       
       // Encrypt message
       this.req_field_2=UTILS.AES.encrypt(req_field_2, k);
       
       // Encrypt message
       this.req_field_3=UTILS.AES.encrypt(req_field_3, k);
       
       // Encrypt message
       this.req_field_4=UTILS.AES.encrypt(req_field_4, k);
       
       // Encrypt message
       this.req_field_5=UTILS.AES.encrypt(req_field_5, k);
       
       // Encrypt key
       CECC enc=new CECC(adr);
       this.key=enc.encrypt(k);
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.key+
                                  this.req_field_1+
                                  this.req_field_2+
                                  this.req_field_3+
                                  this.req_field_4+
                                  this.req_field_5);	
    }
    
    public void getData(String adr, String trans_hash)
    {
       // Load address
       CAddress a=UTILS.WALLET.getAddress(adr);
       
       // Decrypt key
       String k=a.decrypt(this.key);
       
       // Decrypt fields
       this.req_field_1=UTILS.AES.decrypt(req_field_1, k);
       this.req_field_2=UTILS.AES.decrypt(req_field_2, k);
       this.req_field_3=UTILS.AES.decrypt(req_field_3, k);
       this.req_field_4=UTILS.AES.decrypt(req_field_4, k);
       this.req_field_5=UTILS.AES.decrypt(req_field_5, k);
       
       // Insert trans details
       UTILS.DB.executeUpdate("UPDATE my_trans "
                               + "SET field_1='"+this.req_field_1+"',  "
                                   + "field_2='"+this.req_field_2+"',  "
                                   + "field_3='"+this.req_field_3+"',  "
                                   + "field_4='"+this.req_field_4+"',  "
                                   + "field_5='"+this.req_field_5+"'  "
                             + "WHERE hash='"+trans_hash+"'");
       
   }
    
    public CResult check(String adr)
    {
        try
        {
            // Statement
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // Load address data
            ResultSet rs=s.executeQuery("SELECT * FROM req_data WHERE adr='"+adr+"'");
            
            // Has data
            if (!UTILS.DB.hasData(rs))
                 return new CResult(false, "Address doesn't request aditional data", "CMiscDetails", 164);
            
            // Next
            rs.next();
            
            // Hash
            String h=UTILS.BASIC.hash(this.key+
                                      this.req_field_1+
                                      this.req_field_2+
                                      this.req_field_3+
                                      this.req_field_4+
                                      this.req_field_5);	
            
            // Hash match
            if (!this.hash.equals(h))
                return new CResult(false, "Invalid hash", "CMiscDetails", 164);
        }
        catch (SQLException ex) 
       	{  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 57);
        }
        
        return new CResult(true, "Ok", "CMiscDetails", 164);
    }

}
