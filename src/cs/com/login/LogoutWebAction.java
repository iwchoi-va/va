package cs.com.login;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.waf.BaseEntity;
import com.locus.jedi.waf.SessionManager;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class LogoutWebAction extends XwingWebAction
{
	public void perform(JediRequest req, JediResponse res)
	throws WebActionException
	{
		HttpServletRequest request = req.getHttpServletRequest();
		try{
			BaseEntity entity = SessionManager.getInstance().getBaseEntity(request);
			if(entity!=null){
				LogoutCommand logoutCmd = new LogoutCommand();
				logoutCmd.execute(entity);
				SessionManager.logout(request);
			}
		}catch(Exception e){
			throw new WebActionException("logout failed","로그아웃처리에 실패하였습니다",e);
		}	
		
	}
};

