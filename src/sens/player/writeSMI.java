package sens.player;

import java.io.File;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.omg.CORBA.portable.InputStream;

import sun.net.www.http.HttpClient;

import com.locus.jedi.log.ErrorLogger;


public class writeSMI {
	
    private static FileWriter objfile = null;

    public static void main(String[] args) throws Exception {
//        new readSMI().fielWrite();
    }    

    
    /******************************************
     * 파일을 생성해서 내용 쓰기
     ******************************************/
    public void fielWrite(String dir_path, String file_name, String content) throws Exception {
    	/*
    	FileWriter fw = null;

    	try {
    		fw = new FileWriter(DEFAULT_DIR);//"http://127.0.0.1:8080/VSENS/" + file_id + ".smi"); 

    		fw.write(content);

    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {

    	    if(fw != null) {
    	    	try {
    	    		fw.close();
    	    	} catch (IOException e) {}
    	    }
    	}*/

    	
    	URL location = writeSMI.class.getProtectionDomain().getCodeSource().getLocation();
        //System.out.println("*************"+location.getFile());
        

        URL resource = getClass().getResource("/");
        String path = resource.getPath();
        
        //System.out.println("*************file_name "+file_name);
        
        StringBuffer bufLogPath  = new StringBuffer();       
                     bufLogPath.append(file_name);
                     bufLogPath.append(".smi");
        StringBuffer bufLogMsg = new StringBuffer(); 
                     bufLogMsg.append(content);

        try{
        	String chkFile = file_name.substring(file_name.lastIndexOf('\\')+1)+".smi";
//        	System.out.println("###############"+dir_path);
//        	System.out.println("##############"+ file_name.lastIndexOf('\\')+1);
        	dir_path = cleanString(dir_path);
        	File dir = new File(dir_path, chkFile); 
        	
	        // 같은 파일 없으면 생성      
	        if(!dir.exists()){
	        	System.out.println("[[fielWrite]] create SMI File");
	        	String val = cleanString(bufLogPath.toString());
	        	bufLogPath  = new StringBuffer();       
	        	bufLogPath.append(val);
	        	
	        	objfile = new FileWriter(bufLogPath.toString(), true);
	            objfile.write(bufLogMsg.toString());
	            objfile.write("\r\n");
	        }
            
        } catch(IOException e) {
            
        } finally {
            try{
             objfile.close();
            }catch(Exception e1){}
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

