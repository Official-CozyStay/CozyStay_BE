package com.project.cozystay.chat.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.chat.dto.*;
import com.project.cozystay.chat.service.ConversationFacade;
import com.project.cozystay.chat.service.MessageService;
import com.project.cozystay.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ChatController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final ConversationFacade conversationFacade;

    @PostMapping
    public ConversationCreateResponseDTO createOrGetConversation(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody ConversationCreateRequestDTO request
    ) {
        Long guestId = principal.getId();
        return conversationService.createOrGetConversation(request, guestId);
    }

    @GetMapping("/{conversationId}/messages")
    public ChatMessageListResponseDTO getMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomOAuth2User principal) {
        Long userId = principal.getId();
        return messageService.getMessages(conversationId, userId, before, size);
    }

    /**
     * 대화방 목록 조회
     */
    @GetMapping
    public List<ConversationResponseDTO> getConversations(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getId();
        return conversationFacade.getConversations(userId);
    }
}

