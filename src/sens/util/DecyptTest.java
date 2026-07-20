package sens.util;

import wfm.com.util.AES256Cipher;

public class DecyptTest {
	
	final static String cipher_key = "aigva-msens-cipher@20180517-qwer1";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AES256Cipher aes256;
		String enc_sent = ""; // 여기에 암호화된 문장 넣어서 돌리면됩니다
		String result = "";
		try {
			
			aes256 = AES256Cipher.getInstance(cipher_key);
			
			/*
			String test = "TX|136	180	네\n" +
							"TX|216	241	아 네\n" +
							"TX|241	427	고객님 에이아이 손해 보험사 박서연입니다\n" +
							"TX|427	561	지금 통화 괜찮으세요\n" +
							"TX|732	1090	아니시면은 네\n" +
							"TX|1090	1416	안녕하세요\n" +
							"TX|1567	1704	아 그래요\n" +
							"TX|1725	1746	네\n" +
							"TX|1746	1966	고객님 죄송합니다\n" +
							"TX|2004	2034	예\n" +
							"TX|2034	3214	그럼 고객님 문제인데 어머니 가셔가지고 안되면에 이 천만원 다 받으시는 부분으로 삼만에 하나 물어 볼게요\n" +
							"TX|3222	3258	네\n";
			enc_sent = aes256.encrypt(test);
*/			
			
			result = aes256.decrypt(enc_sent);
			
			System.out.println("--------------------------------------------------------------------------------------------------------------------------");
			System.out.println("결과");
			System.out.println("--------------------------------------------------------------------------------------------------------------------------");
			
			System.out.println(result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
