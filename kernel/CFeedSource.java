// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import org.json.*;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
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
    
    
    public CFeedSource(String adr, 
                       String link, 
                       String feed_symbol) throws Exception
    {
         // Address
           this.adr=adr;
    
           // Link
           this.link=link;
    
           // Feed symbol
           this.feed_symbol=feed_symbol;
           
           // Check address
           if (!UTILS.BASIC.isAdr(adr))
               throw new Exception("Invalid adr - CFeedSource.java, 48");
        
            // Check link
            if (!UTILS.BASIC.isLink(link))
                throw new Exception("Invalid adr - CFeedSource.java, 51");
        
            // Check feed symbol
            if (!UTILS.BASIC.isSymbol(feed_symbol))
                throw new Exception("Invalid adr - CFeedSource.java, 56");
    }
    
    public void run()
    {
        String symbol;
        
        try
        {
            // URL
           URL url = new URL(this.link);
           
           // Connection
           URLConnection con = url.openConnection(); 
           
           // No cache
           con.setRequestProperty("Connection", "close");
           
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
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM feeds_branches "
                                             + "WHERE feed_symbol='"+this.feed_symbol+"'");
           
           // Has data ?
           if (!UTILS.DB.hasData(rs)) return;
           
           // Create packet
           packet=new CFeedPacket(this.adr, this.feed_symbol);
               
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
            
            // Close connection
            in.close();
        }
        catch (Exception e) 
	{ 
            System.out.println(e.getMessage());
        }
    }
}
