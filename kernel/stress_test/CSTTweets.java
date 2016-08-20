package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.tweets.CNewTweetPacket;

public class CSTTweets extends CStressTest
{
    public CSTTweets()
    {
        super("tweets");
    }
    
     public void runTweet() throws Exception
     {
       CNewTweetPacket packet=new CNewTweetPacket(this.randomAdr(), 
                                                  this.randomAdr(),
                                                  "Test tweet", 
                                                  "Test tweet", 
                                                  0,
		                                  "https://www.exceptionnotfound.net/content/images/2015/04/the-coder.jpg", 30);
       
        UTILS.NETWORK.broadcast(packet);
     }
}
