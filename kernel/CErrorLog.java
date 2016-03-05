// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

public class CErrorLog 
{
   //Output stream
   PrintWriter out;
   
   // Log messages to db also
   public boolean log_to_db=true;
   
   // Log message to console
   public boolean log_to_console=true;
   
   public CErrorLog() 
   {
       try
       {
         // Port busy ?
        if (portBusy()) System.exit(0);
       
       // Data directory 
       UTILS.WRITEDIR=this.getAppDataDirectory();
		  
       // Check version
       File ver=new File(UTILS.WRITEDIR+"ver.txt");
		  
        // Settings file does not exist
           if (ver.exists()==false)
	   {
	       this.init();
	       FileUtils.writeStringToFile(new File(UTILS.WRITEDIR+"ver.txt"), "0.0.1");
	       FileUtils.deleteQuietly(new File(UTILS.WRITEDIR+"wallet.msk"));
	    }
            else
            {
	        String v=FileUtils.readFileToString(new File(UTILS.WRITEDIR+"ver.txt"));
			  
	        if (!v.equals("0.0.1"))
	        {
		    this.init();
		    FileUtils.writeStringToFile(new File(UTILS.WRITEDIR+"ver.txt"), "0.0.1");  
	        }
	    }
           
           // Check if error log file exists
	File f=new File(UTILS.WRITEDIR+"err_log.txt");
		  
	
	   FileOutputStream f_out=new FileOutputStream(f, true);
           out=new PrintWriter(f_out);
	}
        catch (Exception ex)
        {
			  
	}
		  
	
       
    }
	  
   public boolean portBusy() throws Exception
         {
             try 
             {
               Socket s = new Socket("127.0.0.1", 10000);
               s.close();
               return true;
             }
             catch(Exception e) 
             {
                return false;
             }
         }
   
	  public void init()
	  {
		  try
		  {
		    FileUtils.deleteDirectory(new File(UTILS.WRITEDIR+"wallet.tmp"));
		    FileUtils.deleteQuietly(new File(UTILS.WRITEDIR+"settings.txt"));
		    FileUtils.deleteQuietly(new File(UTILS.WRITEDIR+"wallet.script"));
		    FileUtils.deleteQuietly(new File(UTILS.WRITEDIR+"wallet.properties"));
		    FileUtils.deleteQuietly(new File(UTILS.WRITEDIR+"wallet.log"));
		  }
		  catch (IOException ex)
		  {
			  
		  }
	  }
	  
	  public void log(String type, String mes, String file, int line)
	  {
              try
              {
              if (mes==null) mes="No Message";
              
	      Calendar cal = Calendar.getInstance();
	      cal.getTime();
	      SimpleDateFormat sdf = new SimpleDateFormat("MMM,DD,yyyy HH:mm:ss");
	      
              // Write to file
	      String w="("+sdf.format(System.currentTimeMillis())+") "+type+" : "+mes+"("+file+","+String.valueOf(line)+")";
	      out.println(w);
	      out.flush();
              
              // Write to console
              if (this.log_to_console==true) 
                  System.out.println(w);
	      
              // Write to DB
              if (this.log_to_db==true && UTILS.DB!=null)
                 UTILS.DB.executeUpdate("INSERT INTO err_log(type, mes, file, line, tstamp) "
                                     + "VALUES('"+type+"', '"+UTILS.BASIC.base64_encode(mes)+"', '"+file+"', '"+String.valueOf(line)+"', '"+UTILS.BASIC.tstamp()+"')");
              }
              catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
              }
	  
	  public void close() throws Exception
	  {
		  out.close();
	  }
	  
	  public String getAppDataDirectory()  throws Exception
		{

		    String appDataDirectory;
		    
		    try 
		    {
		        appDataDirectory = System.getenv("APPDATA"); 
		        
		        if (appDataDirectory != null) 
		        {
		            appDataDirectory += File.separator + "Wallet" + File.separator;
		        }
		        else 
		        { 
		            appDataDirectory = System.getenv("HOME"); 
		            if (appDataDirectory != null) 
		            {
		                appDataDirectory +=  File.separator + "Wallet" + File.separator;
		            }
		            else
		            { 
		                throw new Exception("Could not access APPDATA or HOME environment variables");
		            }
		        }
		    }
		    catch(Exception e) {
		        e.printStackTrace();
		        appDataDirectory = "";
		    }

		    if (appDataDirectory != null && appDataDirectory.length() > 0) 
		    {
		    	 File f=new File(appDataDirectory);
		         
		    	 if (!f.exists())	
		    	 {
		    	   try 
		           {
		              File dir = new File(appDataDirectory);
		              dir.mkdir();
		              
		              // Blocks directory
		              dir = new File(appDataDirectory+"blocks"+File.separator);
		              dir.mkdir();
		              
		              // Multisig directory
		              dir = new File(appDataDirectory+"multisig"+File.separator);
		              dir.mkdir();
		           }
		           catch(Exception e) 
		           {
		               e.printStackTrace();
		            }
		    	  }
		    }
		    
		    return appDataDirectory;
		}
}
