package com.project.cozystay.booking.guest.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BookingGuestCreateRequest {

    private Long guestUserId;

    private String guestName;

    @NotBlank(message = "guestEmail은 필수입니다.")
    @Email(message = "guestEmail 형식이 올바르지 않습니다.")
    private String guestEmail;

    @NotBlank(message = "guestPhone은 필수입니다.")
    private String guestPhone;

    @AssertTrue(message = "guestUserId 또는 guestName 중 하나는 반드시 존재해야 합니다.")
    public boolean isGuestIdentityValid(){
        boolean hasUserId = guestUserId != null;
        boolean hasName = guestName != null && !guestName.isBlank();
        return hasUserId || hasName;
    }
}
