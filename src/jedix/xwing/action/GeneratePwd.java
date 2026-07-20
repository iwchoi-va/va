package jedix.xwing.action;

import java.util.*; 

public class GeneratePwd {

		public String Generate(){
			char[] PwdElement = new char[36];
			
			for(int i=0; i<10; i++){
				
		
				int j=i+48;
				PwdElement[i] = (char)j;
				System.out.println(PwdElement[i]);
			

			}
			
			for(int i =10; i<36; i++){
			
				int j=i+55;
				PwdElement[i] = (char)j;
				System.out.println(PwdElement[i]);
			
			
			}


			String NewPassword="";
			
			for(int i =0; i<6; i++){
			
			//System.out.println(randomRange(0, 35));
			
			int EachNum = randomRange(0, 35);
			
			//System.out.println(EachNum);
			
			NewPassword += PwdElement[EachNum];
		
			}		
			
			System.out.println("NewPWD:"+NewPassword);
			
			return NewPassword;			
		}
		
		
		
		public int randomRange(int n1, int n2) {
		return (int) (Math.random() * (n2 - n1 + 1)) + n1;
		}
		
		
}
