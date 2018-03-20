package com.timeondata.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by lcy on 2017/4/17.
 */
public class ServerEnv {
    private static Logger logger = LoggerFactory.getLogger(ServerEnv.class);


    public static String getConfigDir() {
        String CONFIG_DIR = null;
        try {
            CONFIG_DIR = ServerEnv.class.getClassLoader().getResource(".").toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (!OSUtil.isWindowsOS()) {
            Map map = System.getenv();
            if (!map.containsKey("ConfDir")) {
                CONFIG_DIR = "/home/admin/apirun/conf/";
                logger.debug("BaseDir is not found,Use default path");
            } else {
                CONFIG_DIR = map.get("ConfDir").toString();
            }
        }
        logger.info("Get ConfigDir = " + CONFIG_DIR);
//        System.out.println("Get ConfigDir = " + CONFIG_DIR);
        return CONFIG_DIR;
    }
}
