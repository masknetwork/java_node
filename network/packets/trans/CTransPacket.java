// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import java.text.DecimalFormat;

import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.kernel.*;
import wallet.network.packets.adr.CProfilePayload;
        
public class CTransPacket extends CBroadcastPacket 
{		
    // Serial
    private static final long serialVersionUID = 100L;
   
    public CTransPacket(String fee_adr, 
			String src, 
	                String dest, 
	    	        double amount, 
			String cur,
			String mes,
                        String escrower,
                        long reqID) throws Exception
	{
		// Super class
		super("ID_TRANS_PACKET");
		   
		CTransPayload dec_payload = new CTransPayload(src, 
                                                              dest,
                                                              amount,
                                                              cur,
                                                              mes,
                                                              escrower);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(dec_payload);
				
		// Network fee
                if (cur.equals("MSK"))
		  fee=new CFeePayload(fee_adr,  0.0001);
		else
                  fee=new CFeePayload(fee_adr,  amount*0.0001);
                
                // Hash
                UTILS.DB.executeUpdate("UPDATE web_ops "
                                        + "SET response='"+dec_payload.hash+"' "
                                      + "WHERE ID='"+reqID+"'");
                
		// Sign packet
		this.sign();
      }
	
	 // Check 
	   public void check(CBlockPayload block) throws Exception
	   {
	      // Super class
	      super.check(block);
	   	
	      // Check type
	      if (!this.tip.equals("ID_TRANS_PACKET")) 
	   	throw new Exception("Invalid packet type - CTransPacket.java");
	   	  
	      // Deserialize transaction data
	      CTransPayload dec_payload=(CTransPayload) UTILS.SERIAL.deserialize(payload);
	      dec_payload.check(block);
	   	    
              if (dec_payload.cur.equals("MSK"))
                  {
                     if (this.fee.amount<0.0001)
                        throw new Exception("Invalid fee - CTransPacket.java");
                  }
                  else
                  {
                     if (this.fee.amount<(dec_payload.amount*0.0001))
                        throw new Exception("Invalid fee - CTransPacket.java");
                  }
	   	  
                  // Footprint
                  CPackets foot=new CPackets(this);
                  
                 foot.add("Source", dec_payload.src);
                 foot.add("Recipient", dec_payload.dest);
                 foot.add("Amount", UTILS.FORMAT_8.format(dec_payload.amount));
                 foot.add("Currency", dec_payload.cur);
                 foot.add("Escrower", dec_payload.escrower);
                 foot.write();
           }
}