package com.k8.chatApi.rag.pinecone;

import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.*;
import dev.langchain4j.store.embedding.filter.logical.And;
import dev.langchain4j.store.embedding.filter.logical.Not;
import dev.langchain4j.store.embedding.filter.logical.Or;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.util.*;

/**
 * LangChain4j Filter 转换为 Pinecone 查询条件的工具类
 * 
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
public class Langchain4jFilterToPineconeConverter {

    /**
     * 将 LangChain4j 的 Filter 转换为 Pinecone 的查询条件
     * 
     * @param filter LangChain4j 的过滤器
     * @return Pinecone 的查询条件 Map
     */
    public static Map<String, Value> convertFilterToValueMap(Filter filter) {
        if (filter == null) {
            return new HashMap<>();
        }

        // 处理逻辑运算符：And
        if (filter instanceof And) {
            And and = (And) filter;
            Map<String, Value> leftMap = convertFilterToValueMap(and.left());
            Map<String, Value> rightMap = convertFilterToValueMap(and.right());

            List<Value> andValues = new ArrayList<>();
            andValues.add(Value.newBuilder().setStructValue(Struct.newBuilder().putAllFields(leftMap).build()).build());
            andValues.add(Value.newBuilder().setStructValue(Struct.newBuilder().putAllFields(rightMap).build()).build());

            Map<String, Value> result = new HashMap<>();
            result.put("$and", Value.newBuilder().setListValue(ListValue.newBuilder().addAllValues(andValues).build()).build());
            return result;
        }

        // 处理逻辑运算符：Or
        if (filter instanceof Or) {
            Or or = (Or) filter;
            Map<String, Value> leftMap = convertFilterToValueMap(or.left());
            Map<String, Value> rightMap = convertFilterToValueMap(or.right());

            List<Value> orValues = new ArrayList<>();
            orValues.add(Value.newBuilder().setStructValue(Struct.newBuilder().putAllFields(leftMap).build()).build());
            orValues.add(Value.newBuilder().setStructValue(Struct.newBuilder().putAllFields(rightMap).build()).build());

            Map<String, Value> result = new HashMap<>();
            result.put("$or", Value.newBuilder().setListValue(ListValue.newBuilder().addAllValues(orValues).build()).build());
            return result;
        }

        // 处理逻辑运算符：Not
        if (filter instanceof Not) {
            Not not = (Not) filter;
            Map<String, Value> childMap = convertFilterToValueMap(not.expression());
            // 返回{"$not": child}
            Map<String, Value> result = new HashMap<>();
            result.put("$not", Value.newBuilder()
                    .setStructValue(Struct.newBuilder().putAllFields(childMap).build())
                    .build());
            return result;
        }

        // 处理比较运算符：IsEqualTo（等于）
        if (filter instanceof IsEqualTo) {
            IsEqualTo eq = (IsEqualTo) filter;
            return createComparisonValueMap(eq.key(), "$eq", eq.comparisonValue());
        }

        // 处理比较运算符：IsNotEqualTo（不等于）
        if (filter instanceof IsNotEqualTo) {
            IsNotEqualTo ne = (IsNotEqualTo) filter;
            return createComparisonValueMap(ne.key(), "$ne", ne.comparisonValue());
        }

        // 处理比较运算符：IsGreaterThan（大于）
        if (filter instanceof IsGreaterThan) {
            IsGreaterThan gt = (IsGreaterThan) filter;
            return createComparisonValueMap(gt.key(), "$gt", gt.comparisonValue());
        }

        // 处理比较运算符：IsGreaterThanOrEqualTo（大于等于）
        if (filter instanceof IsGreaterThanOrEqualTo) {
            IsGreaterThanOrEqualTo gte = (IsGreaterThanOrEqualTo) filter;
            return createComparisonValueMap(gte.key(), "$gte", gte.comparisonValue());
        }

        // 处理比较运算符：IsLessThan（小于）
        if (filter instanceof IsLessThan) {
            IsLessThan lt = (IsLessThan) filter;
            return createComparisonValueMap(lt.key(), "$lt", lt.comparisonValue());
        }

        // 处理比较运算符：IsLessThanOrEqualTo（小于等于）
        if (filter instanceof IsLessThanOrEqualTo) {
            IsLessThanOrEqualTo lte = (IsLessThanOrEqualTo) filter;
            return createComparisonValueMap(lte.key(), "$lte", lte.comparisonValue());
        }

        // 处理比较运算符：IsIn（在列表中）
        if (filter instanceof IsIn) {
            IsIn in = (IsIn) filter;
            return createComparisonValueMap(in.key(), "$in", in.comparisonValues());
        }

        // 处理比较运算符：IsNotIn（不在列表中）
        if (filter instanceof IsNotIn) {
            IsNotIn nin = (IsNotIn) filter;
            return createComparisonValueMap(nin.key(), "$nin", nin.comparisonValues());
        }

        throw new IllegalArgumentException("不支持的Filter类型: " + filter.getClass().getName());
    }

    /**
     * 创建比较运算符的Map<String, Value>
     * 格式：{"key": {"$operator": value}} 转换为Value包装的结果
     */
    private static Map<String, Value> createComparisonValueMap(String key, String operator, Object value) {
        // 1. 包装基本值，例如 {"$eq": "pdf"} 转换为Struct
        Map<String, Value> operatorMap = new HashMap<>();
        operatorMap.put(operator, wrapValue(value)); // 值包装为Value

        // 2. 包装类型结构，例如 {"type": {"$eq": "pdf"}}
        Map<String, Value> result = new HashMap<>();
        result.put(key, Value.newBuilder()
                .setStructValue(Struct.newBuilder().putAllFields(operatorMap).build())
                .build());

        return result;
    }

    /**
     * 将Java对象转换为Pinecone的Value对象
     * 支持：字符串、布尔值、列表
     */
    private static Value wrapValue(Object value) {
        if (value == null) {
            return Value.newBuilder().setNullValue(com.google.protobuf.NullValue.NULL_VALUE).build();
        }

        if (value instanceof String) {
            return Value.newBuilder().setStringValue((String) value).build();
        }

        if (value instanceof Boolean) {
            return Value.newBuilder().setBoolValue((Boolean) value).build();
        }

        if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Long) {
                return Value.newBuilder().setNumberValue(((Number) value).longValue()).build();
            } else {
                return Value.newBuilder().setNumberValue(((Number) value).doubleValue()).build();
            }
        }

        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            List<Value> listValues = new ArrayList<>();
            for (Object item : collection) {
                listValues.add(wrapValue(item));
            }
            return Value.newBuilder().setListValue(ListValue.newBuilder().addAllValues(listValues).build()).build();
        }

        // 其他类型转换为字符串
        return Value.newBuilder().setStringValue(value.toString()).build();
    }
}
