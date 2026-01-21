package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.dto.AvailabilityResponse;
import com.project.cozystay.booking.dto.AvailabilityUpdateRequest;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.service.BookingAvailabilityCommandService;
import com.project.cozystay.booking.service.BookingAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class BookingAvailabilityController {

    private final BookingAvailabilityService bookingAvailabilityService;
    private final BookingAvailabilityCommandService bookingAvailabilityCommandService;

    // ====== 조회 ======
    @GetMapping("/{accommodationId}/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable("accommodationId") Long accommodationId,
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ){
        AvailabilityResponse response = bookingAvailabilityService.getAvailability(accommodationId, from, to);
        return ResponseEntity.ok(response);
    }

    // ====== 설정 (호스트) ======
    @PutMapping("/{accommodationId}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long accommodationId,
            @RequestBody AvailabilityUpdateRequest request,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null) throw new AuthenticationRequiredException();

        bookingAvailabilityCommandService.updateAvailability(
                accommodationId,
                user.getId(),
                request
        );

        return ResponseEntity.noContent().build();
    }
}
