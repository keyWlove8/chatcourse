package com.k8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.k8.dto.ChatMessageDTO;
import com.k8.entity.ChatDetail;
import com.k8.entity.ChatHistory;
import com.k8.entity.ContextMessage;
import com.k8.entity.MultiChatMessage;
import com.k8.enums.KChatMessageType;
import com.k8.mapper.ChatDetailMapper;
import com.k8.mapper.ChatHistoryMapper;
import com.k8.mapper.ChatMessageMapper;
import com.k8.mapper.ContextMessageMapper;
import com.k8.metadata.ContentMetadata;
import com.k8.param.ImageContextPara;
import com.k8.service.*;
import com.k8.service.ai.DynamicChatService;
import com.k8.util.AuthUtil;
import com.k8.util.JsonUtil;
import com.k8.util.LocalUtil;
import com.k8.vo.*;
import dev.langchain4j.data.message.ChatMessage;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.k8.constants.Constants.*;
import static com.k8.constants.LocalUtilConstants.IMAGE_URL_KEY;

/**
 * 聊天服务实现类（示例，实际项目需对接AI模型）
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final static String USER_CHAT_MESSAGE_TYPE = "user";
    private final static String AI_CHAT_MESSAGE_TYPE = "ai";

    @Resource
    private RemoteStaticService remoteStaticService;

    @Resource
    private ContextMessageMapper contextMessageMapper;

    @Resource
    private ChatDetailMapper chatDetailMapper;

    @Resource
    private ChatHistoryMapper chatHistoryMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Resource
    private SpeechToTextService speechToTextService;

    @Resource
    private CharacterService characterService;

    @Resource
    private DynamicChatService dynamicChatService;

    @Resource
    private TextToSpeechService textToSpeechService;

    @Override
    public List<ChatHistoryVO> getUserChatHistory() {
        String userId = AuthUtil.getUserId();
        List<ChatHistory> chatHistories = chatHistoryMapper.selectHistoryByUserId(userId);
        if (CollectionUtils.isEmpty(chatHistories)) return Collections.emptyList();
        return chatHistories.stream()
                .map(chatHistory -> {
                    ChatHistoryVO chatHistoryVO = new ChatHistoryVO();
                    BeanUtils.copyProperties(chatHistory, chatHistoryVO);
                    return chatHistoryVO;
                }).toList();
    }


    /*public String processMessage(String content, String memoryId, String knowledgeId, MultipartFile image) {
        TextContent textContent = new TextContent(content);
        List<Content> contents = new LinkedList<>();
        contents.add(textContent);
        try {
            ImageContent imageContent = null;
            if (image != null) {
                ImageContextPara imageContextPara = new ImageContextPara();
                byte[] bytes = image.getBytes();
                imageContextPara.setData(bytes);
                imageContextPara.setFileName(image.getOriginalFilename());
                LocalUtil.setImageValue(imageContextPara);
                String url = remoteStaticService.sendImageAndGetUrl(imageContextPara);
                imageContent = new ImageContent(url);
                contents.add(imageContent);
            }
            if (StringUtils.hasText(knowledgeId)) {
                LocalUtil.setRagId(knowledgeId);
            }
            UserMessage userMessage = new UserMessage(contents);
            return baseChatService.chat(userMessage, memoryId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            LocalUtil.removeAll();
        }
    }*/

    @Override
    public ChatDetailVO getChatDetailByMemoryId(String memoryId) {
        ChatDetail chatDetail = chatDetailMapper.getChatDetailByMemoryId(memoryId);
        if (chatDetail == null) return null;
        QueryWrapper<ContextMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ContextMessage::getMemoryId, memoryId)
                .orderBy(true, true, ContextMessage::getItemIndex);
        List<ContextMessage> contextMessages = contextMessageMapper.selectList(queryWrapper);
        ChatDetailVO chatDetailVO = new ChatDetailVO();
        chatDetailVO.setMemoryId(memoryId);
        chatDetailVO.setCreateTime(chatDetail.getCreateTime());
        if (CollectionUtils.isEmpty(contextMessages)) {
            chatDetailVO.setMessages(Collections.emptyList());
        } else {
            List<ChatMessageVO> chatMessageVOS = contextMessages.stream()
                    .filter(message -> {
                        KChatMessageType type = message.getChatMessageType();
                        return KChatMessageType.USER.equals(type) || KChatMessageType.AI.equals(type);
                    })
                    .map(message -> {
                        ChatMessageVO chatMessageVO = new ChatMessageVO();
                        chatMessageVO.setTimestamp(message.getTimestamp());
                        chatMessageVO.setMemoryId(message.getMemoryId());
                        chatMessageVO.setKnowledgeId(message.getKnowledgeId());
                        Class<? extends ChatMessage> clazz = message.getChatMessageType().getMessageClass();
                        KChatMessageType type = KChatMessageType.getType(clazz);
                        if (KChatMessageType.USER.equals(type)) {
                            chatMessageVO.setType(USER_CHAT_MESSAGE_TYPE);
                        } else {
                            chatMessageVO.setType(AI_CHAT_MESSAGE_TYPE);
                        }
                        List<?> metaList = JsonUtil.toObject(message.getContentsMetadata(), List.class);
                        List<ContentVO> list = metaList.stream()
                                .map(obj -> {
                                    Map<String, Object> map = (Map<String, Object>) obj;
                                    String contentType = String.valueOf(map.get(TYPE_KEY));
                                    if (TEXT_CONTENT_TYPE.equals(contentType)) {
                                        return new ContentVO(TEXT_CONTENT_TYPE, String.valueOf(map.get(VALUE_KEY)));
                                    } else if (IMAGE_CONTENT_TYPE.equals(contentType)) {
                                        return new ContentVO(IMAGE_CONTENT_TYPE, String.valueOf(map.get(VALUE_KEY)));
                                    } else {
                                        throw new IllegalStateException("不支持的content类型");
                                    }
                                }).toList();
                        chatMessageVO.setContents(list);
                        return chatMessageVO;
                    }).toList();
            chatDetailVO.setMessages(chatMessageVOS);
        }
        return chatDetailVO;
    }

    @Transactional
    @Override
    public boolean addMessage(String memoryId, ChatMessageDTO chatMessageDTO) {
        if (chatMessageDTO == null) return false;
        Long timeStamp = System.currentTimeMillis();
        //添加到langchain4j上下文中
        addChainMessage(memoryId, chatMessageDTO, timeStamp);
        if ((KChatMessageType.AI.equals(chatMessageDTO.getChatMessageType()) && !StringUtils.hasText(chatMessageDTO.getMessageText())) || KChatMessageType.SYSTEM.equals(chatMessageDTO.getChatMessageType())) {
            return true;
        }
        String userId = AuthUtil.getUserId();
        ChatDetail chatDetail = chatDetailMapper.getChatDetailByMemoryId(memoryId);
        if (chatDetail == null) {
            chatDetail = new ChatDetail();
            chatDetail.setCreateTime(timeStamp);
            chatDetail.setCreatorId(userId);
            chatDetail.setMemoryId(memoryId);
            chatDetailMapper.insert(chatDetail);
        }

        ChatHistory chatHistory = chatHistoryMapper.getChatHistoryByMemoryId(memoryId);
        if (chatHistory == null) {
            chatHistory = new ChatHistory();
            chatHistory.setMemoryId(memoryId);
            chatHistory.setCreatorId(userId);
            chatHistory.setMessageCount(0);
            chatHistory.setLastTime(timeStamp);
            chatHistoryMapper.insert(chatHistory);
            chatHistory = chatHistoryMapper.getChatHistoryByMemoryId(memoryId);
        }

        addContextMessage(chatMessageDTO, memoryId, timeStamp);
        if (KChatMessageType.USER.equals(chatMessageDTO.getChatMessageType())) {
            chatHistory.setLastQuestion(chatMessageDTO.getMessageText());
        } else if (KChatMessageType.AI.equals(chatMessageDTO.getChatMessageType()) || KChatMessageType.TOOL_EXECUTION_RESULT.equals(chatMessageDTO.getChatMessageType())) {
            chatHistory.setLastAnswer(chatMessageDTO.getMessageText());
        }
        //todo 这里可能账户同时多个操作统计的不准确，暂时不管
        chatHistory.setMessageCount(chatHistory.getMessageCount() + 1);
        chatHistory.setLastTime(timeStamp);
        chatHistoryMapper.updateById(chatHistory);
        return true;
    }

    private void addContextMessage(ChatMessageDTO chatMessageDTO, String memoryId, Long timeStamp) {
        List<ContentMetadata> metadataList = new LinkedList<>();
        ContentMetadata textMetadata = new ContentMetadata(TEXT_CONTENT_TYPE, chatMessageDTO.getMessageText());
        metadataList.add(textMetadata);
        if (chatMessageDTO.getChatMessageType().equals(KChatMessageType.USER)) {
            Object imageContentObj = LocalUtil.getLocalParam(IMAGE_URL_KEY);
            if (imageContentObj != null) {
                metadataList.add(new ContentMetadata(IMAGE_CONTENT_TYPE, String.valueOf(imageContentObj)));
            }
        }
        String metadataJson = JsonUtil.objectToString(metadataList);
        ContextMessage contextMessage = new ContextMessage();
        contextMessage.setChatMessageType(chatMessageDTO.getChatMessageType());
        contextMessage.setContentsMetadata(metadataJson);
        contextMessage.setKnowledgeId(chatMessageDTO.getKnowledgeId());
        contextMessage.setTimestamp(timeStamp);
        contextMessage.setMemoryId(memoryId);
        contextMessageMapper.insert(contextMessage);
    }

    private void addChainMessage(String userId, ChatMessageDTO chatMessageDTO, long timeStamp) {
        MultiChatMessage multiChatMessage = new MultiChatMessage();
        multiChatMessage
                .setTimestamp(timeStamp)
                .setRealChatMessage(chatMessageDTO.getRealChatMessage())
                .setKnowledgeId(chatMessageDTO.getKnowledgeId())
                .setUserId(userId)
                .setMemoryId(chatMessageDTO.getMemoryId())
                .setChatMessageType(chatMessageDTO.getChatMessageType());

        chatMessageMapper.insert(multiChatMessage);
    }

    @Override
    public List<LangChainMessageVO> getLangChainMessagesByMemoryId(String memoryId) {
        List<MultiChatMessage> multiChatMessages = chatMessageMapper.selectChatMessagesByMemoryId(memoryId);
        if (CollectionUtils.isEmpty(multiChatMessages)) {
            return Collections.emptyList();
        }

        return multiChatMessages.stream()
                .map(multiChatMessage -> {
                    return new LangChainMessageVO(multiChatMessage.getChatMessageType(), multiChatMessage.getRealChatMessage());
                })
                .toList();
    }

    @Override
    public boolean deleteByMemoryId(String memoryId) {
        boolean b1 = chatMessageMapper.deleteByMemoryId(memoryId) > 0;
        boolean b2 = chatDetailMapper.deleteByMemoryId(memoryId);
        boolean b3 = chatHistoryMapper.deleteHistoryByMemoryId(memoryId);
        return b1 && b2 && b3;
    }

    @Override
    public VoiceChatReplyVO processVoiceMessage(MultipartFile audioFile, String characterId, String memoryId, String knowledgeId) {
        // 1. 语音转文本
        String transcribedText = speechToTextService.convert(audioFile);

        // 2. 调用文本处理逻辑（这会自动保存用户消息和AI回复到数据库）
        String aiReply = processMessageWithCharacter(transcribedText, memoryId, knowledgeId, null, characterId);

        // 3. 文本转语音
        String audioUrl = textToSpeechService.convertToSpeech(aiReply);

        // 4. 返回包含转录文本、AI回复文本和语音URL的结果
        VoiceChatReplyVO replyVO = new VoiceChatReplyVO();
        replyVO.setTranscribedText(transcribedText);
        replyVO.setAiReply(aiReply);
        replyVO.setAudioUrl(audioUrl);
        replyVO.setMemoryId(memoryId);
        replyVO.setTimestamp(System.currentTimeMillis());

        return replyVO;
    }

    @Override
    public String processMessageWithCharacter(String content, String memoryId, String knowledgeId, MultipartFile image, String characterId) {
        // 1. 获取角色信息
        CharacterVO character = characterService.getCharacterById(characterId);
        if (character == null) {
            throw new RuntimeException("角色不存在");
        }

        // 2. 增加角色热度
        characterService.increasePopularity(characterId);

        // 3. 构建用户消息
        try {
            // 4. 处理图片（如果有）
            if (image != null) {
                ImageContextPara imageContextPara = new ImageContextPara();
                byte[] bytes = image.getBytes();
                imageContextPara.setData(bytes);
                imageContextPara.setFileName(image.getOriginalFilename());
                LocalUtil.setImageValue(imageContextPara);
                String url = remoteStaticService.sendImageAndGetUrl(imageContextPara);
                LocalUtil.addLocalParam(IMAGE_URL_KEY, url);
            }

            // 5. 设置知识库（如果有）
            if (StringUtils.hasText(knowledgeId)) {
                LocalUtil.setRagId(knowledgeId);
            }

            String ruleInfo = String.format("名字：%s，角色描述：%s，角色性格：%s，角色背景：%s，说话风格：%s，扩展提示词：%s",
                    character.getName(), character.getDescription(), character.getPersonality(), character.getBackgroundStory(), character.getSpeakingStyle(), character.getSystemPrompt());
            return dynamicChatService.chat(content, memoryId, ruleInfo);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            LocalUtil.removeAll();
        }
    }

    @Override
    public String convertSpeechToText(MultipartFile audioFile) {
        return speechToTextService.convert(audioFile);
    }

    @Override
    public String convertTextToSpeech(String text) {
        return textToSpeechService.convertToSpeech(text);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Node {
        String type;
        Object content;
    }
}
    
