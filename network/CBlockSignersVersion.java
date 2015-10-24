package wallet.network;

import java.util.ArrayList;
import wallet.network.*;
import wallet.network.packets.blocks.*;

public class CBlockSignersVersion 
{
	// Total
	long signers_no;
		
	// Balance
	public double balance;
		
	// Signers
	ArrayList signers=new ArrayList();
		
	// Block hash
	public String block_hash;
		
	public CBlockSignersVersion(String block_hash) 
	{
	   this.block_hash=block_hash;	
	}
	
	public void addSigner(CBlockSignPacket packet)
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
			CBlockSignPacket packet=(CBlockSignPacket)this.signers.get(a);
			if (packet.signer.equals(signer)) return true;
		}
		
		return false;
	}

}
