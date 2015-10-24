package wallet.network.packets.sync;

import java.util.ArrayList;

public class CCheckPointVersion 
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
		
		// Signers
		long signers_no=0;
		
		// Height
		double balance=0;
		
		// Signers list
		ArrayList signers=new ArrayList();
		
	public CCheckPointVersion() 
	{
		
	}
	
	public void addSigner(CCheckPointPacket packet)
	{
		if (this.findSigner(packet.signer)==false) 
		{
			this.signers.add(packet);
			this.signers_no++;
			this.balance=this.balance+packet.balance;
		}
	}
	
	public boolean findSigner(String signer)
	{
		for (int a=0; a<=this.signers.size()-1; a++)
		{
			CCheckPointPacket packet=(CCheckPointPacket)this.signers.get(a);
			if (packet.signer.equals(signer)) return true;
		}
		
		return false;
	}

}
