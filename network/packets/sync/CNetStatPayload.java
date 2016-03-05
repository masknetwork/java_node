package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import kernel.UTILS;

public class CNetStatPayload implements java.io.Serializable
{
	// Status
	String engine_status;
		
	// Last commited block
	long last_block;
		
	
		
	public CNetStatPayload() 
	{
		
	}
	
	public void refreshHashess()
	{
		
	}

}
