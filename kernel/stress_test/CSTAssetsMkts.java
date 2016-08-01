package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.assets.reg_mkts.CNewRegMarketPacket;

public class CSTAssetsMkts extends CStressTest
{
    public CSTAssetsMkts()
    {
        super("assets_mkts");
    }
    
    public void runRegMktPacket() throws Exception
    {
        String symbol=this.randomSymbol();
        
        CNewRegMarketPacket p=new CNewRegMarketPacket(this.randomAdr(),
		                                      this.randomAdr(),
                                                      symbol,
                                                      "MSK",
                                                      4,
                                                      symbol+" / MSK market", 
                                                      "Market between "+symbol+" and MSK",
                                                      1000);
        UTILS.NETWORK.broadcast(p);
    }
}
