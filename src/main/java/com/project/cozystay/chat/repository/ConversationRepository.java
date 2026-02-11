package com.project.cozystay.chat.repository;

import com.project.cozystay.chat.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByAccommodationIdAndHostIdAndGuestId(
            Long accommodationId,
            Long hostId,
            Long guestId
    );

    List<Conversation> findByHostIdOrGuestId(Long hostId, Long guestId);
}
