package network.packets.sync;

import network.packets.CPacket;

public class CNetStatPacket extends CPacket 
{
   public CNetStatPacket()
   {
	   super("ID_NETSTAT_PACKET");
   }
}
