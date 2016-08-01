package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.assets.reg_mkts.CNewRegMarketPosPacket;

public class CSTAssetsMktsPos extends CStressTest
{
    public CSTAssetsMktsPos()
    {
        super("assets_mkts_pos");
    }
    
    public void runRegMktPosPacket() throws Exception
    {
        CNewRegMarketPosPacket packet=new CNewRegMarketPosPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEerR7G4+VPVcsIGpu6G5nzi/sIEo7Tp+rFW81Th2sSLYiCZe9aWVndTeJ4TW7ppT31ULDWUugFJE=",
                                                             "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEerR7G4+VPVcsIGpu6G5nzi/sIEo7Tp+rFW81Th2sSLYiCZe9aWVndTeJ4TW7ppT31ULDWUugFJE=",
                                                             6581027803L,
                                                             "ID_BUY",
                                                             0.0032,
                                                             3,
                                                             2);
        UTILS.NETWORK.broadcast(packet);
    }
}
