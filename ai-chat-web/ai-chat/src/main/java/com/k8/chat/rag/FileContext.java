package com.k8.chat.rag;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class FileContext {
    private final String fileName;
    private final String fileType;
    private final String fileExtension;

    public FileContext(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileExtension = extractExtension(fileName);
    }

    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public String getFileExtension() { return fileExtension; }
}
