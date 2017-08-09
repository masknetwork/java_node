// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CUtils 
{
	public CUtils() 
	{
		// TODO Auto-generated constructor stub
	}
	
        // checks if address is valid
	public boolean isAdr(String adr) throws Exception
	{
            // Length valid
            if (adr.length()!=120 &&
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
	
	
	public boolean adrExist(String adr) throws Exception
	{
            // Checks if address is valid
	    if (this.isAdr(adr)==false)
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
	
        // Timestamp
	public long tstamp() throws Exception 
        { 
            return System.currentTimeMillis()/1000; 
        }
	
        // Mili timestamp
	public long mtstamp() throws Exception 
        { 
            return System.currentTimeMillis(); 
        }
	
        // Hash on string
	public String hash(String hash) throws Exception 
        { 
            return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(hash); 
        }
        
        // Hash on data
       	public String hash(byte[] data) throws Exception 
        { 
           return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(data); 
        }
        
        // Base 64 encode
	public String base64_encode(String s) throws Exception
	{
           if (s.equals("")) return "";
           return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s.getBytes()));
	}
	
        // Base 64 encode on data
	public String base64_encode(byte[] s) throws Exception
	{
           return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s));
	}
	
        // Base 64 decode on string
	public String base64_decode(String s) throws Exception
	{
           if (s==null) return "";
           
           if (!s.equals("")) 
              return new String(org.apache.commons.codec.binary.Base64.decodeBase64(s));	
           else
              return "";
	}
	
        // Base 64 decode on data
	public byte[] base64_decode_data(String s) throws Exception
	{
           return org.apache.commons.codec.binary.Base64.decodeBase64(s);
	}
	
	// Random secure string
        public String randString(int no) throws Exception
	{
	    // Generates a key
	    SecureRandom random = new SecureRandom();
            byte key[] = new byte[no];
	    random.nextBytes(key);
		
	    // Random
	    return this.hash(key).substring(0, no);
	}
	
	// Is IP ? 
        public boolean isIP(String ip) throws Exception
        {
            if (ip==null)
                return false;
                
            String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        }
        
        // Is country
        public boolean isCountry(String cou) throws Exception
        {
            if (!cou.matches("^[A-Z]{2}$"))
	       return false;
	    else 
	       return true; 
        }
         
        // Round
        public double round(double val, int digits) throws Exception
        {
           long i=Math.round(val*(Math.pow(10, digits)));
           double r=i/(Math.pow(10, digits));
           return r;
        }
    
        // Generates an ID
        public long getID() throws Exception
        {
           return Math.round(Math.random()*999999999999999999L);
        }
    
        // Is a margin market address ?
        public boolean isSpecMktAddress(String adr) throws Exception
        {
            // Is address ?
            if (!this.isAdr(adr))
                return false;
            
            // Load data
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM feeds_spec_mkts "
                                              + "WHERE adr='"+adr+"'");
       
            // Return
            if (UTILS.DB.hasData(rs))
               return true;
            else 
               return false;
        }
    
        // Is an asset ?
        public boolean isAsset(String symbol) throws Exception
        {
            // Symbol
            if (!this.isSymbol(symbol))
               return false;
       
            // Load data
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM assets "
                                         + "WHERE symbol='"+symbol+"'");
       
            if (UTILS.DB.hasData(rs))
               return true;
            else
               return false;
        }
    
        // Can spend funds ?
        public boolean canSpend(String adr) throws Exception
        {
            if (this.isSpecMktAddress("adr") ||
                this.hasAttr(adr, "ID_RES_REC"))
            return false;
            
            else
            return true;
        }
    
        // chesk for sha256
        public boolean isHash(String hash) throws Exception
        {
	    if (!hash.matches("^[A-Fa-f0-9]{64}$"))
                return false;
	    else 
	        return true;
        }
	
        // Domain name valid ?
        public boolean isDomain(String domain) throws Exception
        {
	    if (!domain.matches("^[0-9a-z]{2,30}$"))
	       return false;
	    else 
	       return true;
        }
	
        // Input is a String ?
        public boolean isString(String str) throws Exception
        {
	    for (int a=0; a<=str.length()-1; a++)
            {
                if (Character.codePointAt(str, 0)<32 || 
                    Character.codePointAt(str, 0)>126)
                return false;
            }
        
            return true;
        }
        
        // Input is a valid signer ?
        public boolean isSymbol(String symbol) throws Exception
        {
            // Check letters
            if (!symbol.matches("[A-Z0-9]{1,10}"))
               return false;
            else
               return true;
        }
        
        // Input is a valid feed symbol ?
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
        
        // Valid feed branch ?
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
        
        // Valid title ?
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
        
        // Valid description ?
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
        
        // Description with predefined length
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
        
        // Is link ?
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
    
        // Is pic ?
        public boolean isPic(String link) throws Exception
        {   
            // Is link
            if (this.isLink(link)==false)
                return false;
        
            // Length
            if (link.endsWith(".jpg")==false && 
                link.endsWith(".jpeg")==false &&
                link.endsWith(".png")==false &&
                link.endsWith(".gif")==false)
            return false;
        
            return true;
        }
        
        // Is email ?
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
    
        // Format difficulty
        public String formatDif(String dif)
        {
           long d=64-dif.length();
           for (int a=1; a<=d; a++)
             dif="0"+dif;
           
           return dif;
        }
    
        // Is ID ?
        public boolean isID(long ID) throws Exception
        {
            // Search tweets
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM tweets "
                                              + "WHERE tweetID='"+ID+"'");
        
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
                return true;
                            
            // Comments
            rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM comments "
                                    + "WHERE comID='"+ID+"'");
        
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
                return true;
                                
            // Feeds
            rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM feeds "
                                    + "WHERE feedID='"+ID+"'");
        
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
                return true;
                                
            // Bets
            rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM feeds_bets "
                                    + "WHERE betID='"+ID+"'");
           
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
                return true;
        
            // Assets
            rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM assets "
                                    + "WHERE assetID='"+ID+"'");
        
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
                return true;
        
            // Margin mkts pos
            rs=UTILS.DB.executeQuery("SELECT * "
                                     + "FROM feeds_spec_mkts_pos "
                                    + "WHERE posID='"+ID+"'");
        
            // Has data ?
            if (UTILS.DB.hasData(rs)) 
                return true;
        
            // Return
            return false;
    }
    
    // Target valid ?
    public boolean targetValid(String target_type, long targetID) throws Exception
    {
       // Target type
       if (!target_type.equals("ID_POST") && 
           !target_type.equals("ID_COM") && 
           !target_type.equals("ID_FEED") && 
           !target_type.equals("ID_BET") && 
           !target_type.equals("ID_ASSET") &&
           !target_type.equals("ID_ASSET_MKT") &&
           !target_type.equals("ID_MARGIN_MKT") &&
           !target_type.equals("ID_MARGIN_MKT_POS"))
        return false;
       
       // Result
       ResultSet rs=null;
       
       // Load  data
       switch (target_type)
       {
            // Blog post
            case "ID_POST" : rs=UTILS.DB.executeQuery("SELECT * "
                                                      + "FROM tweets "
                                                      + "WHERE tweetID='"+targetID+"'");
                            break;    
                       
            // Comment
            case "ID_COM" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM comments "
                                                    + "WHERE comID='"+targetID+"'");
                                break;    
           
            // Data feed
            case "ID_FEED" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds "
                                                    + "WHERE feedID='"+targetID+"'");
                                break;    
            
            // Bet
            case "ID_BET" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds_bets "
                                                    + "WHERE betID='"+targetID+"'");
                                break;    
                                
            // Asset                   
            case "ID_ASSET" : rs=UTILS.DB.executeQuery("SELECT * "
                                                       + "FROM assets "
                                                      + "WHERE assetID='"+targetID+"'");
                                break;  
            
            // Asset market
            case "ID_ASSET_MKT" : rs=UTILS.DB.executeQuery("SELECT * "
                                                           + "FROM assets_mkts "
                                                          + "WHERE mktID='"+targetID+"'");
                                  break;  
            
            // Margin market
            case "ID_MARGIN_MKT" : rs=UTILS.DB.executeQuery("SELECT * "
                                                            + "FROM feeds_spec_mkts "
                                                           + "WHERE mktID='"+targetID+"'");
                                   break;  
            
            // Margin market pos 
            case "ID_MARGIN_MKT_POS" : rs=UTILS.DB.executeQuery("SELECT * "
                                                                + "FROM feeds_spec_mkts_pos "
                                                               + "WHERE posID='"+targetID+"'");
                                   break;  
                                
           
       }
             
       // Like tweet exist ?
       if (!UTILS.DB.hasData(rs))
           return false;
       
       // Return
       return true;
    }
    
    
    // Get reward amount
    public double getReward(String target) throws Exception
    {
        // Load address
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM adr "
                                          + "WHERE adr='default'");
        
        // Next
        rs.next();
        
        // Undistributed
        double unspend=rs.getDouble("balance");
        
        // Per day
        double per_day=unspend/365/20;
        
        // Reward
        double reward=0;
        
        switch (target)
        {
            // Posts
            case "ID_POST" : reward=per_day*0.2; break;
			
	    // Comments
	    case "ID_COM" : reward=per_day*0.1; break;
			
	    // Feeds
	    case "ID_FEEDS" : reward=per_day*0.05; break;
			
	    // Assets
	    case "ID_ASSETS" : reward=per_day*0.05; break;
			
	    // Bets
	    case "ID_BETS" : reward=per_day*0.1; break;
			
	    // Margin
	    case "ID_MKTS" : reward=per_day*0.1; break;
			
	    // Miners
	    case "ID_MINER" : reward=per_day*0.4/1440; break;
        }
        
        // Return
        return reward;
    }
    
    // Has attribute ?
    public boolean hasAttr(String adr, String attr) throws Exception
    {
        // Check address
        if (!this.isAdr(adr))
           return false;
            
        // Check attribute
        if (!attr.equals("ID_RES_REC") &&  
            !attr.equals("ID_TRUST_ASSET"))
        return false;
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM adr_attr "
                                          + "WHERE adr='"+adr+"' "
                                            + "AND attr='"+attr+"'");
       // Has data ?
       if (UTILS.DB.hasData(rs))
           return true;
       else
           return false;
    }
    
    // Vote target
    public void voteTarget(String adr, 
                           String target_type, 
                           long targetID, 
                           long block) throws Exception
    {
        // Check address
        if (!this.isAdr(adr))
            throw new Exception("Invalid target - CUtils.java, 607");
        
       // Target type
       if (!target_type.equals("ID_POST") && 
           !target_type.equals("ID_COM") && 
           !target_type.equals("ID_FEED") && 
           !target_type.equals("ID_BET") && 
           !target_type.equals("ID_ASSET") &&
           !target_type.equals("ID_ASSET_MKT") &&
           !target_type.equals("ID_MARGIN_MKT"))
        throw new Exception("Invalid target - CUtils.java, 613");
       
        // Voted ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM votes "
                                          + "WHERE adr='"+adr+"' "
                                            + "AND target_type='"+target_type+"' "
                                            + "AND targetID='"+targetID+"'");
                      
        // Has data
        if (!UTILS.DB.hasData(rs))
           UTILS.DB.executeUpdate("INSERT INTO votes "
                                       + "SET target_type='"+target_type+"', "
                                           + "targetID='"+targetID+"', "
                                           + "type='ID_UP', "
                                           + "adr='"+adr+"', "
                                           + "block='"+block+"'");
    }
    
    // Returns feed IS
    public long getFeedID(String symbol) throws Exception
    {
        // Checks symbol
        if (!this.isSymbol(symbol))
            throw new Exception("Invalid symbol");
        
        // Load asset
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE symbol='"+symbol+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
        {
            // NExt
            rs.next();
            
            // Return
            return rs.getLong("feedID");
        }
        
        // Return
        return 0;
    }
    
    // Get branch expiration block
    public long getFeedExpireBlock(String feed, String branch) throws Exception
    {
        // Valid symbols ?
        if (!this.isSymbol(feed) || 
            !this.isSymbol(branch))
        throw new Exception("Invalid values - CNewBetPayload.java"); 
        
        // Load asset
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+feed+"' "
                                            + "AND symbol='"+branch+"'");
        
        // Next
        rs.next();
            
        // Return
        return (rs.getLong("expire")-10);
    }
    
    // Get feed expiration block
    public long getFeedExpireBlock(String feed) throws Exception
    {
        // Valid symbols ?
        if (!this.isSymbol(feed))
           throw new Exception("Invalid values - CNewBetPayload.java"); 
        
        // Load asset
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE feed_symbol='"+feed+"'");
        
        // Next
        rs.next();
            
        // Return
        return (rs.getLong("expire")-10);
    }
    
    // Get asset expiration block
    public long getAssetExpireBlock(String symbol) throws Exception
    {
        // Symbol valid ?
        if (!this.isSymbol(symbol))
           throw new Exception("Invalid values - CNewBetPayload.java"); 
        
        // Load asset
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM assets "
                                          + "WHERE symbol='"+symbol+"'");
        
        // Next
        rs.next();
            
        // Return
        return (rs.getLong("expire")-10);
    }
    
    // Valid currency ?
    public boolean isCur(String cur) throws Exception
    {
        if (!cur.equals("MSK"))
        {
            if (!this.isSymbol(cur))
                return false;
            else
                return true;
        }
        else return true;
    }
    
    // Is base64 encoded string ?
    public boolean isBase64(String txt) throws Exception
    {
        if (txt.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$"))
           return true;
        else 
           return false;
    }
    
    // Input is a string identifier ?
    public boolean isStringID(String str) throws Exception
    {
        // Length
        if (str.length()>50)
            return false;
                
	if (str.matches("^[A-Z_]++$"))
           return true;
        else 
           return false;
    }
    
    // Get feed price
    public double getFeedPrice(String feed, String branch)  throws Exception
    {
        // Valid feed
        if (!this.isBranch(feed, branch))
           throw new Exception("Invalid feed");
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_branches "
                                          + "WHERE feed_symbol='"+feed+"' "
                                            + "AND symbol='"+branch+"'");
        
        // Next
        rs.next();
        
        // Return
        return rs.getDouble("val");
    }
    
    public void stackTrace() throws Exception
    {
	   StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
           
           System.out.println(stackTraceElements[0].toString());
           
           if (stackTraceElements.length>1)
           System.out.println(stackTraceElements[1].toString());
           
           if (stackTraceElements.length>2)
           System.out.println(stackTraceElements[2].toString());
           
           if (stackTraceElements.length>3)
           System.out.println(stackTraceElements[3].toString());
           
           if (stackTraceElements.length>4)
           System.out.println(stackTraceElements[4].toString());
           
           if (stackTraceElements.length>5)
           System.out.println(stackTraceElements[5].toString());
           
           if (stackTraceElements.length>6)
           System.out.println(stackTraceElements[6].toString());
           
           if (stackTraceElements.length>7)
           System.out.println(stackTraceElements[7].toString());
    }
}
