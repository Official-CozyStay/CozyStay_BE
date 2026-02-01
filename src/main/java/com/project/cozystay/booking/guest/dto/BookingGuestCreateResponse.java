package com.project.cozystay.booking.guest.dto;

import com.project.cozystay.booking.guest.domain.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingGuestCreateResponse {

    private Long bookingGuestId;
    private InvitationStatus invitationStatus;
    private LocalDateTime invitedAt;

}
