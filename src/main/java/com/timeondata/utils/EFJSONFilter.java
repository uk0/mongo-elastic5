package com.timeondata.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;

public class EFJSONFilter {
    private JSONObject json;
    private String dropKeyStartStr;

    //新加的
    private SerializeWriter writer = new SerializeWriter();
    private JSONSerializer serializer = new JSONSerializer(writer);

    public EFJSONFilter(JSONObject json) {
        this.json = json;
    }

    public static EFJSONFilter build(JSONObject json) {
        return new EFJSONFilter(json);
    }

    public static EFJSONFilter build(String jsonStr) {
        return new EFJSONFilter(JSON.parseObject(jsonStr));
    }

    public EFJSONFilter setDropKeyStartStr(String keyStartStr) {
        this.dropKeyStartStr = keyStartStr;
        return this;
    }

    public JSONObject getJson() {
        SerializeWriter sw = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(sw);
//        serializer.getPropertyFilters().add(new DropKeyStartWithFilter(json));
        serializer.write(json);
        String s = sw.toString();
        return JSON.parseObject(s);
    }

    public JSONObject getJsonNew() {
        serializer.write(json);
        return JSON.parseObject(writer.toString());
    }

    public EFJSONFilter addFilter(PropertyFilter filter) {
        serializer.getPropertyFilters().add(filter);
        return this;
    }

    private static class DropKeyStartWithFilter implements PropertyFilter {
        //        private JSONObject includeJSON = new JSONObject();
        private String dropKeyStartStr = null;

        public DropKeyStartWithFilter(String dropKeyStartStr) {
            this.dropKeyStartStr = dropKeyStartStr;
        }

        @Override
        public boolean apply(Object source, String name, Object value) {
//            if (name.startsWith(dropKeyStartStr)) {
////                includeJSON.remove(name);
//                return false;
//            }
//            return true;
            return !name.startsWith(dropKeyStartStr);
        }
    }
}

