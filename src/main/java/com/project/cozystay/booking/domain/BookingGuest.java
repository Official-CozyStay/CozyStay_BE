package com.project.cozystay.booking.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_guests")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookingGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_guest_id")
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "guest_user_id")
    private Long guestUserId;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "geust_email", length = 255)
    private String geustEmail;

    @Column(name = "guest_phone", length = 20)
    private String guestPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false, length = 20)
    private InvitationStatus invitationStatus;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    @Column(name = "responsed_at")
    private LocalDateTime responsedAt;

    @PrePersist
    public void onCreate(){
        if(invitedAt == null){
            invitedAt = LocalDateTime.now();
        }
        if(invitationStatus == null){
            invitationStatus = InvitationStatus.PENDING;
        }
    }
}
