package com.project.cozystay.chat.service;

import com.project.cozystay.accommodation.service.AccommodationService;
import com.project.cozystay.chat.domain.Conversation;
import com.project.cozystay.chat.dto.ConversationCreateRequestDTO;
import com.project.cozystay.chat.dto.ConversationCreateResponseDTO;
import com.project.cozystay.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final AccommodationService accommodationService;

    @Transactional
    public ConversationCreateResponseDTO createOrGetConversation(
            ConversationCreateRequestDTO request,
            Long guestId
    ) {
        Long accommodationId = request.getAccommodationId();
        Long requestHostId = request.getHostId();

        accommodationService.accommodationHostCheck(accommodationId, requestHostId);

        Conversation conversation = conversationRepository
                .findByAccommodationIdAndHostIdAndGuestId(accommodationId, requestHostId, guestId)
                .orElseGet(() -> conversationRepository.save(
                        Conversation.create(accommodationId, requestHostId, guestId)
                ));

        return ConversationCreateResponseDTO.builder()
                .conversationId(conversation.getId())
                .build();
    }

    public List<Conversation> getConversations(Long userId) {
        return conversationRepository.findByHostIdOrGuestId(userId, userId);
    }

}
