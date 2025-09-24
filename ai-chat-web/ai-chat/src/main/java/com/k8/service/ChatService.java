package com.k8.service;


import com.k8.dto.ChatMessageDTO;
import com.k8.vo.ChatDetailVO;
import com.k8.vo.ChatHistoryVO;
import com.k8.vo.LangChainMessageVO;
import com.k8.vo.VoiceChatReplyVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 **/
public interface ChatService {
    // 获取AI回复（基于知识库）
    List<ChatHistoryVO> getUserChatHistory();

    //String processMessage(String content, String memoryId, String knowledgeId, MultipartFile image);
    
    // 新增：支持角色选择的聊天
    String processMessageWithCharacter(String content, String memoryId, String knowledgeId, MultipartFile image, String characterId);

    ChatDetailVO getChatDetailByMemoryId(String s);

    // 改为添加单个消息
    boolean addMessage(String memoryId, ChatMessageDTO chatMessageDTO);

    boolean deleteByMemoryId(String s);
    
    // 新增：专门用于大模型上下文的方法
    List<LangChainMessageVO> getLangChainMessagesByMemoryId(String memoryId);
    
    // 新增：语音聊天处理
    VoiceChatReplyVO processVoiceMessage(MultipartFile audioFile, String characterId, String memoryId, String knowledgeId);
    
    // 新增：便捷方法
    String convertSpeechToText(MultipartFile audioFile);
    String convertTextToSpeech(String text);
}
