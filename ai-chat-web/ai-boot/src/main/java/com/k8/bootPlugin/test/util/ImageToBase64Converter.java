package com.k8.bootPlugin.test.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class ImageToBase64Converter {
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("webp", "image/webp");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("tiff", "image/tiff");
    }

    /**
     * 将图片文件转换为Base64 Data URL
     *
     * @param filePath 图片文件路径
     * @return Base64 Data URL字符串
     * @throws IOException 如果文件读取失败
     * @throws IllegalArgumentException 如果文件不是图片或格式不支持
     */
    public static String convertToBase64DataUrl(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return convertToBase64DataUrl(path);
    }

    /**
     * 将图片文件转换为Base64 Data URL
     *
     * @param filePath 图片文件路径对象
     * @return Base64 Data URL字符串
     * @throws IOException 如果文件读取失败
     * @throws IllegalArgumentException 如果文件不是图片或格式不支持
     */
    public static String convertToBase64DataUrl(Path filePath) throws IOException {
        // 验证文件是否存在
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filePath);
        }

        // 验证是否为文件
        if (!Files.isRegularFile(filePath)) {
            throw new IOException("路径不是文件: " + filePath);
        }
        String fileName = filePath.getFileName().toString();
        byte[] fileContent = Files.readAllBytes(filePath);

        return doConvert(fileName, fileContent);

    }
    public static String convertToBase64DataUrl(File file) throws IOException {
        // 验证文件是否存在
        if (file == null || !file.exists()) {
            throw new IOException("文件不存在: ");
        }
        return convertToBase64DataUrl(file.toPath());
    }
    public static String doConvert(String fileName,byte[] fileContent){
        // 获取文件扩展名

        String fileExtension = getFileExtension(fileName).toLowerCase();

        // 验证是否为支持的图片格式
        if (!MIME_TYPES.containsKey(fileExtension)) {
            throw new IllegalArgumentException("不支持的图片格式: " + fileExtension);
        }

        // 读取文件内容

        // 获取MIME类型
        String mimeType = MIME_TYPES.get(fileExtension);

        // 创建Base64编码器
        Base64.Encoder encoder = Base64.getEncoder();

        // 构建Data URL
        return "data:" + mimeType + ";base64," + encoder.encodeToString(fileContent);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名（不带点）
     */
    private static String getFileExtension(String fileName) {
        Objects.requireNonNull(fileName, "文件名不能为空");

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * 检测文件是否为支持的图片格式
     *
     * @param filePath 文件路径
     * @return 如果是支持的图片格式返回true
     */
    public static boolean isSupportedImageFormat(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String fileExtension = getFileExtension(fileName).toLowerCase();
        return MIME_TYPES.containsKey(fileExtension);
    }
}
