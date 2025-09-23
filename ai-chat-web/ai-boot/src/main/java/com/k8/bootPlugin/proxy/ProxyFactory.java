package com.k8.bootPlugin.proxy;

import javassist.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public class ProxyFactory {
    private final String INTERCEPTOR = "k8$interceptor";
    private final String METHOD_ARRAY = "methodArray";
    private final NamingPolicy namingPolicy = new NamingPolicy();
    public <T> T buildProxy(Class<T> interfaceClazz, MethodInterceptor methodInterceptor){
        return buildProxy(interfaceClazz,determineClassLoader(null),methodInterceptor);
    }
    private ClassLoader determineClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            return this.getClass().getClassLoader();
        } else {
            if (classLoader.getParent() == null) {
                ClassLoader aopClassLoader = this.getClass().getClassLoader();
                for(ClassLoader aopParent = aopClassLoader.getParent(); aopParent != null; aopParent = aopParent.getParent()) {
                    if (classLoader == aopParent) {
                        return aopClassLoader;
                    }
                }
            }
            return classLoader;
        }
    }
    public <T> T buildProxy(Class<T> interfaceClazz,ClassLoader classLoader,MethodInterceptor methodInterceptor){
        if (!interfaceClazz.isInterface()) throw new RuntimeException("Class is not a interface , class: "+ interfaceClazz.getName());
        methodInterceptor.init(interfaceClazz);
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new ClassClassPath(classLoader.getClass()));
        try {
            CtClass proxy = pool.makeClass(namingPolicy.getProxyName(interfaceClazz));
            Method[] declaredMethods = interfaceClazz.getDeclaredMethods();
            CtClass interfaceCtClazz = pool.get(interfaceClazz.getName());
            proxy.addInterface(interfaceCtClazz);
            CtClass methodArrayCtClass = pool.get(Method[].class.getName());
            proxy.addField(new CtField(methodArrayCtClass,METHOD_ARRAY,proxy));
            CtClass interceptorCtClass = pool.get(MethodInterceptor.class.getName());
            proxy.addField(new CtField(interceptorCtClass,INTERCEPTOR,proxy));
            int index = 0;
            for (Method method : declaredMethods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                CtClass[] ctParameters = new CtClass[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> clazz = parameterTypes[i];
                    ctParameters[i] = getCtClassForPrimitive(clazz, pool);
                }
                Class<?> returnType = method.getReturnType();
                CtClass ctReturnType = pool.get(returnType.getName());
                String body;
                CtMethod ctMethod = new CtMethod(ctReturnType, method.getName(), ctParameters, proxy);
                if (returnType == Void.TYPE) {
                    body = String.format(
                            "{this.%s.process(this, %s[%d], $args);}",
                            INTERCEPTOR, METHOD_ARRAY, index
                    );
                } else {
                    body = String.format(
                            "{ " +
                                    "Object result = this.%s.process(this, %s[%d], $args);\n" +
                                    "if (result == null) {\n" +
                                    "    return null;\n" +
                                    "}\n" +
                                    "return (%s) result;\n" +
                                    "}",
                            INTERCEPTOR, METHOD_ARRAY, index,
                            returnType.getName()
                    );
                }
                ctMethod.setBody(body);
                proxy.addMethod(ctMethod);
                index++;
            }

            Class<?> proxyClass = proxy.toClass();
            T instance = (T) proxyClass.newInstance();
            Field methodAF = proxyClass.getDeclaredField(METHOD_ARRAY);
            methodAF.setAccessible(true);
            methodAF.set(instance,declaredMethods);
            Field interceptorF = proxyClass.getDeclaredField(INTERCEPTOR);
            interceptorF.setAccessible(true);
            interceptorF.set(instance,methodInterceptor);
            return instance;
        } catch (NotFoundException | CannotCompileException | NoSuchFieldException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private static CtClass getCtClassForPrimitive(Class<?> clazz, ClassPool pool) {
        if (!clazz.isPrimitive()) {
            try {
                return pool.get(clazz.getName());
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (clazz == int.class) return CtClass.intType;
        else if (clazz == void.class) return CtClass.voidType;
        else if (clazz == boolean.class) return CtClass.booleanType;
        else if (clazz == byte.class) return CtClass.byteType;
        else if (clazz == char.class) return CtClass.charType;
        else if (clazz == short.class) return CtClass.shortType;
        else if (clazz == long.class) return CtClass.longType;
        else if (clazz == float.class) return CtClass.floatType;
        else if (clazz == double.class) return CtClass.doubleType;
        else throw new IllegalArgumentException("Unsupported primitive: " + clazz);
    }
    public static class NamingPolicy{
        ConcurrentHashMap<Class<?>, AtomicInteger> classProxyCounter = new ConcurrentHashMap<>();
        public String getProxyName(Class<?> clazz){
            AtomicInteger counter = classProxyCounter.get(clazz);
            if (counter == null) {
                counter = classProxyCounter.computeIfAbsent(clazz,(key)->{
                    return new AtomicInteger(0);
                });
            }
            return clazz.getName()+"$"+counter.getAndIncrement();
        }
    }
}
