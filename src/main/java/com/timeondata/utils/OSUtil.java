package com.timeondata.utils;

import java.util.Properties;

/**
 * Created by lcy on 2017/3/23.
 */
public class OSUtil {
    public static boolean isWindowsOS() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        }
        return false;
    }
}

