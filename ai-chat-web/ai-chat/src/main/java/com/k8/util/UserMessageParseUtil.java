package com.k8.util;

import java.util.List;
import java.util.Map;

/**
 * @Author: k8
 * @CreateTime: 2025-08-31
 * @Version: 1.0
 */
public class UserMessageParseUtil {
    public static String parseQuery(String messageJson){
        Map<String,String> map = JsonUtil.toObject(messageJson, Map.class);
        List<String> contents = JsonUtil.toObject(map.get("contents"), List.class);
        Map<String,String> contentMap = JsonUtil.toObject(contents.get(0), Map.class);
        return contentMap.get("text");
    }
}
