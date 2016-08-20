// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;

public class CSettings 
{
  // Port
  public int port=10000;
  
  // Chk block
  public long chk_blocks=10;
  
  // Database type
  public String db_engine="mysql";
  
  // Database name
  public String db_name="wallet";
  
  // DB username
  public String db_user="root";
  
  // DB pass
  public String db_pass="";
  
  // Wallet file pass
  public String wallet_pass="";
  
  // Min peers
  public int min_peers;
  
  // Settings
  Properties settings = new Properties();
  
  public CSettings() throws Exception
  { 
      // Check if settings file exists
      File f=new File(UTILS.WRITEDIR+"wallet.ini");
		  
      // File exist ?
      if (f.exists()==false)
      {
          FileUtils.writeStringToFile(f, "", "UTF-8");
      }
      else
      {
          // Load settings
          settings.load(new FileInputStream(UTILS.WRITEDIR+"wallet.ini"));
          
          // Port
          if (settings.containsKey("port")) 
              this.port=Integer.parseInt(settings.getProperty("port"));
          
          // Db engine
          if (settings.containsKey("db")) 
              this.db_engine=settings.getProperty("db_engine");
          
          // Db user
          if (settings.containsKey("db_user")) 
              this.db_user=settings.getProperty("db_user");
          
          // Db pass
          if (settings.containsKey("db_pass")) 
              this.db_pass=settings.getProperty("db_pass");
  
      }
  }
  
  public String getWalletPass()
  {
      return this.wallet_pass;
  }
}