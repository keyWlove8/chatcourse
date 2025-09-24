package com.k8.file.util;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class  SizeUtil {
    private static final Pattern SIZE_PATTERN = Pattern.compile("^(\\d+\\.?\\d*)\\s*([a-zA-Z]+)$");
    private static final Map<String, Long> UNIT_MAP = new HashMap<>();

    static {
        // 初始化单位与字节的映射关系
        UNIT_MAP.put("B", 1L);
        UNIT_MAP.put("KB", 1024L);
        UNIT_MAP.put("MB", 1024L * 1024);
        UNIT_MAP.put("GB", 1024L * 1024 * 1024);
        // 支持单字母简写
        UNIT_MAP.put("K", 1024L);
        UNIT_MAP.put("M", 1024L * 1024);
        UNIT_MAP.put("G", 1024L * 1024 * 1024);
    }

    /**
     * 将数据大小字符串转换为字节数
     * @param sizeStr 格式如 "3MB"、"5.5GB"、"1024B"
     * @return 字节数
     * @throws IllegalArgumentException 输入格式无效时抛出异常
     */
    public static long toBytes(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        // 清理空格并统一转为大写
        String normalized = sizeStr.replaceAll("\\s", "").toUpperCase();
        Matcher matcher = SIZE_PATTERN.matcher(normalized);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid format: " + sizeStr);
        }

        // 解析数值和单位
        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);

        // 获取单位对应的字节数
        Long unitBytes = UNIT_MAP.get(unit);
        if (unitBytes == null) {
            String supportedUnits = String.join("/", UNIT_MAP.keySet());
            throw new IllegalArgumentException("Invalid unit: " + unit
                    + ". Supported units: " + supportedUnits);
        }

        // 计算并校验结果范围
        double result = value * unitBytes;
        if (result > Long.MAX_VALUE) {
            throw new IllegalArgumentException("Size exceeds maximum value: " + sizeStr);
        }

        return (long) result;
    }
}
