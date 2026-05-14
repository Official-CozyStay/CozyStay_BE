package com.project.cozystay.chat.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.chat.dto.*;
import com.project.cozystay.chat.service.ConversationFacade;
import com.project.cozystay.chat.service.MessageService;
import com.project.cozystay.chat.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Chat", description = "채팅(대화방 및 메시지) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ChatController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final ConversationFacade conversationFacade;

    @Operation(summary = "대화방 생성 또는 조회", description = "호스트와 게스트 간의 대화방을 생성하거나 기존 대화방을 조회합니다.")
    @PostMapping
    public ConversationCreateResponseDTO createOrGetConversation(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @Valid @RequestBody ConversationCreateRequestDTO request
    ) {
        Long guestId = principal.getId();
        return conversationService.createOrGetConversation(request, guestId);
    }

    @Operation(summary = "메시지 목록 조회", description = "특정 대화방의 메시지 내역을 조회합니다.")
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
    @Operation(summary = "내 대화방 목록 조회", description = "사용자가 참여 중인 모든 대화방 목록을 조회합니다.")
    @GetMapping
    public List<ConversationResponseDTO> getConversations(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getId();
        return conversationFacade.getConversations(userId);
    }
}

