package cs.com.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.commons.logging.LogFactory;

public class Property
{
    private static String baseName = "config";
    private static HashMap<String, String> propertyMap = new HashMap<String, String>();
    
    static
    {
        try
        {
            ResourceBundle property = ResourceBundle.getBundle(baseName);
            Enumeration<String> keys = property.getKeys();
            
            while (keys.hasMoreElements())
            {
                String key = keys.nextElement();
                propertyMap.put(key.toUpperCase(), property.getString(key));
            }
        }
        catch (Exception e)
        {
            LogFactory.getLog(Property.class).error("Can't read the properties file. Make sure " + baseName + ".properties is in the CLASSPATH");
        }
    }
    
    public static int getInt(String key)
    {
        String property = propertyMap.get(key.toUpperCase());
        int retval = -1;
        
        if (property != null)
        {
            try
            {
                retval = Integer.parseInt(property.trim());
            }
            catch (NumberFormatException e)
            {
                LogFactory.getLog(Property.class).error(e);
            }
        }
        
        return retval;
    }
    
    public static int getInt(String key, int defaultValue)
    {
        int property = getInt(key);
        return (property == -1) ? defaultValue : property;
    }
    
    public static String getString(String key)
    {
        return propertyMap.get(key.toUpperCase());
    }
    
    public static String getString(String key, String defaultValue)
    {
        String property = getString(key);
        return (property == null) ? defaultValue : property.trim();
    }
}
