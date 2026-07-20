/*************************************************************
 * This software was developed and owned by YounTaeHee
 * Illegal use of this software will violate the Copy Right Law
 * ************************************************************
 * Program Name : @(#)XCronHelper.java
 * Function description : 
 * 
 * (중요) 메소드 추가시 주의사항 
 * 
 * 1. 메소드 Input argument 는 반드시 String 객체로만 정의할 것.
 *    따라서, 메소드 호출시 Parameter 의 전/후에 \" 을 붙여서는 안됨.
 *    
 *    예제 : ConvertCryptoMD5String(?SOCAIL_ID)
 *    
 * 2. 메소드 호출 방식
 *    
 *    예제 : <ExprVariable Name="SOCAIL_ID" Expr="ConvertCryptoMD5String(?SOCAIL_ID)"/>
 *    예제 : \@ConvertCryptoMD5String(?SOCAIL_ID) 
 *       
 * 3. Primitive Type 변환시 주의
 *    객체생성이 자주 발생하지 않도록 다음과 같이 static method 활용
 *    
 *    예제 : int time = Long.parseLong(time);
 *      
 * Programmer Name : YounTaeHee (taehee.youn@gmail.com)
 * Creation Date : 2006. 3. 07.
 * ************************************************************
 *                P R O G R A M H I S T O R Y
 * ************************************************************
 * DATE          :     PROGRAMMER    :          REASON
 *
 *
 *
 */

package com.inticube.xcron.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import com.inticube.xcron.utils.StringUtil;

public class XCronHelper {
	public String ConvertTime(String time) throws NumberFormatException {
		if(time == null || time.length() == 0) 
			return "";
		
		long ltime = Long.parseLong(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		
		return sdf.format(new Date(ltime - 9*3600000));
	}

	public String ConvertCryptoMD5String(String plainText) throws NoSuchAlgorithmException {
		if (StringUtil.isEmpty(plainText))
			return "";
		
		byte[] ret = MessageDigest.getInstance("MD5").digest(plainText.getBytes());
		return new String(Base64.encodeBase64(ret));
	}

	public String ConvertHtmlBR(String src) {
		return StringUtil.getReplaceCRString(src, "<BR>");
	}
}
