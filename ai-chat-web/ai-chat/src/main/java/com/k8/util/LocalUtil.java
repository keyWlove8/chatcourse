package com.k8.util;

import com.k8.param.ImageContextPara;

import java.util.HashMap;
import java.util.Map;

import static com.k8.constants.LocalUtilConstants.ARG_ID_KEY;
import static com.k8.constants.LocalUtilConstants.IMAGE_CONTEXT_PARA_KEY;

/**
 * @Author: k8
 * @CreateTime: 2025-08-11
 * @Version: 1.0
 */
public class LocalUtil {
    private static ThreadLocal<Map<String, Object>> MAP_LOCAL = new ThreadLocal<>();


    public static void setImageValue(ImageContextPara contextPara) {
        addLocalParam(IMAGE_CONTEXT_PARA_KEY, contextPara);
    }

    public static void addLocalParam(String key, Object value) {
        Map<String, Object> local = MAP_LOCAL.get();
        if (local == null) {
            local = new HashMap<>(200);
            MAP_LOCAL.set(local);
        }
        if (local.containsKey(key)) throw new RuntimeException("key is exist.");
        local.put(key, value);
    }

    public static Object getLocalParam(String key){
        Map<String, Object> local = MAP_LOCAL.get();
        if (local == null) return null;
        else return local.get(key);
    }

    public static ImageContextPara getImageData() {
        Object localParam = getLocalParam(IMAGE_CONTEXT_PARA_KEY);
        if (localParam == null) return null;
        return (ImageContextPara) localParam;
    }

    public static void setRagId(String ragId) {
        addLocalParam(ARG_ID_KEY, ragId);
    }

    public static String getRagId() {
        Object localParam = getLocalParam(ARG_ID_KEY);
        if (localParam == null) return null;
        else return localParam.toString();
    }


    public static void removeAll() {
        MAP_LOCAL.remove();
    }
}
