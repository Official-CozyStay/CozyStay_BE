package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.dto.BookingGuestListResponse;
import com.project.cozystay.booking.guest.service.BookingGuestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingGuestQueryController {

    private final BookingGuestQueryService bookingGuestQueryService;

    @GetMapping("/{bookingId}/guests")
    public List<BookingGuestListResponse> getGuests(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user
            ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestQueryService.getGuests(bookingId, user.getId());
    }


}
