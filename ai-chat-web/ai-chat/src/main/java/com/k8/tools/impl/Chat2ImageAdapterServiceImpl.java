package com.k8.tools.impl;


import com.k8.param.ImageContextPara;
import com.k8.tools.Chat2ImageAdapterService;
import com.k8.tools.ImageService;
import com.k8.util.ImageToBase64Converter;
import com.k8.util.JsonUtil;
import com.k8.util.LocalUtil;
import com.k8.util.UserMessageParseUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ContentType;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Component("chat2ImageAdapterService")
public class Chat2ImageAdapterServiceImpl implements Chat2ImageAdapterService {
    @Resource
    ImageService imageService;

    @Tool("用于处理图片的工具。调用此工具后，请直接给出最终答案，不要继续调用其他工具。此工具会返回完整的图片分析结果。")
    @Override
    public String chatImage(@P("userMessage") String userMessage, @P("memoryId") String memoryId) {
        // 从ThreadLocal获取图片数据
        ImageContextPara data = LocalUtil.getImageData();
        if (data != null){
            String base64 = ImageToBase64Converter.doConvert(data.getFileName(), data.getData());
            // 提取文本内容
            return imageService.chatImage(userMessage, base64, memoryId);
        }
        // 如果没有图片，从消息中提取文本内容
        return "本次请求没用到图片";
    }
}
