package wallet.kernel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
import wallet.network.CResult;

public class CUtils 
{
	public CUtils() 
	{
		// TODO Auto-generated constructor stub
	}
	
        public boolean emailValid(String email)
         {
             return true;
         }
         
         public boolean websiteValid(String email)
         {
             return true;
         }
         
         public boolean picValid()
         {
             return true;
         }
         
	// checks if address is valid
	public boolean adressValid(String adr)
	{
		if (adr.length()!=108 && 
		    adr.length()!=124 && 
		    adr.length()!=160 && 
		    adr.length()!=212 &&
		    !adr.equals("default")) return false;
		                 
		for (int a=0; a<=adr.length()-1; a++)
		{
			int c=adr.codePointAt(a);
			if (c!=47 && c!=61) 
			  if (adr.codePointAt(a)<43 || adr.codePointAt(a)>122)
				  return false;
		}
		
		return true;
	}
	
	
	public boolean addressExist(String adr)
	{
            try
            {
		// Checks if address is valid
		if (this.adressValid(adr)==false)
			return false;
		
		// Search for address
                Statement s=UTILS.DB.getStatement();
		ResultSet rs=s.executeQuery("SELECT * "
				                      + "FROM adr "
				                      + "WHERE adr='"+adr+"'");
		
		// Exist ?
		if (UTILS.DB.hasData(rs)==false) 
                {
                   // Close
		   if (s!=null) s.close();
                   
                   // Return
                   return false;
                }
		else
                {
                   // Close
                   if (s!=null) s.close();
                    
                   // Return 
	  	   return true;
                }
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLExcption", ex.getMessage(), "CUtils.java", 82);
            }
            
            return false;
	}
	
	public long tstamp()
	{
		return System.currentTimeMillis()/1000;
	}
	
	public long mtstamp()
	{
		return System.currentTimeMillis();
	}
	
	public String hash(String hash)
	{
            return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(hash);
	}
	
	public String hash(byte[] data)
	{
		return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(data);
	}
        
        public byte[] hexhash(String data)
	{
		return  org.apache.commons.codec.digest.DigestUtils.sha256(data);
	}
	
	public String hash512(String hash)
	{
		return  org.apache.commons.codec.digest.DigestUtils.sha512Hex(hash); 
	}
	
	public String getFreeAdr()
	{
		for (int a=0; a<=UTILS.WALLET.addresses.size()-1; a++)
		{
			CAddress adr=(CAddress)UTILS.WALLET.addresses.get(a);
			if (adr.options.isFrozen==false && 
			    adr.options.isMultiSig==false && 
			    adr.options.isOTP==false && 
			    adr.balance>0.00001)
			return adr.getPublic();
		}
		
		return "";
	}
	
	public void removeAdrAttr(String adr, 
                              String atr, 
                              String par_1, 
                              String par_2, 
                              String par_3)
	{
		UTILS.DB.executeUpdate("DELETE FROM adr_options "
				                   + "WHERE adr='"+adr+"' "
				                     + "AND op_type='"+atr+"' "
				                     + "AND par_1='"+par_1+"'"
				                     + "AND par_2='"+par_2+"'"
				                     + "AND par_3='"+par_3+"'");
	}
	
	// Check if address has options
	public boolean hasOption(String adr, String tip, String par_1, String par_2, String par_3)
	{
            try
            {
                Statement s=UTILS.DB.getStatement();
	       ResultSet rs=s.executeQuery("SELECT * "
	   		                         + "FROM adr_options "
	   		                        + "WHERE adr='"+adr+"' "
	   		                          + "AND op_type='"+tip+"' "
	   		                          + "AND par_1='"+par_1+"' "
	   		                          + "AND par_2='"+par_2+"' "
	   		                          + "AND par_3='"+par_3+"'");
	   
	       // Option exist
	       if (UTILS.DB.hasData(rs)==true) 
               {
                   // Close
                   if (s!=null) s.close();
                   
                   // Return
                   return true;
               }
            }
	    catch (SQLException ex)
            {
               UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 164);
            }
            
