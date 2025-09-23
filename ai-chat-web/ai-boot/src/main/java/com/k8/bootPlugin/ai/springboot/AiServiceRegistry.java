package com.k8.bootPlugin.ai.springboot;


import com.k8.bootPlugin.ai.message.AiContext;
import com.k8.bootPlugin.ai.AiMethodInterceptorBuilder;
import com.k8.bootPlugin.ai.store.ChatMemoryStore;
import com.k8.bootPlugin.annotation.ToolService;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */

public class AiServiceRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanFactoryAware {
    private Environment env;
    private static String BASE_PACKAGE = "k8.ai.base-package";
    private static String GLOBAL_API_KEY = "k8.ai.global-api-key";
    private static String MODEL_KEY_2_API_KEY = "k8.ai.model-key-to-api-key";
    private static String EXTEND_PROPERTIES_KEY = "k8.ai.extend-properties";
    private BeanFactory beanFactory;

    public AiServiceRegistry() {

    }

    /**
     * 扫描指定包下所有标注了 @AS 的接口
     *
     * @return 所有标注了 @AS 的接口 Class 对象
     */

    private Set<Class<?>> scanASInterfaces() {
        String basepackage = env.getProperty(BASE_PACKAGE);
        Reflections reflections = new Reflections(basepackage);
        // 获取所有带有 @ToolService 注解的接口
        return reflections.getTypesAnnotatedWith(ToolService.class)
                .stream()
                .filter(Class::isInterface)  // 确保是接口
                .collect(Collectors.toSet());
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Class<?>> classes = scanASInterfaces();
        String apiKey = env.getProperty(GLOBAL_API_KEY);
        Map<String, String> model2ApiKey = Binder.get(env)
                .bind(MODEL_KEY_2_API_KEY, Bindable.mapOf(String.class, String.class))
                .orElse(new HashMap<>());
        Map<String, String> extendProperties = Binder.get(env)
                .bind(EXTEND_PROPERTIES_KEY, Bindable.mapOf(String.class, String.class))
                .orElse(new HashMap<>());

        for (Class<?> interfaceType : classes) {
            ToolService annotation = interfaceType.getAnnotation(ToolService.class);
            String modelName = annotation.modelName();
            String modelKey = annotation.modelKey();
            if (modelKey.isEmpty()) {
                modelKey = modelName;
            }
            if (model2ApiKey != null) {
                Object v = model2ApiKey.get(modelKey);
                if (v != null) {
                    apiKey = String.valueOf(v);
                }
            }
            if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(modelName)) throw new IllegalArgumentException();
            String builderName = annotation.interceptorBuilder();
            AiMethodInterceptorBuilder interceptorBuilder = beanFactory.getBean(builderName, AiMethodInterceptorBuilder.class);
            ChatMemoryStore chatMemoryStore = null;
            if (!(annotation.chatMemoryStore() == null || annotation.chatMemoryStore().isEmpty())) {
                chatMemoryStore = beanFactory.getBean(annotation.chatMemoryStore(), ChatMemoryStore.class);
            }
            if (interceptorBuilder == null) throw new IllegalStateException("");
            AiContext aiContext = new AiContext(apiKey, modelName, extendProperties);
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(interfaceType);
            // 关键：设置 FactoryBean，延迟生成代理对象
            beanDefinition.setBeanClassName(AiProxyFactoryBean.class.getName());
            ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
            constructorArgumentValues.addGenericArgumentValue(interfaceType);
            constructorArgumentValues.addGenericArgumentValue(aiContext);
            constructorArgumentValues.addGenericArgumentValue(interceptorBuilder);
            constructorArgumentValues.addGenericArgumentValue(chatMemoryStore);
            // 注册Bean（Bean名称默认使用类名首字母小写）
            String beanName = interfaceType.getSimpleName().substring(0, 1).toLowerCase()
                    + interfaceType.getSimpleName().substring(1);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}