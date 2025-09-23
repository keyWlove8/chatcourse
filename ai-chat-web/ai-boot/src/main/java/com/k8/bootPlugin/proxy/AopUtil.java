package com.k8.bootPlugin.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public class AopUtil {
    final static Set<String> objectMethod=new HashSet<>();
    static {
        for (Method method : Object.class.getMethods()) {
            objectMethod.add(method.getName());
        }
    }
    public static boolean isNoOpMethod(Method method) {
        int mod = method.getModifiers();
        if (Modifier.isFinal(mod)||Modifier.isStatic(mod)){
            return true;
        }else return objectMethod.contains(method.getName());
    }
    public static String getMethodKey(Method method){
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getSimpleName())
                .append("#")
                .append(method.getName())
                .append("(");
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(paramTypes[i].getSimpleName());
        }
        sb.append(")");
        return sb.toString();
    }
}
