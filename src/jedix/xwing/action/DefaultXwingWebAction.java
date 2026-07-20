/*
 * This software was developed and owned by HansolInticube
 * Illegal use of this software will violate the Copy Right Law
 * ******************************************************
 * Program Name : @(#)DefaultXWingWebAction.java
 * Function description :����Ʈ WebAction Ŭ����
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

import com.locus.jedi.biz.BizAppException;
import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import com.locus.jedi.waf.controller.RequestSpec;

public class DefaultXwingWebAction extends XwingWebAction
{
	public void perform(JediRequest request, JediResponse response)
	throws WebActionException
	{
		try{
			RequestSpec spec = request.getRequestSpec();
			String bizRequest = request.param.getString("_bizRequest");
			if(bizRequest == null || "".equals(bizRequest)) 
				bizRequest = spec.getBizRequest();

			if(bizRequest != null && !"".equals(bizRequest)){
				Param param = (Param)BizDelegate.getInstance().execute(bizRequest,request.getCommonDTO(),request.param);
				response.param.addParam(param);
			}else{
				response.param.addParam(request.param);
			}

		}catch(BizAppException e){
			e.printStackTrace();
			throw new WebActionException(e.getCode(),e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
			throw new WebActionException(e.getMessage(),e);
		}
	}
};

