package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.service.BookingHostDecisionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingHostDecisionController {

    private final BookingHostDecisionCommandService bookingHostDecisionCommandService;

    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<Void> confirm(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user){

        if(user == null){
            throw new AuthenticationRequiredException();
        }

        Long hostId = user.getId();
        bookingHostDecisionCommandService.confirm(bookingId, hostId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        Long hostId = user.getId();
        bookingHostDecisionCommandService.reject(bookingId, hostId);
        return ResponseEntity.noContent().build();
    }

}
