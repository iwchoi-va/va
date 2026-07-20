package telecaps.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/*
 * Written by Yeon Jung Mo(2008/10/10)
 * Class Name	 	: ReadConf
 * Argument 		: 없음 
 * Description		: XML 환경파일로 부터 환경정보을 추출하는 객체
 */
public class ReadConf{
    static public final String	CONFIG_PATH = "D:/Appl/Hcheck/config/conf.xml";

	static Properties p;
	
	static{
		
		//System.out.println("CONFIG_PATH: "+CONFIG_PATH);

		p = new Properties();
	    
		try{
	    	
	    	InputStream is = new FileInputStream(CONFIG_PATH);
	    	p.loadFromXML(is);
	    	is.close();
	    }
	    catch(Exception e){
			Logger.write("error",
					"ERROR",
					Logger.getStackTrace(e),
					"Problem loading application properties",
					"",
					"",
					"");
	   	}
	}

	/*
	 * 주어진 환경변수의 설정 값을 가져옴
	 * 만약 설정값이 없거나, 환경변수가 존재하지 않으면 NULL
	 */
	public static String getProperty(String key){
		String value = p.getProperty(key);
	    return expandProperty(key, value);
	}

	/*
	 * 주어진 환경변수의 설정 값을 가져옴
	 * 만약 설정값이 없거나, 환경변수가 존재하지 않으면 디폴트 값으로 설정됨
	 */
	public static String getProperty(String key, String defaultValue){
	    String value = p.getProperty(key, defaultValue);
	    return expandProperty(key, value);
	}

	static String expandProperty(String key, String value){
		if(value == null)
			return null;
	    
		List<String> fragments = null;
	    try{
	    	fragments = parsePropertyValue(value);
	    }
	    catch(Exception e){
			Logger.write("error",
					"ERROR",
					Logger.getStackTrace(e),
					"Property expansion failed for property key",
					key,
					"with value",
					value);
            return value;
	    }
	    
	    StringBuffer newValue = new StringBuffer("");
	    for(int i = 0; i < fragments.size(); i++){
	    	String fragment = (String)fragments.get(i);if(fragment == null)
	                break;
	            if(fragment.charAt(0) != '$')
	            {
	                newValue.append(fragment);
	                continue;
	            }
	            String expandKey = fragment.substring(1);
	            if(expandKey == null || expandKey == "" || key == expandKey)
	            {
	                System.out.println((new StringBuilder()).append("Property expansion failed for property key: ").append(key).append(" within value ").append(value).toString());
	                return value;
	            }
	            String expandValue = getProperty(expandKey);
	            if(expandValue == null)
	            {
	                expandValue = System.getProperty(expandKey);
	                if(expandValue == null)
	                    expandValue = System.getenv(expandKey);
	            }
	            if(expandValue == null)
	                newValue.append((new StringBuilder()).append("${").append(expandKey).append("}").toString());
	            else
	                newValue.append(expandValue);
	        }

	        return newValue.toString();
	    }

	    static List<String> parsePropertyValue(String value)
	        throws Exception
	    {
	        StringTokenizer st = new StringTokenizer(value, "${}", true);
	        ArrayList<String> fragments = new ArrayList<String>();
	        while(st.hasMoreTokens()) 
	        {
	            String element = st.nextToken();
	            if(element.charAt(0) != '$')
	            {
	                fragments.add(element);
	            }
	            else{
	                if(st.countTokens() < 3)
	                    throw new Exception("Malformed property expression");
	                if(st.nextToken().charAt(0) != '{')
	                    throw new Exception("Malformed property expression");
	                fragments.add((new StringBuilder()).append("$").append(st.nextToken()).toString());
	                if(st.nextToken().charAt(0) != '}')
	                    throw new Exception("Malformed property expression");
	            }
	        }
	        return fragments;
	    }

}
