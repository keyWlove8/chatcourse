package com.k8.controller;

import com.k8.result.Result;
import com.k8.service.ChatService;
import com.k8.vo.ChatDetailVO;
import com.k8.vo.ChatHistoryVO;
import com.k8.vo.UnifiedChatReplyVO;
import com.k8.vo.VoiceChatReplyVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@RequestMapping("/chat")
@RestController
public class ChatController {

    @Resource
    private ChatService chatService;

    /**
     * 发送聊天消息
     */
    @PostMapping("/send")
    public Result<UnifiedChatReplyVO> sendChatMessage(
            @RequestParam("content") String content,
            @RequestParam("characterId") String characterId,
            @RequestParam(value = "knowledgeId", required = false) String knowledgeId,
            @RequestParam("memoryId") String memoryId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        String aiReply = chatService.processMessageWithCharacter(content, memoryId, knowledgeId, image, characterId);

        // 返回统一响应体
        UnifiedChatReplyVO replyVO = new UnifiedChatReplyVO();
        replyVO.setReply(aiReply);
        replyVO.setMemoryId(memoryId);
        replyVO.setTimestamp(System.currentTimeMillis());
        replyVO.setMessageType("text");

        return Result.success(replyVO);
    }

    @GetMapping("/history/list")
    public Result<List<ChatHistoryVO>> getChatHistoryList() {
        List<ChatHistoryVO> historyList = chatService.getUserChatHistory();
        return Result.success(historyList);
    }

    @GetMapping("/history/detail/{memoryId}")
    public Result<ChatDetailVO> getChatHistoryDetail(@PathVariable String memoryId) {
        ChatDetailVO detail = chatService.getChatDetailByMemoryId(memoryId);
        return Result.success(detail);
    }

    /**
     * 发送角色聊天消息
     */
    @PostMapping("/send/character")
    public Result<UnifiedChatReplyVO> sendCharacterMessage(
            @RequestParam("content") String content,
            @RequestParam("memoryId") String memoryId,
            @RequestParam(value = "knowledgeId", required = false) String knowledgeId,
            @RequestParam("characterId") String characterId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        String aiReply = chatService.processMessageWithCharacter(content, memoryId, knowledgeId, image, characterId);

        UnifiedChatReplyVO replyVO = new UnifiedChatReplyVO();
        replyVO.setReply(aiReply);
        replyVO.setMemoryId(memoryId);
        replyVO.setTimestamp(System.currentTimeMillis());
        replyVO.setMessageType("text");

        return Result.success(replyVO);
    }

    /**
     * 发送语音消息
     */
    @PostMapping("/voice")
    public Result<UnifiedChatReplyVO> sendVoiceMessage(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("characterId") String characterId,
            @RequestParam("memoryId") String memoryId,
            @RequestParam(value = "knowledgeId", required = false) String knowledgeId
    ) {
        VoiceChatReplyVO voiceReply = chatService.processVoiceMessage(audioFile, characterId, memoryId, knowledgeId);

        // 转换为统一响应体
        UnifiedChatReplyVO replyVO = new UnifiedChatReplyVO();
        replyVO.setReply(voiceReply.getAiReply());
        replyVO.setAudioUrl(voiceReply.getAudioUrl());
        replyVO.setTranscribedText(voiceReply.getTranscribedText());
        replyVO.setMemoryId(voiceReply.getMemoryId());
        replyVO.setTimestamp(voiceReply.getTimestamp());
        replyVO.setMessageType("voice");

        return Result.success(replyVO);
    }

    /**
     * 发送语音角色聊天消息
     */
    @PostMapping("/voice/character")
    public Result<UnifiedChatReplyVO> sendVoiceCharacterMessage(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("memoryId") String memoryId,
            @RequestParam("characterId") String characterId,
            @RequestParam(value = "knowledgeId", required = false) String knowledgeId
    ) {
        // 1. 语音转文本
        String transcribedText = chatService.convertSpeechToText(audioFile);

        // 2. 角色聊天处理
        String aiReply = chatService.processMessageWithCharacter(transcribedText, memoryId, knowledgeId, null, characterId);

        // 3. 文本转语音
        String audioUrl = chatService.convertTextToSpeech(aiReply);

        // 4. 返回统一响应体
        UnifiedChatReplyVO replyVO = new UnifiedChatReplyVO();
        replyVO.setReply(aiReply);
        replyVO.setAudioUrl(audioUrl);
        replyVO.setTranscribedText(transcribedText);
        replyVO.setMemoryId(memoryId);
        replyVO.setTimestamp(System.currentTimeMillis());
        replyVO.setMessageType("voice");

        return Result.success(replyVO);
    }
}
