package com.project.cozystay.chat.dto;

import com.project.cozystay.chat.domain.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponseDTO {
    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String messageText;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDTO from(Message message) {
        return ChatMessageResponseDTO.builder()
                .messageId(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .messageText(message.getMessageText())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
