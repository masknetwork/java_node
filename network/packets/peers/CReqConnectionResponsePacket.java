// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.peers;

import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.*;

public class CReqConnectionResponsePacket extends CPacket 
{
    // Aproved
    boolean aproved;
	
    // IP
    String ip;
	
    // Version  
    String ver;
    
    // Serial
   private static final long serialVersionUID = 100L;
        
	
    public CReqConnectionResponsePacket(boolean aproved, CPeer peer) throws Exception
    {
	   super("ID_REQ_CON_RESPONSE_PACKET");
	   
	    // Aproved
	   this.aproved=aproved;
	   
	   // IP
	   this.ip=peer.adr;
	   
	   // Version
	   this.ver=UTILS.STATUS.version;
	   
	   // Hash
	   this.hash=UTILS.BASIC.hash(UTILS.BASIC.mtstamp()+
			              String.valueOf(aproved)+
			              this.ver+
			              UTILS.STATUS.engine_status);
    }
    
    public void check(CPeer peer) throws Exception
    {
    	 // Aproved
    	 if (this.aproved==true) 
    		 UTILS.NETWORK.peers.addPeer(peer, peer.port); 
    	 else
    		System.out.println("Connection refused");
    	
    }
}