package wallet.agents.VM.sys.agent.general;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.agents.VM.CCell;
import wallet.kernel.UTILS;

public class GENERAL 
{ 
    // Owner address
    public String owner_adr;
    
    // Title
    public String title;
    
    // Description
    public String description;
    
    // Block
    public long block;
    
    // Exire
    public long expire;
    
    // Balance
    public double balance;
    
    // Agent ID
    public long aID;
    
    // Password
    public String pass;
    
    public GENERAL(long agentID, boolean sandbox) throws Exception
    {
        // Statement
        
        
        // Result set
        ResultSet rs;
        
        // Load data
        if (sandbox==true)
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM agents_mine "
                         + "WHERE ID='"+agentID+"'");
        else
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM agents "
                         + "WHERE aID='"+agentID+"'");
        
        // Next
        rs.next();
        
        // Owner address
        this.owner_adr=rs.getString("adr");
    
        // Title
        this.title=UTILS.BASIC.base64_decode(rs.getString("name"));
    
        // Description
        this.description=UTILS.BASIC.base64_decode(rs.getString("description"));
    
        // Block
        this.block=rs.getLong("block");
    
        // Exire
        this.expire=rs.getLong("expire");
        
        // Agent ID
        this.aID=agentID;
        
        // Balance
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM adr "
                         + "WHERE adr='"+this.owner_adr+"'");
        
        // Next
        rs.next();
        
        // Balance
        this.balance=rs.getDouble("balance");
        
        // Close
        
    }
    
    public boolean validField(String field) throws Exception
    {
        // Uppercase
        field=field.toUpperCase();
        
        if (!field.equals("OWNER") && 
            !field.equals("NAME") && 
            !field.equals("AID") && 
            !field.equals("DESC") && 
            !field.equals("BLOCK") && 
            !field.equals("EXPIRE") &&
            !field.equals("BALANCE") &&
            !field.equals("PASS"))
          return false;
        else
          return true;
    }
    
    public CCell getData(String field) throws Exception
    {
        // Upper case
        field=field.toUpperCase();
        
        // Valid
        if (!this.validField(field))
            throw new Exception("Invalid field "+field);
        
        // Field selector
        switch (field)
        {
            case "AID" : return new CCell(this.aID); 
            case "OWNER" : return new CCell(this.owner_adr); 
            case "NAME" : return new CCell(this.title); 
            case "BALANCE" : return new CCell(this.balance); 
            case "DESC" : return new CCell(this.description); 
            case "BLOCK" : return new CCell(this.block); 
            case "EXPIRE" : return new CCell(this.expire); 
            case "PASS" : return new CCell(this.pass); 
        }
        
        // Blank
        return new CCell("");
    }
}
