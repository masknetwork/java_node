// Author : Vlad Cristian
// Contact : vcris@gmx.com

// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.*;
import wallet.network.packets.adr.*;

public class CAdrOptions 
{
	// Address
	public String adr;
    
	// Address exist ?
	public boolean address_exist=false;
	
	// Last interest
	public long lastInterest=0;
	
	// Balance
	public double balance=0;
	
	// Block
        public double block=0;
	
	// Blocked
	public boolean isFrozen=false;
	
	// Block expires
	public long frozenExpires=0;
	
	// Domain bought
	public boolean hasDomain=false;
	
	// Domain expires
	public long domainExpires=0;
	
	// Public address expires
	public long publicExpires=0;
	
	// Multisig adress
	public boolean isMultiSig=false;
	
	// Signers
	public CSigner[] signers=new CSigner[5];
	
	// Request western union
	public boolean requestWUDetails=false;
	
	// Request western union expires
	public long WUDetailsExpires=0;
	
	// Request bank details
        public boolean requestBankDetails=false;
		
	// Request western union expires
	public long bankDetailsExpires=0;
	
	// Request address
    public boolean requestAddressDetails=false;
		
	// Request address expires
	public long requestAddressExpires=0;
	
	// Request email
    public boolean requestEmailDetails=false;
		
	// Request email expires
	public long requestEmailExpires=0;
	
	// Request telephone
    public boolean requestTelDetails=false;
		
	// Request telephone expires
	public long requestTelExpires=0;
	
	// Restrict recipients
    public boolean isRestrictRec=false;
    
    // restrict expires
    public long restrictRecExpires=0;
    
    // Recipients list
    CRecipient[] recipients=new CRecipient[25];
    
    // Restrict escrowers
    public boolean restrictEscrowers=false;
    
    
    // Escrowers number
    public int escrowers_no=0;
	
    // Transaction limits
    public boolean hasTransLimits=false;
    
    // Trans limits min
    public double transLimitsMin=0;
    
    // Trans limits max
    public double transLimitsMax=0;
    
    // Trans limits expires
    public long transLimitsExpires=0;
    
    // Profile
    public boolean hasProfile=false;
    
    // Profile expires
    public long profileExpires=0;
    
    // OTP Active
    public boolean isOTP=false;
    
    // OTP
    public long otpExpires=0;
    
    // Frozen
    public boolean isSealed=false;
    
    // Frozen expires
    public long sealedExpires=0;
    
    // Next hash
    public String otpNextHash="";
    
    // Backup address
    public String otpBackup="";
    
    // Signers Number
    public int signers_no=0;
    
    // Request address
    public boolean req_address=false;
    public long req_address_expires=0;
    
    // Request bank account
    public boolean req_bank_account=false;
    public long req_bank_account_expires=0;
    
    // Request WU Details
    public boolean req_wu=false;
    public long req_wu_expires=0;
    
    // Request email
    public boolean req_email=false;
    public long req_email_expires=0;
    
    // Request telephone
    public boolean req_tel;
    public long req_tel_expires=0;
    
    // Request cryptocur address
    public boolean req_crypto;
    public long req_crypto_expires=0;
    
    // Adds a new signer
    private void addSigner(String adr, long expires)
    {
    	signers_no++;
    	CSigner signer=new CSigner(adr, expires);
    	this.signers[signers_no]=signer;
    }
    
   
    
	public CAdrOptions(String adr, boolean from_adr_pool) throws Exception
	{
            try
            {
		// Address
		this.adr=adr;
		
		// Balance and other details
                Statement s=UTILS.DB.getStatement();
		ResultSet rs_adr=s.executeQuery("SELECT * "
                                                + "FROM adr "
                                               + "WHERE adr='"+adr+"'");	
		
		// Address exist ?
		if (UTILS.DB.hasData(rs_adr)==false)
		{
			this.address_exist=false;
			this.balance=0;
			this.lastInterest=0;
			this.block=0;
		}
		else this.loadData(adr, from_adr_pool);
                
                if (s!=null) 
                {
                    rs_adr.close();
                    s.close();
                } 
            }
            catch (SQLException ex)
            {
                UTILS.LOG.log("SQLException", ex.getMessage(), "CAdrOptions.java", 191);
            }
	}
	
