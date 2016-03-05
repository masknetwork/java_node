// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import wallet.kernel.UTILS;

public class CResult 
{

	// Result
		public boolean passed;
		
		// Reason
		public String reason;
		
		// Error File
		public String file;
		
		// Error line
		public int line;
		
		public CResult(boolean passed, String reason, String file, int line) 
		{
			// Result
			this.passed=passed;
			
			// Reason
			this.reason=reason;
			
			// Error File
			this.file=file;
			
			// Error line
			this.line=line;
		}
		
		public void report() throws Exception
		{
			if (this.passed==false) 
				System.out.print(this.reason+"("+this.file+", "+this.line+")");
			else
				UTILS.CONSOLE.write("This packet passed. ");
			
		}
}
