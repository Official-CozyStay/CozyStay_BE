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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking Decision (Host)", description = "호스트의 예약 수락/거절 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingHostDecisionController {

    private final BookingHostDecisionCommandService bookingHostDecisionCommandService;

    @Operation(summary = "예약 수락", description = "호스트가 들어온 예약을 수락합니다.")
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

    @Operation(summary = "예약 거절", description = "호스트가 들어온 예약을 거절합니다.")
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
