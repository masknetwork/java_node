package wallet.kernel;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;

public class CGeoIP {

	public CGeoIP() 
	{
	}
	
	public String getCountry(String ip)
	{
		try
		{
	 	   File database = new File("countries.mmdb");
		   DatabaseReader reader = new DatabaseReader.Builder(database).build();
		   CountryResponse response = reader.country(InetAddress.getByName(ip));
		   return(response.getCountry().getIsoCode());
		}
		catch (IOException ex)
		{
		    UTILS.LOG.log("IOException", ex.getMessage(), "CGeoIP.java", 24);	
		}
		catch (GeoIp2Exception ex)
		{
		    UTILS.LOG.log("GeoIp2Exception", ex.getMessage(), "CGeoIP.java", 24);	
		}
		
		 return("");
	}
	
}
