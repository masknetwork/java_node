// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import org.json.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.io.IOUtils;
import wallet.network.packets.trade.feeds.CFeedPacket;
import wallet.network.packets.trade.feeds.CFeedPayload;

public class CFeedSource  extends Thread
{
    // Address
    String adr;
    
    // Link
    String link;
    
    // Feed symbol
    String feed_symbol;
    
    // Data packet
    CFeedPacket packet;
    
    // Payload
    CFeedPayload payload;
    
    
    public CFeedSource(String adr, String link, String feed_symbol) throws Exception
    {
         // Address
           this.adr=adr;
    
           // Link
           this.link=link;
    
           // Feed symbol
           this.feed_symbol=feed_symbol;
         
    }
    
    public void run()
    {
        String symbol;
        
         
        
        try
        {
            // // Statement
         Statement s=UTILS.DB.getStatement();
         
           // URL
           URL url = new URL(this.link);
           
           // Connection
           URLConnection con = url.openConnection();
           
           // Agent
           con.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
           
           // Input stream
           InputStream in = con.getInputStream();   
           
           // Body
           String data = IOUtils.toString(in, con.getContentEncoding());
           
           // Object
           JSONObject obj = new JSONObject(data); 
           
           // Response
           JSONArray response = obj.getJSONArray("response");           
           
          
           // Load branches
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_branches "
                                      + "WHERE feed_symbol='"+this.feed_symbol+"'");
           
           // Create packet
           packet=new CFeedPacket(this.adr, this.feed_symbol, "", "");
               
           // Payload
           payload=new CFeedPayload(this.adr, this.feed_symbol);
           
           // Load data
           while (rs.next())
           {
               // Load value
               for (int a=0; a<=response.length()-1; a++)
               {
                  // Symbol
                  symbol=rs.getString("symbol");
                  
                  if (response.getJSONObject(a).getString("symbol").equals(symbol))
                  {
                      // Get the price
                      double price=response.getJSONObject(a).getDouble("price");
                      
                      // Status
                      String status=response.getJSONObject(a).getString("status");
                      
                      // Add to payload
                      payload.addVal(rs.getString("symbol"), price, status);
                  }
               }
           }
           
            // Sign payload
            payload.doSeal();
            
            // Load payload
            packet.addPayload(payload);
            
            // Broadcast
            UTILS.NETWORK.broadcast(packet);
            
            // Update
            UTILS.DB.executeUpdate("INSERT INTO feeds_sources_res(feed, "
                                                               + "result, "
                                                               + "tstamp) "
                                                   + "VALUES('"+this.feed_symbol+"', "
                                                               + "'ID_OK', '"
                                                               +UTILS.BASIC.tstamp()+"')");
            
            rs.close();
            s.close();
        }
        catch (Exception e) 
	{ 
            
        }
        
       
    }
    
    public void feedErr(String mes) throws Exception
    {
        // Null ?
        if (mes==null) mes="null";
            
        // Update
        UTILS.DB.executeUpdate("INSERT INTO feeds_sources_res(feed, "
                                                           + "result, "
                                                           + "err_mes, "
                                                           + "tstamp) "
                                               + "VALUES('"+this.feed_symbol+"', "
                                                           + "'ID_ERR', '"
                                                           +UTILS.BASIC.base64_encode(mes)+"', '"
                                                           +UTILS.BASIC.tstamp()+"')");
    }
}
