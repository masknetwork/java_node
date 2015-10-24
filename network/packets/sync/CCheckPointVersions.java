package wallet.network.packets.sync;

import java.util.ArrayList;

public class CCheckPointVersions 
{
	// Versions
	ArrayList versions=new ArrayList();
	
	public CCheckPointVersions() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public void addSigner(CCheckPointPacket packet)
	{
		if (this.findSigner(packet.signer)==false)
		{
			CCheckPointVersion ver=this.findVersion(packet.checkpoint.hash);
			if (ver!=null) 
			{
				ver.addSigner(packet);
			}
			else
			{
				CCheckPointVersion v=new CCheckPointVersion();
				this.versions.add(v);
				v.addSigner(packet);
			}
		}
	}
	
	public boolean findSigner(String signer)
	{
		for (int a=0; a<=this.versions.size()-1; a++)
		{
			CCheckPointVersion ver=(CCheckPointVersion) this.versions.get(a);
			if (ver.findSigner(signer)==true) return true;
		}
		
		return false;
	}
	
	public CCheckPointVersion findVersion(String hash)
	{
		for (int a=0; a<=this.versions.size()-1; a++)
		{
			CCheckPointVersion ver=(CCheckPointVersion) this.versions.get(a);
			if (ver.hash.equals(hash)) return ver;
		}
		
		return null;
	}
	
	public CCheckPointVersion getBestVersion()
	{
		double balance=0;
		CCheckPointVersion ver=null;
		
		for (int a=0; a<=this.versions.size()-1; a++)
		{
			CCheckPointVersion v=(CCheckPointVersion) this.versions.get(a);
			if (v.balance>balance)
			{
				balance=v.balance;
				ver=v;
			}
		}
		
		return ver;
	}

}
