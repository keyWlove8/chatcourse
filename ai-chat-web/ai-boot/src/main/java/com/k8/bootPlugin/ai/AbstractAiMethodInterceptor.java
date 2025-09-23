package com.k8.bootPlugin.ai;

import com.k8.bootPlugin.ai.impl.DefaultAiMethodContextBuilder;
import com.k8.bootPlugin.annotation.SystemMessage;
import com.k8.bootPlugin.proxy.AopUtil;
import com.k8.bootPlugin.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public abstract class AbstractAiMethodInterceptor implements MethodInterceptor {
    private Map<String, AiMethodContext> methodContextMap;
    private final AiProxyStarter aiProxyStarter;
    private boolean isInit;

    public AbstractAiMethodInterceptor(AiProxyStarter aiProxyStarter) {
        this.aiProxyStarter = aiProxyStarter;
    }

    /**
     * 这里只是提供一个默认参考，将Ai方法的MethodContext提前缓存下来，调用时可以更快
     *
     * @param superInterface
     * @param aiProxyStarter
     * @return
     */
    private Map<String, AiMethodContext> initMethodContexts(Class<?> superInterface, AiProxyStarter aiProxyStarter) {
        Map<String, AiMethodContext> map = new HashMap<>();
        AiMethodContextBuilder aiMethodContextBuilder = new DefaultAiMethodContextBuilder();
        for (Method declaredMethod : superInterface.getDeclaredMethods()) {
            //对于jdk的方法就不再进行代理
            if (!AopUtil.isNoOpMethod(declaredMethod) && !noAi(declaredMethod) && !filterMethod(declaredMethod)) {
                //构建方法上下文
                AiMethodContext aiMethodContext = aiMethodContextBuilder.build(aiProxyStarter.getAiContext(), declaredMethod);
                aiMethodContext.setChatMemoryStore(aiProxyStarter.getChatMemoryStore());
                String key = AopUtil.getMethodKey(declaredMethod);
                map.put(key, aiMethodContext);
            }
        }
        return map;
    }

    /**
     * 这里需要检测返回值是否是要调用的chat工具的返回值类型
     *
     * @param declaredMethod
     * @return
     */
    private boolean noAi(Method declaredMethod) {
        if (declaredMethod.isAnnotationPresent(SystemMessage.class)) return false;
        return true;
    }

    /**
     * 因为注册器使用单线程进行注册扫描，这里不用加锁判断，甚至不用volatile
     *
     * @param interfaceClazz
     */
    @Override
    public void init(Class<?> interfaceClazz) {
        if (isInit) throw new IllegalStateException("Method interceptor has init.");
        this.methodContextMap = initMethodContexts(interfaceClazz, aiProxyStarter);
        isInit = true;
    }

    public abstract void doInit(AiContext aiContext);

    /**
     * 过滤不需要的method
     *
     * @param method
     * @return
     */
    public abstract boolean filterMethod(Method method);

    /**
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object process(Object proxy, Method method, Object[] args) {
        String key = AopUtil.getMethodKey(method);
        boolean isNoOp = methodContextMap == null || !methodContextMap.containsKey(key);
        if (isNoOp) {
            try {
                return method.invoke(proxy, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        AiMethodContext aiMethodContext = methodContextMap.get(key);
        aiMethodContext.setArgs(args);
        try {
            //所以这里必须检测是否返回值是String的
            Object result = doProcess(aiMethodContext);
            if (!(result instanceof String)) throw new IllegalStateException("返回值应当为String");
            ChatMemoryStore chatMemoryStore = aiProxyStarter.getChatMemoryStore();
            String memoryId = aiMethodContext.getMemoryId();
            chatMemoryStore.addMessage(new FullMessage(MessageRole.assistant, List.of(new Content("text", result.toString()))), memoryId);
            //如果成功执行，则将本次会话记忆化
            aiMethodContext.memory(result);
            return result;
        } finally {
            aiMethodContext.clear();
        }
    }

    public abstract Object doProcess(AiMethodContext aiMethodContext);
}
