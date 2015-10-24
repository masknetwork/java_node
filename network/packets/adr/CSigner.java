package wallet.network.packets.adr;

public class CSigner 
{
	// Signer address
	public String signer;
	
	// Expires
	public long expires;
	
	public CSigner(String signer, long expires) 
	{
		// Signer
		this.signer=signer;
		
		// Expires
		this.expires=expires;
	}
}