package wallet.network.packets.app;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CUpdateSettingsPayload extends CPayload
{
    // AppID
    long appID;
    
    // Settings
    String settings;
    
    public CUpdateSettingsPayload(String adr, 
                                  long appID, 
                                  String settings) throws Exception
    {
        // Constructor
        super(adr);
        
        // App ID
        this.appID=appID;
        
        // Settings
        this.settings=settings;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash()+
                                   this.appID+
                                   this.settings);
        
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
        
        // Load app data
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM agents "
                                   + "WHERE sealed=0 "
                                     + "AND aID='"+this.appID+"' "
                                     + "AND adr='"+this.target_adr+"'");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
            throw new Exception ("Invalid appID");
        
        // Next
        rs.next();
        
        // Load variables
        String vars_src=UTILS.BASIC.base64_decode(rs.getString("globals"));
        
        // Load root
        JSONObject root = new JSONObject(vars_src); 
        
        // Load vars
        JSONArray vars = root.getJSONArray("globals"); 
        
        // Parse
        for (int a=0; a<=vars.length()-1; a++)
        {
            // Load variable
            JSONObject var = vars.getJSONObject(a); 
            
            // Check
            if (!this.checkVar(var.getString("ID"), 
                               var.getString("data_type"), 
                               var.getDouble("min"), 
                               var.getDouble("max")))
                throw new Exception("Invalid variable "+var.getString("ID"));
        }
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                   this.appID+
                                   this.settings);
        
        // Hash match ?
        if (!h.equals(hash))
            throw new Exception("Invalid hash");
        
        // Return
  	return new CResult(true, "Ok", "CDeployAppNetPayload", 67); 
    }
    
    public boolean checkVar(String ID, 
                            String data_type, 
                            double min, 
                            double max) throws Exception 
    {
        // Decode
        String dec=UTILS.BASIC.base64_decode(this.settings); 
        
        // Load root
        JSONObject root = new JSONObject(dec); 
        
        // Load vars
        JSONArray vars = root.getJSONArray("vars");
        
        // Find var
        for (int a=0; a<=vars.length()-1; a++)
        {
            // Load variable
            JSONObject var = vars.getJSONObject(a);
            
            // Match ?
            if (var.getString("ID").equals(ID))
            {
                // Load value
                if (data_type.equals("double"))
                {
                    // Load double value
                    double val=var.getDouble("value");
                    
                    // Min and max
                    if (val<min || val>max)
                        return false;
                }
                else if (data_type.equals("long"))
                {
                    // Load double value
                    long val=var.getLong("value");
                    
                    // Min and max
                    if (val<min || val>max)
                        return false;
                }
                else if (data_type.equals("string"))
                {
                    // Load double value
                    String val=var.getString("value");
                    
                    // Min and max
                    if (val.length()<min || val.length()>max)
                        return false;
                }
                else throw new Exception("Internal error");
            }
        }
        
        return true;
    }
    
    public String getVal(String ID) throws Exception
    {
        // Decode
        String dec=UTILS.BASIC.base64_decode(this.settings);
        
        // Load root
        JSONObject root = new JSONObject(dec); 
        
        // Load vars
        JSONArray vars = root.getJSONArray("vars");
        
      
        // Find var
        for (int a=0; a<=vars.length()-1; a++)
        {
            // Load variable
            JSONObject var = vars.getJSONObject(a);
            
            // Match ?
            if (var.getString("ID").equals(ID))
               return var.getString("value");
        }
        
        throw new Exception("Variable not found "+ID);
    }
    
    public String commitValues() throws Exception
    {
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load app data
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM agents "
                                   + "WHERE sealed=0 "
                                     + "AND aID='"+this.appID+"' "
                                     + "AND adr='"+this.target_adr+"'");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
            throw new Exception ("Invalid appID");
        
        // Next
        rs.next();
        
        // Load variables
        String vars_src=UTILS.BASIC.base64_decode(rs.getString("globals"));
        
        // Load root
        JSONObject root = new JSONObject(vars_src); 
        
        // Load vars
        JSONArray vars = root.getJSONArray("globals"); 
        
        // Find var
        for (int a=0; a<=vars.length()-1; a++)
        {
            // Load variable
            JSONObject var = vars.getJSONObject(a);
            
            // Replace
            var.put("value", this.getVal(var.getString("ID")));
        }
        
        // Write
        return root.toString();
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Commit parent
 	CResult res=super.commit(block);
 	if (res.passed==false) return res;
        
        // New globals
        String vars=this.commitValues();
        
        // Execute
        UTILS.DB.executeUpdate("UPDATE agents "
                                + "SET globals='"+UTILS.BASIC.base64_encode(vars)+"' "
                              + "WHERE aID='"+this.appID+"'");
        
        // Return
  	return new CResult(true, "Ok", "CDeployAppNetPayload", 67); 
    }
}
