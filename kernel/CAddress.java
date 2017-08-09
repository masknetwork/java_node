// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.ResultSet;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class CAddress 
{
	 
    // Public and private key
    public byte[] public_key;
    
    // Private key
    public byte[] private_key;
    
    // Description
    public String description="";
    
    // Public key
    ECPublicKey ecPublicKey;
    
    // PRivate key
    ECPrivateKey ecPrivateKey;
 	
    
 	
    public CAddress() 
    {
		
    }

	
	public CAddress(String public_key, 
                        String private_key)  throws Exception
	{
            // Key factory
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  
            // Encoded key
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_key));
		  
            // Public key
            this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		  
            // Encoded key spec
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_key));
		  
            // Private key
            this.ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		
	    // Public key
	    this.public_key=Base64.decodeBase64(public_key);
		
            // Private key
            this.private_key=Base64.decodeBase64(private_key);
	}
	
	public boolean importAddress(long userID, 
                                     String public_key, 
                                     String private_key, 
                                     String tag)  throws Exception
	{
            // Adr
            if (!UTILS.BASIC.isAdr(public_key)) 
                throw new Exception("Invalid public key - CAddress.java, 78");
            
	    // Load provider
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  
            // Key factory
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  
            // Key spec
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_key));
		  
            // Public
            this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		  
            // Private
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_key));
		  
            // Set private key
            this.ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		
	    // Public key format
	    this.public_key=Base64.decodeBase64(public_key);
	
            // Private key format
            this.private_key=Base64.decodeBase64(private_key);
		
            // Description
            this.description=tag;
		
	    // Address exist 
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM my_adr "
                                              + "WHERE adr='"+public_key+"'");
		
            if (!UTILS.DB.hasData(rs))
            {
                // Insert address
	        UTILS.DB.executeUpdate("INSERT INTO my_adr SET userID='"+userID+"', "
                                                                + "adr='"+public_key+"', "
                                                                + "description='"+UTILS.BASIC.base64_encode(tag)+"'");
				        
		
		
		// Adds to wallet
		UTILS.WALLET.add(this);
            }
                
	    return true;
	}
	
	
	public void generate() throws Exception
	{
            // Set curve
            
           
            // Key generator
	    KeyPairGenerator keyPairGenerator =KeyPairGenerator.getInstance("EC", "BC");
	           
            // Curve specification     
            ECNamedCurveParameterSpec curveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
		   
            // Init
            keyPairGenerator.initialize(curveParameterSpec, new SecureRandom()); 
	    
            // Pair
            KeyPair KeyPair = keyPairGenerator.generateKeyPair();
		   
            // Pub key
            ecPublicKey = (ECPublicKey) KeyPair.getPublic();
		   
            // Private key
            ecPrivateKey = (ECPrivateKey) KeyPair.getPrivate();
	
            // Encoded
            this.public_key=ecPublicKey.getEncoded();
        
            // Encoded private
            this.private_key=ecPrivateKey.getEncoded();
	}
	
	public String getPublic() 
	{
            return Base64.encodeBase64String(this.public_key);
        }
        
        
	public String getPrivate() 
	{
           return Base64.encodeBase64String(this.private_key);
        }
	
	public String encrypt(String data) throws Exception
	{
	    // Chiper
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
		   
            // Init
            c.init(c.ENCRYPT_MODE, this.ecPublicKey, new SecureRandom());   
		   
            // Encrypt
            byte[] encrypted=c.doFinal(data.getBytes(), 0, (data.getBytes()).length);
		   
            // Return
            return Base64.encodeBase64String(encrypted);
	}
	
	public String decrypt(String data) throws Exception
	{
            // Decode
	    byte[] d=Base64.decodeBase64(data);
	    
            // Choper
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
			
            // Init
            c.init(c.DECRYPT_MODE, this.ecPrivateKey, new SecureRandom());   
			
            // Decrypt
            byte[] decrypted=c.doFinal(d, 0, d.length);
			
            // Return
            return new String(decrypted);
	}
	
	public String sign(String data) throws Exception
	{
	    // Signature
            Signature signature = Signature.getInstance("ECDSA", "BC");
	
            // Init signature
            signature.initSign(this.ecPrivateKey, new SecureRandom());
		   
            // Update
            signature.update(data.getBytes());
		   
            // Sign
            byte[] signed=signature.sign();
		   
            // Return
            return Base64.encodeBase64String(signed);
	}
}