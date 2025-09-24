package com.k8.config;

import com.k8.chatApi.rag.EmbeddingManger;
import com.k8.service.KnowledgeService;
import com.k8.chatApi.rag.KnowledgeResolver;
import com.k8.chatApi.rag.MultiContentRetriever;
import com.k8.chatApi.rag.impl.EmbeddingMangerImpl;
import com.k8.properties.PineconeProperties;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(PineconeProperties.class)
public class RagConfig {

    @Autowired
    PineconeProperties properties;


    @Bean
    public ContentRetriever contentRetriever2(KnowledgeService knowledgeService, EmbeddingManger embeddingManger) {
        return new MultiContentRetriever(knowledgeService, embeddingManger);
    }

    @Bean
    public EmbeddingManger embeddingManger(EmbeddingModel embeddingModel, KnowledgeService knowledgeService) {
        EmbeddingMangerImpl embeddingManger = new EmbeddingMangerImpl(properties, embeddingModel, remoteKnowledgeInitializer(), knowledgeService);
        knowledgeService.addEmbeddingManger(embeddingManger);
        return embeddingManger;
    }


    /*@Bean
    public EmbeddingStore<TextSegment> pineconeEmbeddingStore() {
        AttentionRagProperties.EmbeddingStoreConfig embeddingStore = attentionRagProperties.getEmbeddingStore();
        return PineconeEmbeddingStore.builder()
                .apiKey(embeddingStore.getApiKey())
                .projectId(embeddingStore.getProjectId())
                .index(embeddingStore.getIndex())
                                .build();
    }*/

    @Bean
    public KnowledgeResolver remoteKnowledgeInitializer() {
        return new KnowledgeResolver();
    }


}
