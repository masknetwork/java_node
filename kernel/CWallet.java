// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.crypto.SealedObject;

import org.apache.commons.io.FileUtils;

public class CWallet 
{
   // Addresses
   public ArrayList addresses=new ArrayList();
   
   // Output buffer
   ObjectOutputStream f_out;
   ObjectInputStream f_in;
   
   // Addresses number
   public int adr_no=0;
   
   // Wallet data
   String wallet="";
   
   // Password
   String pass;
   
   // Wallet Balance
   public double balance=0;
   
   // Last generated address
   public CAddress last_adr=null; 
   
   // Wallet
   File f;
   
   public CWallet() throws Exception
   {
	     // Check if settings file exists
		  f=new File(UTILS.WRITEDIR+"wallet.msk");
		  
		  // Records the password
		  this.pass=org.apache.commons.codec.digest.DigestUtils.sha512Hex(UTILS.SETTINGS.pass); 
		  
		  // File exist ?
		  if (f.exists()==false)
		  {
			  UTILS.CONSOLE.write("Creating initial wallet....");
			  
			  try
			  {
			    FileOutputStream out=new FileOutputStream(f);
			    f_out=new ObjectOutputStream(out);
			    
			    // Generate an address
			    CAddress adr=new CAddress();
			    adr.generate("secp224r1");
			    this.addresses.add(adr);
			    
			    // Insert address
		            UTILS.DB.executeUpdate("INSERT INTO my_adr(userID, adr) "
				 		             + "VALUES('1', '"+adr.getPublic()+"')");
			    
			    // Save wallet
			    save();
			  }
			  catch (FileNotFoundException e) 
			  { 
				  UTILS.LOG.log("FileNotFoundException", e.getMessage(), "CWallet", 72);  
			  }
			  catch (IOException e) 
			  { 
				  UTILS.LOG.log("IOException", e.getMessage(), "CWallet", 73);  
			  }
		  }
		  else
		  {
		     try
		     {
		         // Input Streqm
		    	 String wallet = FileUtils.readFileToString(f, "UTF-8");
		    	 wallet=UTILS.AES.decrypt(wallet, UTILS.SETTINGS.pass);
		       
		       // Load addresses
		       String[] s=wallet.split("\\*");
		      
		       
		       for (int a=0; a<=s.length-1; a++)
		       {
		    	   String[] adr=s[a].split(",");
		    	   CAddress address=new CAddress(adr[0], adr[1], adr[2], Float.parseFloat(adr[3])); 
		    	   this.addresses.add(address);
		    	   this.balance=this.balance+address.balance;
		       }
		     }
		     catch (FileNotFoundException e) 
		     { 
		    	 UTILS.LOG.log("ERROR", e.getMessage(), "CWallet", 87); 
		     }
		     catch (IOException e) 
		     { 
		    	 UTILS.LOG.log("ERROR", e.getMessage(), "CWallet", 88);  
		     }
		     catch(Exception ex) 
		     {  
		    	 UTILS.LOG.log("ERROR", ex.getMessage(), "CWallet", 90);  
		     }
		  }
     }
   
   public void checkForNewAdr() throws Exception
   {
	// Check for new adddresses
	for (int a=1; a<=100; a++)
	{   
            if (UTILS.SETTINGS.settings.containsKey("add_adr_"+String.valueOf(a))) 
            {
    	        // Split address
		String ad=UTILS.SETTINGS.settings.getProperty("add_adr_"+String.valueOf(a));
		String v[]=ad.split("\\,");
		 
		   
    	    if (!this.isMine(v[0]))
    	    {
    		   CAddress adr=new CAddress();
    		   adr.importAddress("root", v[0], v[1], v[2]);
    	    }
         }
	   }
   }
   
     
     public void save() throws Exception
     {
    	 String w="";
    	 
    	 // Get wallet data
    	 for (int a=0; a<=this.addresses.size()-1; a++)
    	 {
    		 w=w+((CAddress)addresses.get(a)).getPublic()+","+
    				 ((CAddress)addresses.get(a)).getPrivate()+","+
    				 ((CAddress)addresses.get(a)).description+","+
    				 ((CAddress)addresses.get(a)).balance+"*";
    	 }
    	 
 
    	 
    	 // Encrypt
    	 try
    	 {
    	     String enc=UTILS.AES.encrypt(w, UTILS.SETTINGS.pass); 
    	     FileUtils.writeStringToFile(f, enc, "UTF-8");
    	 }
    	 catch (Exception ex) 
    	 { 
    		 UTILS.LOG.log("ERROR", ex.getMessage(), "CWallet", 153); 
    	 }
     }
     
     public void removeAdr(int no) throws Exception
     {
       this.addresses.remove(no);
       save();
     }
     
    
     
      public boolean newAddress(String user, String curve, String description) throws Exception
      {
         try
         {    
    	    // Generate an address
            CAddress adr=new CAddress();
	    adr.generate(curve);
		 
	    // Add
	    this.addresses.add(adr);
         
            // Load user ID
            Statement s=UTILS.DB.getStatement();
           
            // Load address data
            ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM web_users "
                                      + "WHERE user='"+user+"'");
         
            // Next
            rs.next();
         
            // User ID
            long userID=rs.getLong("ID");
         
	    // Insert address
	    UTILS.DB.executeUpdate("INSERT INTO my_adr(userID, adr, description) "
		 		      + "VALUES('"+userID+"', '"+
                                                   adr.getPublic()+"', '"+
                                                   UTILS.BASIC.base64_encode(description)+"')");
        
           // Close
           rs.close(); s.close();
         
           // Save wallet
	   save();
	
				 
	   // Last generated
	   this.last_adr=adr;
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
        }
	
    	 return true;
     }
     
     public boolean add(CAddress adr) throws Exception
     {
    	 // Generate an address
		 this.addresses.add(adr);
		 
		 // Save wallet
		 save();
		 
		
		 
		 // Last generated
		 this.last_adr=adr;
	
    	 return true;
     }
     
     public String getFirst() throws Exception
     {
         CAddress temp=(CAddress) this.addresses.get(0);
         return temp.getPublic();
     }
     
     public boolean isMine(String adr) throws Exception
     {
    	 for (int a=0; a<=this.addresses.size()-1; a++)
    		 if (((CAddress)this.addresses.get(a)).getPublic().equals(adr))
    	       return true;
    	 
    	 return false;
     }
     
     public boolean checkPass(String pass) throws Exception
     {
    	 if (org.apache.commons.codec.digest.DigestUtils.sha512Hex(pass)!=this.pass) 
    		 return false;
    	 else
    		 return true;
     }
     
     public CAddress getAddress(String adr) throws Exception
     {
    	 if (adr.length()<40) adr=UTILS.BASIC.addressFromDomain(adr);
    		 
    	 for (int a=0; a<=this.addresses.size()-1; a++)
    	 {
    		 CAddress ad=(CAddress) this.addresses.get(a);
    		 if (ad.getPublic().equals(adr))
    			 return ad;
    	 }
    	 
    	 return null;
     }
     
   
}
