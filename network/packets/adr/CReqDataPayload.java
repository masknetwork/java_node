package wallet.network.packets.adr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CReqDataPayload extends CPayload 
{
    // Field 1
    String field_1_name;
    int field_1_min;
    int field_1_max;
    
    // Field 2
    String field_2_name;
    int field_2_min;
    int field_2_max; 
    
    // Field 3
    String field_3_name;
    int field_3_min;
    int field_3_max;
    
    // Field 4
    String field_4_name;
    int field_4_min;
    int field_4_max;
    
    // Field 5
    String field_5_name;
    int field_5_min;
    int field_5_max;
    
    // Message
    String mes;
    
    // Days
    int days;
	
    public CReqDataPayload(String adr, 
                           String mes,
                           String field_1_name, int field_1_min, int field_1_max, 
                           String field_2_name, int field_2_min, int field_2_max, 
                           String field_3_name, int field_3_min, int field_3_max, 
                           String field_4_name, int field_4_min, int field_4_max, 
                           String field_5_name, int field_5_min, int field_5_max, 
                           int days)
    {
    	   // Superclass
  	   super(adr);
  	   
           // Message
           this.mes=mes;
           
  	   // Field 1
  	   this.field_1_name=field_1_name;
           this.field_1_min=field_1_min;
           this.field_1_max=field_1_max;
           
           // Field 2
  	   this.field_2_name=field_2_name;
           this.field_2_min=field_2_min;
           this.field_2_max=field_2_max;
           
           // Field 3
  	   this.field_3_name=field_3_name;
           this.field_3_min=field_3_min;
           this.field_3_max=field_3_max;
           
           // Field 4
  	   this.field_4_name=field_4_name;
           this.field_4_min=field_4_min;
           this.field_4_max=field_4_max;
  	   
           // Field 5
  	   this.field_5_name=field_5_name;
           this.field_5_min=field_5_min;
           this.field_5_max=field_5_max;
           
  	   // Expires
	   this.days=days;
	   
  	   // Hash
   	   hash=UTILS.BASIC.hash(this.getHash()+
                                 this.mes+
   		 	         this.field_1_name+String.valueOf(this.field_1_min)+String.valueOf(this.field_1_max)+
                                 this.field_2_name+String.valueOf(this.field_2_min)+String.valueOf(this.field_2_max)+
                                 this.field_3_name+String.valueOf(this.field_3_min)+String.valueOf(this.field_3_max)+
                                 this.field_4_name+String.valueOf(this.field_4_min)+String.valueOf(this.field_4_max)+
                                 this.field_5_name+String.valueOf(this.field_5_min)+String.valueOf(this.field_5_max)+
                                 String.valueOf(days));
           
           // Sign
           this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {
    	// Super class
    	CResult res=super.check(block);
    	if (res.passed==false) return res;
    	
    	 // Sealed address ?
         if (UTILS.BASIC.hasAttr(this.target_adr, "ID_SEALED"))
              return new CResult(false, "Target address is sealed.", "CAddSignPayload", 104);
           
        // Message
        this.mes=UTILS.BASIC.base64_decode(this.mes);
        if (!this.mes.equals(""))
          if (this.mes.length()<5 || this.mes.length()>250)
                return new CResult(false, "Invalid message length", "CReqDataPayload", 47);
        
        // Field 1
        if (!this.field_1_name.equals(""))
        {
            // Decode
            this.field_1_name=UTILS.BASIC.base64_decode(this.field_1_name);
            
            // Length
            if (this.field_1_name.length()<3 || this.field_1_name.length()>50)
               return new CResult(false, "Invalid field 1 name length", "CReqDataPayload", 47);
            
            // Min value
            if (this.field_1_min<1 || this.field_1_min>250)
               return new CResult(false, "Invalid field 2 min value", "CReqDataPayload", 47);
            
            // Max value
            if (this.field_1_max<1 || this.field_1_max>250)
               return new CResult(false, "Invalid field 2 max value", "CReqDataPayload", 47);
        }
        
        // Field 2
        if (!this.field_2_name.equals(""))
        {
            // Decode
            this.field_2_name=UTILS.BASIC.base64_decode(this.field_2_name);
            
            // Length
            if (this.field_2_name.length()<3 || this.field_2_name.length()>50)
               return new CResult(false, "Invalid field 1 name length", "CReqDataPayload", 47);
            
            // Min value
            if (this.field_2_min<1 || this.field_2_min>250)
               return new CResult(false, "Invalid field 2 min value", "CReqDataPayload", 47);
            
            // Max value
            if (this.field_2_max<1 || this.field_2_max>250)
               return new CResult(false, "Invalid field 2 max value", "CReqDataPayload", 47);
        }
        
        // Field 3
        if (!this.field_3_name.equals(""))
        {
            // Decode
            this.field_3_name=UTILS.BASIC.base64_decode(this.field_3_name);
            
            // Length
            if (this.field_3_name.length()<3 || this.field_3_name.length()>50)
               return new CResult(false, "Invalid field 3 name length", "CReqDataPayload", 47);
            
            // Min value
            if (this.field_3_min<1 || this.field_3_min>250)
               return new CResult(false, "Invalid field 3 min value", "CReqDataPayload", 47);
            
            // Max value
            if (this.field_3_max<1 || this.field_3_max>250)
               return new CResult(false, "Invalid field 3 max value", "CReqDataPayload", 47);
        }
        
        // Field 4
        if (!this.field_4_name.equals(""))
        {
            // Decode
            this.field_4_name=UTILS.BASIC.base64_decode(this.field_4_name);
            
            // Length
            if (this.field_4_name.length()<3 || this.field_4_name.length()>50)
               return new CResult(false, "Invalid field 4 name length", "CReqDataPayload", 47);
            
            // Min value
            if (this.field_4_min<1 || this.field_4_min>250)
               return new CResult(false, "Invalid field 4 min value", "CReqDataPayload", 47);
            
            // Max value
            if (this.field_4_max<1 || this.field_4_max>250)
               return new CResult(false, "Invalid field 4 max value", "CReqDataPayload", 47);
        }
        
        // Field 5
        if (!this.field_5_name.equals(""))
        {
            // Decode
            this.field_5_name=UTILS.BASIC.base64_decode(this.field_5_name);
            
            // Length
            if (this.field_5_name.length()<3 || this.field_5_name.length()>50)
               return new CResult(false, "Invalid field 4 name length", "CReqDataPayload", 47);
            
            // Min value
            if (this.field_5_min<1 || this.field_5_min>250)
               return new CResult(false, "Invalid field 4 min value", "CReqDataPayload", 47);
            
            // Max value
            if (this.field_5_max<1 || this.field_5_max>250)
               return new CResult(false, "Invalid field 4 max value", "CReqDataPayload", 47);
        }
        
    	// Hash
  	String h=UTILS.BASIC.hash(this.getHash()+
                                  UTILS.BASIC.base64_encode(this.mes)+
   		 	          UTILS.BASIC.base64_encode(this.field_1_name)+String.valueOf(this.field_1_min)+String.valueOf(this.field_1_max)+
                                  UTILS.BASIC.base64_encode(this.field_2_name)+String.valueOf(this.field_2_min)+String.valueOf(this.field_2_max)+
                                  UTILS.BASIC.base64_encode(this.field_3_name)+String.valueOf(this.field_3_min)+String.valueOf(this.field_3_max)+
                                  UTILS.BASIC.base64_encode(this.field_4_name)+String.valueOf(this.field_4_min)+String.valueOf(this.field_4_max)+
                                  UTILS.BASIC.base64_encode(this.field_5_name)+String.valueOf(this.field_5_min)+String.valueOf(this.field_5_max)+
                                  String.valueOf(days));
        
        // Hash
        if (!hash.equals(h))
    	   return new CResult(false, "Invalid hash", "CReqDataPayload", 47);
        
  	// Return
  	return new CResult(true, "Ok", "CReqDataPayload", 63);
    }
    
    public CResult commit(CBlockPayload block)
    {
 	// Check
        CResult res=this.check(block);
 	if (res.passed==false) return res;
 	  
        // Superclass
        super.commit(block);
        
        // Data exist ?
        try
    	{ 
	  // Load transaction data
            Statement s=UTILS.DB.getStatement();
    	          
            // Finds the user
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM req_data "
                                       + "WHERE adr='"+this.target_adr+"'");
           
            // Has data
            if (UTILS.DB.hasData(rs))
               UTILS.DB.executeUpdate("UPDATE req_data "
                                       + "SET mes='"+UTILS.BASIC.base64_encode(this.mes)+"', "
                                           + "field_1_name='"+UTILS.BASIC.base64_encode(this.field_1_name)+"', "
                                           + "field_1_min='"+this.field_1_min+"', "
                                           + "field_1_max='"+this.field_1_max+"', "
                                           + "field_2_name='"+UTILS.BASIC.base64_encode(this.field_2_name)+"', "
                                           + "field_2_min='"+this.field_2_min+"', "
                                           + "field_2_max='"+this.field_2_max+"', "
                                           + "field_3_name='"+UTILS.BASIC.base64_encode(this.field_3_name)+"', "
                                           + "field_3_min='"+this.field_3_min+"', "
                                           + "field_3_max='"+this.field_3_max+"', "
                                           + "field_4_name='"+UTILS.BASIC.base64_encode(this.field_4_name)+"', "
                                           + "field_4_min='"+this.field_4_min+"', "
                                           + "field_4_max='"+this.field_4_max+"', "
                                           + "field_5_name='"+UTILS.BASIC.base64_encode(this.field_5_name)+"', "
                                           + "field_5_min='"+this.field_5_min+"', "
                                           + "field_5_max='"+this.field_5_max+"' "
                                     + "WHERE adr='"+this.target_adr+"'");
            else
                UTILS.DB.executeUpdate("INSERT INTO req_data(adr, "
                                                          + "mes, "
                                                          + "field_1_name, "
                                                          + "field_1_min, "
                                                          + "field_1_max, "
                                                          + "field_2_name, "
                                                          + "field_2_min, "
                                                          + "field_2_max, "
                                                          + "field_3_name, "
                                                          + "field_3_min, "
                                                          + "field_3_max, "
                                                          + "field_4_name, "
                                                          + "field_4_min, "
                                                          + "field_4_max, "
                                                          + "field_5_name, "
                                                          + "field_5_min, "
                                                          + "field_5_max) VALUES('"
                                                          +this.target_adr+"', '"
                                                          +UTILS.BASIC.base64_encode(this.mes)+"', '"
                                                          +UTILS.BASIC.base64_encode(this.field_1_name)+"', '"
                                                          +this.field_1_min+"', '"
                                                          +this.field_1_max+"', '"
                                                          +UTILS.BASIC.base64_encode(this.field_2_name)+"', '"
                                                          +this.field_2_min+"', '"
                                                          +this.field_2_max+"', '"
                                                          +UTILS.BASIC.base64_encode(this.field_3_name)+"', '"
                                                          +this.field_3_min+"', '"
                                                          +this.field_3_max+"', '"
                                                          +UTILS.BASIC.base64_encode(this.field_4_name)+"', '"
                                                          +this.field_4_min+"', '"
                                                          +this.field_4_max+"', '"
                                                          +UTILS.BASIC.base64_encode(this.field_5_name)+"', '"
                                                          +this.field_5_min+"', '"
                                                          +this.field_5_max+"')");
	
            // Commit
    	    UTILS.BASIC.applyAdrAttr(this.target_adr, 
    			             "ID_REQ_DATA", 
	                             "", 
	                             "", 
	                             "",
	                             "",
                                     "",
                                     "",
                                     this.days,  
                                     block.block);
    	
        }
    	catch (SQLException ex)
    	{
    	       UTILS.LOG.log("SQLException", ex.getMessage(), "CMesPayload.java", 158);
    	}
        
    	// Return 
    	return new CResult(true, "Ok", "CReqDataPayload", 70);
     }
}
