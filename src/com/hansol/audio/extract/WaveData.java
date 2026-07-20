package com.hansol.audio.extract;

/*
 * Copyright (c) 2015, 2016 Hansol Inticube its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.hansol.audio.util.PropertyUtil;
import com.locus.jedi.log.ErrorLogger;

/**
 *
 * @author user
 */
public class WaveData {
    private static Properties prop;
    private static int iPeriod = 0;
    private static int iPeak = 0;
    private static int iMinFileSize = 0;
    private static int iMaxFileSize = 0;
    private static int iMaxPeriod = 0;
    
    private static int[] extractedData = null;
    private static int[] tempArray = null;
    
    private static StringBuffer buffer = new StringBuffer();

    private static void initialize(){
        try{
        	//prop = PropertyUtil.loadProperty("C:/project/VSENS_LOTTE/webapps/WEB-INF/conf.properties");
        	//prop = PropertyUtil.loadProperty(System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties");
        	prop = PropertyUtil.loadProperty(System.getProperty("jedi.home")+"/WEB-INF/conf.properties");

            if(prop != null){
                //System.out.println("waveform_period: "+PropertyUtil.readProperty(prop, "waveform_period"));
                iPeriod = Integer.parseInt(PropertyUtil.readProperty(prop, "waveform_period") == null ? "1000":PropertyUtil.readProperty(prop, "waveform_period").trim());
                iPeak = Integer.parseInt(PropertyUtil.readProperty(prop, "waveform_peak") == null ? "3":PropertyUtil.readProperty(prop, "waveform_peak").trim());
                iMinFileSize = Integer.parseInt(PropertyUtil.readProperty(prop, "waveform_MinFileSize") == null ? "-1" : PropertyUtil.readProperty(prop, "waveform_MinFileSize").trim());
                iMaxFileSize = Integer.parseInt(PropertyUtil.readProperty(prop, "waveform_MaxFileSize") == null ? "-1":PropertyUtil.readProperty(prop, "waveform_MaxFileSize").trim());
                iMaxPeriod = Integer.parseInt(PropertyUtil.readProperty(prop, "waveform_MaxPeriod") == null ? "-1":PropertyUtil.readProperty(prop, "waveform_MaxPeriod").trim());
            } else {
                iPeriod = 1000;
                iPeak = 3;
                iMinFileSize = -1;
                iMaxFileSize = -1;
                iMaxPeriod = -1;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String extractData(String filePath){
        File audioFile = null;
        int max = 0;
        int min = 0;
        int std = 0;
        //StringBuffer buffer = new StringBuffer();
        
        try{
            initialize();
            audioFile = new File(filePath);
            ErrorLogger.debug("#####filePath = "+filePath);
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            
            extractedData = new SimpleWaveformExtractor().extract(ais);
            
            ErrorLogger.debug("############추출 end###########");
            tempArray = Arrays.copyOf(extractedData, extractedData.length);
            
            Arrays.sort(tempArray);
            ErrorLogger.debug("Min: "+tempArray[0]);
            ErrorLogger.debug("Max: "+tempArray[tempArray.length-1]);

            max = tempArray[tempArray.length-1];
            min = tempArray[0];
            
            std = (Math.abs(max) > Math.abs(min)) ? Math.abs(max):Math.abs(min);
            
            int cnt = 0;
            //System.out.println("File Size: "+audioFile.length()/1000);
            //if minimum size is set and then check size of file
            if(iMinFileSize != -1){
                if(iMinFileSize >= (audioFile.length()/1000)){
                    iPeriod = 10;
                }
            }
            
            //if maximum size is set and then check size of file
            if(iMaxFileSize != -1){
                if(iMaxFileSize <= (audioFile.length()/1000)){
                    iPeriod = iMaxPeriod;
                }
            }
            
            //long start = System.currentTimeMillis();
            if(buffer != null && buffer.length() != 0)  buffer.delete(0, buffer.length());

            for(int i=0;i<extractedData.length;i+=iPeriod){
                //double b = extractedData[i]/(double)(tempArray[tempArray.length-1]/2);
                double b = extractedData[i]/(double)(std/iPeak);
                if(i < extractedData.length){
                    buffer.append(b+",");
                } else {
                    buffer.append(b);
                }
                //cnt++;
            }
            //System.out.println("Total Length: "+cnt);
            //long end = System.currentTimeMillis();
            //long elasped = end - start;
        
            //System.out.println("Elasped Time(WaveData): "+(elasped/1000)/(double)60);
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
    
    public static void main(String[] args){
        //WaveData test = new WaveData();
        //test.extractData("D:\\Hansol\\M_SENS 파트\\Development\\ie_player\\8k16bitpcm.wav");
        //test.extractData("D:\\Project\\Player\\example_wav\\uncompressed_t5.wav");
        //test.extractDataByDouble("D:\\Project\\Player\\example_wav\\uncompressed_t5.wav");
        //WaveData.initialize
        for(int i=0;i<50;i++){
            WaveData.extractData("D:\\Hansol\\M_SENS 파트\\Development\\ie_player\\test.wav");
            System.out.println("Count: "+i);
        }
    }
}
