// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;


import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.interfaces.ECPrivateKey;


public class CECC 
{
    // Public key
    public byte[] public_key;
    
    // Private key
    public byte[] private_key;
    
    // EC public key
    ECPublicKey ecPublicKey;
 	
    // EC private key
    ECPrivateKey ecPrivateKey;
 	
    // Curve
    String curve;
 	
    
    public CECC()
    {
    	
    }
    
    public CECC(String pubKey) throws Exception
    {
    	// PRovider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  
        // Key factory
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  
        // Key spec
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
		  
        // Public key
        this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		
	// Decode
	this.public_key=Base64.decodeBase64(public_key);
    }
    
    public String encrypt(String data) throws Exception
    {
        // Chiper   
	javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
	
        // Init
        c.init(c.ENCRYPT_MODE, this.ecPublicKey, new SecureRandom());   
	     
        // Encrypt
        byte[] encrypted=c.doFinal(data.getBytes(), 0, (data.getBytes()).length);
	     
        // Encoded
        return Base64.encodeBase64String(encrypted);
    }
    
    public boolean checkSig(String data, String sig) throws Exception
    {  
        // Signature
        Signature signature = Signature.getInstance("ECDSA", "BC");	
	
        // Verify
        signature.initVerify(this.ecPublicKey);
	
        // Update
        signature.update(data.getBytes());
		
	if (signature.verify(Base64.decodeBase64(sig)))
	    return true;
	else
	    return false;
    }
}