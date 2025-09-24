package com.k8.bootPlugin.test.tools.impl;


import com.k8.bootPlugin.test.param.ImageContextPara;
import com.k8.bootPlugin.test.tools.Chat2ImageAdapterService;
import com.k8.bootPlugin.test.service.ImageService;
import com.k8.bootPlugin.test.util.ImageToBase64Converter;
import com.k8.bootPlugin.test.util.LocalUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
//@Component("chat2ImageAdapterService")
public class Chat2ImageAdapterServiceImpl implements Chat2ImageAdapterService {
    @Autowired
    ImageService imageService;

    @Tool("用于解析图片的工具")
    @Override
    public String chatImage(@P("用户的消息") String userMessage, @P("memoryId") String memoryId) {
        ImageContextPara data = LocalUtil.getData();
        if (data != null){
            String base64 = ImageToBase64Converter.doConvert(data.getFileName(), data.getData());
            return imageService.chatImage(userMessage, base64, memoryId);
        }
        return imageService.chatImageByMemory(userMessage,memoryId);
    }
}
