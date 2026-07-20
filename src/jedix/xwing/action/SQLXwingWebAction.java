/*
 * This software was developed and owned by Inticube
 * Illegal use of this software will violate the Copy Right Law
 * ******************************************************
 * Program Name : @(#)SQLXWingWebAction.java
 * Function description : �������� �̿��� ����Ʈ WebAction Ŭ����
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

import com.locus.jedi.service.sql.*;
import com.locus.jedi.waf.action.*;
import com.locus.jedi.biz.*;
import com.locus.jedi.waf.controller.*;

import kr.co.kico.wfm.encmapper.process.DecrypProcess;
import kr.co.kico.wfm.encmapper.process.EncProcess;

import com.locus.jedi.transfer.*;

public class SQLXwingWebAction extends XwingWebAction
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void perform(JediRequest req, JediResponse res)
		throws WebActionException{
		try{
			String sqlName = req.param.getString("_sqlName", "");
            StringTokenizer tokens = new StringTokenizer(sqlName, ",");

            int count = tokens.countTokens();
            String sqlNames[] = new String[count];
            for(int i = 0; i < count; i++){

            	sqlNames[i] = tokens.nextToken().trim();
            }
            
            SQLParam sqlparam[] = new SQLParam[count];
            
            //교보생명 암호화 추가
            EncProcess encProcess = new EncProcess();
            
            for(int i = 0; i < count; i++){
            	
                sqlparam[i] = new SQLParam(req);
                //파라미터암호화과정
                sqlparam[i].addParam(encProcess.encParam(sqlNames[i], req.param));
                sqlparam[i].setSqlName(sqlNames[i]);
            }

            sqlparam = (SQLParam[])BizDelegate.getInstance().execute("sqlService", req.getCommonDTO(), sqlparam);

            //교보생명 복호화 추가
            DecrypProcess decrypProcess = new DecrypProcess();
            
            for(int i = 0; i < count; i++){
            	//파라미터복호화과정
            	res.param.addValue(sqlNames[i], decrypProcess.decrypSQLParam(sqlNames[i], sqlparam[i]));
            }

		}catch(BizAppException e){
			e.printStackTrace();
			throw new WebActionException(e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebActionException(e.getMessage(),e);
		}
	}
};
