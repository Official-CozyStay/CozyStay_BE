package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.dto.MyInvitationResponse;
import com.project.cozystay.booking.guest.service.BookingGuestMeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/booking-guests")
public class BookingGuestMeQueryController {

    private final BookingGuestMeQueryService bookingGuestMeQueryService;

    @GetMapping("/me")
    public Page<MyInvitationResponse> getMyInvitations(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(required = false)InvitationStatus status,
            Pageable pageable
            ) {
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestMeQueryService.getMyInvitations(user.getId(), status, pageable);
    }
}
