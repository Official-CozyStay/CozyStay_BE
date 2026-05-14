package com.project.cozystay.chat.service;

import com.project.cozystay.accommodation.service.AccommodationService;
import com.project.cozystay.chat.domain.Conversation;
import com.project.cozystay.chat.dto.ConversationCreateRequestDTO;
import com.project.cozystay.chat.dto.ConversationCreateResponseDTO;
import com.project.cozystay.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final AccommodationService accommodationService;

    public ConversationCreateResponseDTO createOrGetConversation(
            ConversationCreateRequestDTO request,
            Long guestId
    ) {
        Long accommodationId = request.getAccommodationId();
        Long requestHostId = request.getHostId();

        Long hostId = accommodationService.accommodationHostCheck(accommodationId, requestHostId);

        Conversation conversation = conversationRepository
                .findByAccommodationIdAndHostIdAndGuestId(accommodationId, hostId, guestId)
                .orElseGet(() -> conversationRepository.save(
                        Conversation.create(accommodationId, hostId, guestId)
                ));

        return ConversationCreateResponseDTO.builder()
                .conversationId(conversation.getId())
                .build();
    }

    public List<Conversation> getConversations(Long userId) {
        return conversationRepository.findByHostIdOrGuestId(userId, userId);
    }

}
