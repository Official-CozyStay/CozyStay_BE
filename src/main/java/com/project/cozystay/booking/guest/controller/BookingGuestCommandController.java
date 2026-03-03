package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.dto.BookingGuestCreateRequest;
import com.project.cozystay.booking.guest.dto.BookingGuestCreateResponse;
import com.project.cozystay.booking.guest.service.BookingGuestCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Booking Guest")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingGuestCommandController {

    private final BookingGuestCommandService bookingGuestCommandService;

    @Operation(summary = "동반 게스트 초대", description = "게스트가 다른 사용자를 동반 게스트로 초대합니다.")
    @PostMapping("/{bookingId}/guests")
    public BookingGuestCreateResponse inviteGuest(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody @Valid BookingGuestCreateRequest request
            ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestCommandService.invite(bookingId, user.getId(), request);
    }
}
