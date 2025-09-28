package com.k8.chat.mcp.voice;

import com.fasterxml.jackson.databind.JsonNode;
import com.k8.chat.mcp.voice.properties.TtsProperties;
import com.k8.util.JsonUtil;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Test {
    //
    public static void main2(String[] args) {
        TtsProperties ttsProperties = new TtsProperties();
        ttsProperties.setModelName("qwen3-tts-flash");
        ttsProperties.setUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation");
        ttsProperties.setApiKey("sk-b49ba");
        TtsClient ttsClient = new TtsClient(ttsProperties);
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", ttsProperties.getModelName());
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("text", "鸡你太美");
        inputMap.put("voice", "Cherry");
        inputMap.put("language_type", "Chinese");
        bodyMap.put("input", inputMap);
        String bodyString = JsonUtil.objectToString(bodyMap);
        String response = ttsClient.convert(bodyString);
        JsonNode jsonNode = JsonUtil.parse(response);
        JsonNode output = jsonNode.get("output");
        if (output == null) throw new RuntimeException("转录失败");
        JsonNode audio = output.get("audio");
        if (audio == null) throw new RuntimeException("转录失败");
        JsonNode urlNode = audio.get("url");
        if (urlNode == null) throw new RuntimeException("转录失败");
        String audiUrl = urlNode.asText();
        // 下载音频文件到本地
        try (InputStream in = new URL(audiUrl).openStream();
             FileOutputStream out = new FileOutputStream("downloaded_audio.wav")) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("\n音频文件已下载到本地: downloaded_audio.wav");
        } catch (Exception e) {
            System.out.println("\n下载音频文件时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (InputStream in = new URL("http://dashscope-result").openStream();
             FileOutputStream out = new FileOutputStream("downloaded_audio.wav")) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("\n音频文件已下载到本地: downloaded_audio.wav");
        } catch (Exception e) {
            System.out.println("\n下载音频文件时出错: " + e.getMessage());
        }
    }
}
