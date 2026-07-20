package com.hansol.audio.extract;

import org.apache.commons.lang3.ArrayUtils;

import com.locus.jedi.log.ErrorLogger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created by Dmitry on 7/6/2014.
 */
public class SimpleWaveformExtractor implements WaveformExtractor {

    //private static final int DEFAULT_BUFFER_SIZE = 32768;
    //private static final int DEFAULT_BUFFER_SIZE = 32768;
	private static final int DEFAULT_BUFFER_SIZE = 50000;

    @Override
    public int[] extract(AudioInputStream in) {
    	ErrorLogger.debug("#################EXTRACT###########################");
        long start = System.currentTimeMillis();
        
        AudioFormat format = in.getFormat();
        byte[] audioBytes = readBytes(in);

        int[] result = null;
        ErrorLogger.debug("##################################################");
        ErrorLogger.debug("Sample Bit: "+format.getSampleSizeInBits());
        ErrorLogger.debug("##################################################");
        
        int cnt = 0;
        if (format.getSampleSizeInBits() == 16) {
            //int samplesLength = audioBytes.length / 2;
        	int samplesLength = audioBytes.length / 20;
            int sampleStep = 2;
            //int samplesLength = audioBytes.length;
            result = new int[samplesLength];
            if (format.isBigEndian()) {
                System.out.println("BigEndian");
                for (int i = 0; i < samplesLength; ++i) {
                    byte MSB = audioBytes[i * 2];
                    byte LSB = audioBytes[i * 2 + 1];
                    result[i] = MSB << 8 | (255 & LSB);
                    //System.out.println("Big Endian: "+result[i]);
                    cnt++;
                }
            } else {
                System.out.println("BigEndian False");
                //for (int i = 0; i < samplesLength; i += 2) {
                for (int i = 0; i < samplesLength; i += 2) {
                    byte LSB = audioBytes[i * 2];
                    byte MSB = audioBytes[i * 2 + 1];
                    result[i / 2] = MSB << 8 | (255 & LSB);
                    cnt++;
                }
            }
        } else {
            int samplesLength = audioBytes.length;
            result = new int[samplesLength];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < samplesLength; ++i) {
                    result[i] = audioBytes[i];
                }
            } else {
                for (int i = 0; i < samplesLength; ++i) {
                    result[i] = audioBytes[i] - 128;
                }
            }
        }
        System.out.println("Audio Bytes: "+audioBytes.length);
        System.out.println("Count: "+cnt);
        System.out.println("length: "+result.length);
        System.out.println("Before Return of Array: "+result[result.length-1]);

        int[] arr_int = Arrays.copyOf(result, cnt);
        
        //return result;
        long end = System.currentTimeMillis();
        long elasped = end - start;
        
        //System.out.println("Elasped Time: "+(elasped/1000)/(double)60);
        return arr_int;
    }

    private byte[] readBytes(AudioInputStream in) {
        byte[] result = new byte[0];
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
      
        try {
            int bytesRead = 0;
            do {
                bytesRead = in.read(buffer);
                result = ArrayUtils.addAll(result, buffer);
            } while (bytesRead != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
