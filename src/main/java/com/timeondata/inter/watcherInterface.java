package com.timeondata.inter;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhangjianxin on 2018/3/19.
 * Github Breakeval13
 * blog firsh.me
 */
public interface watcherInterface {

    int insert(JSONObject inData, JSONObject dbTable);
    int delete(JSONObject inData, JSONObject dbTable);
    int updata(JSONObject inData, JSONObject dbTable);

}
