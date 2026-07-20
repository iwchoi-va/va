package cs.com.util;


public class Config
{
    private static final String INSTALLED_DATE        = "installed_date";
    private static final String DURATION_DAY          = "duration_day";
    private static final String WAS_PATH          = "was_path";
    private static final String DIR_PATH              = "dir_path";
    
    public static String    getInstalledDate()     { return Property.getString(INSTALLED_DATE); }
    public static int       getDurationDay()       { return Property.getInt(DURATION_DAY); }
    public static String    getWasPath()           { return Property.getString(WAS_PATH); }
    public static String    getDirPath()           { return Property.getString(DIR_PATH); }
    
}
