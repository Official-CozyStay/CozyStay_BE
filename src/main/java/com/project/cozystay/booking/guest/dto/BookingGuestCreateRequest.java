package com.project.cozystay.booking.guest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BookingGuestCreateRequest {

    @NotBlank(message = "guestEmail은 필수입니다.")
    @Email(message = "guestEmail 형식이 올바르지 않습니다.")
    private String guestEmail;
}
