package com.project.cozystay.booking.guest.dto;

import com.project.cozystay.booking.guest.domain.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingGuestListResponse {

    private Long bookingGuestId;
    private Long guestUserId; // 가입자
    private String guestName; // 비가입자
    private String guestEmail;
    private String guestPhone;
    private InvitationStatus invitationStatus;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;

}
