package com.k8.chat.rag;

import com.k8.enums.KChatMessageType;
import com.k8.service.KnowledgeService;
import com.k8.util.LocalUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */

public class MultiContentRetriever implements ContentRetriever {
    private final KnowledgeService knowledgeService;
    private final EmbeddingManger embeddingManger;

    private static int DEFAULT_MAX_RESULTS = 10;

    private static double DEFAULT_MIN_SCORE = 0.8;

    private int maxResults = DEFAULT_MAX_RESULTS;

    private double minScore = DEFAULT_MIN_SCORE;

    public MultiContentRetriever(KnowledgeService knowledgeService, EmbeddingManger embeddingManger) {
        this.knowledgeService = knowledgeService;
        this.embeddingManger = embeddingManger;
    }

    @Override
    public List<Content> retrieve(Query query) {
        String ragId = LocalUtil.getRagId();
        if (ragId != null) {
            Embedding embeddedQuery = null;
            for (ChatMessage chatMessage : query.metadata().chatMemory()) {
                if (ChatMessageType.USER.equals(chatMessage.type())) {
                    UserMessage userMessage = (UserMessage) chatMessage;
                    List<dev.langchain4j.data.message.Content> contents = userMessage.contents();
                    for (dev.langchain4j.data.message.Content content : contents) {
                        if (ContentType.TEXT.equals(content.type())) {
                            TextContent textContent = (TextContent) content;
                            embeddedQuery = embeddingManger.embed(textContent.text());
                        }
                    }
                }
            }
            if (embeddedQuery == null) return Collections.emptyList();

            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(embeddedQuery)
                    .maxResults(maxResults)
                    .minScore(minScore)
                    .filter(null)//todo 暂时没有过滤条件
                    .build();

            EmbeddingSearchResult<TextSegment> searchResult = embeddingManger.search(ragId, searchRequest);

            return searchResult.matches().stream()
                    .map(EmbeddingMatch::embedded)
                    .map(Content::from)
                    .collect(toList());
        }
        return Collections.emptyList();
    }
}
