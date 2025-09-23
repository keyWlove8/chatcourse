package com.k8.chatApi.rag.impl;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.k8.chatApi.rag.EmbeddingManger;
import com.k8.chatApi.rag.KnowledgeResolver;
import com.k8.chatApi.rag.pinecone.Langchain4jFilterToPineconeConverter;
import com.k8.chatApi.rag.pinecone.PineconeHelper;
import com.k8.entity.KnowledgeInfo;
import com.k8.properties.PineconeProperties;
import com.k8.service.KnowledgeService;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.filter.Filter;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import io.pinecone.exceptions.PineconeException;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import io.pinecone.unsigned_indices_model.ScoredVectorWithUnsignedIndices;
import io.pinecone.unsigned_indices_model.VectorWithUnsignedIndices;
import org.openapitools.db_control.client.model.DeletionProtection;
import org.openapitools.db_control.client.model.IndexModel;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static dev.langchain4j.internal.Utils.randomUUID;
import static io.pinecone.commons.IndexInterface.buildUpsertVectorWithUnsignedIndices;
import static java.util.stream.Collectors.toList;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public class EmbeddingMangerImpl implements EmbeddingManger {
    private static final String DEFAULT_METRIC = "cosine";
    private static final int DEFAULT_DIMENSION = 1024;
    private static final String DEFAULT_CLOUD = "aws";
    private static final String DEFAULT_REGION = "us-east-1";
    private final Pinecone pineconeClient;
    private final EmbeddingModel embeddingModel;

    private final KnowledgeResolver knowledgeResolver;

    private final KnowledgeService knowledgeService;

    private final Map<String, Index> indexConnectionCache = new ConcurrentHashMap<>();

    private final String metadataTextKey;

    private static final String DEFAULT_METADATA_TEXT_KEY = "text_segment";

    private static final String DEFAULT_NAMESPACE = "default";
    private final String namespace;

    private final String metric;
    private final int dimension;
    private final String cloud;
    private final String region;

    public EmbeddingMangerImpl(PineconeProperties properties, EmbeddingModel embeddingModel, KnowledgeResolver knowledgeResolver, KnowledgeService knowledgeService) {
        this.embeddingModel = embeddingModel;
        this.knowledgeResolver = knowledgeResolver;
        this.knowledgeService = knowledgeService;
        this.pineconeClient = new Pinecone.Builder(properties.getApiKey())
                .withTlsEnabled(true)
                .build();
        this.metadataTextKey = properties.getMetadataTextKey() == null ? DEFAULT_METADATA_TEXT_KEY : properties.getMetadataTextKey();
        this.namespace = properties.getNameSpace() == null ? DEFAULT_NAMESPACE : properties.getNameSpace();
        this.metric = properties.getMetric() == null ? DEFAULT_METRIC : properties.getMetric();
        this.dimension = properties.getDimension() == null ? DEFAULT_DIMENSION : properties.getDimension();
        this.cloud = properties.getCloud() == null ? DEFAULT_CLOUD : properties.getCloud();
        this.region = properties.getRegion() == null ? DEFAULT_REGION : properties.getRegion();
    }


    @Override
    public Embedding embed(String text) {
        return embeddingModel.embed(text).content();
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(String ragId, EmbeddingSearchRequest request) {
        try {
            KnowledgeInfo knowledgeById = knowledgeService.getKnowledgeById(ragId);
            if (knowledgeById == null) {
                throw new RuntimeException("rag not exist, ragId: " + ragId);
            } else if (!isIndexExists(ragId)) {
                knowledgeService.deleteKnowledge(ragId);
                throw new RuntimeException("remote rag not exist, ragId: " + ragId);
            }
            Index index = getOrCreateIndexConnection(ragId);
            Embedding embedding = request.queryEmbedding();
            Filter langchainFilter = request.filter();
            QueryResponseWithUnsignedIndices response;
            if (Objects.isNull(langchainFilter)) {
                response = index.queryByVector(request.maxResults(), embedding.vectorAsList(), namespace, true, true);
            } else {
                Map<String, Value> metadataFilter = Langchain4jFilterToPineconeConverter.convertFilterToValueMap(request.filter());
                Struct structFilter = Struct.newBuilder().putAllFields(metadataFilter).build();
                response = index.queryByVector(request.maxResults(), embedding.vectorAsList(), namespace, structFilter, true, true);
            }

            List<ScoredVectorWithUnsignedIndices> matchesList = response.getMatchesList();
            List<EmbeddingMatch<TextSegment>> matches = matchesList.stream().map((indices) -> {
                return this.toEmbeddingMatch(indices, embedding);
            }).filter((match) -> {
                return match.score() >= request.minScore();
            }).sorted(Comparator.comparingDouble(EmbeddingMatch::score)).collect(Collectors.toList());
            Collections.reverse(matches);
            return new EmbeddingSearchResult(matches);

        } catch (PineconeException e) {
            throw new RuntimeException("agId: " + ragId + ", error: " + e.getMessage(), e);
        }
    }

    private EmbeddingMatch<TextSegment> toEmbeddingMatch(ScoredVectorWithUnsignedIndices indices, Embedding referenceEmbedding) {
        Map<String, Value> filedsMap = indices.getMetadata().getFieldsMap();
        Value textSegmentValue = (Value) filedsMap.get(this.metadataTextKey);
        Metadata metadata = Metadata.from(PineconeHelper.structToMetadata(filedsMap, this.metadataTextKey));
        Embedding embedding = Embedding.from(indices.getValuesList());
        double cosineSimilarity = CosineSimilarity.between(embedding, referenceEmbedding);
        return new EmbeddingMatch(RelevanceScore.fromCosineSimilarity(cosineSimilarity), indices.getId(), embedding, textSegmentValue == null ? null : TextSegment.from(textSegmentValue.getStringValue(), metadata));
    }

    @Override
    public void uploadKnowledge(KnowledgeInfo knowledgeInfo, String fileName, InputStream inputStream) {
        List<TextSegment> textSegments = knowledgeResolver.loadAndSplitDocumentByStream(fileName, inputStream);
        doUploadKnowledge(knowledgeInfo, textSegments);
    }

    @Override
    public void uploadKnowledge(KnowledgeInfo knowledgeInfo, File file) {
        List<TextSegment> textSegments = knowledgeResolver.loadAndSplitDocumentByFile(file);
        doUploadKnowledge(knowledgeInfo, textSegments);
    }

    /**
     * 创建知识库容器（使用知识库ID作为索引名称）
     */
    public void createNewContainer(KnowledgeInfo knowledgeInfo) {
        if (knowledgeInfo == null || knowledgeInfo.getId() == null) {
            throw new IllegalArgumentException("知识库信息不能为空");
        }

        String indexName = knowledgeInfo.getId(); // 使用知识库ID作为索引名称

        try {
            if (isIndexExists(indexName)) {
                return;
            }

            pineconeClient.createServerlessIndex(
                    indexName,
                    metric,
                    dimension,
                    cloud,
                    region,
                    DeletionProtection.DISABLED,
                    new HashMap<>()
            );

            // 等待Index就绪
            waitForIndexReady(indexName);
        } catch (PineconeException e) {
            // 分析Pinecone异常，提供更友好的错误信息
            String errorMessage = analyzePineconeError(e, knowledgeInfo.getId());
            throw new RuntimeException(errorMessage, e);
        } catch (InterruptedException e) {
            throw new RuntimeException("创建知识库容器失败（ID: " + knowledgeInfo.getId() + "）：等待索引就绪时被中断", e);
        }
    }

    /**
     * 分析Pinecone异常，提供更友好的错误信息
     */
    private String analyzePineconeError(PineconeException e, String knowledgeId) {
        String errorMessage = e.getMessage();
        
        if (errorMessage != null) {
            // 检查是否是索引数量限制错误
            if (errorMessage.contains("max serverless indexes allowed") || 
                errorMessage.contains("FORBIDDEN") || 
                errorMessage.contains("403")) {
                return "创建知识库失败：已达到Pinecone免费计划的最大索引数量限制（5个）。请删除不需要的知识库或升级Pinecone计划。知识库ID: " + knowledgeId;
            }
            
            // 检查是否是API密钥错误
            if (errorMessage.contains("Unauthorized") || errorMessage.contains("401")) {
                return "创建知识库失败：Pinecone API密钥无效或已过期。请检查配置。知识库ID: " + knowledgeId;
            }
            
            // 检查是否是项目ID错误
            if (errorMessage.contains("project") && errorMessage.contains("not found")) {
                return "创建知识库失败：Pinecone项目ID无效。请检查配置。知识库ID: " + knowledgeId;
            }
        }
        
        // 默认错误信息
        return "创建知识库容器失败（ID: " + knowledgeId + "）：" + errorMessage;
    }

    /**
     * 上传知识到指定知识库（使用知识库ID作为索引名称）
     */
    private void doUploadKnowledge(KnowledgeInfo knowledgeInfo, List<TextSegment> segments) {
        if (knowledgeInfo == null || knowledgeInfo.getId() == null) {
            throw new IllegalArgumentException("知识库信息不能为空");
        }
        String indexName = knowledgeInfo.getId(); // 使用知识库ID作为索引名称
        
        if (segments == null || segments.isEmpty()) {
            return;
        }

        try {
            Index index = getOrCreateIndexConnection(indexName);
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            List<String> ids = embeddings.stream()
                    .map(ignored -> randomUUID())
                    .collect(toList());
            addAllInternal(index, ids, embeddings, segments);
        } catch (PineconeException e) {
            throw new RuntimeException("找不到对应知识库: " + indexName + ", error: " + e.getMessage(), e);
        }
    }

    private void addAllInternal(Index index, List<String> ids, List<Embedding> embeddings, List<TextSegment> textSegments) {
        List<VectorWithUnsignedIndices> vectors = new ArrayList<>(embeddings.size());

        for (int i = 0; i < embeddings.size(); i++) {

            String id = ids.get(i);
            Embedding embedding = embeddings.get(i);

            Struct struct = null;
            if (textSegments != null) {
                TextSegment textSegment = textSegments.get(i);
                struct = metadataToStruct(textSegment, metadataTextKey);
            }

            vectors.add(buildUpsertVectorWithUnsignedIndices(id, embedding.vectorAsList(), null, null, struct));
        }

        index.upsert(vectors, namespace);
    }

    public static Struct metadataToStruct(TextSegment textSegment, String metadataTextKey) {
        Map<String, Object> metadata = textSegment.metadata().toMap();
        Struct.Builder metadataBuilder = Struct.newBuilder()
                .putFields(metadataTextKey, Value.newBuilder().setStringValue(textSegment.text()).build());
        if (!metadata.isEmpty()) {
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String || value instanceof UUID) {
                    metadataBuilder.putFields(key, Value.newBuilder().setStringValue(value.toString()).build());
                } else if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
                    metadataBuilder.putFields(key, Value.newBuilder().setNumberValue(((Number) value).doubleValue()).build());
                }
            }
        }

        return metadataBuilder.build();
    }

    /**
     * 获取或创建Index连接（并缓存�?
     */
    private Index getOrCreateIndexConnection(String indexName) {
        return indexConnectionCache.computeIfAbsent(indexName, name -> {
            try {
                return pineconeClient.getIndexConnection(name);
            } catch (PineconeException e) {
                throw new RuntimeException("获取Index连接失败: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 检查索引是否存在
     */
    private boolean isIndexExists(String indexName) {
        try {
            return pineconeClient.listIndexes().getIndexes().stream()
                    .anyMatch(index -> index.getName().equals(indexName));
        } catch (PineconeException e) {
            return false;
        }
    }

    private void waitForIndexReady(String indexName) throws InterruptedException {
        int retry = 5;
        while (true) {
            if (retry == 0) {
                throw new PineconeException("等待Index就绪超时");
            }
            try {
                IndexModel indexModel = pineconeClient.describeIndex(indexName);

                if (indexModel != null && indexModel.getStatus() != null) {
                    if (indexModel.getStatus().getReady()) break;
                }
            } catch (PineconeException e) {
                if (!e.getMessage().contains("not found")) {
                    throw e;
                }
            }
            Thread.sleep(1000);
            retry--;
        }
    }
}
