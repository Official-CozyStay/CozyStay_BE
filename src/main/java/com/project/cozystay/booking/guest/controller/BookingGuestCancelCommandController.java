package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.service.BookingGuestCancelCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingGuestCancelCommandController {

    private final BookingGuestCancelCommandService bookingGuestCancelCommandService;

    @DeleteMapping("/{bookingId}/guests/{bookingGuestId}")
    public void cancelInvitation(
            @PathVariable Long bookingId,
            @PathVariable Long bookingGuestId,
            @AuthenticationPrincipal CustomOAuth2User user
            ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        bookingGuestCancelCommandService.cancelInvitation(bookingId, bookingGuestId, user.getId());

    }
}
