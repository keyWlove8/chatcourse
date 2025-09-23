package com.k8.util;

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
 * @CreateTime: 2025-07-27
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
     * Base64 Data URL
     *
     * @param filePath
     * @return Base64 Data URL?
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static String convertToBase64DataUrl(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return convertToBase64DataUrl(path);
    }

    /**
     * Base64 Data URL
     *
     * @param filePath
     * @return Base64 Data URL
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static String convertToBase64DataUrl(Path filePath) throws IOException {
        //
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在 " + filePath);
        }

        //
        if (!Files.isRegularFile(filePath)) {
            throw new IOException(": " + filePath);
        }
        String fileName = filePath.getFileName().toString();
        byte[] fileContent = Files.readAllBytes(filePath);

        return doConvert(fileName, fileContent);

    }
    public static String convertToBase64DataUrl(File file) throws IOException {
        //
        if (file == null || !file.exists()) {
            throw new IOException("文件不存在");
        }
        return convertToBase64DataUrl(file.toPath());
    }
    public static String doConvert(String fileName,byte[] fileContent){

        String fileExtension = getFileExtension(fileName).toLowerCase();

        if (!MIME_TYPES.containsKey(fileExtension)) {
            throw new IllegalArgumentException("不支持的类型: " + fileExtension);
        }

        String mimeType = MIME_TYPES.get(fileExtension);


        Base64.Encoder encoder = Base64.getEncoder();
        return "data:" + mimeType + ";base64," + encoder.encodeToString(fileContent);
    }

    /**
     *
     *
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        Objects.requireNonNull(fileName, "文件名错�?");

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * ?
     *
     * @param filePath
     * @return true
     */
    public static boolean isSupportedImageFormat(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String fileExtension = getFileExtension(fileName).toLowerCase();
        return MIME_TYPES.containsKey(fileExtension);
    }
}
