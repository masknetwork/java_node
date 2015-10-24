package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wallet.network.*;
import wallet.kernel.*;

public class CCheckPoint 
{
	// Hash
	public String hash="";
	
	// Block
	public long block;
	
	// Adr table hash
	public String adr_hash="";
	
	// Adr options table hash
	public String adr_options_hash="";
	
	// Ads table hash
	public String ads_hash="";
	
	// Assets owners table hash
	public String assets_owners_hash="";
	
	// Assets Markets table hash
	public String assets_markets_hash="";
	
	// Domains table Hash
	public String domains_hash="";
	
	// Market prods table hash
	public String prods_hash="";
	
	// Reviews table hash
	public String reviews_hash="";
	
	public CCheckPoint() 
	{
		// Block
		this.block=UTILS.BASIC.block()-2;
		
		// Address
		this.adr_hash=this.getTableHash("adr");
		
		// Adr options table hash
		this.adr_options_hash=this.getTableHash("adr_options");
		
		// Ads table hash
		this.ads_hash=this.getTableHash("ads");
		
		// Assets owners table hash
		this.assets_owners_hash=this.getTableHash("assets_owners");
		
		// Assets Markets table hash
		this.assets_markets_hash=this.getTableHash("assets_markets");
		
		// Domains table Hash
		this.domains_hash=this.getTableHash("domains");
		
		// Market prods table hash
		this.prods_hash=this.getTableHash("prods_market");
		
		// Hash
		this.hash=UTILS.BASIC.hash(this.adr_hash+
				                   this.adr_options_hash+
				                   this.ads_hash+
				                   this.assets_markets_hash+
				                   this.assets_owners_hash+
				                   this.domains_hash+
				                   this.prods_hash+
				                   this.prods_hash+
				                   String.valueOf(this.hash));
	}
    
	public CResult check()
	{
		// Check hash
		String h=UTILS.BASIC.hash(this.adr_hash+
                this.adr_options_hash+
                this.ads_hash+
                this.assets_markets_hash+
                this.assets_owners_hash+
                this.domains_hash+
                this.prods_hash+
                this.prods_hash+
                String.valueOf(this.hash));
		
		if (h.equals(this.hash)==false)
			return new CResult(false, "Invalid hash", "CCheckPoint.java", 82);
		
		// Return
	   return new CResult(true, "Ok", "CCheckPoint.java", 82);
	}
	
	public String getTableHash(String table)
	{
		String h="";
		
		try
		{
                   Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		   ResultSet rs=s.executeQuery("SELECT * "
		  		                        + "FROM "+table+" "
		  		                    + "ORDER BY rowhash ASC");
		  if (UTILS.DB.hasData(rs))
		  {
			  while (rs.next()) 
				h=h+UTILS.BASIC.hash(rs.getString("rowhash"));
		  }
		  else
		  {
			  h=UTILS.BASIC.hash("0");
		  }
                  
                  // Close
                  s.close();
		  
		  return h;
		  
		}
		catch (SQLException ex)
		{
			UTILS.LOG.log("SQLException", ex.getMessage(), "CCheckPoint.java", 56);
		}
		
		return "";
	}
}