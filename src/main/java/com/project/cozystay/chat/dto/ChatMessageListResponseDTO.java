package com.project.cozystay.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageListResponseDTO {
    private List<ChatMessageResponseDTO> messages;
    private Long nextCursor; //다음 메세지의 번호
    private boolean hasMore; //메세지가 더 있는지 여부
}
