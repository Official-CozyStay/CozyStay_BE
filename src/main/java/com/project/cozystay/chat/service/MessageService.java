package com.project.cozystay.chat.service;


import com.project.cozystay.chat.domain.Conversation;
import com.project.cozystay.chat.domain.Message;
import com.project.cozystay.chat.dto.ChatMessageListResponseDTO;
import com.project.cozystay.chat.dto.ChatMessageRequestDTO;
import com.project.cozystay.chat.dto.ChatMessageResponseDTO;
import com.project.cozystay.chat.repository.ConversationRepository;
import com.project.cozystay.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.Jsoup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatMessageResponseDTO saveMessage(Long conversationId, ChatMessageRequestDTO request, Long senderId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 없습니다."));

        boolean isParticipant = senderId.equals(conversation.getHostId()) || senderId.equals(conversation.getGuestId());
        if (!isParticipant) {
            throw new IllegalArgumentException("전송자와 일치하는 채팅방 사용자가 없습니다.");
        }

        //XSS 취약점 방어를 위한 모든 HTML 태그 제거
        String cleanText = Jsoup.clean(request.getMessageText(), Safelist.none());

        Message message = Message.create(conversationId, senderId, cleanText);
        Message saved = messageRepository.save(message);

        // 메세지 전송 시, 보낸 사람의 마지막 읽은 메시지 업데이트
        conversation.updateLastReadMessage(senderId, saved.getId());

        return ChatMessageResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponseDTO getMessages(Long conversationId, Long userId, Long before, int size) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("메세지를 전송할 채팅방이 없습니다."));

        boolean isParticipant = userId.equals(conversation.getHostId())
                || userId.equals(conversation.getGuestId());
        if (!isParticipant) {
            throw new IllegalArgumentException("해당 대화방의 참여자가 아닙니다.");
        }

        List<Message> messages;
        if (before == null) {
            messages = messageRepository.findByConversationIdOrderByIdDesc(
                    conversationId, PageRequest.of(0, size)); //before 값이 없다면 가장 최근의 10개 메세지 반환
        } else {
            messages = messageRepository.findByConversationIdAndIdLessThanOrderByIdDesc(
                    conversationId, before, PageRequest.of(0, size));
        }

        List<ChatMessageResponseDTO> response = messages.stream()
                .map(ChatMessageResponseDTO::from)
                .toList();

        //다음 커서: 조회된 가장 오래된 메시지 ID (스크롤 시 before로 사용)
        Long nextCursor = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();

        //메세지가 더 있는지 없는지 알려주는 변수
        boolean hasMore = messages.size() == size;

        return ChatMessageListResponseDTO.builder()
                .messages(response)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    public Map<Long, Message> getLastMessages(List<Long> conversationIds) {

        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Message> lastMessages = messageRepository.findLastMessagesByConversationIds(conversationIds);

        return lastMessages.stream()
                .collect(Collectors.toMap(
                        Message::getConversationId,
                        message -> message,
                        (oldValue, newValue) -> newValue //중복 발생 시 최신 값 사용
                ));
    }

}
