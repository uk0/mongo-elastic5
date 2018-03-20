package com.timeondata.utils;


import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * Created by lcy on 2017/3/23.
 */
public class FileUtil {
    public static String readJSONFile(String filePath) {
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String respStr = "";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if(lineTxt.trim().startsWith("//")){
                        continue;
                    }
                    respStr += lineTxt;
                }

                return respStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static JSONObject readPropertiesFile(String filePath) {
        InputStreamReader read = null;
        JSONObject obj = new JSONObject();
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String respStr = "";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    respStr = lineTxt.trim();
                    if (respStr.indexOf("#") == 0 || !respStr.contains("=")) {
                        continue;
                    }
                    String[] kv = respStr.split("=");
                    if (kv == null || kv.length == 0) {
                        continue;
                    }
                    String key = kv[0];
                    String value = "";
                    if (kv.length > 1) {
                        value = kv[1];
                    }
                    obj.put(key, value);
                }
                return obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
