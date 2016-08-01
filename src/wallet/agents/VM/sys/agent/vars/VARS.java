package wallet.agents.VM.sys.agent.vars;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.kernel.UTILS;

public class VARS 
{
  JSONArray globals;
    
   public VARS(long agentID, boolean sandbox) throws Exception
   {
      
      // Statement
      
      
      // Result set
      ResultSet rs;
      
      // Result set
      if (sandbox==true)
      rs=UTILS.DB.executeQuery("SELECT * "
                        + "FROM agents_mine "
                       + "WHERE ID='"+agentID+"'");
      else
      rs=UTILS.DB.executeQuery("SELECT * "
                        + "FROM agents "
                       + "WHERE aID='"+agentID+"'");
      
      // Next
      rs.next();
      
      if (rs.getString("globals").length()>5)
      {
           JSONObject obj = new JSONObject(UTILS.BASIC.base64_decode(rs.getString("globals")));
           globals = obj.getJSONArray("globals");
      }
      
   }
   
   public boolean exist(String varID) throws Exception
   {
       for (int a=0; a<=this.globals.length()-1; a++)
       {
           JSONObject var = globals.getJSONObject(a);
           if (var.getString("ID").equals("varID"))
               return true;
       }
       
       return false;
   }
   
   public CCell getData(String varID) throws Exception
   {
       // Search variable
       for (int a=0; a<=this.globals.length()-1; a++)
       {
           // Load object
           JSONObject var = globals.getJSONObject(a);
           
           // ID
           if (var.getString("ID").equals(varID))
           {
                // String
                if (var.getString("data_type").equals("string"))  
                    return new CCell(var.getString("value"));
                
                else if (var.getString("data_type").equals("double"))  
                    return new CCell(var.getDouble("value"));
                
                else if (var.getString("data_type").equals("long"))  
                    return new CCell(var.getLong("value"));
           }
       }
       
       throw new Exception("Invalid global variable "+varID);
   } 
   
   
}
