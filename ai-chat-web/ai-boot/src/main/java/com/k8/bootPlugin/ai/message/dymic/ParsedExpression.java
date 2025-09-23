package com.k8.bootPlugin.ai.message.dymic;

import java.util.List;

/**
 * 解析后的表达式对象
 */
public class ParsedExpression {
    private final String originalString;  // 原始字符串
    private final List<String> parameters; // 提取的参数名列表
    private final boolean canReplace;     // 是否可以替换（是否包含参数）

    public ParsedExpression(String originalString, List<String> parameters) {
        this.originalString = originalString;
        this.parameters = parameters;
        this.canReplace = !parameters.isEmpty();
    }

    /**
     * 获取原始字符串
     */
    public String getOriginalString() {
        return originalString;
    }

    /**
     * 获取所有参数名列表
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * 判断是否可以替换（是否包含参数）
     */
    public boolean canReplace() {
        return canReplace;
    }

    /**
     * 获取值（如果不能替换则直接返回原始字符串）
     */
    public String getValue() {
        return originalString;
    }
}
