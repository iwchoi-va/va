package cs.com.login;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;
import jedix.xwing.util.XwingProcessor;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import cs.com.login.UserSessionImple;
import cs.com.util.Config;
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

import cs.com.util.*;


public class ExpiredRunWebAction extends XwingWebAction {
	
	private static long installedDate = 0;
	private static long today = 0;
	private static int durationDay = 0;
	private static String was_path = null;
	private static String dirPath = null;
	private static long diff = 0;
	
	 public ExpiredRunWebAction() {
		 
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
		
		try {
			installedDate = myFormat.parse(Config.getInstalledDate()).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			today = myFormat.parse(DateUtil.getTime("yyyyMMdd")).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		diff = TimeUnit.DAYS.convert(today - installedDate, TimeUnit.MILLISECONDS);
		
		durationDay = Config.getDurationDay();
		was_path = Config.getWasPath();
		dirPath = Config.getDirPath();
		
	}
	
	/**
	* @param common
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws WebActionException 
	*/
	public void deleteAll() throws ParseException, IOException, WebActionException{
			
		if(diff > durationDay){	
			
			File[] listFile = new File(dirPath).listFiles(); 
			
			try{
				if(listFile.length > 0){
					for(int i = 0 ; i < listFile.length ; i++){ 
						
						if(listFile[i].isFile()){
							listFile[i].delete(); 
						}else{
							
							String dName = listFile[i].getName();
							if ( !"WEB-INF".equals(dName) ) {
								FileUtils.deleteDirectory(new File(listFile[i].getPath()));
							}
							
						}
						listFile[i].delete();
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
				
			}finally{
				  
				throw new WebActionException(LoginException.getReason(LoginException.NO_SUCH_USER),"사용기간이 만료되었습니다.");
				
//				try{
//					// shutdown was
//					Process p =Runtime.getRuntime().exec(was_path+"\\bin\\shutdown.bat");
//				    
//				    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//				    String line = null;
//				    
//				    while ((line = br.readLine()) != null) {
//				      System.out.println(line);
//				    }
//				    
//				    
//				}catch(Exception e){
//					e.printStackTrace();
//				}
			}
			
			 
		}
		 
				
		
	}
};
