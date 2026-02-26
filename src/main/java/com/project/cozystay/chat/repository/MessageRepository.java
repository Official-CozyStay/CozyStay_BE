package com.project.cozystay.chat.repository;

import com.project.cozystay.chat.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByIdDesc(Long conversationId, Pageable pageable);

    List<Message> findByConversationIdAndIdLessThanOrderByIdDesc(Long conversationId, Long beforeMessageId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.id IN " +
            "(SELECT MAX(m2.id) FROM Message m2 " +
            " WHERE m2.conversationId IN :ids " +
            " GROUP BY m2.conversationId)")
    List<Message> findLastMessagesByConversationIds(@Param("ids") List<Long> ids);
}
