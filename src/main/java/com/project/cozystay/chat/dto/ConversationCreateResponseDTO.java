package com.project.cozystay.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ConversationCreateResponseDTO {
    private Long conversationId;
}
