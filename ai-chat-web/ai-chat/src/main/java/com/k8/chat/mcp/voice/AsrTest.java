package com.k8.chat.mcp.voice;

import com.k8.chat.mcp.voice.properties.AsrProperties;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * ASR测试类
 * 直接使用桌面的downloaded_audio.wav文件进行测试
 * @Author: k8
 * @CreateTime: 2025-01-20
 * @Version: 1.0
 */
public class AsrTest {
    
    public static void main(String[] args) {
        System.out.println("=== ASR测试开始 ===");
        
        try {
            // 1. 创建配置
            AsrProperties asrProperties = new AsrProperties();
            asrProperties.setApiKey("sk-b49bab490292431a8ecfce2c6c2f31b9");
            asrProperties.setModelName("paraformer-realtime-v2");
            asrProperties.setUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc/speech-recognition/generation");
            
            // 2. 创建ASR客户端
            AsrClient asrClient = new AsrClient(asrProperties);
            
            // 3. 使用桌面的音频文件
            String desktopPath = System.getProperty("user.home") + "/Desktop/asr_input_wav.wav";
            File audioFile = new File(desktopPath);
            
            if (!audioFile.exists()) {
                System.err.println("音频文件不存在: " + desktopPath);
                return;
            }
            
            System.out.println("开始识别音频文件: " + audioFile.getAbsolutePath());
            System.out.println("文件大小: " + audioFile.length() + " bytes");
            
            // 4. 读取音频文件并转换为字节数组
            byte[] audioData = java.nio.file.Files.readAllBytes(audioFile.toPath());
            
            // 5. 设置超时机制
            long startTime = System.currentTimeMillis();
            
            // 6. 调用ASR识别
            String result = asrClient.convert(audioData, "wav");
            
            long endTime = System.currentTimeMillis();
            
            // 7. 输出结果
            System.out.println("识别结果: " + result);
            System.out.println("耗时: " + (endTime - startTime) + "ms");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("=== ASR测试结束 ===");
            
            // 强制退出，避免线程挂起
            System.out.println("程序即将退出...");
            try {
                Thread.sleep(1000); // 等待1秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(0);
        }
    }
}
