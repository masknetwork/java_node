package wallet.agents.VM.sys.events.signal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;

public class CSignals 
{
    // Signals
    ArrayList<CSignal> signals=new ArrayList<CSignal>();
            
    public CSignals(long agentID, boolean sandbox) throws Exception
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
      
      if (rs.getString("signals").length()>5)
         this.loadSignals(rs.getString("signals")); 
    }
    
    public void addSignal(CSignal sig) throws Exception
    {
        this.signals.add(sig);
    }
    
    public void loadSignals(String data) throws Exception
    {
       JSONObject obj = new JSONObject(data);
       JSONArray signals = obj.getJSONArray("signals");
       
       for (int a=0; a<=signals.length()-1; a++)
       {
          // Load signal ID
          JSONObject sig=signals.getJSONObject(a);
          
          // Create signal
          CSignal s=new CSignal(sig.getString("ID"));
          
          // Load params
          JSONArray par = sig.getJSONArray("params");
          
          // Load params
          for (int b=0; b<=par.length()-1; b++)
          {
              // Load param
              JSONObject parameter=par.getJSONObject(b);
              
              // Load param
              CSignalParam p=new CSignalParam(parameter.getString("ID"), 
                                              parameter.getString("data_type"),
                                              parameter.getDouble("min"), 
                                              parameter.getDouble("max"));
              
              // Add param
              s.addParam(p);
          }
              
          // Add signal
          this.signals.add(s);
       }
    }
}
