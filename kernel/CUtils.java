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
import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPayload;

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
	public boolean adressValid(String adr) throws Exception
	{
            // Address name ?
            if (adr.length()<30)
               if (this.domainExist(adr))
                  return true;
            
            // Length valid
            if (adr.length()!=108 && 
		adr.length()!=124 && 
		adr.length()!=160 && 
		adr.length()!=212 &&
		!adr.equals("default")) return false;
	    
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
		   if (s!=null) rs.close(); s.close();
                   
                   // Return
                   return false;
                }
		else
                {
                   // Close
                   if (s!=null) rs.close(); s.close();
                    
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
	
	public long tstamp() throws Exception
	{
		return System.currentTimeMillis()/1000;
	}
	
	public long mtstamp() throws Exception
	{
		return System.currentTimeMillis();
	}
	
	public String hash(String hash) throws Exception
	{
            return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(hash);
	}
	
	public String hash(byte[] data) throws Exception
	{
		return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(data);
	}
        
        public byte[] hexhash(String data) throws Exception
	{
		return  org.apache.commons.codec.digest.DigestUtils.sha256(data);
	}
	
	public String hash512(String hash) throws Exception
	{
		return  org.apache.commons.codec.digest.DigestUtils.sha512Hex(hash); 
	}
	
	public String getFreeAdr() throws Exception
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
                              String par_3) throws Exception
	{
		UTILS.DB.executeUpdate("DELETE FROM adr_options "
				                   + "WHERE adr='"+adr+"' "
				                     + "AND op_type='"+atr+"' "
				                     + "AND par_1='"+par_1+"'"
				                     + "AND par_2='"+par_2+"'"
				                     + "AND par_3='"+par_3+"'");
	}
	
	// Check if address has options
	public boolean hasOption(String adr, String tip, String par_1, String par_2, String par_3) throws Exception
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
                   if (s!=null) rs.close(); s.close();
                   
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
                                 long block) throws Exception
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
		   
                   if (s!=null) 
                   {
                       rs_adr.close();
                       s.close();
                   } 
                   
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
                          if (s!=null) rs.close(); s.close();
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
	
	public boolean isTransID(String transID) throws Exception
	{
		return true;
	}
	
	public boolean isSHA512(String txt) throws Exception
	{
		// Check length
		if (txt.length()!=128) return false;
		
		// Return 
		return true;
	}
	
	public boolean isSHA256(String txt) throws Exception
	{
		// Check content
		
		// Check length
		
		// Return 
		return true;
	}
	
	// Compress a bytearray
	public static byte[] compress(byte[] data)  throws Exception
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
	public static byte[] decompress(byte[] data)  throws Exception
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
	
	public int getImgSize(String adr) throws Exception
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
	
	public String getTime(long dif) throws Exception
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
	
	public String splitText(int width, int letter_width, String txt) throws Exception
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
	
	public String format_price(double price) throws Exception
	{
		String[] v=String.valueOf(price).split("\\.");
		if (v[1].length()==0) return v[0]+".0000"; 
		if (v[1].length()==1) return v[0]+"."+v[1]+"000"; 
		if (v[1].length()==2) return v[0]+"."+v[1]+"00"; 
		if (v[1].length()==3) return v[0]+"."+v[1]+"0"; 
			
		return String.valueOf(price);
	}
	
	public String format_price_2_digits(double price) throws Exception
	{
		String[] v=String.valueOf(price).split("\\.");
		if (v[1].length()==0) return v[0]+".00"; 
		if (v[1].length()==1) return v[0]+"."+v[1]+"0"; 
			
		return String.valueOf(price);
	}
	
	public boolean isPrice(String price) throws Exception
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
	
	public boolean isInteger(String price) throws Exception
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
	
	public String dateFromTstamp(long tstamp) throws Exception
	{
		 Date time=new Date(tstamp*1000);
		 SimpleDateFormat dt = new SimpleDateFormat("MMM, dd, yyyy");
		 return dt.format(time);
	}
	
	public boolean isEmail(String email) throws Exception
	{
		return true;
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
	
	public byte[] base64_decode_data(String s) throws Exception
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
	
        public void clearTrans(String hash, String tip) throws Exception
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
                                         hash, 
                                         rs_trans.getDouble("invested"));
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
                rs_trans.close(); s.close();
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
            }
        }
        
        
        public boolean transExist(String table, String hash, String cur, double amount) throws Exception
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
        
        public void newTransfer(String src, 
                                String dest, 
                                double amount, 
                                boolean send_trans,
                                String cur, 
                                String expl, 
                                String escrower, 
                                String hash, 
                                long block,
                                CBlockPayload block_payload, 
                                double invested) throws Exception
        {
            this.newTrans(src, 
                          dest, 
                          -amount, 
                          send_trans,
                          cur, 
                          expl, 
                          escrower, 
                          hash, 
                          block,
                          block_payload,
                          invested);  
            
            this.newTrans(dest, 
                          src, 
                          amount, 
                          send_trans,
                          cur, 
                          expl, 
                          escrower, 
                          hash, 
                          block,
                          block_payload,
                          invested);  
        }
        
        public void newTrans(String adr, 
                             String adr_assoc, 
                             double amount, 
                             boolean send_trans,
                             String cur, 
                             String expl, 
                             String escrower, 
                             String hash, 
                             long block,
                             CBlockPayload block_payload,
                             double invested) throws Exception
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
                // Address valid
                if (!UTILS.BASIC.adressValid(adr)) throw new Exception("Invalid address");
                
                // Statement
                Statement s=UTILS.DB.getStatement();
                    
                // Trans ID
                long tID=UTILS.BASIC.getID();
                
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
                    UTILS.DB.executeUpdate("INSERT INTO my_trans(userID, " +
                                                                "adr, " +
                                                                "adr_assoc, " +
            		                                        "amount, " +
   		                                                "cur, " +
                                                                "expl, " +
   		                                                "escrower, "+
                                                                "hash, " +
                                                                "tID, " +
   		                                                "block, " +
   		                                                "tstamp, "+
   		                                                "status) "
   		                                      + "VALUES('"+
                                                                 this.getAdrUserID(adr)+"', '"+
   		                                                 adr+"', '"+
                                                                 adr_assoc+"', '"+
   		                                                 UTILS.FORMAT.format(amount)+"', '" +
   		                                                 cur+"', '"+
                                                                 expl+"', '"+
   		                                                 escrower+"', '" +
   		                                                 hash+"', '"+
                                                                 tID+"', '"+
   		                                                 block+"', '" +
   		                                                 UTILS.BASIC.tstamp()+"', '" +
   		                                                 "ID_UNCONFIRMED')");
                    
                    // Debit asset fee
                    if (!cur.equals("MSK") && asset_fee>0)
                    {
	    	    UTILS.DB.executeUpdate("INSERT INTO my_trans(userID, " +
                                                                "adr, " +
            		                                        "amount, " +
   		                                                "cur, " +
                                                                "expl, " +
   		                                                "escrower, "+
                                                                "hash, " +
                                                                "tID, " +
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
                                                                 tID+"', '"+
   		                                                 block+"', '" +
   		                                                 UTILS.BASIC.tstamp()+"', '" +
   		                                                 "ID_UNCONFIRMED')");
                    
                    
                     UTILS.DB.executeUpdate("INSERT INTO my_trans(userID,"+
                                                                "adr, " +
            		                                        "amount, " +
   		                                                "cur, " +
                                                                "expl, " +
   		                                                "escrower, "+
                                                                "hash, " +
                                                                "tID, " +
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
                                                                 tID+"', '"+
   		                                                 block+"', '" +
   		                                                 UTILS.BASIC.tstamp()+"', '" +
   		                                                 "ID_UNCONFIRMED')");
                    }
                    
                    UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_trans=unread_trans+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(adr)+"' ");
           
                 
	    }
               
            // Insert transaction 
            UTILS.DB.executeUpdate("INSERT INTO trans(src, " +
            		                              "amount, " +
   		                                      "cur, " +
   		                                      "escrower, "+
                                                      "hash, " +
                                                      "tID, " +
   		                                      "block, " +
   		                                      "tstamp, "+
   		                                      "status) "
   		                             + "VALUES('"+ 
                                                       adr+"', '"+
   		                                       UTILS.FORMAT.format(amount)+"', '" +
   		                                       cur+"', '"+
   		                                       escrower+"', '" +
   		                                       hash+"', '"+
                                                       tID+"', '"+
   		                                       block+"', '" +
   		                                       UTILS.BASIC.tstamp()+"', '" +
   		                                       "ID_UNCONFIRMED')");
            
            // Insert transaction 
            if (!cur.equals("MSK") && 
                asset_fee>0)
            {
	    UTILS.DB.executeUpdate("INSERT INTO trans(src, " +
            		                              "amount, " +
   		                                      "cur, " +
   		                                      "escrower, "+
                                                      "hash, " +
                                                      "tID, " +
   		                                      "block, " +
   		                                      "tstamp, "+
   		                                      "status) "
   		                             + "VALUES('"+
                                                       adr+"', '"+
   		                                       UTILS.FORMAT.format(-asset_fee)+"', '" +
   		                                       cur+"', '"+
   		                                       escrower+"', '" +
   		                                       hash+"', '"+
                                                       tID+"', '"+
   		                                       block+"', '" +
   		                                       UTILS.BASIC.tstamp()+"', '" +
   		                                       "ID_UNCONFIRMED')");
            
           
                 UTILS.DB.executeUpdate("INSERT INTO trans(src, " +
            		                              "amount, " +
   		                                      "cur, " +
   		                                      "escrower, "+
                                                      "hash, " +
                                                      "tID, " +
   		                                      "block, " +
   		                                      "tstamp, "+
   		                                      "status) "
   		                             + "VALUES('"+asset_fee_adr+"', '"+
   		                                       UTILS.FORMAT.format(asset_fee)+"', '" +
   		                                       cur+"', '"+
   		                                       escrower+"', '" +
   		                                       hash+"', '"+
                                                       tID+"', '"+
   		                                       block+"', '" +
   		                                       UTILS.BASIC.tstamp()+"', '" +
   		                                       "ID_UNCONFIRMED')");
            }
            
            // Close
            s.close();
            
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 909);
            }
          
        }
        
        // Get address owner
        public long getAdrUserID(String adr) throws Exception
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
                                    String hash,
                                    double inv) throws Exception
        {
            double balance=0;
            double new_balance=0;
            double invested=0;
                    
            try
            {
                     // Statement
                     Statement s=UTILS.DB.getStatement();
                     
                     // Load asset data
                     ResultSet rs=s.executeQuery("SELECT * "
                                                 + "FROM assets "
                                                + "WHERE symbol='"+cur+"'");
                     
                     // Next
                     rs.next();
                     
                     // Get asset type
                     long linked_mktID=rs.getLong("linked_mktID");
		     
                     // Load source
                     rs=s.executeQuery("SELECT * "
                                                 + "FROM assets_owners "
                                                + "WHERE owner='"+adr+"' "
                                                 + " AND symbol='"+cur+"'");
		         
                     // Address exist ?
                     if (!UTILS.DB.hasData(rs))
                     {
                        // Insert address
                        UTILS.DB.executeUpdate("INSERT INTO assets_owners(owner, "
                                                                       + "symbol, "
                                                                       + "qty, "
                                                                       + "invested, "
                                                                       + "last_interest) VALUES('"
                                                                       +adr+"', '"
                                                                       +cur+"', '0', '0', '"
                                                                       +UTILS.NET_STAT.last_block+"')");
                         
                        // New balance
                        new_balance=amount;
                     }
                     else
                     {
                        // Next
                        rs.next();
                     
                        // Balance
		        balance=rs.getDouble("qty");
		     
                        // New balance
                        new_balance=balance+amount;
                        
                        
                        // Market pegged asset ?
                        if (linked_mktID>0)
                        {
                           if (amount<0)
                           {
                              // Percent
                              double p=amount*100/balance;
                        
                              // Invested
                              invested=p*rs.getDouble("invested")/100;
                           }
                           else if (inv==0) 
                           {
                               // Load asset market data
                                rs=s.executeQuery("SELECT * "
                                               + "FROM feeds_assets_mkts "
                                              + "WHERE asset_symbol='"+cur+"'");
                             
                                // Next 
                                rs.next();
                             
                                // Last price
                                double last_price=rs.getDouble("last_price");
                             
                                // Invested
                                invested=last_price*amount;
                           }
                           else invested=inv;
                        }
                     }
                     
		     // Source balance update
		     UTILS.DB.executeUpdate("UPDATE assets_owners "
		   		                + "SET qty="+new_balance+", "
                                                    + "invested=invested+"+invested+", "
		   		                    + "block='"+UTILS.BASIC.block()+
                                                "' WHERE owner='"+adr+"' "
                                                  + "AND symbol='"+cur+"'");
                     
                     
                     // Close
                     if (!s.isClosed()) s.close();
            
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 1085);
            }
            
            // Return
            return new CResult(true, "Ok", "CUtils.java", 130);
        }
        
        public CResult doTrans(String adr, double amount, String hash) throws Exception
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
                                                                  + "'"+UTILS.NET_STAT.last_block+"')");
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
		   		                + "SET balance="+UTILS.FORMAT_8.format(new_balance)+", "
		   		                    + "block='"+UTILS.BASIC.block()+
                                                "' WHERE adr='"+adr+"'");
                        
                     if (amount>0)
                        UTILS.DB.executeUpdate("UPDATE adr "
                                                   + "SET total_received=total_received+"+UTILS.FORMAT_8.format(amount)+", "
                                                       + "trans_no=trans_no+1 "
                                                 + "WHERE adr='"+adr+"'");
                     else
                        UTILS.DB.executeUpdate("UPDATE adr "
                                                   + "SET total_spent=total_spent+"+UTILS.FORMAT_8.format(Math.abs(amount))+", "
                                                       + "trans_no=trans_no+1 "
                                                 + "WHERE adr='"+adr+"'");
                     
                    
                     // Close
                     rs.close(); s.close();
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 1164);
            }
            
            // Return
            return new CResult(true, "Ok", "CUtils.java", 130);
        }
        
	public boolean isLink(String link) throws Exception
	{
		if (link.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
                    return true;
                else 
                    return false;
	}
	
	public String getLinkCode(String link) throws Exception
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
	
	public long block() throws Exception
	{
	    if (UTILS.CBLOCK!=null) 
                return UTILS.NET_STAT.last_block+1;
            else
                return 0;
	}
	
	public long daysFromBlock(long block) throws Exception
	{
		return Math.round((block-this.block())/1440);
	}
	
	public long blocskFromDays(long days) throws Exception
	{
		return Math.round(1440*days);
	}
	
	public String addressFromDomain(String adr) throws Exception
	{
		if (adr.length()>30) return adr;
		
		try
		{
                   Statement s=UTILS.DB.getStatement();
		   ResultSet rs=s.executeQuery("SELECT * FROM domains WHERE domain='"+adr+"'");
		   if (UTILS.DB.hasData(rs)==false) 
		   {
                       if (s!=null) rs.close(); s.close();
			   return "";
		   }
		   else
		   {
                       // Next
		       rs.next();
		       
                       // Address
                       String a=rs.getString("adr");
                       
                       // Close
                       if (s!=null) rs.close(); s.close();
                       
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
	
	public byte[] randKey(int no) throws Exception
	{
		// Generates a key
	    SecureRandom random = new SecureRandom();
		byte key[] = new byte[no];
		random.nextBytes(key);
		
		// Random
		return key;
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
                Statement s=UTILS.DB.getStatement();
		ResultSet rs=s.executeQuery("SELECT * "
				                      + "FROM domains "
				                     + "WHERE domain='"+domain+"'");
		
		if (UTILS.DB.hasData(rs)) 
                {
                    // Close
                    if (s!=null) rs.close(); s.close();
                    
                    // Return
		    return true;
                }
		else
                {
                    // Close
                    if (s!=null) rs.close(); s.close();
                    
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
                    if (s!=null) rs.close(); s.close();
                    
                    // Address
                    String adr=rs.getString("adr");
                    
                    // Return
                    return adr;
                }
                else
                {
                    // Close
                    if (s!=null) rs.close(); s.close();
                    
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
	
	public String domainFromAdr(String adr) throws Exception
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
              if (s!=null) rs.close(); s.close();
              return domain;
           }
           else
           {
               if (s!=null) rs.close(); s.close();
              return "";
           }
		}
		catch (SQLException ex) 
		{ 
			UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 600); 
	    }
        
        return "";
	}
	
	public long daysAheadFromBlock(long block) throws Exception
	{
		long aBlock=UTILS.BASIC.block();
		long dif=Math.round(((block-aBlock)*100)/144000);
		return dif;
	}
	
	public boolean hasAttr(String adr, String atr) throws Exception
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
                    rs_opt.close(); s.close();
                    
                    // Return
                    return true;
                }
                else
                {
                    // Close
                    rs_opt.close(); s.close();
                    
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
	
	public void stackTrace() throws Exception
	{
	   StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
           UTILS.CONSOLE.write(stackTraceElements[0].toString());
           UTILS.CONSOLE.write(stackTraceElements[1].toString());
           UTILS.CONSOLE.write(stackTraceElements[2].toString());
           UTILS.CONSOLE.write(stackTraceElements[3].toString());
           UTILS.CONSOLE.write(stackTraceElements[4].toString());
	}
	
	public long attrExpires(String adr, String atr) throws Exception
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
                     if (s!=null) rs.close(); s.close();
                     
                     // Return
                     return expires;
                 }
                 else
                 {
                     // Close
                     if (s!=null) rs.close(); s.close();
                     
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
	
	public String getAttrPar(String adr, String op_type, String par) throws Exception
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
                     if (s!=null) rs.close(); s.close();
                    
                     // Return
                     return pa;
                 }
                 else
                 {
                     // Close
                     if (s!=null) rs.close(); s.close();
                     
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
	
	public double getBalance(String adr, String cur) throws Exception
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
                   if (s!=null) rs.close(); s.close();
                   
                   // Return
                   return balance;
                } 
                else
                {
                    // Close
                    if (s!=null) rs.close(); s.close();
                    
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
	
        public boolean isLong(String n) throws Exception
        {
            if (n.length()>20) return false;

            if (n.matches("[0-9]"))
               return true;
            else
               return false;
        }
        
	public String formatExpireBlock(long block) throws Exception
	{
		long dif=(block-this.block())*100;
		return(this.getTime(dif));
	}
	
	public String getTableHash(String table) throws Exception
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
                           if (s!=null) rs.close(); s.close();
		   }
		   catch (SQLException ex) 
		   {
			   UTILS.LOG.log("SQLException", ex.getMessage(), "CRestrictedRec.java", 31);
		   }
		  
		 return UTILS.BASIC.hash(hash);
	}
	
	public String getAppDataDirectory()  throws Exception
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
        
        public boolean IPValid(String ip) throws Exception
        {
            String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        }
        
        public boolean domainValid(String domain) throws Exception
        {
             if (!domain.matches("[A-Za-z0-9\\.-]+"))
                return false;
             else
                return true;
        }
        
        public boolean mkt_days_valid(long days) throws Exception
        {
             if (days<1)
                return false;
             else
                return true;
        }
        
        public boolean titleValid(String title) throws Exception
        {
            // Check length
            if (title.length()<3 || title.length()>60)
                return false;
            else
                return true;
        }
        
        public boolean descriptionValid(String desc) throws Exception
        {
           // Description length
            if (desc.length()<5 || desc.length()>250)
                return false;
            else
                return true;
        }
        
        public boolean symbolValid(String symbol) throws Exception
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
        
        public boolean UIDValid(String uid) throws Exception
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
        
        public boolean assetExist(String symbol) throws Exception
        {
            if (!this.symbolValid(symbol))
               return false;
            
            try
            {
               Statement s=UTILS.DB.getStatement();
               ResultSet rs=s.executeQuery("SELECT * FROM assets WHERE symbol='"+symbol+"'");
               
               if (!UTILS.DB.hasData(rs))
               {
                   rs.close(); s.close();
                   return false;
               }
               else
               {
                   rs.close(); s.close();
                   return true;
               }
            }
            catch (SQLException ex) 
       	    {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
            }
            
            return false;
        }
        
        public boolean mktBidValid(double mkt_bid) throws Exception
        {
             if (mkt_bid<0.0001)
                return false;
             else
                return true;
        }
        
        public boolean mktDaysValid(long mkt_days) throws Exception
        {
             if (mkt_days<1)
                return false;
             else
                return true;
        }
        
        public boolean mktDays(long mkt_days) throws Exception
        {
             if (mkt_days<1)
                return false;
             else
                return true;
        }
        
        public boolean feedExist(String feed) throws Exception
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
                 rs.close(); s.close();
                 return true;
              }
              
              rs.close(); s.close();
              return false;
           }
           catch (SQLException ex) 
       	   {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
           }
           
           return false;
        }
        
      
        public boolean countryExist(String cou) throws Exception
        {
           return true;  
        }
        
        public boolean marketSymbolUsed(String symbol) throws Exception
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
                rs.close(); s.close();
                return true;
              }
              
              // Used in asset markets ?
              rs=s.executeQuery("SELECT * "
                                + "FROM assets_markets "
                               + "WHERE mkt_symbol='"+symbol+"'");
              if (UTILS.DB.hasData(rs))
              {
                rs.close(); s.close();
                return true;
              }
              
              // Used in feeds markets ?
              rs=s.executeQuery("SELECT * "
                                + "FROM feeds_markets "
                               + "WHERE mkt_symbol='"+symbol+"'");
              if (UTILS.DB.hasData(rs))
              {
                rs.close(); s.close();
                return true;
              }
            
              rs.close(); s.close();
           }
           catch (SQLException ex) 
       	   {  
       	      UTILS.LOG.log("SQLException", ex.getMessage(), "CUtils.java", 1826);
           }
          
          return false;
        }
        
         public double getFeedVal(String feed, String branch) throws Exception
         {
             try
             {
                // Statement
                Statement s=UTILS.DB.getStatement();
              
                // Load feed data
                ResultSet rs=s.executeQuery("SELECT * "
                                            + "FROM feeds_branches "
                                           + "WHERE feed_symbol='"+feed+"' "
                                             + "AND symbol='"+branch+"'");
                
                // Next 
                rs.next();
                
                // Value
                double val=rs.getDouble("val");
                
                // Close
                rs.close(); 
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
         
         public String formatDif(String dif) throws Exception
         {
             return (dif.substring(0, 3)+"-"+String.valueOf(dif.length()));
         }
         
         public long getUserID(String user) throws Exception
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
                rs.close(); s.close();
                
                // Return
                return userID;
         }
         
         public String clean(String str) throws Exception
         {
             String clean=str.replace("<", "");
             clean=clean.replace(">", "");
             for (int a=1; a<=100; a++) clean=clean.replace("  ", " ");
             return clean;
         }
         
         public boolean feedValid(String feed, String branch) throws Exception
         {
            try
            {
               // Statement
               Statement s=UTILS.DB.getStatement();
            
               // Feed symbol valid
               if (!this.symbolValid(feed))
               {
                  s.close();
                  return false;
               }
           
               // Feed symbol exist ?
               ResultSet rs=s.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE symbol='"+feed+"'");
           
               if (!UTILS.DB.hasData(rs)) 
               {
                  rs.close(); s.close();
                  return false;
               }
           
               // Feed branch valid
               if (!UTILS.BASIC.symbolValid(branch)) 
               {
                  rs.close(); s.close();
                  return false;
               }
           
               // Feed symbol exist ?
               rs=s.executeQuery("SELECT * "
                                 + "FROM feeds_branches "
                                + "WHERE feed_symbol='"+feed
                                 +"' AND symbol='"+branch+"'");
           
              if (!UTILS.DB.hasData(rs)) 
              {
                 rs.close(); s.close();
                 return false;
              }
           
              rs.close(); s.close();
        }
        catch (SQLException ex) 
       	{  
            UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
        }
        
        
        return true;
    }
         
    public double round(double val, int digits) throws Exception
    {
        long i=Math.round(val*(Math.pow(10, digits)));
        double r=i/(Math.pow(10, digits));
        return r;
    }
    
    public double getBalance(String adr, String cur, CBlockPayload block) throws Exception
    {
        if (block==null)
            return UTILS.NETWORK.TRANS_POOL.getBalance(adr, cur);
        else
            return this.getBalance(adr, cur);
    }
    
    public long getExpireBlock(long days) throws Exception
    {
        return UTILS.NET_STAT.last_block+(UTILS.NET_STAT.blocks_per_day*days);
    }
    
    public long getID() throws Exception
    {
        return Math.round(Math.random()*10000000000L);
    }
    
    
}
