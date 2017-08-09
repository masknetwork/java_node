package wallet.kernel;

import java.io.File;
import java.net.Socket;

public class CConstants 
{
   // Minimum transaction amount
   public double MIN_TRANS_AMOUNT=0.0001;  
   
   // Minimum fee
   public double MIN_FEE_AMOUNT=0.0001;  
   
   // Fee multiplier
   public double FEE_MULT=0.0001;  
   
   public CConstants() throws Exception
   {
      UTILS.WRITEDIR=this.getAppDataDirectory();
   }
   
   public boolean portBusy() throws Exception
   {
        try 
        {
            // Connects to local
            Socket s = new Socket("127.0.0.1", UTILS.SETTINGS.port);
               
            // Return
            System.out.println("Port busy. Exiting !!!");
            System.exit(0);
        }
        catch(Exception ex) 
        {
            return false;
        }
        
        return false;
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
