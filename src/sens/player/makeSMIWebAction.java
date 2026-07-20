package sens.player;
 
import java.io.File;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;
import jedix.xwing.util.XwingProcessor;

import org.json.JSONObject;

import cs.com.login.UserSessionImple;
import cs.com.util.SecurityUtil;
import cs.com.util.SeedX;

import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.DateUtil;
import com.locus.jedi.waf.BaseEntity;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.LoginException;
import com.locus.jedi.waf.SessionManager;
import com.locus.jedi.waf.WebKeys;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import com.locus.jedi.waf.controller.WebInitializer;

public class makeSMIWebAction extends XwingWebAction {
	
    public static final String DEFAULT_DIR = "temp";
    		
	public void perform(JediRequest req, JediResponse res) 
		throws WebActionException {
		
		HttpServletRequest request = req.getHttpServletRequest();
		String file_id = req.param.getString("file_id");

		CommonDTO common = new CommonDTO(file_id);

		try{		
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("getContent.sel");
			sqlparam.setResultName("content");
			sqlparam.addValue("file_id", file_id);
			sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);

			
			Param aContent = sqlparam.getListParam("content").getParam(0);
			String content = aContent.getString( "CONTENT"		, "" );
			String file_name = file_id.substring(0, file_id.indexOf('.'));
			
			String dir_path = request.getServletContext().getRealPath("/");
			ErrorLogger.debug("#####[makeSMIWebAction] dir_path : " + dir_path + "#####");
			
			File dir = new File(dir_path, DEFAULT_DIR);
	        // 폴더가 없으면 생성      
	        if(!dir.exists()){
	           dir.mkdir();
	        }
	        
	        
			file_name = dir_path + DEFAULT_DIR + File.separator + file_name;
			ErrorLogger.debug("#####[makeSMIWebAction] file_path : " + file_name + "#####");

			writeSMI rs = new writeSMI();
			rs.fielWrite(dir_path + DEFAULT_DIR, file_name, content);
			
		} catch(Exception e){
			throw new WebActionException("fail: read Content","SMI Content 읽기에 실패 하였습니다",e);
		}
	}
};
