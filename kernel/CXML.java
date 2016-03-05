// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import wallet.network.packets.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CXML 
{
	public CXML() 
	{
		
	}
	
	public String getStatus(String data)
	{
		SAXBuilder builder = new SAXBuilder();
		
		try
		{
		   Document document = (Document) builder.build(new StringReader(data));
		   Element rootNode = document.getRootElement();
		   List list = rootNode.getChildren();
		   
		   for (int i = 0; i < list.size(); i++) 
		   {
			   Element node = (Element) list.get(i);
	                      if (node.getName().equals("status")) return (node.getText());
		   }
		} 
		catch (IOException ex) 
		{
		   UTILS.LOG.log("CSoftwareUpdate.java", ex.getMessage(), "CSoftwareUpdate.java", 95);
	    } 
		catch (JDOMException ex) 
		{
			 UTILS.LOG.log("CSoftwareUpdate.java", ex.getMessage(), "CSoftwareUpdate.java", 99);
		}
		
		return "";
	}
	
	

}
