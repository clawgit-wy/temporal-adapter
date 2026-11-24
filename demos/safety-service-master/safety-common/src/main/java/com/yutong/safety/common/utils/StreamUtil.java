package com.yutong.safety.common.utils;

import java.io.*;

public class StreamUtil {
    public static void close(FileInputStream o){
        try {
            if(o != null){
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(OutputStream o){
        try {
            if(o != null){
                o.flush();
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void close(InputStream o){
        try {
            if(o != null){
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(BufferedInputStream o){
        try {
            if(o != null){
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void close(BufferedReader o){
        try {
            if(o != null){
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Writer o){
        try {
            if(o != null){
                o.flush();
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Reader o){
        try {
            if(o != null){
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileCode(String file){
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            //File f = new File(file);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            //br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
            String s = "";
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(br);
        }
        return sb.toString();
    }
}
