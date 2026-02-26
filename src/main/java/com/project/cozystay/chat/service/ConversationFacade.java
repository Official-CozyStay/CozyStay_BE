package com.project.cozystay.chat.service;

import com.project.cozystay.chat.domain.Conversation;
import com.project.cozystay.chat.domain.Message;
import com.project.cozystay.chat.dto.ConversationResponseDTO;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConversationFacade {
    private final ConversationService conversationService;
    private final UserService userService;
    private final MessageService messageService;

    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getConversations(Long userId) {
        List<Conversation> conversations = conversationService.getConversations(userId);
        if (conversations.isEmpty()) {
            return List.of();
        }
        List<Long> conversationIds = conversations.stream()
                .map(Conversation::getId)
                .toList();
        List<Long> partnerIds = conversations.stream()
                .map(c -> c.getPartnerId(userId))
                .toList();

        Map<Long, User> userMap = userService.getUsers(partnerIds);
        Map<Long, Message> lastMessageMap = messageService.getLastMessages(conversationIds);

        return conversations.stream()
                .map(conversation -> {
                    Long partnerId = conversation.getPartnerId(userId);
                    return ConversationResponseDTO.of(
                            conversation,
                            userMap.get(partnerId),
                            lastMessageMap.get(conversation.getId()),
                            partnerId
                    );
                })
                .toList();
    }
}
