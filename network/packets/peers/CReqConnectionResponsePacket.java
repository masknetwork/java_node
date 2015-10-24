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
        
	
    public CReqConnectionResponsePacket(boolean aproved, CPeer peer)
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
    
    public CResult checkWithPeer(CPeer peer)
    {
    	 // Aproved
    	 if (this.aproved==true) 
    		 UTILS.NETWORK.peers.addPeer(peer, peer.port); 
    	 else
    		 UTILS.CONSOLE.write("Connection refused");
    	 
    	 // IP
    	 UTILS.STATUS.IP=this.ip;
    	 
    	 // Country
    	 CGeoIP geo=new CGeoIP();
    	 UTILS.STATUS.country=geo.getCountry(UTILS.STATUS.IP);
    	
    	 // Return
 	     return new CResult(true, "Ok", "CReqConnectionPacket.java", 22);
    }
}