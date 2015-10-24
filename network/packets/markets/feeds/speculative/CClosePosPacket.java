package wallet.network.packets.markets.feeds.speculative;

import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPayload;

public class CClosePosPacket 
{
   public CResult check(CBlockPayload block)
    {
         // Return
	 return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}