	   // Return
	   return false;	
	}
	
	// Apply address options
	public void applyAdrAttr(String adr, 
			         String atr, 
			         String par_1, 
			         String par_2, 
			         String par_3,
			         String par_4,
                                 String par_5,
                                 String par_6,
                                 long days, 
                                 long block)
	{
                long expire=0;
                
		try
		{
		   // Search for address
                   Statement s=UTILS.DB.getStatement();
		   ResultSet rs_adr=s.executeQuery("SELECT * "
                                                     + "FROM adr "
                                                    + "WHERE adr='"+adr+"'");
		   
		   // Address does not exist
		   if (UTILS.DB.hasData(rs_adr)==false)
			   UTILS.DB.executeUpdate("INSERT INTO ADR (adr, balance, block, last_interest) "
	                    + "VALUES('"+adr+"', '0', '"+UTILS.BASIC.block()+"', '"+UTILS.BASIC.tstamp()+"')");
		   
                   if (s!=null) s.close();
                   
		   // Search for option
		   ResultSet rs;
                   s=UTILS.DB.getStatement();
		   
                   rs=s.executeQuery("SELECT * "
				     + "FROM adr_options "
				    + "WHERE adr='"+adr+"' "
				      + "AND op_type='"+atr+"'");
		
		   // Option already there ?
		   if (UTILS.DB.hasData(rs)==true)
		   {
			  // Next
			  rs.next();
			
			  // Expires
			  expire=rs.getLong("expires")+days*1440;
			
			  // Update
			  UTILS.DB.executeUpdate("UPDATE adr_options "
			  		          + "SET expires='"+expire+"',"
			  		              + "block='"+block+"'"
			  		        + "WHERE adr='"+adr+"' "
			  		          + "AND op_type='"+atr+"'");
                          
                          // Close
                          if (s!=null) s.close();
		   }
		   else
		   {
                          // Expire
                          expire=block+days*1440;
                          
			  // Insert option
			  UTILS.DB.executeUpdate("INSERT INTO adr_options(adr, "
			  		                                       + "op_type, "
			  		                                       + "par_1, "
			  		                                       + "par_2, "
			  		                                       + "par_3, "
			  		                                       + "par_4, "
                                                                               + "par_5, "
                                                                               + "par_6, "
			  		                                       + "expires, "
			  		                                       + "block) "
			  		                           + "VALUES('"+adr+"', '"+
			  		                                        atr+"', '"+
			  		                                        par_1+"', '"+
			  		                                        par_2+"', '"+
			  		                                        par_3+"', '"+
			  		                                        par_4+"', '"+
                                                                                par_5+"', '"+
                                                                                par_6+"', '"+
			  		                                        expire+"', '"+
			  		                                        block+"')");
		   }
		   
		   
		}
                catch (SQLException ex)
                {
                    UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 264);           
                }
		
	}
	
	public boolean isTransID(String transID)
	{
		return true;
	}
	
	public boolean isSHA512(String txt)
	{
		// Check length
		if (txt.length()!=128) return false;
		
		// Return 
		return true;
	}
	
	public boolean isSHA256(String txt)
	{
		// Check content
		
		// Check length
		
		// Return 
		return true;
	}
	
	// Compress a bytearray
	public static byte[] compress(byte[] data) 
	{ 
		try
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
		catch (IOException ex) 
		{
			UTILS.LOG.log("IOException", ex.getMessage(), "CUtils.java", 172); 
		}
	    
		// Return
		return new byte[1];
	}
	
	// Decompress a bytearray
	public static byte[] decompress(byte[] data) 
	{ 
		try
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
		catch (IOException ex) { UTILS.LOG.log("IOException", ex.getMessage(), "CUtils.java", 172); }
		catch (DataFormatException ex) { UTILS.LOG.log("DataFormatException", ex.getMessage(), "CUtils.java", 172); }
	    
		return new byte[1];
	}
	
	public int getImgSize(String adr)
	{
		try
	      {
	        URL url = new URL("adr");
	        URLConnection conn = url.openConnection();
	        return conn.getContentLength();
	      }
	      catch (MalformedURLException ex) {}
	      catch (IOException ex) {}
		
		return 0;
	}
	
	public String getTime(long dif)
	{
		// Seconds
		if (dif<60) return (String.valueOf(dif)+" seconds");
		
		// Minutes
		if (dif>=60 && dif<3600) return String.valueOf(Math.round(dif/60))+" minutes";
		
		// Hours
		if (dif>=3600 && dif<144000) return String.valueOf(Math.round(dif/3600))+" hours";
		
		// Day
		if (dif>=144000 && dif<172800) return "1 day";
		
		// Days
		if (dif>=172800 && dif<2592000) return String.valueOf(Math.round(dif/144000))+" days";
		
		// Month
		if (dif>=2592000 && dif<5184000) return "1 month";
				
		// Months
		if (dif>=5184000 && dif<31536000) return String.valueOf(Math.round(dif/2592000))+" months";
		
		// Year
		if (dif>=31536000 && dif<63172000) return "1 year";
		
		// Years
		if (dif>=63172000) return String.valueOf(Math.round(dif/31536000))+" years";
		
		return "";
	}
	
	public String splitText(int width, int letter_width, String txt)
	{
		int last_word=0;
		int line_size=0;
		String sText="";
		int found=0;
		
		for (int a=0; a<=txt.length()-1; a++)
		{
			// Increase line width
		   	line_size=line_size+letter_width;
		   	
		   	// Add character to text
		   	sText=sText+txt.charAt(a);
		   	
		   	// Space ?
		   	if (txt.charAt(a)==' ') last_word=a;
		   	
		   	if (line_size>=width)
		   	{
		   		line_size=0;
		   		sText=sText.substring(0, (last_word+found))+"-"+sText.substring(last_word+found, sText.length());
		   	    found++;
		   	}
		}
		
		return sText;
	}
	
	public String format_price(double price)
	{
		String[] v=String.valueOf(price).split("\\.");
		if (v[1].length()==0) return v[0]+".0000"; 
		if (v[1].length()==1) return v[0]+"."+v[1]+"000"; 
		if (v[1].length()==2) return v[0]+"."+v[1]+"00"; 
		if (v[1].length()==3) return v[0]+"."+v[1]+"0"; 
			
		return String.valueOf(price);
	}
	
	public String format_price_2_digits(double price)
	{
		String[] v=String.valueOf(price).split("\\.");
		if (v[1].length()==0) return v[0]+".00"; 
		if (v[1].length()==1) return v[0]+"."+v[1]+"0"; 
			
		return String.valueOf(price);
	}
	
	public boolean isPrice(String price)
	{
		boolean point=false;
		
		if (price.length()<1 || price.length()>6) return false;
		for (int a=0; a<=price.length()-1; a++)
		{
		  if (price.charAt(a)!='0' && 
		      price.charAt(a)!='1' &&
		      price.charAt(a)!='2' &&
		      price.charAt(a)!='3' &&
		      price.charAt(a)!='4' &&
		      price.charAt(a)!='5' &&
		      price.charAt(a)!='6' &&
		      price.charAt(a)!='7' &&
		      price.charAt(a)!='8' &&
		      price.charAt(a)!='9' &&
		      price.charAt(a)!='.')
			  return false;
		  
		  if (price.charAt(a)=='.' && point==true) return false;
		  
		  if (price.charAt(a)=='.') point=true;
		}
			
		return true;
	}
	
	public boolean isInteger(String price)
	{
		for (int a=0; a<=price.length()-1; a++)
		  if (price.charAt(a)!='0' && 
		      price.charAt(a)!='1' &&
		      price.charAt(a)!='2' &&
		      price.charAt(a)!='3' &&
		      price.charAt(a)!='4' &&
		      price.charAt(a)!='5' &&
		      price.charAt(a)!='6' &&
		      price.charAt(a)!='7' &&
		      price.charAt(a)!='8' &&
		      price.charAt(a)!='9')
			  return false;
			
		return true;
	}
	
	public String dateFromTstamp(long tstamp)
	{
		 Date time=new Date(tstamp*1000);
		 SimpleDateFormat dt = new SimpleDateFormat("MMM, dd, yyyy");
		 return dt.format(time);
	}
	
	public boolean isEmail(String email)
	{
		return true;
	}
	
	public String base64_encode(String s)
	{
           return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s.getBytes()));
	}
	
	public String base64_encode(byte[] s)
	{
	   return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s));
	}
	
	public String base64_decode(String s)
	{
		try
		{
		   return new String(org.apache.commons.codec.binary.Base64.decodeBase64(s));
		}
		catch(Exception ex) 
		{
			UTILS.LOG.log("Exception", ex.getMessage(), "CUtils.java", 464);
			return "";
		}
	}
	
	public byte[] base64_decode_data(String s)
	{
		try
		{
		   return org.apache.commons.codec.binary.Base64.decodeBase64(s);
		}
		catch(Exception ex) 
		{
			UTILS.LOG.log("Exception", ex.getMessage(), "CUtils.java", 464);
		}
		
		return null;
	}
	
        public void clearTrans(String hash, String tip)
        {
            try
            {
                // Statement
                Statement s=UTILS.DB.getStatement();
                
                // Load trans data
                ResultSet rs_trans=s.executeQuery("SELECT * "
                                                  + "FROM trans "
                                                 + "WHERE hash='"+hash+"' "
                                                   + "AND status='ID_UNCONFIRMED'");
                
                // Clear
                while (rs_trans.next())
                {
                    if ((rs_trans.getDouble("amount")<0 && (tip.equals("ID_ALL") || tip.equals("ID_SEND"))) ||
                        (rs_trans.getDouble("amount")>0 && (tip.equals("ID_ALL") || tip.equals("ID_RECEIVE"))))
                    {
                      if (!rs_trans.getString("cur").equals("MSK"))
                       this.doAssetTrans(rs_trans.getString("src"), 
                                         rs_trans.getDouble("amount"), 
                                         rs_trans.getString("cur"),
                                         hash);
                      else
                        this.doTrans(rs_trans.getString("src"), 
                                   rs_trans.getDouble("amount"), 
                                   hash);
                    }
                }
                
                if (tip.equals("ID_SEND")) 
                {
                    UTILS.DB.executeUpdate("UPDATE trans SET status='ID_CLEARED' WHERE hash='"+hash+"' AND amount<0");
                    UTILS.DB.executeUpdate("UPDATE my_trans SET status='ID_CLEARED' WHERE hash='"+hash+"' AND amount<0");
                }
                
                if (tip.equals("ID_RECEIVE"))
                {
                    UTILS.DB.executeUpdate("UPDATE trans SET status='ID_CLEARED' WHERE hash='"+hash+"' AND amount>0");
                    UTILS.DB.executeUpdate("UPDATE my_trans SET status='ID_CLEARED' WHERE hash='"+hash+"' AND amount>0");
                }
                
                if (tip.equals("ID_ALL")) 
                {
                    UTILS.DB.executeUpdate("UPDATE trans SET status='ID_CLEARED' WHERE hash='"+hash+"'");
                    UTILS.DB.executeUpdate("UPDATE my_trans SET status='ID_CLEARED' WHERE hash='"+hash+"'");
                }
                
                // Close
                s.close();
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
            }
        }
        
        
        public boolean transExist(String table, String hash, String cur, double amount)
        {
            try
            {
               // Statement
               Statement s=UTILS.DB.getStatement();
                
               // Load trans data
               ResultSet rs_trans=s.executeQuery("SELECT * "
                                                 + "FROM "+table+" "
                                                + "WHERE hash='"+hash+"' "
                                                  + "AND cur='"+cur+"' "
                                                  + "AND amount='"+UTILS.FORMAT.format(amount)+"'");
               
               // Has data
               if (UTILS.DB.hasData(rs_trans))
                   return true;
               else
                   return false;
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
            }
            
            return true;
        }
        
        public void newTrans(String adr, 
                             String adr_assoc, 
                             double amount, 
                             boolean send_trans,
                             String cur, 
                             String expl, 
                             String escrower, 
                             String hash, 
                             long block)
        {
            // ResultSet
            ResultSet rs;
            
            // Asset fee
            double asset_fee=0;
            
            // Asset fee address
            String asset_fee_adr="";
            
            // Market fee
            double mkt_fee=0;
            
            // Market fee address
            String mkt_fee_adr="";
            
            try
            {
            // Statement
            Statement s=UTILS.DB.getStatement();
            
            String query="SELECT * "
                              + "FROM trans "
                             + "WHERE src='"+adr+"' "
                               + "AND hash='"+hash+"' "
                               + "AND amount="+UTILS.FORMAT.format(amount)+" "
                               + "AND cur='"+cur+"'";
            
            rs=s.executeQuery(query); 
            //UTILS.CONSOLE.write(query);
            
            if (UTILS.DB.hasData(rs)) return;
            
            // Add trans to trans pool
            if (amount<0)
	       UTILS.NETWORK.TRANS_POOL.addTrans(adr, 
		    		                 amount, 
		    		                 cur, 
		    		                 hash, 
		    		                 block);
            
            // Credit transaction ?
            if (amount>0)
            {
                if (!cur.equals("MSK"))
                {
                  // Loads asset data
                  rs=s.executeQuery("SELECT * FROM assets WHERE symbol='"+cur+"'");
                  
                  // Next
                  rs.next();
                  
                  if (rs.getDouble("trans_fee")>0)
                  {
                     // Fee
                     asset_fee=Math.abs(rs.getDouble("trans_fee")*amount/100);
                  
                     // Fee address
                     asset_fee_adr=rs.getString("trans_fee_adr");
                             
                     // Min fee
                     if (asset_fee<0.0001) asset_fee=0.0001;
                  }
                }
            }
             
             // Mine ?
	     if (UTILS.WALLET.isMine(adr))
	     {
                    // Insert into my transactions 
                    if (!transExist("my_trans", hash, cur, amount))
	    	    UTILS.DB.executeUpdate("INSERT INTO my_trans(userID, " +
                                                                "adr, " +
                                                                "adr_assoc, " +
            		                                        "amount, " +
   		                                                "cur, " +
                                                                "expl, " +
   		                                                "escrower, "+
                                                                "hash, " +
   		                                                "block, " +
   		                                                "tstamp, "+
   		                                                "status, "
                                                                + "mes, "
                                                                + "field_1, "
                                                                + "field_2, "
                                                                + "field_3, "
                                                                + "field_4, "
                                                                + "field_5, "
                                                                + "cartID) "
   		                                      + "VALUES('"+
                                                                 this.getAdrUserID(adr)+"', '"+
   		                                                 adr+"', '"+
                                                                 adr_assoc+"', '"+
   		                                                 UTILS.FORMAT.format(amount)+"', '" +
   		                                                 cur+"', '"+
                                                                 expl+"', '"+
   		                                                 escrower+"', '" +
   		                                                 hash+"', '"+
   		                                                 block+"', '" +
   		                                                 UTILS.BASIC.tstamp()+"', '" +
   		                                                 "ID_UNCONFIRMED', '', '', '', '', '', '', '0')");
                    
                    // Debit asset fee
                    if (!transExist("my_trans", hash, cur, -asset_fee) && 
                        !cur.equals("MSK") &&
                        asset_fee>0)
	    	    UTILS.DB.executeUpdate("INSERT INTO my_trans(userID, " +
                                                                "adr, " +
            		                                        "amount, " +
   		                                                "cur, " +
                                                                "expl, " +
   		                                                "escrower, "+
                                                                "hash, " +
   		                                                "block, " +
   		                                                "tstamp, "+
   		                                                "status) "
   		                                      + "VALUES('"+
   		                                                 this.getAdrUserID(adr)+"', '"+
                                                                 adr+"', '"+
   		                                                 UTILS.FORMAT.format(-asset_fee)+"', '" +
   		                                                 cur+"', '"+
                                                                 expl+"', '"+
   		                                                 escrower+"', '" +
   		                                                 hash+"', '"+
   		                                                 block+"', '" +
   		                                                 UTILS.BASIC.tstamp()+"', '" +
   		                                                 "ID_UNCONFIRMED')");
                    
                    // Credit Asset fee
                    if (!transExist("my_trans", hash, cur, asset_fee) && 
                        !cur.equals("MSK") &&
                        asset_fee>0)
                     UTILS.DB.executeUpdate("INSERT INTO my_trans(userID,"+
                                                                "adr, " +
            		                                        "amount, " +
   		                                                "cur, " +
                                                                "expl, " +
   		                                                "escrower, "+
                                                                "hash, " +
   		                                                "block, " +
   		                                                "tstamp, "+
   		                                                "status) "
   		                                      + "VALUES('"+
                                                                 this.getAdrUserID(asset_fee_adr)+"', '"+
                                                                 asset_fee_adr+"', '"+
   		                                                 UTILS.FORMAT.format(asset_fee)+"', '" +
   		                                                 cur+"', '"+
                                                                 expl+"', '"+
   		                                                 escrower+"', '" +
   		                                                 hash+"', '"+
   		                                                 block+"', '" +
   		                                                 UTILS.BASIC.tstamp()+"', '" +
   		                                                 "ID_UNCONFIRMED')");
                    
                    UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_trans=unread_trans+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(adr)+"' ");
           
                 
	    }
               
            // Insert transaction 
            if (!transExist("trans", hash, cur, amount))
	    UTILS.DB.executeUpdate("INSERT INTO trans(src, " +
            		                              "amount, " +
   		                                      "cur, " +
   		                                      "escrower, "+
                                                      "hash, " +
   		                                      "block, " +
   		                                      "tstamp, "+
   		                                      "status) "
   		                             + "VALUES('"+ 
                                                       adr+"', '"+
   		                                       UTILS.FORMAT.format(amount)+"', '" +
   		                                       cur+"', '"+
   		                                       escrower+"', '" +
   		                                       hash+"', '"+
   		                                       block+"', '" +
   		                                       UTILS.BASIC.tstamp()+"', '" +
   		                                       "ID_UNCONFIRMED')");
            
            // Insert transaction 
            if (!transExist("trans", hash, cur, asset_fee) && 
                !cur.equals("MSK") && 
                asset_fee>0)
	    UTILS.DB.executeUpdate("INSERT INTO trans(src, " +
            		                              "amount, " +
   		                                      "cur, " +
   		                                      "escrower, "+
                                                      "hash, " +
   		                                      "block, " +
   		                                      "tstamp, "+
   		                                      "status) "
   		                             + "VALUES('"+
                                                       adr+"', '"+
   		                                       UTILS.FORMAT.format(-asset_fee)+"', '" +
   		                                       cur+"', '"+
   		                                       escrower+"', '" +
   		                                       hash+"', '"+
   		                                       block+"', '" +
   		                                       UTILS.BASIC.tstamp()+"', '" +
   		                                       "ID_UNCONFIRMED')");
            
            if (!transExist("trans", hash, cur, asset_fee) && 
                !cur.equals("MSK") && 
                asset_fee>0)
                 UTILS.DB.executeUpdate("INSERT INTO trans(src, " +
            		                              "amount, " +
   		                                      "cur, " +
   		                                      "escrower, "+
                                                      "hash, " +
   		                                      "block, " +
   		                                      "tstamp, "+
   		                                      "status) "
   		                             + "VALUES('"+asset_fee_adr+"', '"+
   		                                       UTILS.FORMAT.format(asset_fee)+"', '" +
   		                                       cur+"', '"+
   		                                       escrower+"', '" +
   		                                       hash+"', '"+
   		                                       block+"', '" +
   		                                       UTILS.BASIC.tstamp()+"', '" +
   		                                       "ID_UNCONFIRMED')");
            
            // Close
            s.close();
            
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 909);
            }
          
        }
        
        // Get address owner
        public long getAdrUserID(String adr)
        {
            try
            {
               // Statement
               Statement s=UTILS.DB.getStatement();
		     
               // Load source
               ResultSet rs=s.executeQuery("SELECT * FROM my_adr WHERE adr='"+adr+"'"); 
               
               // Next
               rs.next();
            
               // Return
               return rs.getLong("userID");
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 933);
            }
            
             return 0;
        }
        
        // Transfer assets
        public CResult doAssetTrans(String adr, 
                                    double amount, 
                                    String cur,
                                    String hash)
        {
            double balance;
            double new_balance;
                    
            try
            {
                     // Statement
                     Statement s=UTILS.DB.getStatement();
		     
                     // Load source
                     ResultSet rs=s.executeQuery("SELECT * FROM assets_owners WHERE owner='"+adr+"'");
		         
                     // Address exist ?
                     if (!UTILS.DB.hasData(rs))
                     {
                        // Insert address
                        UTILS.DB.executeUpdate("INSERT INTO assets_owners(owner, symbol, qty) VALUES('"+adr+"', '"+cur+"', '0')");
                         
                        // New balance
                        new_balance=Double.parseDouble(UTILS.FORMAT.format(amount));
                     }
                     else
                     {
                        // Next
                        rs.next();
                     
                        // Balance
		        balance=rs.getDouble("qty");
		     
                        // New balance
                        new_balance=balance+amount;
                     
                        // Format
                        new_balance=Double.parseDouble(UTILS.FORMAT.format(new_balance));
                     }
                     
		     // Source balance update
		     UTILS.DB.executeUpdate("UPDATE assets_owners "
		   		                + "SET qty="+new_balance+", "
		   		                    + "block='"+UTILS.BASIC.block()+
                                                "' WHERE owner='"+adr+"'");
                     
                     // Hash
                     UTILS.ROWHASH.update("assets_owners", "owner", adr, "symbol", cur);
                     
                
                     
                     // Close
                     s.close();
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 663);
            }
            
            // Return
            return new CResult(true, "Ok", "CUtils.java", 130);
        }
        
        public CResult doTrans(String adr, double amount, String hash)
        {
            double balance;
            double new_balance;
                    
            try
            {
                     // Statement
                     Statement s=UTILS.DB.getStatement();
		     
                     // Load source
                     ResultSet rs=s.executeQuery("SELECT * FROM adr WHERE adr='"+adr+"'");
		         
                     // Address exist ?
                     if (!UTILS.DB.hasData(rs))
                     {
                        UTILS.DB.executeUpdate("INSERT INTO adr (adr, "
                                                              + "balance, "
                                                              + "block, "
                                                              + "total_received, "
                                                              + "total_spent, "
                                                              + "trans_no, "
                                                              + "rowhash, "
                                                              + "last_interest) "
                                                     + "VALUES('"+adr+"', "+
                                                                  "'0', '"+
                                                                  UTILS.BASIC.block()+"', "
                                                                  + "'0', "
                                                                  + "'0', "
                                                                  + "'0', "
                                                                  + "'', "
                                                                  + "'0')");
                        // New balance
                        new_balance=Double.parseDouble(UTILS.FORMAT.format(amount));
                     }
                     else
                     {
                        // Next
                        rs.next();
                     
                        // Balance
		        balance=rs.getDouble("balance");
		     
                        // New balance
                        new_balance=balance+amount;
                     
                        // Format
                        new_balance=Double.parseDouble(UTILS.FORMAT.format(new_balance));
                     }
                     
		     // Source balance update
		     UTILS.DB.executeUpdate("UPDATE adr "
		   		                + "SET balance="+new_balance+", "
		   		                    + "block='"+UTILS.BASIC.block()+
                                                "' WHERE adr='"+adr+"'");
                        
                     if (amount>0)
                        UTILS.DB.executeUpdate("UPDATE adr "
                                                   + "SET total_received=total_received+"+amount+", "
                                                       + "trans_no=trans_no+1 "
                                                 + "WHERE adr='"+adr+"'");
                     else
                        UTILS.DB.executeUpdate("UPDATE adr "
                                                   + "SET total_received=total_spent+"+Math.abs(amount)+", "
                                                       + "trans_no=trans_no+1 "
                                                 + "WHERE adr='"+adr+"'");
                     
                     // Hash
                     UTILS.ROWHASH.update("adr", "adr", adr);
                     
                     
                     
                     // Close
                     s.close();
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 663);
            }
            
            // Return
            return new CResult(true, "Ok", "CUtils.java", 130);
        }
        
	public boolean isLink(String link)
	{
		if (link.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
                    return true;
                else 
                    return false;
	}
	
	public String getLinkCode(String link)
	{
		int c=0;
		String code="";
		
		String h=this.hash(link);
		for (int a=0; a<=h.length()-1; a++)
			if (h.charAt(a)=='0' || 
				h.charAt(a)=='1' || 
				h.charAt(a)=='2' || 
				h.charAt(a)=='3' || 
				h.charAt(a)=='4' || 
				h.charAt(a)=='5' || 
				h.charAt(a)=='6' || 
				h.charAt(a)=='7' || 
				h.charAt(a)=='8' ||
				h.charAt(a)=='9')
			{
				c++;
				if (c<10) code=code+h.charAt(a);
			}
		
		return code;
	}
	
	public long block()
	{
	    if (UTILS.CBLOCK!=null) 
                return UTILS.CBLOCK.prev_block_no+1;
            else
                return 0;
	}
	
	public long daysFromBlock(long block)
	{
		return Math.round((block-this.block())/1440);
	}
	
	public long blocskFromDays(long days)
	{
		return Math.round(1440*days);
	}
	
	public String addressFromDomain(String adr)
	{
		if (adr.length()>30) return adr;
		
		try
		{
                   Statement s=UTILS.DB.getStatement();
		   ResultSet rs=s.executeQuery("SELECT * FROM domains WHERE domain='"+adr+"'");
		   if (UTILS.DB.hasData(rs)==false) 
		   {
                       if (s!=null) s.close();
			   return "";
		   }
		   else
		   {
                       // Next
		       rs.next();
		       
                       // Address
                       String a=rs.getString("adr");
                       
                       // Close
                       if (s!=null) s.close();
                       
                       // Return
                       return a;
		   }
		}
		catch (SQLException ex) 
		{ 
			UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 520); 
	    }
		
		return "";
	}
	
	public byte[] randKey(int no)
	{
		// Generates a key
	    SecureRandom random = new SecureRandom();
		byte key[] = new byte[no];
		random.nextBytes(key);
		
		// Random
		return key;
	}
        
        public String randString(int no)
	{
		// Generates a key
	    SecureRandom random = new SecureRandom();
            byte key[] = new byte[no];
	    random.nextBytes(key);
		
	    // Random
	    return this.hash(key).substring(0, no);
	}
	
	public boolean domainExist(String domain)
	{
            try
            {
                Statement s=UTILS.DB.getStatement();
		ResultSet rs=s.executeQuery("SELECT * "
				                      + "FROM domains "
				                     + "WHERE domain='"+domain+"'");
		
		if (UTILS.DB.hasData(rs)) 
                {
                    // Close
                    if (s!=null) s.close();
                    
                    // Return
		    return true;
                }
		else
                {
                    // Close
                    if (s!=null) s.close();
                    
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
	
	public String adrFromDomain(String domain)
	{
	    try
	    {
                 Statement s=UTILS.DB.getStatement();
		 ResultSet rs=s.executeQuery("SELECT * "
                                                 + "FROM domains "
                                                + "WHERE domain='"+domain+"'");

                if (UTILS.DB.hasData(rs))
                {
                    // Next
                    rs.next();
                    
                    // Close
                    if (s!=null) s.close();
                    
                    // Address
                    String adr=rs.getString("adr");
                    
                    // Return
                    return adr;
                }
                else
                {
                    // Close
                    if (s!=null) s.close();
                    
                    // Return
                    return "";
                }
	    }
	    catch (SQLException ex) 
	    { 
		UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 600); 
	    }
        
        return "";
	}
	
	public String domainFromAdr(String adr)
	{
		try
		{
                     Statement s=UTILS.DB.getStatement();
		     ResultSet rs=s.executeQuery("SELECT * "
                                                   + "FROM domains "
                                                  + "WHERE adr='"+adr+"'");

           if (UTILS.DB.hasData(rs))
           {
              rs.next();
              String domain=rs.getString("domain");
              if (s!=null) s.close();
              return domain;
           }
           else
           {
               if (s!=null) s.close();
              return "";
           }
		}
		catch (SQLException ex) 
		{ 
			UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 600); 
	    }
        
        return "";
	}
	
	public long daysAheadFromBlock(long block)
	{
		long aBlock=UTILS.BASIC.block();
		long dif=Math.round(((block-aBlock)*100)/144000);
		return dif;
	}
	
	public boolean hasAttr(String adr, String atr)
	{
            try
            {
		 // Frozen address ?
                 Statement s=UTILS.DB.getStatement();
		 ResultSet rs_opt=s.executeQuery("SELECT * "
                                          + "FROM adr_options "
                                         + "WHERE adr='"+adr+"' "
                                    	    + "AND op_type='"+atr+"'");

                if (UTILS.DB.hasData(rs_opt)==true)
                {
                    // Close
                    s.close();
                    
                    // Return
                    return true;
                }
                else
                {
                    // Close
                    s.close();
                    
                    // Return
                    return false;
                }
	    }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 794);
            }
            
            // Return
            return false;
        }
	
	public void stackTrace()
	{
	   StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
           UTILS.CONSOLE.write(stackTraceElements[0].toString());
           UTILS.CONSOLE.write(stackTraceElements[1].toString());
           UTILS.CONSOLE.write(stackTraceElements[2].toString());
           UTILS.CONSOLE.write(stackTraceElements[3].toString());
           UTILS.CONSOLE.write(stackTraceElements[4].toString());
	}
	
	public long attrExpires(String adr, String atr)
	{
	   try
	   {
		 // Frozen address ?
                 Statement s=UTILS.DB.getStatement();
		 ResultSet rs=s.executeQuery("SELECT * "
                                          + "FROM adr_options "
                                         + "WHERE adr='"+adr+"' "
                                    	    + "AND op_type='"+atr+"'");

                 if (UTILS.DB.hasData(rs)==true)
                 {
                     // Next
                     rs.next();
                     
                     // Expires
                     long expires=rs.getLong("expires");
                     
                     // Close
                     if (s!=null) s.close();
                     
                     // Return
                     return expires;
                 }
                 else
                 {
                     // Close
                     if (s!=null) s.close();
                     
                     // Return
                     return 0;
                 }
	    }
	    catch (SQLException ex) 
	    { 
		UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 44);
	    }
		
		return 0;
	}
	
	public String getAttrPar(String adr, String op_type, String par)
	{
	    try
	    {
		 // Frozen address ?
                 Statement s=UTILS.DB.getStatement();
		 ResultSet rs=s.executeQuery("SELECT * "
                                          + "FROM adr_options "
                                         + "WHERE adr='"+adr+"' "
                                    	    + "AND op_type='"+op_type+"'");

                 if (UTILS.DB.hasData(rs)==true)
                 {
                     // Close
                     rs.next();
                     
                     // Par
                     String pa=rs.getString(par);
                     
                     // Close
                     if (s!=null) s.close();
                    
                     // Return
                     return pa;
                 }
                 else
                 {
                     // Close
                     if (s!=null) s.close();
                     
                     // Return
                     return "";
                 }
	    }
	    catch (SQLException ex) 
	    { 
			UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 44);
	    }
		
		return "";
	}
	
	public double getBalance(String adr, String cur)
	{
	   try
	   {
		 // Frozen address ?
                 Statement s=UTILS.DB.getStatement();
		 
                 ResultSet rs;
                 if (cur.equals("MSK"))
                    rs=s.executeQuery("SELECT * "
                                      + "FROM adr "
                                     + "WHERE adr='"+adr+"'");
                 else
                    rs=s.executeQuery("SELECT * "
                                      + "FROM assets_owners "
                                     + "WHERE owner='"+adr+"' "
                                       + "AND symbol='"+cur+"'");
                 
                if (UTILS.DB.hasData(rs)==true)
                {
                   // Next
                   rs.next();
                   
                   // Balance
                   double balance;
                   if (cur.equals("MSK"))
                      balance=rs.getDouble("balance");
                   else
                      balance=rs.getDouble("qty");
                   
                   // Close
                   if (s!=null) s.close();
                   
                   // Return
                   return balance;
                } 
                else
                {
                    // Close
                    if (s!=null) s.close();
                    
                    // Return
                    return 0;
                }
	   }
	   catch (SQLException ex) 
	   { 
	       UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 44);
	   }
		
	   return 0;
	}
	
        public boolean isLong(String n)
        {
            if (n.length()>20) return false;

            if (n.matches("[0-9]"))
               return true;
            else
               return false;
        }
        
	public String formatExpireBlock(long block)
	{
		long dif=(block-this.block())*100;
		return(this.getTime(dif));
	}
	
	public String getTableHash(String table)
	{
		String hash="";
		
		 try
		   {
                        Statement s=UTILS.DB.getStatement();
			   ResultSet rs=s.executeQuery("SELECT * "
			   		                         + "FROM "+table+" "
			   		                     + "ORDER BY rowhash ASC");
			   
			   while (rs.next()) hash=hash+rs.getString("rowhash");
                           
                           // Close
                           if (s!=null) s.close();
		   }
		   catch (SQLException ex) 
		   {
			   UTILS.LOG.log("SQLException", ex.getMessage(), "CRestrictedRec.java", 31);
		   }
		  
		 return UTILS.BASIC.hash(hash);
	}
	
	public String getAppDataDirectory() 
	{

	    String appDataDirectory;
	    
	    try 
	    {
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
        
        public boolean IPValid(String ip)
        {
            String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        }
        
        public boolean domainValid(String domain)
        {
             if (!domain.matches("[A-Za-z0-9\\.-]+"))
                return false;
             else
                return true;
        }
        
        public boolean mkt_days_valid(long days)
        {
             if (days<1)
                return false;
             else
                return true;
        }
        
        public boolean titleValid(String title)
        {
            // Check length
            if (title.length()<5 || title.length()>60)
                return false;
            else
                return true;
        }
        
        public boolean descriptionValid(String desc)
        {
           // Description length
            if (desc.length()<5 || desc.length()>250)
                return false;
            else
                return true;
        }
        
        public boolean symbolValid(String symbol)
        {
           // Description length
            if (symbol.length()!=6)
                return false;
            
            // Check letters
            if (!symbol.matches("[A-Z]+"))
                return false;
             else
                return true;
        }
        
        public boolean UIDValid(String uid)
        {
           // Description length
            if (uid.length()!=10)
                return false;
            
            // Check letters
            if (!uid.matches("[A-Z][a-z][0-9]+"))
                return false;
             else
                return true;
        }
        
        public boolean assetExist(String symbol)
        {
            if (!this.symbolValid(symbol))
               return false;
            
            try
            {
               Statement s=UTILS.DB.getStatement();
               ResultSet rs=s.executeQuery("SELECT * FROM assets WHERE symbol='"+symbol+"'");
               
               if (!UTILS.DB.hasData(rs))
               {
                   s.close();
                   return false;
               }
               else
               {
                   s.close();
                   return true;
               }
            }
            catch (SQLException ex) 
       	    {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
            }
            
            return false;
        }
        
        public boolean mktBidValid(double mkt_bid)
        {
             if (mkt_bid<0.0001)
                return false;
             else
                return true;
        }
        
        public boolean mktDaysValid(long mkt_days)
        {
             if (mkt_days<1)
                return false;
             else
                return true;
        }
        
        public boolean mktDays(long mkt_days)
        {
             if (mkt_days<1)
                return false;
             else
                return true;
        }
        
        public boolean feedExist(String feed)
        {
           if (!this.symbolValid(feed))
              return false;
            
           try
           {
              // Statement
              Statement s=UTILS.DB.getStatement();
           
              // Market
              ResultSet rs=s.executeQuery("SELECT * FROM feeds WHERE symbol='"+feed+"'");
              if (UTILS.DB.hasData(rs))
              {
                 s.close();
                 return true;
              }
              
              s.close();
              return false;
           }
           catch (SQLException ex) 
       	   {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
           }
           
           return false;
        }
        
        public boolean feedComponentExist(String feed, String feed_component)
        {
            if (!this.symbolValid(feed_component) || !this.symbolValid(feed))
              return false;
             
            try
            {
              // Statement
              Statement s=UTILS.DB.getStatement();
           
              // Market
              ResultSet rs=s.executeQuery("SELECT * "
                                          + "FROM feeds_components "
                                         + "WHERE feed_symbol='"+feed+"' "
                                           + "AND symbol='"+feed_component+"'");
              if (UTILS.DB.hasData(rs))
              {
                 s.close();
                 return true;
              }
              
              s.close();
              return false;
           }
           catch (SQLException ex) 
       	   {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
           } 
            
           return false;
        }
        
        public boolean countryExist(String cou)
        {
           return true;  
        }
        
        public boolean marketSymbolUsed(String symbol)
        {
          try
          {
              // Statement
              Statement s=UTILS.DB.getStatement();
              
              // Used in a bet ?
              ResultSet rs=s.executeQuery("SELECT * "
                                          + "FROM feeds_bets "
                                         + "WHERE bet_symbol='"+symbol+"'");
              if (UTILS.DB.hasData(rs))
              {
                s.close();
                return true;
              }
              
              // Used in asset markets ?
              rs=s.executeQuery("SELECT * "
                                + "FROM assets_markets "
                               + "WHERE mkt_symbol='"+symbol+"'");
              if (UTILS.DB.hasData(rs))
              {
                s.close();
                return true;
              }
              
              // Used in feeds markets ?
              rs=s.executeQuery("SELECT * "
                                + "FROM feeds_markets "
                               + "WHERE mkt_symbol='"+symbol+"'");
              if (UTILS.DB.hasData(rs))
              {
                s.close();
                return true;
              }
            
              s.close();
           }
           catch (SQLException ex) 
       	   {  
       	      UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 1826);
           }
          
          return false;
        }
        
         public double getFeedVal(String feed, String branch)
         {
             try
             {
                // Statement
                Statement s=UTILS.DB.getStatement();
              
                // Load feed data
                ResultSet rs=s.executeQuery("SELECT * "
                                            + "FROM feeds_components "
                                           + "WHERE feed_symbol='"+feed+"' "
                                             + "AND symbol='"+branch+"'");
                
                // Next 
                rs.next();
                
                // Value
                double val=rs.getDouble("val");
                
                // Close
                s.close();
                
                // Return
                return val;
             }
             catch (SQLException ex) 
       	     {  
       	        UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 1859);
             }
             
             return 0;
         }
         
         public String formatDif(String dif)
         {
             return (dif.substring(0, 3)+"-"+String.valueOf(dif.length()));
         }
         
         public long getUserID(String user) throws SQLException
         {
             // Statement
             Statement s=UTILS.DB.getStatement();
                
              // Load feed data
              ResultSet rs=s.executeQuery("SELECT * "
                                            + "FROM web_users "
                                           + "WHERE user='"+user+"'");
                
                // Next 
                rs.next();
                
                // User
                long userID=rs.getLong("ID");
                
                // Close
                s.close();
                
                // Return
                return userID;
         }
}
