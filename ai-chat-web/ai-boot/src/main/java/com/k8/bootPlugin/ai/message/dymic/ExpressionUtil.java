package com.k8.bootPlugin.ai.message.dymic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式处理工具类
 */
public class ExpressionUtil {
    // 匹配{{参数名}}格式的正则表达式
    private static final Pattern PATTERN = Pattern.compile("\\{\\{\\s*([^}]+?)\\s*\\}\\}");

    /**
     * 解析字符串，生成ParsedExpression对象
     * @param input 输入字符串
     * @return 解析后的对象
     */
    public static ParsedExpression parse(String input) {
        if (input == null || input.isEmpty()) {
            return new ParsedExpression(input, new ArrayList<>());
        }

        List<String> parameters = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(input);

        while (matcher.find()) {
            parameters.add(matcher.group(1));
        }

        return new ParsedExpression(input, parameters);
    }

    /**
     * 重载的getValue方法，根据替换参数获取替换后的字符串
     * @param expression 解析后的表达式对象
     * @param replacements 替换参数映射表
     * @return 替换后的字符串
     */
    public static String getValue(ParsedExpression expression, Map<String, String> replacements) {
        // 如果不能替换，直接返回原始字符串
        if (!expression.canReplace()) {
            return expression.getValue();
        }

        String result = expression.getOriginalString();
        Matcher matcher = PATTERN.matcher(result);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            // 获取替换值，如果没有则保留原始格式
            String replacement = replacements.getOrDefault(paramName, matcher.group(0));
            // 替换并处理特殊字符
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}