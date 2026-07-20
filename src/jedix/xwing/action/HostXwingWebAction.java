/*
 * This software was developed and owned by Inticube
 * Illegal use of this software will violate the Copy Right Law
 * ******************************************************
 * Program Name : @(#)HostXWingWebAction.java
 * Function description : ȣ��Ʈ ���� �̿��� ����Ʈ WebAction Ŭ����
 * Programmer Name : sewoo Yi[rainer@inticube.com]
 * Creation Date : 2007.09.09
 * ******************************************************
 *                    P R O G R A M H I S T O R Y
 * *******************************************************
 * DATE			:	PRGMMER		: REASON
 * ------------------------------------------------
 *
 */
package jedix.xwing.action;
 
import java.util.*;

import com.locus.jedi.service.host.*;
import com.locus.jedi.waf.action.*;
import com.locus.jedi.biz.*;
import com.locus.jedi.waf.controller.*;

/**
 * ȣ��Ʈ ���񽺸� ȣ���Ҷ� ���Ǵ� ����Ʈ WebAction
 *
 */
public class HostXwingWebAction extends XwingWebAction
{
	public void perform(JediRequest req, JediResponse res)
		throws WebActionException{
		try{
			String serviceName = req.param.getString("_serviceName");
            if(serviceName == null)
                serviceName = "";
            StringTokenizer tokens = new StringTokenizer(serviceName, ",");
            int count = tokens.countTokens();
            String serviceNames[] = new String[count];
            for(int i = 0; i < count; i++)
                serviceNames[i] = tokens.nextToken().trim();

            HostParam hostparam[] = new HostParam[count];
            for(int i = 0; i < count; i++)
            {
                hostparam[i] = new HostParam(req);
                hostparam[i].addParam(req.param);
                hostparam[i].setServiceName(serviceNames[i]);
            }

            hostparam = (HostParam[])BizDelegate.getInstance().execute("hostService", req.getCommonDTO(), hostparam);
            
            for(int i = 0; i < count; i++) {
            }
            
			 for(int i = 0; i < count; i++){
				String code = hostparam[i].getResultCode();
				String msg = hostparam[i].getResultMessage();
				hostparam[i].remove("_resultCode");
				hostparam[i].remove("_resultMessage");
				res.setResultCode(code);
				res.setResultMessage(msg);
                res.param.addParam(hostparam[i]);
                res.param.addParam(req.param);
				
			}
			     
		}catch(BizAppException e){
			e.printStackTrace();
			throw new WebActionException("HostServiceWebAction : "+e.toString(),e);
		}
	}
};
