package wallet.kernel;

import java.util.ArrayList;

public class CSyntaxGen 
{
   // Instruction
   String ins;
   
   // Parameters
   long params;
   
   // Par 1 
   public ArrayList par_1=new ArrayList();
   
   // Par 2
   public ArrayList par_2=new ArrayList();
   
   // Par 3 
   public ArrayList par_3=new ArrayList();
   
   // Par 4 
   public ArrayList par_4=new ArrayList();
   
   // Versions
   ArrayList ver=new ArrayList();
   
    
   public CSyntaxGen(String ins, long params)
   {
      // Instruction
      this.ins=ins;
      
      // Params
      this.params=params;
   }
   
   public void generate()
   {
       if (this.params==2)
       {
           for (int a=0; a<=this.par_1.size()-1; a++)
           {
               for (int b=0; b<=this.par_2.size()-1; b++)
               {
                   String v=this.ins+", "+this.par_1.get(a)+", ID_COMMA, "+this.par_2.get(b);
                  
                   if (!exist(v)) 
                   {
                       this.ver.add(v);
                       System.out.println(v);
                   }
               }
           }
       }
       
       if (this.params==3)
       {
           for (int a=0; a<=this.par_1.size()-1; a++)
           {
               for (int b=0; b<=this.par_2.size()-1; b++)
               {
                   for (int c=0; c<=this.par_3.size()-1; c++)
                   {
                          String v=this.ins+", "+this.par_1.get(a)+", ID_COMMA, "+this.par_2.get(b)+", ID_COMMA, "+this.par_3.get(c);
                  
                           if (!exist(v)) 
                           {
                              this.ver.add(v);
                              System.out.println(v);
                           }
                       
                   }
               }
           }
       }
       
       if (this.params==4)
       {
           for (int a=0; a<=this.par_1.size()-1; a++)
           {
               for (int b=0; b<=this.par_2.size()-1; b++)
               {
                   for (int c=0; c<=this.par_3.size()-1; c++)
                   {
                       for (int d=0; d<=this.par_4.size()-1; d++)
                       {
                           String v=this.ins+", "
                                    +this.par_1.get(a)+", ID_COMMA, "
                                    +this.par_2.get(b)+", ID_COMMA, "
                                    +this.par_3.get(c)+", ID_COMMA, "
                                    +this.par_4.get(d);
                  
                           if (!exist(v)) 
                           {
                              this.ver.add(v);
                              System.out.println(v);
                           }
                       }
                   }
               }
           }
       }
       
   }
   
   public boolean exist(String v)
   {
       for (int a=0; a<=this.ver.size()-1; a++)
           if (this.ver.get(a).equals(v))
               return true;
       
       return false;
   }
}
