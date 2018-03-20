package com.timeondata.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lcy on 2017/3/23.
 */
public class File2Json {
    public static void main(String[] args) {
    }

    private static Logger logger = LoggerFactory.getLogger(File2Json.class);

    private static Map<String, JSONObject> file_json_hm = new HashMap<String, JSONObject>();

    public static void reload() {
        loadConfig(ServerEnv.getConfigDir());
        megerImport();
    }


    public static void load() {
        if (file_json_hm != null && file_json_hm.size() > 0) {
            return;
        }
        loadConfig(ServerEnv.getConfigDir());
        megerImport();

    }

    public static void addLoad(List<String> path) {
        for (String p : path) {
            loadConfig(p);
        }

        megerImport();
    }

    private static void loadConfig(String pathDir) {
        List<JSONObject> jsonList = getResorcesJson(pathDir);
        if (jsonList != null && jsonList.size() > 0) {
            for (JSONObject json : jsonList) {
                String name = json.getString("name");
                String path = json.getString("path");
                String type = json.getString("type");
                JSONObject jsonConfig = null;
                if (type.equals("json")) {
                    String str = FileUtil.readJSONFile(path);
                    jsonConfig = JSON.parseObject(str);

                } else if (type.equals("properties")) {
                    jsonConfig = FileUtil.readPropertiesFile(path);
                }
                file_json_hm.put(name, jsonConfig);
                logger.info("JsonParser: " + name + ".json to JSONObject");
            }

        }
    }

    private static void megerImport() {
        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();
        map.putAll(file_json_hm);
        for (String key : map.keySet()) {
            JSONObject fileJson = map.get(key);
            if (fileJson.containsKey(Constant.SYS.JSON_IMPORT_KEY)) {
                JSONArray importArr = fileJson.getJSONArray(Constant.SYS.JSON_IMPORT_KEY);
                for (int i = 0; i < importArr.size(); i++) {
                    JSONObject json = importArr.getJSONObject(i);
                    for (String k : json.keySet()) {
                        String importKey = json.getString(k);
                        Object value = getImportValue(importKey);
                        fileJson.put(Constant.SYS.CONFIG_IMPORT_KEYFLAG + k, value);
                        file_json_hm.put(key, fileJson);
                    }
                }
            }
        }
    }

    //@services.#server.port
    private static Object getImportValue(String importKey) {
        String[] vs = importKey.split("\\.");
        String filename = vs[0];
        if (filename.startsWith(Constant.SYS.JSON_IMPORT_TYPE_FILE)) {
            JSONObject fileJSON = file_json_hm.get(Constant.SYS.clearImportFlag(filename));
            Object tmp = null;
            for (int i = 1; i < vs.length; i++) {
                String key = vs[i];
                key = Constant.SYS.clearImportFlag(key);
                if (tmp == null) {
                    tmp = fileJSON.get(key);
                } else {
                    tmp = ((JSONObject) tmp).get(key);
                }
            }
            return tmp;
        } else {
            //本JSON中的key
        }
        return null;
    }

    private static String getImportKey(String key) {
        return Constant.SYS.JSON_IMPORT_KEY + "." + key;
    }


    public static String getValue(String fileName, String key) {
        return getConfig(fileName) == null ? null : getConfig(fileName).getString(key);
    }

    public static boolean getBooleanValue(String fileName, boolean defalut, String... key) {
        String s = getValue(fileName, key);
        try {
            return Boolean.valueOf(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defalut;
    }

    public static String getValue(String fileName, String... key) {
        JSONObject json = getConfig(fileName);
        for (int i = 0; i < key.length; i++) {
            if (!json.containsKey(key[i])) {
                return null;
            }
            if (i == key.length - 1) {
                return json.getString(key[i]);
            } else {
                json = json.getJSONObject(key[i]);
            }
        }
        return null;
    }

    public static JSONObject getConfig(String fileName) {
        if (file_json_hm == null || file_json_hm.size() == 0) {
            load();
        }
        return file_json_hm.get(fileName);
    }

    private static List<JSONObject> getResorcesJson(String configDir) {
        File resourcesFile = new File(configDir);
        String[] files = null;
        files = resourcesFile.list();
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (files != null && files.length > 0) {
            for (String fileName : files) {
                if (fileName.endsWith(".json") || fileName.endsWith(".properties")) {
                    System.out.println("Loading Config " + configDir + fileName + "...");
                    String path = configDir + fileName;
                    String name = fileName.substring(0, fileName.lastIndexOf("."));
                    String type = fileName.substring(fileName.lastIndexOf(".") + 1);
                    System.out.println(name);
                    System.out.println(path);
                    System.out.println(type);
                    System.out.println("------------");

                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("path", path);
                    json.put("type", type);
                    list.add(json);
                }
            }
        }
        return list;
    }
}
