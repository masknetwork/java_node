package wallet.agents.VM;

import java.util.regex.Pattern;

public class CVMUtils 
{
    public boolean isRegister(String reg)
    {
        // Upper case
        reg=reg.toUpperCase();
        
        // Check if register
        if (reg.equals("R1")) return true;
        if (reg.equals("R2")) return true;
        if (reg.equals("R3")) return true;
        if (reg.equals("R4")) return true;
        if (reg.equals("R5")) return true;
        if (reg.equals("R6")) return true;
        if (reg.equals("R7")) return true;
        if (reg.equals("R8")) return true;
        if (reg.equals("R9")) return true;
        if (reg.equals("R10")) return true;
        
        return false;
    }
    
    public boolean typeValid(String type)
    {
        if (type.equals("ID_LONG") || 
            type.equals("ID_DOUBLE") || 
            type.equals("ID_STRING"))
            return true;
        else
            return false;
    }
    
    public boolean isInstruction(String str)
    {
        // Upper
        str=str.toUpperCase();
        
        switch (str)
        {
            case "MOV" : return true;
            case "ADD" : return true;
            case "SUB" : return true;
            case "MUL" : return true;
            case "DIV" : return true;
            case "MATH" : return true;
            case "RAND" : return true;
            case "TOSTRING" : return true;
            
            
            case "EXIT" : return true;
            case "GOTO" : return true;
            case "SIZE" : return true;
            case "LOG" : return true;
            case "IF" : return true;
            
            // Storage
            case "STORGET" : return true;
            case "STORSET" : return true;
            case "STORDEL" : return true;
            case "STORADDLINE" : return true;
            case "STORQUERY" : return true;
            
            // System calls
            case "DBQUERY" : return true;
            case "MES" : return true;
            case "TWEET" : return true;
            case "TRANS" : return true;
            case "GETFEED" : return true;
            case "SETFEED" : return true;
            case "TSTAMP" : return true;
            case "DATE" : return true;
            
            // String
            case "CONCAT" : return true;
            case "INDEXOF" : return true;
            case "LASTCH" : return true;
            case "REPLACE" : return true;
            case "SPLIT" : return true;
            case "STRLEN" : return true;
            case "SUBSTR" : return true;
            case "TRIM" : return true;
            
            case "ATPOS" : return true;
            case "REGEX" : return true;
            case "REFUND" : return true;
            
            case "PUSH" : return true;
            case "POP" : return true;
            
            case "HASH" : return true;
        }
        
        // Not found
        return false;
    }
    
    public boolean isDouble(String str)
    {
        if (str.indexOf('.')>0)
        {
            try
            {
               double l=Double.parseDouble(str);
            }
            catch (Exception ex)
            {
                return false;
            }
        } else return false;
        
        return true;
    }
    
    public boolean isLong(String str)
    {
        try
        {
           long l=Long.parseLong(str);
        }
        catch (Exception ex)
        {
            return false;
        }
        
        return true;
    }
}
