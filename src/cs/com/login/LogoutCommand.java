package cs.com.login;

import com.locus.jedi.service.sql.*;
import com.locus.jedi.waf.BaseEntity;
import com.locus.jedi.waf.LogOutAction;
import com.locus.jedi.biz.*;

public class LogoutCommand implements LogOutAction {
    public void execute(BaseEntity entity) {
    	
		try{
			SQLParam sqlparam = new SQLParam();
			
			sqlparam.clear();
			sqlparam.setSqlName("cs.com.logout_insertlog");			
			sqlparam.addValue("user_id", entity.getUserId());			
			sqlparam.addValue("was_ip", entity.getCommonDTO().getAttribute("was_ip"));
			sqlparam.addValue("login_yn", "N");
			
			BizDelegate.getInstance().execute("sqlService", entity.getCommonDTO(), sqlparam);
			
			// wf_usermaster update...
			// USERMSTAER에 LOGOUT TIME 없어짐
			/*sqlparam.clear();
			sqlparam.setSqlName("cs.com.logout.logoutchk");			
			sqlparam.addValue("user_id", entity.getUserId());			
			sqlparam.addValue("was_ip", entity.getCommonDTO().getAttribute("was_ip"));
			sqlparam.addValue("login_yn", "N");
			
			
			BizDelegate.getInstance().execute("sqlService", entity.getCommonDTO(), sqlparam);*/

		}catch(BizAppException e){
			e.printStackTrace();
		}
		
	}
}