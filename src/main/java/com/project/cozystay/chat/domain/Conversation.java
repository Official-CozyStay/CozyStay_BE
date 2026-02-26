package com.project.cozystay.chat.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @Column(name = "accommodation_id")
    private Long accommodationId;

    //Todo : bookingId 활용
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(name = "last_read_message_id_host")
    private Long lastReadMessageIdHost;

    @Column(name = "last_read_message_id_guest")
    private Long lastReadMessageIdGuest;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Conversation create(Long accommodationId, Long hostId, Long guestId){
        if (accommodationId == null || hostId == null || guestId == null) {
            throw new IllegalArgumentException("Conversation 생성 필수값이 누락되었습니다.");
        }
        if (hostId.equals(guestId)) {
            throw new IllegalArgumentException("hostId와 guestId는 동일할 수 없습니다.");
        }
        return new Conversation(accommodationId, hostId, guestId);
    }

    private Conversation(Long accommodationId,  Long hostId, Long guestId){
        this.accommodationId = accommodationId;
        this.hostId = hostId;
        this.guestId = guestId;
    }

    public void updateLastReadMessage(Long senderId, Long messageId){
        if (this.hostId.equals(senderId)) {
            this.lastReadMessageIdHost = messageId;
        } else if (this.guestId.equals(senderId)) {
            this.lastReadMessageIdGuest = messageId;
        }
    }

    public Long getPartnerId(Long currentUserId){
        return this.hostId.equals(currentUserId) ? this.guestId : this.hostId;
    }

}
