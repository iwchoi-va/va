/*
 * Copyright (c) 2015, 2016 Hansol Inticube its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package com.hansol.audio.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.Map;

/**
 *
 * @author Ethan Park 2015.06.02
 */
public class PropertyUtil {
    public static Properties loadProperty(String filePath){
        Properties prop = new Properties();
        try{
            prop = loadProperty(new FileInputStream(filePath));
        }catch(FileNotFoundException ffe){
            ffe.printStackTrace();
            prop = null;
        }
        
        return prop;
    }
    
    public static Properties loadProperty(InputStream filePath){
        Properties prop = new Properties();
        try{
            prop.load(filePath);
        }catch(FileNotFoundException ffe){
            ffe.printStackTrace();
            prop = null;
        }catch(IOException ioe){
            ioe.printStackTrace();
            prop = null;
        }catch(NullPointerException npe){
            npe.printStackTrace();
            prop = null;
        }
        
        return prop;
    }
    
    public static String readProperty(Properties property, String key){
        String message = "No Message";
        
        if(property != null){
            message = property.getProperty(key);
        }
        
        return message;
    }
    
    public static String[] readProperties(Properties property, String key){
        String tmpTarget = "";
        String[] keyString = null;
        Vector keyList = new Vector();
        Object[] objList = property.keySet().toArray();//.stream().sorted().toArray();
        Arrays.sort(objList);

        for(Object obj:objList){
            if(obj.toString().contains(key)){
                keyList.addElement(obj.toString());
            }
        }
        
        if(keyList.size() != 0){
            keyString = Arrays.copyOf(keyList.toArray(), keyList.size(), String[].class);
        }
        
        return keyString;
    }
    
    public static String[] readPropertiesValues(Properties property, String key){
        String[] valueString = null;
        String[] keyString = null;
        
        keyString = readProperties(property, key);
        
        if(keyString != null && keyString.length != 0){
            valueString = new String[keyString.length];
            for(int i=0;i<keyString.length;i++){
                valueString[i] = readProperty(property, keyString[i]);
            }
        }

        return valueString;
    }
    
    public static String addPrefix(String rexp, String pre){
        
        if(rexp.indexOf(pre) != 0){
            rexp = pre+rexp;
        }
        
        return rexp;
    }

    public static void makeFile(File path){
        try{
            if(!path.exists()){
                File dir = new File(path.getPath().replace(path.getName(), ""));
                if(!dir.exists()){
                    dir.mkdirs();
                }
                if(!path.exists()){
                    path.createNewFile();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        /*Properties prop = loadProperty("resources/fileConfig.properties");
        System.out.println(readProperty(prop, "target1"));
        readPropertiesValues(prop, "watch_dir");*/
        String a = addPrefix("test/run/app", "/");
        System.out.println(a);
        String b = addPrefix("/test/run/app", "/");
        System.out.println(b);
    }
}
