package wallet.agents.VM.sys.agent.general;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.agents.VM.CCell;
import wallet.kernel.UTILS;

public class GENERAL 
{ 
    // Owner address
    String owner_adr;
    
    // Title
    String title;
    
    // Description
    String description;
    
    // Block
    long block;
    
    // Exire
    long expire;
    
    // Balance
    double balance;
    
    // Agent ID
    long aID;
    
    // Password
    String pass;
    
    public GENERAL(long agentID, boolean sandbox) throws Exception
    {
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Result set
        ResultSet rs;
        
        // Load data
        if (sandbox==true)
        rs=s.executeQuery("SELECT * "
                          + "FROM agents_mine "
                         + "WHERE ID='"+agentID+"'");
        else
        rs=s.executeQuery("SELECT * "
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
        
        // Password
        this.expire=rs.getLong("url_pass");
        
        // Agent ID
        this.aID=agentID;
        
        // Balance
        rs=s.executeQuery("SELECT * "
                          + "FROM adr "
                         + "WHERE adr='"+this.owner_adr+"'");
        
        // Next
        rs.next();
        
        // Balance
        this.balance=rs.getDouble("balance");
        
        // Close
        s.close();
    }
    
    public boolean validField(String field) throws Exception
    {
        // Uppercase
        field=field.toUpperCase();
        
        if (!field.equals("OWNER") && 
            !field.equals("NAME") && 
            !field.equals("ID") && 
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
            case "ID" : return new CCell(this.aID); 
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
