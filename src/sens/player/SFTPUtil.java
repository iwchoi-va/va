package sens.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

import com.hansol.audio.util.PropertyUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.locus.jedi.log.IVRLogger;



/**
 * 서버와 연결하여 파일을 업로드하고, 다운로드한다.
 */

public class SFTPUtil{

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    /**
     * 서버와 연결에 필요한 값들을 가져와 초기화 시킴
     *
     * @param host
     *            서버 주소
     * @param userName
     *            접속에 사용될 아이디
     * @param password
     *            비밀번호
     * @param port
     *            포트번호
     *            
     * cipher 256 필요하면 java_home/jre/security/보안용 jar파일 업데이트 치고 테스트 해야됨
     */

    public void init(String host, String userName, String password, int port) {

        JSch jsch = new JSch();

        try {
            session = jsch.getSession(userName, host, port);
            session.setPassword(password);
            
            IVRLogger.debug(userName + "//" + password + "// " + host + "// " + port);

            java.util.Properties config = new java.util.Properties();

            config.put("StrictHostKeyChecking", "no");
            
            
            IVRLogger.debug("######SFTP 테스트입니다..");
            
            config.put("PreferredAuthentications", "password");
            //config.put("cipher.c2s",
            //        "aes128-cbc,aes192-cbc, aes256-cbc, aes128-ctr, aes192-ctr, aes256-ctr, 3des-cbc, seed-cbc@ssh.com, crypticore1282ssh.com,blowfish-cbc");
           // config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
           // config.put("kex", "diffie-hellman-group14-sha1");
            //config.put("cipher",
            //        "aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
            config.put("kex", "diffie-hellman-group14-sha1"); // 만약에이거 없어도 되면 제거해도됨
            config.put("cipher.c2s",   "aes256-ctr"); // 만약에 이거 없어도 되면 제거해도됨
            session.setConfig(config);
            IVRLogger.debug(session.getConfig("kex"));
            IVRLogger.debug(session.getConfig("cipher.c2s"));
            //IVRLogger.debug(session.get);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();

        } catch (JSchException e) {
        	IVRLogger.error("##########connection error : "+e.getMessage());
            e.printStackTrace();
        }

        channelSftp = (ChannelSftp) channel;
    }


    /**
     * 하나의 파일을 다운로드 한다.
     * @param dir
     *            저장할 경로(서버)
     * @param downloadFileName
     *            다운로드할 파일
     * @param path
     *            저장될 공간
     * @throws IOException 
     */
    public int download(String dir, String downloadFileName, String path){

        InputStream in = null;
        FileOutputStream out = null;
        byte[] buffer = new byte[32768];
        File file = null;

        try {
        	
            channelSftp.cd(dir);
            channelSftp.setFilenameEncoding("EUC-KR");
            in = channelSftp.get(downloadFileName);
        } catch (SftpException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        	return 1;
		}

        try {
        	file = new File(path);
 
        	out = new FileOutputStream(file);
            int i;
            while ((i = in.read(buffer)) != -1) { //속도향상을 위한 방법
                   out.write(buffer,0,i);
             }

            /*while ((i = in.read()) != -1) {
                out.write(i);
            }*/
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	IVRLogger.error("##########download error : "+e.getMessage());
            e.printStackTrace();
            return 2;
          
        } finally {
            try {
            	
            	out.close();
                in.close();
                IVRLogger.debug("Download Complete!");

                return 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return -1;
    }
    
    public void upload(File src, String path, String serverType)throws Exception{
    	FileInputStream fis = null;
    	try{
    		
    		//Properties prop = new Properties();
    		//prop = PropertyUtil.loadProperty(System.getProperty("jedi.home")+"/webapps/WEB-INF/sftpServer.properties");
    		//prop = PropertyUtil.loadProperty(System.getProperty("jedi.home")+"/WEB-INF/sftpServer.properties");
    		//String server = prop.getProperty("serverMode");
    		
    		try{
    			channelSftp.mkdir(path);
    		}catch(SftpException ee){
    		
    		}
    		//serverMode=dev
    		channelSftp.cd(path);
	    	fis = new FileInputStream(src);
	    	channelSftp.setFilenameEncoding("EUC-KR");
	    	if(serverType.equals("va")){
	    		channelSftp.put(fis, src.getName()); //운영
	    	}else{
	    		channelSftp.put(fis, new String(src.getName().getBytes() )); //개발 uat
	    	}
	    	
	    	//channelSftp.put(fis, new String(src.getName().getBytes("UTF-8"),"EUC-KR"));
	    	//channelSftp.put(fis, new String(src.getName().getBytes() )); //개발 uat
	    	channelSftp.exit();
    	}catch(Exception e){
    		e.printStackTrace();
    		throw e;
    	}finally{
    		try{fis.close();}catch(Exception ie){}
    	}
    }


    /**
     * 서버와의 연결을 끊는다.
     */

    public void disconnection() {
        channelSftp.quit();
        session.disconnect();
    }

}

