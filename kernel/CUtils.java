// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.commons.codec.binary.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPayload;

public class CUtils 
{
	public CUtils() 
	{
		// TODO Auto-generated constructor stub
	}
	// checks if address is valid
	public boolean adressValid(String adr) throws Exception
	{
            // Length valid
            if (adr.length()!=108 && 
		adr.length()!=124 && 
		adr.length()!=160 && 
		adr.length()!=212 &&
		!adr.equals("default")) 
            return false;
	    
            // Characters
	    for (int a=0; a<=adr.length()-1; a++)
	    {
		int c=adr.codePointAt(a);
		if (c!=47 && c!=61) 
                   if (adr.codePointAt(a)<43 || adr.codePointAt(a)>122)
		      return false;
	    }
            
            // Return
	    return true;
	}
	
	
	public boolean addressExist(String adr) throws Exception
	{
            // Checks if address is valid
	    if (this.adressValid(adr)==false)
			return false;
		
	    // Search for address
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
				               + "FROM adr "
				              + "WHERE adr='"+adr+"'");
          	
	    // Exist ?
	    if (UTILS.DB.hasData(rs)) 
               return true;
            else
               return false;
           
	}
	
	public long tstamp() throws Exception { return System.currentTimeMillis()/1000; }
	
	public long mtstamp() throws Exception { return System.currentTimeMillis(); }
	
	public String hash(String hash) throws Exception { return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(hash); }
	
	public String hash(byte[] data) throws Exception { return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(data); }
        
        public byte[] hexhash(String data) throws Exception { return  org.apache.commons.codec.digest.DigestUtils.sha256(data); }
	
	public String hash512(String hash) throws Exception { return  org.apache.commons.codec.digest.DigestUtils.sha512Hex(hash); }
	
	// Compress a bytearray
	public static byte[] compress(byte[] data)  throws Exception
	{ 
			// Initialize deflater
		    Deflater deflater = new Deflater(); 
		    
		    // Best compression
		    deflater.setLevel(Deflater.BEST_COMPRESSION);
		    
		    // Input data
		    deflater.setInput(data); 
		  
		    // Output
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
		 
		    // Finish
		    deflater.finish(); 
		    
		    // Bufer
		    byte[] buffer = new byte[1024];  
		    
		    // Compress
		    while (!deflater.finished()) 
		    { 
		      int count = deflater.deflate(buffer); // returns the generated code... index 
		      outputStream.write(buffer, 0, count);  
		    }
		
		    // Close
		    outputStream.close(); 
		    
		    // To bytearray
		    byte[] output = outputStream.toByteArray(); 
		 
		    // Return
		    return output; 
		
	    
	}
	
	// Decompress a bytearray
	public static byte[] decompress(byte[] data)  throws Exception
	{ 
		   // Initialize inflater
	       Inflater inflater = new Inflater();  
		   
	       // Input data
	       inflater.setInput(data); 
		   
	       // Outpur stream
		   ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length); 
		   
		   // Buffer
		   byte[] buffer = new byte[1024]; 
		   
		   // Inflate
		   while (!inflater.finished()) 
		   { 
		     int count = inflater.inflate(buffer); 
		     outputStream.write(buffer, 0, count); 
		   } 
		   
		   // Close output
		   outputStream.close(); 
		   
		   // To bytearray
		   byte[] output = outputStream.toByteArray(); 
		   
		   // Return
		   return output; 
		
	}
	
	public String base64_encode(String s) throws Exception
	{
           if (s.equals("")) return "";
           return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s.getBytes()));
	}
	
	public String base64_encode(byte[] s) throws Exception
	{
           return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s));
	}
	
	public String base64_decode(String s) throws Exception
	{
           if (s==null) return "";
           
           if (!s.equals("")) 
              return new String(org.apache.commons.codec.binary.Base64.decodeBase64(s));	
           else
              return "";
	}
	
	public byte[] base64_decode_data(String s) throws Exception
	{
           return org.apache.commons.codec.binary.Base64.decodeBase64(s);
	}
	
	 
        public String randString(int no) throws Exception
	{
		// Generates a key
	    SecureRandom random = new SecureRandom();
            byte key[] = new byte[no];
	    random.nextBytes(key);
		
	    // Random
	    return this.hash(key).substring(0, no);
	}
	
	public boolean domainExist(String domain) throws Exception
	{
            try
            {
                
		ResultSet rs=UTILS.DB.executeQuery("SELECT * "
				                      + "FROM domains "
				                     + "WHERE domain='"+domain+"'");
		
		if (UTILS.DB.hasData(rs)) 
                {
                    // Close
                  
                    
                    // Return
		    return true;
                }
		else
                {
                    // Close
                  
                    
                    // Return
		    return false;
                }
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 692);
            }
            
            return false;
	}
	
	public String adrFromDomain(String domain) throws Exception
	{
	   ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                 + "FROM domains "
                                                + "WHERE domain='"+domain+"'");

                if (UTILS.DB.hasData(rs))
                {
                    // Next
                    rs.next();
                   
                    // Address
                    String adr=rs.getString("adr");
                    
                    // Return
                    return adr;
                }
                else
                {
                    // Return
                    return "";
                }
	}
	
	public void stackTrace() throws Exception
	{
	   StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
           System.out.println(stackTraceElements[0].toString());
           System.out.println(stackTraceElements[1].toString());
           System.out.println(stackTraceElements[2].toString());
           System.out.println(stackTraceElements[3].toString());
           System.out.println(stackTraceElements[4].toString());
	}
	
	
       
	public String getAppDataDirectory()  throws Exception
	{

	    String appDataDirectory;
	    
	      appDataDirectory = System.getenv("APPDATA"); 
	        
	        if (appDataDirectory != null) 
	        {
	            appDataDirectory += File.separator + "MaskWallet" + File.separator;
	        }
	        else 
	        { 
	            appDataDirectory = System.getenv("HOME"); 
	            if (appDataDirectory != null) 
	            {
	                appDataDirectory +=  File.separator + "MaskWallet" + File.separator;
	            }
	            else
	            { 
	                throw new Exception("Could not access APPDATA or HOME environment variables");
	            }
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
	              
	              File dir_blocks = new File(appDataDirectory+"blocks"+File.separator);
	              dir_blocks.mkdir();
	              
	              File dir_multisig = new File(appDataDirectory+"multisig"+File.separator);
	              dir_multisig.mkdir();
	            }
	            catch(Exception e) 
	            {
	               e.printStackTrace();
	            }
	    	  }
	    }
	    
	    return appDataDirectory;
	}
        
        public boolean isIP(String ip) throws Exception
        {
            String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        }
        
        public boolean countryExist(String cou) throws Exception
        {
           return true;  
        }
         
         public long getUserID(String user) throws Exception
         {
             // Load feed data
              ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                            + "FROM web_users "
                                           + "WHERE user='"+user+"'");
                
                // Next 
                rs.next();
                
                // User
                long userID=rs.getLong("ID");
                
                // Return
                return userID;
         }
         
         
         
    public double round(double val, int digits) throws Exception
    {
        long i=Math.round(val*(Math.pow(10, digits)));
        double r=i/(Math.pow(10, digits));
        return r;
    }
    
    
    
    public long getID() throws Exception
    {
        return Math.round(Math.random()*999999999999999999L);
    }
    
    public boolean isContractAdr(String adr) throws Exception
    {
       // Found
       boolean found;
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM agents WHERE adr='"+adr+"'");
       
       if (UTILS.DB.hasData(rs))
           found=true;
       else
           found=false;
       
       // Return
       return found;
    }
    
    public String toHexStr(byte[] data) throws Exception
    {
        return new String(Hex.encodeHex(data));
    }
    
    public String fromHexStr(String data) throws Exception
    {
        return new String(Hex.decodeHex(data.toCharArray()));
    }
    
    public boolean isSpecMkt(String adr) throws Exception
    {
       // Found
       boolean found;
      
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM feeds_spec_mkts WHERE adr='"+adr+"'");
       
       if (UTILS.DB.hasData(rs))
           found=true;
       else
           found=false;
       
       // Return
       return found;
    }
    
    public boolean isMktPeggedAssetAdr(String adr) throws Exception
    {
        // Found
       boolean found;
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM assets WHERE adr='"+adr+"' AND linked_mktID>0");
       
       if (UTILS.DB.hasData(rs))
           found=true;
       else
           found=false;
       
       // Return
       return found;
    }
    
    public boolean isAssetAdr(String adr) throws Exception
    {
        // Found
       boolean found;
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM assets "
                                         + "WHERE adr='"+adr+"' "
                                           + "AND linked_mktID>0");
       
       if (UTILS.DB.hasData(rs))
           found=true;
       else
           found=false;
        
       // Return
       return found;
    }
    
    public boolean isSpecMktAdr(String adr) throws Exception
    {
        // Found
       boolean found;
       
       
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM feeds_spec_mkts "
                                         + "WHERE adr='"+adr+"'");
       
       if (UTILS.DB.hasData(rs))
           found=true;
       else
           found=false;
        
       // Return
       return found;
    }
    
    public boolean isAsset(String symbol) throws Exception
    {
        // Found
       boolean found;
       
      
       // Load data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                   + "FROM assets "
                                  + "WHERE symbol='"+symbol+"'");
       
       if (UTILS.DB.hasData(rs))
           found=true;
       else
           found=false;
      
       // Return
       return found;
    }
    
    public boolean canSpend(String adr, long block) throws Exception
    {
        if (this.isContractAdr(adr)==false || 
            this.isBlockSigner(adr, block))
            return true;
        else
            return false;
    }
    
    public boolean isBlockSigner(String adr, long block) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM blocks "
                                          + "WHERE adr='' AND block>"+(block-1000));
        
        // Has data
        if (UTILS.DB.hasData(rs))
            return true;
        else
            return false;
    }
    
    public boolean isMktAdr(String adr) throws Exception
    {
        if (this.isAssetAdr(adr)==false && 
            this.isMktPeggedAssetAdr(adr)==false)
            return false;
        else
            return true;
    }
    
    public boolean isLong(String var) throws Exception
    {
	if (!var.matches("^[0-9]{1,10}$"))
	   return false;
	else 
	   return true;
    }
	
    public boolean isDecimal(String var) throws Exception
    {
	if (!var.matches("^[0-9]{1,10}\\.[0-9]{1,10}$"))
	    return false;
	else 
	    return true;
    }
	
    public boolean isNumber(String var) throws Exception
    {
	if (!var.matches("^[0-9]{1,10}(\\.[0-9]{1,10})?$"))
            return false;
	else 
	    return true;
    }
	
    public boolean isHash(String hash) throws Exception
    {
	if (!hash.matches("^[A-Fa-f0-9]{64}$"))
            return false;
	else 
	    return true;
    }
	
    public boolean isDomain(String domain) throws Exception
    {
	if (!domain.matches("^[0-9a-z]{2,30}$"))
	   return false;
	else 
	   return true;
    }
	
    public boolean isString(String str) throws Exception
    {
	for (int a=0; a<=str.length()-1; a++)
        {
            //System.out.println(str.charAt(a));
	    if (Character.codePointAt(str, 0)<32 || 
                Character.codePointAt(str, 0)>126)
                return false;
        }
        
        return true;
    }
    
    public boolean isSymbol(String symbol) throws Exception
    {
        // Check letters
        if (!symbol.matches("[A-Z0-9]{6}"))
           return false;
        else
           return true;
    }
    
    public boolean isFeed(String feed) throws Exception
    {
        if (!this.isSymbol(feed))
              return false;
            
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE symbol='"+feed+"'");
        
        if (UTILS.DB.hasData(rs))
           return true;
        else
           return false;
    }
    
    public boolean isBranch(String feed_symbol, String symbol) throws Exception
    {
        // Is symbol
        if (!this.isSymbol(feed_symbol) || 
            !this.isSymbol(symbol))
        return false;
            
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+feed_symbol+"' "
                                            + "AND symbol='"+symbol+"'");
           
        // Return results
        if (!UTILS.DB.hasData(rs))
            return false;
        else
            return true;
    }
    
    public boolean isTitle(String title) throws Exception
    {
        // String ?
        if (!this.isString(title))
            return false;
        
        // Length
        if (title.length()<2 || title.length()>100)
            return false;
        
        // Passed
        return true;
    }
    
    public boolean isDesc(String desc) throws Exception
    {
        // String ?
        if (!this.isString(desc))
            return false;
        
        // Length
        if (desc.length()<5 || desc.length()>1000)
            return false;
        
        // Passed
        return true;
    }
    
    public boolean isDesc(String desc, long max_length) throws Exception
    {
        // String ?
        if (!this.isString(desc))
            return false;
        
        // Length
        if (desc.length()<5 || desc.length()>max_length)
            return false;
        
        // Passed
        return true;
    }
    
    public boolean isLink(String link) throws Exception
    {
        // Lnegth
        if (link.length()>250)
           return false;
                
	if (link.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
           return true;
        else 
           return false;
    }
    
    public boolean isPic(String link) throws Exception
    {   
        // Is link
        if (this.isLink(link)==false)
            return false;
        
        // Length
        if (link.endsWith(".jpg")==false && 
            link.endsWith(".jpeg")==false &&
            link.indexOf("/")>0)
        return false;
        
        return true;
    }
    
    public boolean isEmail(String email) throws Exception
    {
	 // Lnegth
        if (email.length()>250)
           return false;
                
	if (email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"))
           return true;
        else 
           return false;
    }
    
    public String zeros_8(String num)
    {
        String[] a=num.split("\\.");
        
        if (a.length==1) 
        {
            num=num+"+00000000";
        }
        else
        {
            if (a[1].length()==1) num=num+"0000000";
            if (a[1].length()==2) num=num+"000000";
            if (a[1].length()==3) num=num+"00000";
            if (a[1].length()==4) num=num+"0000";
            if (a[1].length()==5) num=num+"000";
            if (a[1].length()==6) num=num+"00";
            if (a[1].length()==7) num=num+"0";
        }
        
        return num;
    }
    
    public String zeros_4(String num)
    {
        String[] a=num.split("\\.");
        
        if (a.length==1) 
        {
            num=num+"+0000";
        }
        else
        {
            if (a[1].length()==1) num=num+"000";
            if (a[1].length()==2) num=num+"00";
            if (a[1].length()==3) num=num+"0";
        }
        
        return num;
    }
    
    public String zeros_2(String num)
    {
        String[] a=num.split("\\.");
        
        if (a.length==1) 
        {
            num=num+"+00";
        }
        else
        {
            if (a[1].length()==1) num=num+"0";
        }
        
        return num;
    }
    
    public boolean isSync() throws Exception
    {
        
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM sync");
        if (UTILS.DB.hasData(rs)) 
        {
            
            return true;
        }
        else
        {
            
            return false;
        }
    }
    
    public String formatDif(String dif)
     {
         long d=64-dif.length();
         for (int a=1; a<=d; a++)
             dif="0"+dif;
         
         return dif;
     }
    
    public boolean isSealed(String adr) throws Exception
    {
       // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM adr "
                                   + "WHERE adr='"+adr+"' "
                                     + "AND sealed>0");
        
        if (UTILS.DB.hasData(rs)) 
           return true;
        else
            return false;
    }
    
    public long getAssetContract(String symbol) throws Exception
    {
        long aID=0;
        
        // Statement
        
        
        // Load asset data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM assets WHERE symbol='"+symbol+"'");
        
        // Next
        rs.next();
        
        // Asset address
        String adr=rs.getString("adr");
        
        // Contract ?
        rs=UTILS.DB.executeQuery("SELECT * FROM agents WHERE adr='"+adr+"'");
        
        if (UTILS.DB.hasData(rs))
        {
            // Next
            rs.next();
            
            // ID
            aID=rs.getLong("aID");
        }
       
        return aID;
    }
    
    public long getMarketContract(long mktID, String table) throws Exception
    {
       // Load market data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM "+table
                                          + " WHERE mktID='"+mktID+"'");
        
        // Next
        rs.next();
        
        // Load contract
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM agents "
                                + "WHERE adr='"+rs.getString("adr")+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
        {
            // Next
            rs.next();
            
            // ID
            return rs.getLong("aID");
        }
       
        
        // Return
        return 0;
    }
    
    public void commited(String block_hash, 
                         long block_no, 
                         String packet_hash, 
                         String payload_hash, 
                         String fee_hash) throws Exception
    {
        UTILS.DB.executeUpdate("UPDATE packets "
                                + "SET block_hash='"+block_hash+"', "
                                    + "block='"+block_no+"' "
                              + "WHERE packet_hash='"+packet_hash+"' "
                                 + "OR payload_hash='"+payload_hash+"' "
                                 + "OR fee_hash='"+fee_hash+"'");
    }
    
    public boolean hasRecords(String table) throws Exception
    {
        // Res
        boolean res=false;
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM "+table);
        
        // Has data
        if (UTILS.DB.hasData(rs))
            res=true;
        else
            res=false;
        
        // Return
        return res;
    }
    
    public boolean validID(long ID) throws Exception
    {
        // Is asset
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM assets "
                                          + "WHERE assetID='"+ID+"'");
        if (UTILS.DB.hasData(rs)) return false;
                
        // Is feed ID
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds "
                                + "WHERE feedID='"+ID+"'");
        if (UTILS.DB.hasData(rs)) return false;
        
        // Is post ID
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM tweets "
                                + "WHERE tweetID='"+ID+"'");
        if (UTILS.DB.hasData(rs)) return false;
        
        // IS comment ID
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM comments "
                                + "WHERE comID='"+ID+"'");
        if (UTILS.DB.hasData(rs)) return false;
        
        // Is app ID
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM agents "
                                + "WHERE aID='"+ID+"'");
        if (UTILS.DB.hasData(rs)) return false;
        
        // Is bet
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds_bets "
                                + "WHERE betID='"+ID+"'");
        if (UTILS.DB.hasData(rs)) return false;
        
        // Return
        return true;
    }
    
    public boolean targetValid(String target_type, long targetID) throws Exception
    {
       // Target type
       if (!target_type.equals("ID_POST") && 
           !target_type.equals("ID_COM") && 
           !target_type.equals("ID_FEED") && 
           !target_type.equals("ID_BET") && 
           !target_type.equals("ID_APP") && 
           !target_type.equals("ID_ASSET") &&
           !target_type.equals("ID_ASSET_MKT"))
        return false;
       
       // Result
       ResultSet rs=null;
       
       // Load  data
       switch (target_type)
       {
           case "ID_POST" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM tweets "
                                                    + "WHERE tweetID='"+targetID+"'");
                            break;    
                            
           case "ID_COM" : rs=UTILS.DB.executeQuery("SELECT * "
                                                        + "FROM comments "
                                                       + "WHERE comID='"+targetID+"'");
                                break;    
                                
           case "ID_FEED" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds "
                                                    + "WHERE feedID='"+targetID+"'");
                                break;    
                                
            case "ID_BET" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds_bets "
                                                    + "WHERE betID='"+targetID+"'");
                                break;    
                                
            case "ID_APP" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM agents "
                                                    + "WHERE aID='"+targetID+"'");
                                break;  
                                
            case "ID_ASSET" : rs=UTILS.DB.executeQuery("SELECT * "
                                                       + "FROM assets "
                                                      + "WHERE assetID='"+targetID+"'");
                                break;  
                                
            case "ID_ASSET_MKT" : rs=UTILS.DB.executeQuery("SELECT * "
                                                           + "FROM assets_mkts "
                                                          + "WHERE mktID='"+targetID+"'");
                                break;  
       }
             
       // Like tweet exist ?
       if (!UTILS.DB.hasData(rs))
           return false;
       
       // Return
       return true;
    }
    
    public String getTargetAdr(String target_type, long targetID) throws Exception
    {
       // Result
       ResultSet rs=null;
       
       // Load  data
       switch (target_type)
       {
           case "ID_POST" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM tweets "
                                                    + "WHERE tweetID='"+targetID+"'");
                            break;    
                            
           case "ID_COM" : rs=UTILS.DB.executeQuery("SELECT * "
                                                        + "FROM comments "
                                                       + "WHERE comID='"+targetID+"'");
                                break;    
                                
           case "ID_FEED" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds "
                                                    + "WHERE feedID='"+targetID+"'");
                                break;    
                                
            case "ID_BET" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds_bets "
                                                    + "WHERE betID='"+targetID+"'");
                                break;    
                                
            case "ID_APP" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM agents "
                                                    + "WHERE aID='"+targetID+"'");
                                break;  
                                
            case "ID_ASSET" : rs=UTILS.DB.executeQuery("SELECT * "
                                                       + "FROM assets "
                                                      + "WHERE assetID='"+targetID+"'");
                                break;  
                                
            case "ID_ASSET_MKT" : rs=UTILS.DB.executeQuery("SELECT * "
                                                           + "FROM assets_mkts "
                                                          + "WHERE mktID='"+targetID+"'");
                                break;  
       }
       
       // Next
       rs.next();
       
       // Return
       return rs.getString("adr");
    }
    
    public long getAgentID(String adr) throws Exception
    {
       // Load agent data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM agents "
                                         + "WHERE adr='"+adr+"'");
       // Next
       rs.next();
       
       // Return
       return rs.getLong("aID");
    }
}
