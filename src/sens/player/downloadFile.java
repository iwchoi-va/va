package sens.player;

import ie.corballis.sox.SoXEffect;
import ie.corballis.sox.SoXEncoding;
import ie.corballis.sox.Sox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;
import wfm.com.util.AES256Cipher;

import com.hansol.audio.util.PropertyUtil;
import com.initech.core.util.URLDecoder;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class downloadFile extends XwingWebAction {
	
	/***************************************
	 * - AIG 스테레오-> 모노 변환 작업 반영시 반영 목록(2018.09.05)
		downloadFile.java
		sql_rm.xml
		sftpServer.properties
	 ***************************************/
	
	//final String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/sftpServer.properties";
	final String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/sftpServer.properties"; //war 파일 압축으로 path 수정됨
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		
		IVRLogger.error("########downloadFile########");
		
		HttpServletRequest request = req.getHttpServletRequest();
		//String serverIp = "102.90.1.177:8090"; //req.param.getString("serverIp");
		String contactId = req.param.getString("contactId"); 
		String file_path = req.param.getString("file_path","");
		String batch_yn = req.param.getString("batch_yn","");
		String rxtx_gb = "N";
		String aes_key = "";
		Boolean getFilePathFromDb = false;
		//String startTime = req.param.getString("startTime");
		
		//테스트용
		//file_path = "/tov/SMC/2018050301/00003083681525311185.wav";
		//ErrorLogger.debug("########file_path - " + file_path);
		
		if(file_path == null) file_path = "";
		
		try{	
			
			if(!batch_yn.equals("Y")){ 
				// tm에서 팝업으로 호출시 우리 merge에 없을 수 있기 때문에 파일패스를 녹취 DB를 보게 했다.(파일청취라도 가능하도록)
				//화자분리 안된 스폰서 센터 조회하기(화자구분이 원래부터 되었던 경우는 등록X)
				Code[] code = CodeUtil.getCodes("SYS120");
				
				//화자구분 여부 조회해오는 쿼리 호출하기
				SQLParam sqlparam = new SQLParam();
				sqlparam.setSqlName("rec.player.getRXTXinfo.sel");
				sqlparam.addValue("ucid", contactId);
				SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlparam);
				
				if(sqlResult.getCount() > 0){
					Param result = sqlResult.getListParam("DS_RXTX_RES").getParam(0);
					
					
					
					if("".equals(file_path)){ 
						file_path = result.getString("FILE_PATH", "");
						getFilePathFromDb = true;
					}
					//file_path = file_path.substring(file_path.indexOf("/")+1);
					
					//ErrorLogger.debug("###file_path = " + file_path);
					boolean rxtx_flag = true;
					String sp_code = result.getString("SP_CODE", "");
					String stt_r_time = result.getString("STT_R_TIME", "");
					
					for(int l = 0; code != null && l < code.length; l++){
						if (!"Y".equalsIgnoreCase(code[l].getUseYn())) {
							continue;
						}
						
						if(sp_code.equals(code[l].getEtc1())){
							ErrorLogger.debug("stt_r_time === "+stt_r_time);
							//스폰서 센터의 화자분리가 10/15일 이후 부터 됐기때문에
							if(Integer.parseInt(stt_r_time) < Integer.parseInt(code[l].getEtc3())){ 
								rxtx_flag = false;
								break;
							}else{
								if("Y".equals(code[l].getEtc2())){ 
									rxtx_flag = true;
									break;
								}else{
									rxtx_flag = false;
									break;
								}
								
							}
						}
					}
					
					if(!rxtx_flag) rxtx_gb = "N";
					else rxtx_gb = "Y";
					//rxtx_gb = result.getString("RXTX_GB", "N");
					//IVRLogger.debug("###########rxtx 결과 = " +"r_time= "+stt_r_time + " flag = "+rxtx_flag +" ##### gb : "+rxtx_gb);
				}
			}else{
				
				SQLParam sqlparam = new SQLParam();//RXTX_GB
				sqlparam.setSqlName("rec.player.getRXTXGB.sel");
				sqlparam.addValue("ucid", contactId);
				SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlparam);
				
				if(sqlResult.getCount() > 0){
					Param result = sqlResult.getListParam("DS_RXTX_RES").getParam(0);
					rxtx_gb = result.getString("RXTX_GB"); 	
				}
				
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
			ErrorLogger.error("### downloadFile : rxtx_yn 정보 조회 과정 중 에러 발생");
			IVRLogger.info("### downloadFile : rxtx_yn 정보 조회 과정 중 에러 발생");
		}
		
		Properties prop = new Properties();
        FileInputStream fis;
        
		try {
			fis = new FileInputStream(PROP_DIR);
			prop.load(new java.io.BufferedInputStream(fis));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Properties prop2 = new Properties();
		
		//prop2 = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/webapps/WEB-INF/conf.properties");
		prop2 = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/WEB-INF/conf.properties");
		aes_key = prop2.getProperty("cipher_key");
		
		//로컬에선 임시주석(서버 반영시에는 주석 풀어야됨)
		try {
			int flag = -1; //sftp 파일 다운로드 flag
			if(!getFilePathFromDb) file_path = URLDecoder.decode(file_path,"UTF-8");
			String fileName = file_path.substring(file_path.lastIndexOf("/")+1);
			String remoteFilePath = file_path.substring(0, file_path.lastIndexOf("/"));
			
			remoteFilePath = remoteFilePath.substring(3, remoteFilePath.length()); // 앞에 E:/을 빼고 보낸다
			
//			IVRLogger.error(fileName + " /////   " + remoteFilePath);
			//String fileName = contactId+".wav";
			//fileName = "303420180208103512.wav"; //parameter로 가져와
			String local_input_path = prop.getProperty("local_path");
			
			local_input_path = cleanString(local_input_path); //input에다가 떨어뜨려준다
			File file = new File(local_input_path+fileName);

			/*녹취 파일 다운로드 상태값 정의
			 * -1 : 소스상의 오류
			 * 0 : 다운로드 완료
			 * 1 : 녹취파일 서버에서 해당 파일이 존재하지 않음
			 * 2 : 파일 sftp 다운로드 과정 중의 에러 발생*/
			 
			if(file.exists()){ //현재 디렉토리에 파일이 존재하면 바로 읽을 수 있도록 처리
				flag = 0;
			}else{
				
				Code[] code = CodeUtil.getCodes("SYS022"); //VA서버도메인정보
				String serverIp = InetAddress.getLocalHost().getHostAddress(); 
				String []  temp = null;
				String serverType = "";
				
				String host = prop.getProperty("sftp_host");
				
				for (int j = 0; code != null && j < code.length; j++) {
					if (!"Y".equalsIgnoreCase(code[j].getUseYn())) {
						continue;
					}
				
					if(serverIp.equals(code[j].getEtc1())){ 
						temp = code[j].getEtc2().split("\\.");
						break;
					}
				}
				if(temp != null && temp.length > 0){
					serverType = temp[0];
				}
				
				switch (serverType) {
				case "va":
					host = prop.getProperty("sftp_host");
					break;
				case "vadev":
				case "vauat":
					host = prop.getProperty("sftp_host_dev");
					break;
				default:
					host = prop.getProperty("sftp_host_dev");
					break;
				}
				

				IVRLogger.info("##serverIp == " + serverIp + "// servertype = " + serverType + "// host = " + host);	
					
				
				int port = Integer.parseInt(prop.getProperty("sftp_port"));
				
				AES256Cipher aes256 = AES256Cipher.getInstance(aes_key);
				//Aes256 aes256 = new Aes256();
				
				String userName = prop.getProperty("sftp_user");
				String password = prop.getProperty("sftp_password");
				//password = URLEncoder.encode(password,"UTF-8");
				
				
				//IVRLogger.debug("########ivrlogger ----  "+userName + " // " +  password);
				//IVRLogger.debug(aes256.decrypt(userName));
				
				userName = aes256.decrypt(userName); //암호화된 username 복호화함
				password = aes256.decrypt(password); //암호화된 password 복호화함
							
				//IVRLogger.debug("########username = "+userName + "//" + password);
				String dir = remoteFilePath;
				SFTPUtil util = new SFTPUtil();
				
				util.init(host, userName, password, port);
				
				//보안 취약점 적용으로 인한 수정 19-12-17
				if("Y".equals(rxtx_gb)) local_input_path = cleanString(prop.getProperty("local_input_path")); //화자구분이 있는 경우는 sftp를 통해 받은 결과를 다른 저장소에 저장하는 걸로 변경함
				
				String saveDir=local_input_path+fileName;
				
				flag = util.download(dir, fileName, saveDir);
				
				util.disconnection();
				
				
				IVRLogger.info("파일 다운로드################"+dir+"////"+saveDir);
				
				
				if(flag == 0 && "Y".equals(rxtx_gb)){ //화자구분의 경우 스테레오를 모노로 변경하는 작업 추가
					String sox_dir = prop.getProperty("sox_dir");
					String local_output_path = cleanString(prop.getProperty("local_output_path"));
					
					local_output_path = local_output_path + fileName;
					
					//IVRLogger.info("local_path = " + saveDir);
					//IVRLogger.info("output path = " + local_output_path);
					
					try{
						Sox sox = new Sox(sox_dir);
						
						sox.sampleRate(8000)
						   .inputFile(saveDir) //SFTP를 통해 내려 받은 파일
						   .encoding(SoXEncoding.SIGNED_INTEGER)
						   .bits(16)
						   .outputFile(local_output_path) // 스테레오 -> 모노 변환된 파일
						   .effect(SoXEffect.REMIX, "1,2") // 스테레오 -> 모노 변환 작업
						   .execute();
						
						flag = 0;
						
						//파일 변환이 완료되면 input_file에 있는 파일 삭제하기
						File cp_input_file = new File(saveDir); //SFTP를 통해 내려 받은 파일
						File cp_output_file = new File(local_output_path); // 스테레오 -> 모노 변환된 파일
						
						if(cp_output_file.exists()){
							if(cp_input_file.exists()){
								cp_input_file.delete();
							}
						}
						
					}catch(Exception e){
						e.printStackTrace();
						IVRLogger.info("streo -> mono convert Exception :: " + e.getMessage());
						ErrorLogger.error("streo -> mono convert Exception :: " + e.getMessage());
						flag = 3;
					}

				}
				
			}
			//System.exit(0);
			IVRLogger.error("Error 사유 = "+ flag);
			
			if(flag == 0){
				res.param.addValue( "result", "success" );
				res.param.addValue( "reason", "" );
				res.param.addValue("file_path", file_path);
			}else if(flag == -1){
				res.param.addValue( "result", "failed" );
				res.param.addValue( "reason", "소스 내 오류 발생" );
			}else if(flag == 1){
				res.param.addValue( "result", "failed" );
				res.param.addValue( "reason", "해당 파일이 존재하지 않음" );
			}else if(flag == 2){
				res.param.addValue( "result", "failed" );
				res.param.addValue( "reason", "파일 다운로드 과정에서 오류 발생" );
			}else if(flag == 3){
				res.param.addValue( "result", "failed" );
				res.param.addValue( "reason", "스테레오 -> 모노 변환 과정에서 오류 발생" );
			}
			
			//----------다운로드 연동후 해당 부분 주석-----------------
			//res.param.addValue( "result", "success" );
			//res.param.addValue( "reason", "" );
			//---------------------------------------------------------
			
			//res.param.addValue( "wavedata", WaveData.extractData(TEMP_FILE_DIR+contactId+".wav") );
		    
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			IVRLogger.error("Download File Error 사유"+e1.getMessage());
			ErrorLogger.error("Download File Error 사유"+e1.getMessage());
		}
	}
	
	public static String cleanString(String aString) { 
	    if (aString == null) { return null; } 
	    String cleanString = ""; 
	    for (int i = 0; i < aString.length(); ++i) { 
	         cleanString += cleanChar(aString.charAt(i)); } 
	    return cleanString; } 

	private static char cleanChar(char aChar) { 
	  // 0 - 9 
		for (int i = 48; i < 58; ++i) { if (aChar == i){ return (char) i; } } 
	  // 'A' - 'Z' 
		for (int i = 65; i < 91; ++i) { if (aChar == i){ return (char) i; } } 
	  // 'a' - 'z' 
		for (int i = 97; i < 123; ++i) { if (aChar == i){ return (char) i; } } 
	  // other valid characters 
		return getSpecialLetter(aChar); 
	} 

	public static char getSpecialLetter(char aChar){ 
	  switch (aChar) { case '/': return '/'; case '.': return '.'; case '-': return '-'; case '_': return '_'; case ' ': return ' '; 
	                       case ':': return ':'; case '&': return '&'; default: return '%'; }
	}
}
