package com.k8.chat.tools.impl;

import com.k8.chat.mcp.search.SearchMCPClient;
import com.k8.chat.tools.SearchToolService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Service
public class SearchToolServiceImpl implements SearchToolService {
    @Autowired
    private SearchMCPClient searchMcpClient;

    @Value("${k8.mcp.search.api-key}")
    private String apiKey;

    @Value("${k8.mcp.search.url}")
    private String url;

    @Value("${k8.mcp.search.model-name}")
    private String modelName;

    @Override
    @Tool("这是一个搜索工具，当用户需要查询相关信息的时候调用")
    public String query(@P("userMessage") String userMessage) throws Exception {
        return searchMcpClient.callSearch(userMessage, apiKey, modelName, url);
    }
}
