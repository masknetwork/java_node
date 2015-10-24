package wallet.network.packets.adr;

public class CRecipient 
{
	// Address
	public String adr;
	
	// Expires
	public long expires;
	
	public CRecipient(String adr, long expires) 
	{
	   // Address
	   this.adr=adr;
	   
	   // Expires
	   this.expires=expires;
	}

}