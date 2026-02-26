package com.project.cozystay.chat.controller;

import com.project.cozystay.chat.dto.ChatMessageRequestDTO;
import com.project.cozystay.chat.dto.ChatMessageResponseDTO;
import com.project.cozystay.chat.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWsController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트 SEND: /pub/conversations/{conversationId}/messages
     * 서버 브로드캐스트: /sub/conversations/{conversationId}
     */
    @MessageMapping("/conversations/{conversationId}/messages")
    public void send(@DestinationVariable Long conversationId,
                     @Valid ChatMessageRequestDTO request,
                     Authentication authentication) {
        Long senderId = (Long) authentication.getPrincipal();

        ChatMessageResponseDTO response = messageService.saveMessage(conversationId, request, senderId);

        messagingTemplate.convertAndSend(
                "/sub/conversations/" + conversationId,
                response
        );
    }
}
