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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/booking-guests")
public class BookingGuestDecisionController {

    private final BookingGuestDecisionCommandService decisionService;

    @PatchMapping("/{bookingGuestId}/accept")
    public void accept(
            @PathVariable Long bookingGuestId,
            @AuthenticationPrincipal CustomOAuth2User user
            ){
        if(user == null) throw new AuthenticationRequiredException();
        decisionService.accept(bookingGuestId, user.getId());
    }

    @PatchMapping("/{bookingGuestId}/decline")
    public void decline(
            @PathVariable Long bookingGuestId,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null) throw new AuthenticationRequiredException();
        decisionService.decline(bookingGuestId, user.getId());
    }

}
