// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.*;
import java.util.*;

public class CSettings 
{
  // Port
  public int port=10000;
  
  // Chk block
  public long chk_blocks=10;
  
  // Database type
  public String db="mysql";
  
  // Database name
  public String db_name="wallet";
  
  // DB username
  public String db_user="root";
  
  // DB pass
  public String db_pass="";
  
  // Sync
  public String sync="Y";
  
  // Wallet file pass
  public String pass="";
  
  // Min peers
  public int min_peers;
  
  public CSettings() throws Exception
  {
	  
  }
  
  public String getWalletPass()
  {
      return this.pass;
  }
}