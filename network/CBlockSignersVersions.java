package wallet.network;

import java.util.ArrayList;
import wallet.network.packets.blocks.*;

public class CBlockSignersVersions 
{
	// Versions
	public ArrayList versions=new ArrayList();
		
	public CBlockSignersVersions() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public void addSigner(CBlockSignPacket packet)
	{
		if (this.findSigner(packet.signer)==false)
		{
			CBlockSignersVersion ver=this.findVersion(packet.block_hash);
			if (ver!=null) 
			{
				ver.addSigner(packet);
			}
			else
			{
				CBlockSignersVersion v=new CBlockSignersVersion(packet.block_hash);
				this.versions.add(v);
				v.addSigner(packet);
			}
		}
	}
	
	public boolean findSigner(String signer)
	{
		for (int a=0; a<=this.versions.size()-1; a++)
		{
			CBlockSignersVersion ver=(CBlockSignersVersion) this.versions.get(a);
			if (ver.findSigner(signer)==true) return true;
		}
		
		return false;
	}
	
	public CBlockSignersVersion findVersion(String hash)
	{
		for (int a=0; a<=this.versions.size()-1; a++)
		{
			CBlockSignersVersion ver=(CBlockSignersVersion) this.versions.get(a);
			if (ver.block_hash.equals(hash)) return ver;
		}
		
		return null;
	}
	
	public CBlockSignersVersion getBestVersion()
	{
		double balance=0;
		CBlockSignersVersion ver=null;
		
		for (int a=0; a<=this.versions.size()-1; a++)
		{
			CBlockSignersVersion v=(CBlockSignersVersion) this.versions.get(a);
			if (v.balance>balance)
			{
				balance=v.balance;
				ver=v;
			}
		}
		
		return ver;
	}

}