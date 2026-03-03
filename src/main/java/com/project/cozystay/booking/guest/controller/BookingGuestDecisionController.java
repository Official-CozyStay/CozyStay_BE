package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.service.BookingGuestDecisionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking Guest")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/booking-guests")
public class BookingGuestDecisionController {

    private final BookingGuestDecisionCommandService decisionService;

    @Operation(summary = "동반 게스트 초대 수락", description = "초대받은 사용자가 동반 게스트 초대를 수락합니다.")
    @PatchMapping("/{bookingGuestId}/accept")
    public void accept(
            @PathVariable Long bookingGuestId,
            @AuthenticationPrincipal CustomOAuth2User user
            ){
        if(user == null) throw new AuthenticationRequiredException();
        decisionService.accept(bookingGuestId, user.getId());
    }

    @Operation(summary = "동반 게스트 초대 거절", description = "초대받은 사용자가 동반 게스트 초대를 거절합니다.")
    @PatchMapping("/{bookingGuestId}/decline")
    public void decline(
            @PathVariable Long bookingGuestId,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null) throw new AuthenticationRequiredException();
        decisionService.decline(bookingGuestId, user.getId());
    }

}
