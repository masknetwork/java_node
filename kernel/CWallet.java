// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
   
   // Wallet Balance
   public double balance=0;
   
   // Last generated address
   public CAddress last_adr=null; 
   
   // Wallet
   File f;
   
   public CWallet() throws Exception
   {
	// Check if settings file exists
	f=new File(UTILS.WRITEDIR+"wallet.MSK");
        	  
	// File exist ?
	if (f.exists()==false)
	{
	    System.out.println("Creating initial wallet....");
			  
	    FileOutputStream out=new FileOutputStream(f);
	    f_out=new ObjectOutputStream(out);
			    
	    // Generate an address
	    CAddress adr=new CAddress();
	    adr.generate();
	    this.addresses.add(adr);
			    
	    // Insert address
            UTILS.DB.executeUpdate("INSERT INTO my_adr "
                                         + "SET userID='1', "
                                             + "adr='"+adr.getPublic()+"'");
			    
	    // Save wallet
	    save();
        }
	else
        {
            // Display
            System.out.println("Decrypting wallet file...");
        
            // Input Streqm
	    String wallet = FileUtils.readFileToString(f, "UTF-8");
	    wallet=UTILS.AES.decrypt(wallet, UTILS.SETTINGS.getWalletPass());
		       
	    // Load addresses
            String[] s=wallet.split("\\*");
		      
	    for (int a=0; a<=s.length-1; a++)
	    {
		String[] adr=s[a].split(",");
		CAddress address=new CAddress(adr[0], adr[1]); 
		this.addresses.add(address);
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
    	             ((CAddress)addresses.get(a)).getPrivate()+"*";
    	 }
    	 
 
    	 
    	 // Encrypt
    	 try
    	 {
             String enc=UTILS.AES.encrypt(w, UTILS.SETTINGS.getWalletPass()); 
    	     FileUtils.writeStringToFile(f, enc, "UTF-8");
    	 }
    	 catch (Exception ex) 
    	 { 
            System.out.println(ex.getMessage() + " - CWallet, 123"); 
    	 }
     }
     
     public void removeAdr(int no) throws Exception
     {
       this.addresses.remove(no);
       save();
     }
     
    
     
      public String newAddress(long userID, String description) throws Exception
      {
        // Generate an address
        CAddress adr=new CAddress();
        
        // Generate
	adr.generate();
		 
	// Add
	this.addresses.add(adr);
         
	// Insert address
	UTILS.DB.executeUpdate("INSERT INTO my_adr "
                                     + "SET userID='"+userID+"', "
                                         + "adr='"+adr.getPublic()+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(description)+"'");
        
        // Save wallet
	save();
				 
        // Last generated
	this.last_adr=adr;
        
        // Return
        return adr.getPublic();
     }
     
     public void add(CAddress adr) throws Exception
     {
        // Already in wallet ?
        if (this.adrExist(adr))
            return;
            
    	// Add address
        this.addresses.add(adr);
		 
	// Save wallet
	save();
	
        // Last generated
	this.last_adr=adr;
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
     
    
     public CAddress getAddress(String adr) throws Exception
     {
         for (int a=0; a<=this.addresses.size()-1; a++)
    	 {
    		 CAddress ad=(CAddress) this.addresses.get(a);
    		 if (ad.getPublic().equals(adr))
    			 return ad;
    	 }
    	 
    	 throw new Exception("Address doesn't exist");
     }
     
     public void list()
     {
         for (int a=0; a<=this.addresses.size()-1; a++)
         {
            CAddress adr=(CAddress)this.addresses.get(a);
            System.out.println("Address : "+adr.getPublic());
            System.out.println("Address : "+adr.getPrivate());
            System.out.println("-------------------------------------------");
         }
     }
     
     public boolean adrExist(CAddress address)
     {
         for (int a=0; a<=this.addresses.size()-1; a++)
         {
            CAddress adr=(CAddress)this.addresses.get(a);
            if (adr.getPublic().equals(address.getPublic()))
                return true;
         }
         
         return false;
     }
   
}
