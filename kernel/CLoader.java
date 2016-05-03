// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import interfata.misc.CPanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CLoader extends Thread
{
	// Url
	URL url;
	
	// InputStream
    InputStream is = null;
    
    // Buffer
    BufferedReader br;
    
    // Line
    String line;
    
    // Data
    String data="";
    
    // Password
    String pass;
    
    // Parent
    CPanel parent=null;
    
    // Params
    String params_str="";
    
    // Connection
    URLConnection conn;
    
    // Params
    Map<String,String> params = new LinkedHashMap<String, String>();
    
	public CLoader(String link, String pass)  throws Exception
	{
		 try 
		 {
                    // URL
		    this.url=new URL(link);
		    
                    // Connection
                    this.conn = url.openConnection();
                    
                    // Pass
                    this.pass=pass;
		 }
		 catch (MalformedURLException ex) 
	     {
	        UTILS.LOG.log("MalformedURLException", ex.getMessage(), "CLoader.java", 34);
	     } 
		 catch (IOException ex) 
	     {
	        UTILS.LOG.log("IOException", ex.getMessage(), "CLoader.java", 34);
	     } 
        
	}
	
	public void addParam(String name, String value) throws Exception
	{
		params.put(name, value);
                
                // Add to params string
                if (!pass.equals("")) this.params_str.concat(name+value);
	}
	
	public CLoader(CPanel parent, String link)  throws Exception
	{
		 try 
		 {
		    this.url=new URL(link);
		    this.parent=parent;
		 }
		 catch (MalformedURLException ex) 
	     {
	        UTILS.LOG.log("MalformedURLException", ex.getMessage(), "CLoader.java", 34);
	     } 
	}
	
	
	
	 public void run()
	 {	 
		 
		 try 
		 {
			 if (this.params.size()>0)
			 {
                             // CRC enabled ?
                             if (!pass.equals("")) this.addParam("crc", UTILS.BASIC.hash(this.params_str+this.params_str));
                           
                             
                            // String builder     
                            StringBuilder postData = new StringBuilder();
		             
                             // Load params
                             for (Map.Entry<String,String> param : params.entrySet()) 
		             {
		                if (postData.length() != 0) postData.append('&');
		                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
		                postData.append('=');
		                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		              }
		    
		          byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                          HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		          conn.setRequestMethod("POST");
		          conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		          conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		          conn.setDoOutput(true);
		          conn.getOutputStream().write(postDataBytes);
		       
		          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));  
			     while ((line = in.readLine()) != null) data=data+line;
			 }
			 else
			 {
		            br = new BufferedReader(new InputStreamReader(url.openStream()));
		            while ((line = br.readLine()) != null) data=data+line;
			 }
		 } 
		 catch (Exception ex) 
		 {
		    if (parent!=null) this.parent.loaderError();
		 }
		 
		 if (parent!=null) 
			 this.parent.loaded(this.data);
		 
	 }
}
