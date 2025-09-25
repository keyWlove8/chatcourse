package com.k8.chat.rag;

import com.k8.chat.rag.spliter.SemanticChineseDocumentSplitter;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class KnowledgeResolver {

    private final DocumentSplitter documentSplitter;

    public KnowledgeResolver() {
        this.documentSplitter = new SemanticChineseDocumentSplitter(50, 20, 10);
    }

    public List<TextSegment> loadAndSplitDocumentByStream(String fileName, InputStream inputStream) {
        try {
            Document content = DocumentLoaderFactory.parseDocument(fileName, inputStream);
            List<TextSegment> textSegments = generateEmbeddingsWithRetryByContent(List.of(content));
            return textSegments;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TextSegment> loadAndSplitDocumentByFile(File file) {
        try {
            Document content = DocumentLoaderFactory.parseDocument(file.getName(), new FileInputStream(file));
            List<TextSegment> textSegments = generateEmbeddingsWithRetryByContent(List.of(content));
            return textSegments;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<TextSegment> generateEmbeddingsWithRetryByContent(List<Document> contents) {
        List<TextSegment> segments = new ArrayList<>();
        for (Document document : contents) {
            List<TextSegment> tokenizedSegments = documentSplitter.split(document);
            segments.addAll(tokenizedSegments);
        }
        return segments;
    }

}
