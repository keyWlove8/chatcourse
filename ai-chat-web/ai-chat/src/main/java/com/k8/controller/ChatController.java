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
     * 发送语音角色聊天消息
     */
    @PostMapping("/voice/character")
    public Result<UnifiedChatReplyVO> sendVoiceCharacterMessage(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("memoryId") String memoryId,
            @RequestParam("characterId") String characterId,
            @RequestParam(value = "knowledgeId", required = false) String knowledgeId
    ) {
        VoiceChatReplyVO voiceChatReplyVO = chatService.processVoiceMessage(audioFile, characterId, memoryId, knowledgeId);
        UnifiedChatReplyVO unifiedChatReplyVO = new UnifiedChatReplyVO();
        unifiedChatReplyVO.setReply(voiceChatReplyVO.getAiReply());
        unifiedChatReplyVO.setMessageType("voice");
        unifiedChatReplyVO.setTimestamp(System.currentTimeMillis());
        unifiedChatReplyVO.setAudioUrl(voiceChatReplyVO.getAudioUrl());
        unifiedChatReplyVO.setTranscribedText(voiceChatReplyVO.getTranscribedText());
        unifiedChatReplyVO.setMemoryId(voiceChatReplyVO.getMemoryId());
        return Result.success(unifiedChatReplyVO);
    }
}
