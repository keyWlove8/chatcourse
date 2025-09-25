package com.k8.chat.rag;

import com.k8.entity.KnowledgeInfo;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

import java.io.File;
import java.io.InputStream;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface EmbeddingManger {
    Embedding embed(String text);

    EmbeddingSearchResult<TextSegment> search(String ragId, EmbeddingSearchRequest searchRequest);

    void uploadKnowledge(KnowledgeInfo knowledgeInfo, String fileName, InputStream inputStream);

    void uploadKnowledge(KnowledgeInfo knowledgeInfo, File file);

    void createNewContainer(KnowledgeInfo knowledgeInfo);
}
