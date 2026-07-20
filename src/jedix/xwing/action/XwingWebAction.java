/*
 * This software was developed and owned by HansolInticube
 * Illegal use of this software will violate the Copy Right Law
 * ******************************************************
 * Program Name : @(#)XWingWebAction.java
 * Function description : �ֻ��� Xwing WebAction Ŭ����
 * Programmer Name : sewoo Yi[rainer@inticube.com]
 * Creation Date : 2009.06.24
 * ******************************************************
 *                    P R O G R A M H I S T O R Y
 * *******************************************************
 * DATE			:	PRGMMER		: REASON
 * ------------------------------------------------
 *
 */


package jedix.xwing.action; 

import com.locus.jedi.waf.*;
import com.locus.jedi.waf.action.*;
import com.locus.jedi.waf.controller.*;
import com.locus.jedi.log.*;
import jedix.xwing.util.XwingProcessor;

import javax.servlet.http.*;
 
public class XwingWebAction extends DefaultWebAction {
	public void doStart(HttpServletRequest request){
		try{
			ErrorLogger.debug("Xwing HTTP Request Parsing..");
			JediRequest jediReq = (JediRequest)request.getAttribute(WebKeys.JEDI_REQUEST);
			XwingProcessor.parseRequest(jediReq,jediReq.param);
			ErrorLogger.debug("Xwing HTTP Request Pars Completed.");
		}catch(Exception e){
			e.printStackTrace();
			ErrorLogger.error(e);
		}
	}
	
	/*
	public void doEnd(HttpServletRequest request,Object result){
		try{
			ErrorLogger.debug("result ��� ����");
			JediResponse jediRes = (JediResponse)request.getAttribute(WebKeys.JEDI_RESPONSE);
			MiProcessor.sendResponse(jediRes,jediRes.param);
			ErrorLogger.debug("result ��� ��");
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
};

