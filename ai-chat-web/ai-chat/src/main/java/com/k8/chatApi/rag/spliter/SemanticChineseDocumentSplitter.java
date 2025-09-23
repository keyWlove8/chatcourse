package com.k8.chatApi.rag.spliter;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 基于语义的中文文档分割器
 * 使用HanLP进行中文分词，支持智能分割和重叠
 * 
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
public class SemanticChineseDocumentSplitter implements DocumentSplitter {

    private final int maxSegmentLength;  // 最大片段长度（字符数）
    private final int minSegmentLength;  // 最小片段长度（字符数）
    private final int minFinalSegmentLength;  // 最后一段的最小长度（字符数）
    private final int overlapSize;       // 重叠大小（字符数）
    private final Segment segment;       // HanLP 分词实例
    private final double avgWordLength;  // 平均词长度
    
    // 中文句子结束标点符号：句号，问号，感叹号，分号，对短句子的保护
    private static final Pattern SENTENCE_DELIMITER =
            Pattern.compile("(?<=[。？！；])\\s*(?=\\S{2,})");  // 中文标点符号分割

    // 段落分割符：换行符，空格，制表符
    private static final Pattern PARAGRAPH_DELIMITER =
            Pattern.compile("\\n( {1,2}|\\t|\\n)+");

    /**
     * 构造函数
     * @param maxSegmentLength 最大片段长度
     * @param minSegmentLength 最小片段长度
     * @param overlapSize 重叠大小
     */
    public SemanticChineseDocumentSplitter(int maxSegmentLength, int minSegmentLength, int overlapSize) {
        this(maxSegmentLength, minSegmentLength, Math.max(2, minSegmentLength / 2), overlapSize, 2.5);
    }

    /**
     * 构造函数
     * @param maxSegmentLength 最大片段长度
     * @param minSegmentLength 最小片段长度
     * @param minFinalSegmentLength 最后一段的最小长度
     * @param overlapSize 重叠大小
     * @param avgWordLength 平均词长度
     */
    public SemanticChineseDocumentSplitter(int maxSegmentLength, int minSegmentLength,
                                           int minFinalSegmentLength, int overlapSize, double avgWordLength) {
        if (maxSegmentLength <= minSegmentLength) {
            throw new IllegalArgumentException("maxSegmentLength must be greater than minSegmentLength");
        }
        if (minFinalSegmentLength < 1 || minFinalSegmentLength > minSegmentLength) {
            throw new IllegalArgumentException("minFinalSegmentLength must be between 1 and minSegmentLength");
        }
        if (overlapSize < 0 || overlapSize >= maxSegmentLength) {
            throw new IllegalArgumentException("overlapSize must be between 0 and maxSegmentLength - 1");
        }
        if (avgWordLength <= 0) {
            throw new IllegalArgumentException("avgWordLength must be positive");
        }

        this.maxSegmentLength = maxSegmentLength;
        this.minSegmentLength = minSegmentLength;
        this.minFinalSegmentLength = minFinalSegmentLength;  // 记录最后一段的最小长度
        this.overlapSize = overlapSize;
        this.avgWordLength = avgWordLength;

        this.segment = HanLP.newSegment()
                .enableIndexMode(true)
                .enableNumberQuantifierRecognize(true)
                .enableCustomDictionary(false);
    }

    @Override
    public List<TextSegment> split(Document document) {
        String text = document.text();
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<TextSegment> segments = new ArrayList<>();
        String[] paragraphs = PARAGRAPH_DELIMITER.split(text);

        for (String paragraph : paragraphs) {
            String trimmedPara = paragraph.trim();
            if (trimmedPara.isEmpty()) continue;

            if (trimmedPara.length() >= minSegmentLength && trimmedPara.length() <= maxSegmentLength) {
                segments.add(TextSegment.from(trimmedPara, document.metadata()));
            } else if (trimmedPara.length() > maxSegmentLength) {
                segments.addAll(splitLongParagraph(trimmedPara, document));
            } else {
                mergeWithAdjacentSegment(trimmedPara, segments, document);
            }
        }

        // 合并最后一段
        mergeFinalShortSegment(segments);

        return segments;
    }

