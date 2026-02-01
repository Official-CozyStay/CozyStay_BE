package com.project.cozystay.booking.guest.dto;

import com.project.cozystay.booking.guest.domain.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyInvitationResponse {

    private Long bookingGuestId;
    private Long bookingId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private InvitationStatus invitationStatus;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;

}
