package com.project.cozystay.chat.dto;

import com.project.cozystay.chat.domain.Conversation;
import com.project.cozystay.chat.domain.Message;
import com.project.cozystay.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ConversationResponseDTO {
    private Long conversationId;
    private Long accommodationId;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserProfileImage;
    private String lastMessageText;
    private LocalDateTime lastMessageTime;

    public static ConversationResponseDTO of(Conversation conversation, User partner, Message lastMessage, Long partnerId) {
        return ConversationResponseDTO.builder()
                .conversationId(conversation.getId())
                .accommodationId(conversation.getAccommodationId())
                .otherUserId(partnerId)
                .otherUserName(partner != null ? partner.getNickName() : "알 수 없는 사용자")
                .otherUserProfileImage(partner != null ? partner.getProfileImageUrl() : null)
                .lastMessageText(lastMessage != null ? lastMessage.getMessageText() : "대화 내용이 없습니다.")
                .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : conversation.getCreatedAt())
                .build();
    }
}