	public void loadData(String adr, boolean from_trans_pool) throws Exception
	{
		
		try
		{
                    // Balance and other details
                    Statement s=UTILS.DB.getStatement();
		    ResultSet rs_adr=s.executeQuery("SELECT * "
		                                    + "FROM adr "
		                                   + "WHERE adr='"+adr+"'");	
				
		    // Next
		    rs_adr.next();
			
		    // Address exist ?
		    this.address_exist=true;
		    
		    // Load balance
		    this.balance=UTILS.NETWORK.TRANS_POOL.getBalance(adr, "MSK");
		    
		    // Last interest
		    this.lastInterest=rs_adr.getLong("last_interest");
		    
		    // Block
		    this.block=rs_adr.getLong("block");
                    
                    if (s!=null) 
                    {
                        rs_adr.close();
                        s.close();
                    }
		}
		catch (SQLException ex) 
		{ 
			UTILS.LOG.log("ID_ERROR", ex.getMessage(), "CAdrOptions", 176); 
		}
		catch (Exception ex) 
		{ 
			UTILS.LOG.log("ID_ERROR", ex.getMessage(), "CAdrOptions", 177); 
		}
	
	
	    
        try
        {
            // Find address options
                Statement s=UTILS.DB.getStatement();
            ResultSet rs=s.executeQuery("SELECT * "
    		                      + "FROM adr_options "
    		                      + "WHERE adr='"+adr+"'");
    
            
          while (rs.next())
    	  {
    	    // Blocked address
    		if (rs.getString("op_type").equals("ID_FROZEN"))
    		{
    			this.isFrozen=true;
    			this.frozenExpires=rs.getLong("expires");
    		}
    		
    		// Sealed address
    		if (rs.getString("op_type").equals("ID_SEALED"))
    		{
    			this.isSealed=true;
    			this.sealedExpires=rs.getLong("expires");
    		}
    		
    		// Restrict rec
    		if (rs.getString("op_type").equals("ID_RESTRICT_REC"))
    		{
    			this.isRestrictRec=true;
    			this.restrictRecExpires=rs.getLong("expires");
    		}
    		
    		// Multisig address
    		if (rs.getString("op_type").equals("ID_MULTISIG"))
    		{
    			this.isMultiSig=true;
    			this.addSigner(rs.getString("par_1"), rs.getLong("expires"));
    		}
    		
    		// OTP address
    		if (rs.getString("op_type").equals("ID_OTP"))
    		{
    			this.isOTP=true;
    			this.otpExpires=rs.getLong("expires");
    			this.otpNextHash=rs.getString("par_1");
    			this.otpBackup=rs.getString("par_2");
    		}
    		
    		
    		// Request additional data
    		if (rs.getString("op_type").equals("ID_REQ_ADDRESS"))
    		{
    			this.req_address=true;
    			this.req_address_expires=rs.getLong("expires");
    		}
    		
    		if (rs.getString("op_type").equals("ID_REQ_BANK_ACCOUNT"))
    		{
    			this.req_bank_account=true;
    			this.req_bank_account_expires=rs.getLong("expires");
    		}
    		
    		if (rs.getString("op_type").equals("ID_REQ_WU"))
    		{
    			this.req_wu=true;
    			this.req_wu_expires=rs.getLong("expires");
    		}
    		
    		if (rs.getString("op_type").equals("ID_REQ_EMAIL"))
    		{
    			this.req_email=true;
    			this.req_email_expires=rs.getLong("expires");
    		}
    		
    		if (rs.getString("op_type").equals("ID_REQ_TEL"))
    		{
    			this.req_tel=true;
    			this.req_tel_expires=rs.getLong("expires");
    		}
    		
    		if (rs.getString("op_type").equals("ID_REQ_CRYPTO"))
    		{
    			this.req_crypto=true;
    			this.req_crypto_expires=rs.getLong("expires");
    		}
    	}
          
          if (s!=null) rs.close(); s.close();
    }
    catch (SQLException ex) 
    { 
        UTILS.LOG.log("SQLException", ex.getMessage(), "CAdrOptions.java", 313);
    }
    }
}