// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.*;
import java.util.*;

public class CSettings 
{
	// Properties
	public Properties settings;
	
	// Min peers
	public int min_peers=1;
	
	// Headless
	public boolean headless=true;
	
	// Commit any block
	public boolean commit_any_block=false;
	
	// Display WIP
	public boolean display_wip=false;
	
	// Display sync
	public boolean sync=true;
	
        // Relay
        public boolean relay=true;
        
        // Display queries
        public boolean display_queries=false;
        
        // Initiate blocks
        public boolean init_blocks=false;
        
        // Port
        public int port=10000;
        
	// DB type
	public String db;
	
	// DB Database name
	public String db_name;
	
	// DB User
	public String db_user;
	
	// DB pass
	public String db_pass;
        
        // Wallet pass
        public String pass;
       
	
  public CSettings() throws Exception
  {
	  // Check if settings file exists
	  File f=new File(UTILS.WRITEDIR+"settings.txt");
	  
	  // Settings file does not exist
	  if (f.exists()==false)
	  {
              // Message to console
	      UTILS.CONSOLE.write("Initializing settings...");
		  
	      // Write inital settings
              try
	      {
		  // Output stream  
                  FileOutputStream f_out=new FileOutputStream(f);
		  
                  // New properties
		  settings=new Properties();
                  
                  // Headless
		  settings.setProperty("headless", "false");
		  
                  // Port
                  settings.setProperty("port", "10000");
		  
                  // Get in sync
                  settings.setProperty("sync", "true");
		  
                  // Check DB tables integrity
                  settings.setProperty("check_tables", "true");
		  
                  // Fill database with initial data
                  settings.setProperty("fill_db", "true");
		  
                  // When set to true, all broadcast packets are rebroadcasted
                  settings.setProperty("relay", "false");
                  
                  // When true will try to broadcast new blocks
		  settings.setProperty("broadcast_blocks", "true");
                  
                   // Database type
		  settings.setProperty("db", "mysql");
                  
                   // Database name
		  settings.setProperty("db_name", "wallet_lite");
                  
                   // Database user
		  settings.setProperty("db_user", "root");
                  
                  // Wallet pass
		  settings.setProperty("pass", "");
                  
                   // Database pass
		  settings.setProperty("db_pass", "");
                  
		  // Minimum peers
		  settings.setProperty("min_peers", "3");
		  this.min_peers=3;
               
		    
                    // Write settings
		    settings.store(f_out, "VWallet Properties");
		    f_out.close();
		  }
		  catch (FileNotFoundException e) 
		  { 
			  UTILS.LOG.log("ERROR", "FileNotFoundException", "CSettings.java", 26); 
		  }
		  catch (IOException e) 
		  { 
			  UTILS.LOG.log("ERROR", "IOException", "CSettings.java", 27); 
		  }
	  }
	  
	  // Load Settings
	  try
	  {
		  FileInputStream f_in=new FileInputStream(f);
	    
		  settings=new Properties();
		  settings.load(f_in);
	      f_in.close();
	      
	      // Minimum peers
	      if (this.settings.containsKey("min_peers"))
	         this.min_peers=Integer.parseInt(this.settings.getProperty("min_peers"));
	      
	      // Headless
	      if (this.settings.containsKey("headless"))
	         this.headless=Boolean.parseBoolean((this.settings.getProperty("headless")));
	      
	      // Commit any block
	      if (this.settings.containsKey("commit_any_block"))
	         this.commit_any_block=Boolean.parseBoolean((this.settings.getProperty("commit_any_block")));
	      
	      // Display wip
	      if (this.settings.containsKey("display_wip"))
	         this.display_wip=Boolean.parseBoolean((this.settings.getProperty("display_wip")));
	      
	      // Sync
	      if (this.settings.containsKey("sync"))
	         this.sync=Boolean.parseBoolean((this.settings.getProperty("sync")));
              
              // Display queries
	      if (this.settings.containsKey("display_queries"))
	          this.display_queries=Boolean.parseBoolean((this.settings.getProperty("display_queries")));
              
               // Initiate blocks
	      if (this.settings.containsKey("init_blocks"))
	          this.init_blocks=Boolean.parseBoolean((this.settings.getProperty("init_blocks")));
              
              // Relay
	      if (this.settings.containsKey("relay"))
	         this.relay=Boolean.parseBoolean((this.settings.getProperty("relay")));
              
              // Port
	      if (this.settings.containsKey("port"))
	         this.port=Integer.parseInt((this.settings.getProperty("port")));
	      
              // Password
	      if (this.settings.containsKey("pass"))
	         this.pass=(this.settings.getProperty("pass"));
              
	      // DB type
	      if (this.settings.containsKey("db"))
	         this.db=(this.settings.getProperty("db"));
	      
	      // DB name
	      if (this.settings.containsKey("db_name"))
	         this.db_name=(this.settings.getProperty("db_name"));
	      
	      // DB user
	      if (this.settings.containsKey("db_user"))
	         this.db_user=(this.settings.getProperty("db_user"));
	      
	      // DB pass
	      if (this.settings.containsKey("db_pass"))
	         this.db_pass=(this.settings.getProperty("db_pass"));
              
	  }
	  catch (IOException e) 
          { 
              UTILS.LOG.log("ERROR", "IOException", "CSettings.java", 24); 
          }
  }
}