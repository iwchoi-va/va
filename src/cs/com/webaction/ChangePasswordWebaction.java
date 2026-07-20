package cs.com.webaction;


import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.*;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.LoginException;
import com.locus.jedi.waf.action.*;
import com.locus.jedi.biz.*;
import com.locus.jedi.waf.controller.*;

import cs.com.util.SecurityUtil;

public class ChangePasswordWebaction extends  XwingWebAction {
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try{
			String user_id = req.param.getString("userid");
			String pw = req.param.getString("password");
			String regid = req.param.getString("regid");
			String changeCd = req.param.getString("change_cd");	// I : 초기화, C : 변경
			String current_pw = req.param.getString("c_password");
			
//			ErrorLogger.debug("################current_pw " + current_pw);
			
			SQLParam sqlparam = new SQLParam();
			
			//pw =  SecurityUtil.getCryptoMD5String((String)pw);

			// 비밀번호를 변경하는 경우 현재 비밀번호에 대한 동일 여부,  
			// 3회 이내 재사용 여부를 체크한다.
			if("C".equals(changeCd)) {
//				ErrorLogger.debug("################changeCd : C");
				
				sqlparam.clear();
				sqlparam.setSqlName("cs.com.com110p.pw.list.sel");
				sqlparam.addValue("userid", user_id);				
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService", req.getCommonDTO(), sqlparam);
				
				if(sqlparam.getCount() <= 0) {
					throw new WebActionException(LoginException.getReason(
							LoginException.NO_SUCH_USER), 
							"등록되지 않은 사용자입니다"
						);					
				}
				
				ListParam pwList = sqlparam.getListParam("passwordList");
				
				String recent0 = (String)pwList.getValue(0, "PASSWORD");
				String recent1 = (String)pwList.getValue(0, "PWD_RECENT1");
				String recent2 = (String)pwList.getValue(0, "PWD_RECENT2");
				
//				ErrorLogger.debug("####################ay PASSWORD : " + recent0);

				String password = (String)pw;
				String currentPassword = (String)current_pw; //SecurityUtil.getCryptoMD5String((String)current_pw);				
				//String tempPassword = "9999"; //SecurityUtil.getCryptoMD5String("9999");				
					
				if(! currentPassword.equals(recent0)) {
					res.setResultCode("CURPASS_ERROR");
					res.setResultMessage("현재 비밀번호 오류입니다");
					return;
				} else if(password.equals(recent0) || password.equals(recent1) || password.equals(recent2)) {
					res.setResultCode("PASSWORD_CHANGE_ERROR");
					res.setResultMessage("현재 비밀번호 포함 3번 이내 같은 암호는 사용 불가합니다");
					return;
				}
		
			}

			// 비밀번호 변경 처리
			sqlparam.clear();
			sqlparam.setSqlName("cs.com.com110p.pw.upd");
			sqlparam.addValue("userid", user_id);
			sqlparam.addValue("password", pw);
			BizDelegate.getInstance().execute("sqlService", req.getCommonDTO(), sqlparam);

			/* 비밀번호 변경 내역 저장
			sqlparam.clear();
			sqlparam.setSqlName("wfm.com.password_sethistory");
			sqlparam.addValue("passwordList", passwordList);
			BizDelegate.getInstance().execute("sqlService", req.getCommonDTO(), sqlparam);
			*/
		} catch(Exception e) {
			e.printStackTrace();
			throw new WebActionException("PasswordChangeWebaction : "+e.getMessage(),e);
		}
	}
}