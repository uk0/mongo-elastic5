package com.timeondata.utils;

/**
 * Created by lcy on 2017/4/26.
 */
public class Constant {
    public static class CHARSET {
        public static final String CHARSET_UTF8 = "utf-8";
        public static final String CHARSET_GBK = "gbk";
        public static final String CHARSET_GB2312 = "gb2312";
    }

    public static class SYS {
        public static final String CONFIG_IMPORT_KEYFLAG = "$";
        public static final String JSON_IMPORT_KEY = "import";
        public static final String JSON_IMPORT_TYPE_JSONOBJ = "#";
        public static final String JSON_IMPORT_TYPE_FILE = "@";
        public static final String JSON_IMPORT_TYPE_JSONARR = "%";

        public static final String clearImportFlag(String str) {
            return str = str.replace(JSON_IMPORT_TYPE_JSONOBJ, "")
                    .replace(JSON_IMPORT_TYPE_FILE, "")
                    .replace(JSON_IMPORT_TYPE_JSONARR, "");
        }
    }
}