    /**
     * 分割长段落
     */
    private List<TextSegment> splitLongParagraph(String paragraph, Document document) {
        List<TextSegment> segments = new ArrayList<>();
        String[] sentences = SENTENCE_DELIMITER.split(paragraph);
        StringBuilder currentSegment = new StringBuilder();

        for (String sentence : sentences) {
            String trimmedSentence = sentence.trim();
            if (trimmedSentence.isEmpty()) continue;

            // 合并短句，避免单字符
            int potentialLength = currentSegment.length() + trimmedSentence.length();
            if (potentialLength <= maxSegmentLength) {
                currentSegment.append(trimmedSentence);
            } else {
                if (currentSegment.length() > 0) {
                    segments.add(TextSegment.from(currentSegment.toString(), document.metadata()));
                    currentSegment = new StringBuilder();
                }

                if (trimmedSentence.length() > maxSegmentLength) {
                    segments.addAll(splitByWords(trimmedSentence, document));
                } else {
                    currentSegment.append(trimmedSentence);
                }
            }
        }

        // 处理最后一段
        if (currentSegment.length() > 0) {
            segments.add(TextSegment.from(currentSegment.toString(), document.metadata()));
        }

        return segments;
    }

    /**
     * 按词分割长句子
     */
    private List<TextSegment> splitByWords(String sentence, Document document) {
        List<TextSegment> segments = new ArrayList<>();
        List<String> words = segment.seg(sentence).stream()
                .map(term -> term.word)
                .toList();

        StringBuilder currentSegment = new StringBuilder();
        for (String word : words) {
            if (currentSegment.length() + word.length() <= maxSegmentLength) {
                currentSegment.append(word);
            } else {
                if (currentSegment.length() > 0) {
                    segments.add(TextSegment.from(currentSegment.toString(), document.metadata()));
                    currentSegment = new StringBuilder();
                }
                currentSegment.append(word);
            }
        }

        if (currentSegment.length() > 0) {
            segments.add(TextSegment.from(currentSegment.toString(), document.metadata()));
        }

        return segments;
    }

    /**
     * 合并相邻的短片段
     */
    private void mergeWithAdjacentSegment(String shortParagraph, List<TextSegment> segments, Document document) {
        if (segments.isEmpty()) {
            segments.add(TextSegment.from(shortParagraph, document.metadata()));
            return;
        }

        TextSegment lastSegment = segments.get(segments.size() - 1);
        String lastText = lastSegment.text();
        
        if (lastText.length() + shortParagraph.length() <= maxSegmentLength) {
            // 合并到上一个片段
            String mergedText = lastText + shortParagraph;
            segments.set(segments.size() - 1, TextSegment.from(mergedText, document.metadata()));
        } else {
            // 创建新片段
            segments.add(TextSegment.from(shortParagraph, document.metadata()));
        }
    }

    /**
     * 合并最后的短片段
     */
    private void mergeFinalShortSegment(List<TextSegment> segments) {
        if (segments.size() < 2) return;

        TextSegment lastSegment = segments.get(segments.size() - 1);
        TextSegment secondLastSegment = segments.get(segments.size() - 2);

        if (lastSegment.text().length() < minFinalSegmentLength) {
            String mergedText = secondLastSegment.text() + lastSegment.text();
            segments.set(segments.size() - 2, TextSegment.from(mergedText, lastSegment.metadata()));
            segments.remove(segments.size() - 1);
        }
    }

    /**
     * 添加重叠
     */
    private void addOverlap(List<TextSegment> segments) {
        if (overlapSize <= 0 || segments.size() < 2) return;

        for (int i = 0; i < segments.size() - 1; i++) {
            TextSegment current = segments.get(i);
            TextSegment next = segments.get(i + 1);

            String currentText = current.text();
            String nextText = next.text();

            // 在末尾添加重叠内容
            if (currentText.length() + overlapSize <= maxSegmentLength) {
                String overlapText = nextText.substring(0, Math.min(overlapSize, nextText.length()));
                String newText = currentText + overlapText;
                segments.set(i, TextSegment.from(newText, current.metadata()));
            }
        }
    }
}
