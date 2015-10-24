package wallet.network.packets.trans.details;

import wallet.kernel.*;

public class CCartItem implements java.io.Serializable
{
    // ID
	String ID;
	
	// Title
	String title;
	
	// QTY
	long qty;
	
	// Hash
	public String hash;
	
	public CCartItem(String ID, String title, long qty) 
	{
		// ID
		this.ID=ID;
		
		// Title
		this.title=title;
		
		// QTY
		this.qty=qty;
		
		// Hash
		hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(ID+title+qty); 
	}

}
