// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import wallet.kernel.UTILS;

public class CWebImageLoader extends Thread 
{
	// URL
	public String url;
	
	// Image
	public BufferedImage img=null;
        
        // Use proxy
        boolean use_proxy=false;
    
	public CWebImageLoader(String url) 
	{
		// Url
		this.url=url;
	}
	
	public void run() 
	{
	    try 
	    {
		Thread.sleep(Math.round(Math.random()*1000));
			
		// URL
		URL url = new URL(this.url);
		
                // Proxy
                SocketAddress address = new InetSocketAddress(this.url, 80);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
                
                // Length
                long len=url.openConnection().getContentLength();
                
                // Try proxy
                if (len<0) 
                {
                    len=url.openConnection(proxy).getContentLength();
                    this.use_proxy=true;
                }
                
                if (len<1000000 && len>1000)
                {
          	   // Read Image
                    if (this.use_proxy)
                    {
                      URLConnection con = url.openConnection(proxy);
                      InputStream inStream = con.getInputStream();
		      img = ImageIO.read(inStream);
                    }
                    else img = ImageIO.read(url);
                    
                   // Hash
                   String hash=UTILS.BASIC.hash(this.url).substring(0, 10);
                   
                   // File
                   String path=UTILS.WRITEDIR+"images/"+hash+".jpg";
                   
		   // Create file
		   File outputfile = new File(path);
		   
		   // Write image
		   ImageIO.write(img, "jpg", outputfile);
                   
                   // Image exist ?
                   UTILS.DB.executeUpdate("INSERT INTO imgs(hash, img) VALUES('"+hash+"', LOAD_FILE('"+path+"'))");
                }
                
                // Delete from stack
                UTILS.DB.executeUpdate("DELETE FROM imgs_stack WHERE url='"+this.url+"'");
                
	    } 
	    catch (IOException e) 
	    { 
		UTILS.LOG.log("IOException", e.getMessage(), "CWebImageLoader.java", 66); 
                
	    }
	    catch (InterruptedException ex) 
	    { 
		UTILS.LOG.log("InterruptedException", ex.getMessage(), "CWebImageLoader.java", 57); 
              
	    }
	    catch (Exception e) 
	    { 
	       UTILS.LOG.log("Exception", e.getMessage(), "CWebImageLoader.java", 67); 
              
	    }
		   
	}
}
