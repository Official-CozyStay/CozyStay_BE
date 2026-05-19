package com.project.cozystay.booking.guest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingGuestConnectionResponse {

    private Long userId;
    private String nickName;
    private String profileImageUrl;
}
