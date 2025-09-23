package com.k8.bootPlugin.test.util;


import com.k8.bootPlugin.test.param.ImageContextPara;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public class LocalUtil {
    private static ThreadLocal<ImageContextPara> imageLocal = new ThreadLocal<>();

    public static void setValue(ImageContextPara contextPara) {
        imageLocal.set(contextPara);
    }

    public static ImageContextPara getData() {
        return imageLocal.get();
    }

    public static void remove(){
        imageLocal.remove();
    }
}
