package wallet.network.packets.trans.details;

import wallet.kernel.*;

public class CCartDetails extends CPayDetails 
{
	// Items
	CCartItem[] items=new CCartItem[100];
	
	// Items number
	int items_no=0;
	
	public CCartDetails() 
	{
	   	
	}
	
	// Adds a new item
	public void addItem(String ID, String title, long qty)
	{
		// Increases items number
		items_no++;
		
		// Adds new item
		this.items[items_no]=new CCartItem(ID, title, qty);
		
		// Recalculates hash
		this.hash();
	}
	
	// Recalculates hash
    private void hash()
    {
    	for (int a=1; a<=this.items_no; a++)
    		this.hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.hash+this.items[a]); 
    }
}
