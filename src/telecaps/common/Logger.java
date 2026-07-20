package telecaps.common;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
	
	/*
	 * 로그가 생성되는 위치을 설정할 수 있는 로그 함수
	 */
	public static void write(String filePath,
							String fileName,
							String code,
							String arg1,
							String arg2,
							String arg3,
							String arg4,
							String arg5){
		File dir = null, file = null;
		FileWriter fw = null;
		String logName = null, logDir = null;
		String today = null, mtime = null;

		today = DateTime.getDate();
		mtime = DateTime.getmTime();
		/*리눅스버전*/
		//logDir = filePath + "/"+today+"/" +fileName;
		/*윈도우버전*/
		logDir = filePath + "\\"+today+"\\" +fileName;
		
		//logDir = ReadConf.getProperty("LogPath");
		
		/*리눅스버전*/
		//logName = logDir + "/" + fileName + ".log";
		/*윈도우버전*/
		logName = logDir + "\\" + fileName + ".log";
 
		dir = new File(logDir);

		if(!dir.isDirectory()){
			dir.mkdirs();
		}

		file = new File(logName);

		try{				
			fw = new FileWriter(file, true);
			fw.write(today+"!"+
					mtime+"!"+
					code+"!"+
					arg1+"!"+
					arg2+"!"+
					arg3+"!"+
					arg4+"!"+
					arg5+"!");
			fw.write("\n");
			fw.flush();
		}
		catch(Exception e){
			//e.printStackTrace();
			
			Logger.write(fileName,
					"ERROR",
					Logger.getStackTrace(e),
					"",
					"",
					"",
					"");
			
		}
		finally{
			try{
				fw.close();
				return;
			}
			catch(Exception e){
				//e.printStackTrace();
				Logger.write(fileName,
						"ERROR",
						Logger.getStackTrace(e),
						"",
						"",
						"",
						"");

			}
		}
	}
	
	/*
	 * 로그가 생성되는 경로가 고정되어 있는 로그 함수
	 */
	public static void write(String fileName,
							String code,
							String arg1,
							String arg2,
							String arg3,
							String arg4,
							String arg5){
		File dir = null, file = null;
		FileWriter fw = null;
		String logName = null, logDir = null;
		String today = null, mtime = null;

		today = DateTime.getDate();
		mtime = DateTime.getmTime();
		
		//System.out.println("logDir1 ");
		/*리눅스버전*/
		//logDir = ReadConf.getProperty("LogPath")+"/"+today+"/"+fileName;
		/*윈도우버전*/
		logDir = ReadConf.getProperty("LogPath")+"\\"+today+"\\"+fileName;
		//logDir = ReadConf.getProperty("LogPath");
		/*리눅스버전*/
		//logName = logDir+"/"+fileName+".log";
		/*윈도우버전*/
		logName = logDir+"\\"+fileName+".log";

		//System.out.println("logDir: "+logDir);
		dir = new File(logDir);

		if(!dir.isDirectory()){
			dir.mkdirs();
		}

		file = new File(logName);

		try{				
			fw = new FileWriter(file, true);
			fw.write(today+"!"+
					mtime+"!"+
					code+"!"+
					arg1+"!"+
					arg2+"!"+
					arg3+"!"+
					arg4+"!"+
					arg5+"!");
			fw.write("\n");
			fw.flush();
		}
		catch(Exception e){
			//e.printStackTrace();
			Logger.write(fileName,
					"ERROR",
					Logger.getStackTrace(e),
					"",
					"",
					"",
					"");

		}
		finally{
			try{
				fw.close();
				return;
			}
			catch(Exception e){
				//e.printStackTrace();
				Logger.write(fileName,
						"ERROR",
						Logger.getStackTrace(e),
						"",
						"",
						"",
						"");
			}
		}
	}
    
	/*
	 * Exception 에러를 String으로 변환하는 함수(로그 파일에 남기기 위함)
	 */
	public static String getStackTrace(Throwable t){
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();
		return stringWritter.toString();
	}
}