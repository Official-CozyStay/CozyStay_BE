package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.service.BookingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingQueryController {

    private final BookingQueryService bookingQueryService;

    // 내 예약 목록
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(required = false)BookingStatus status
            ){
        List<BookingResponse> response = bookingQueryService.getMyBookings(user.getId(), status);
        return ResponseEntity.ok(response);
    }

    // 내 예약 상세
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getMyBookingDetail(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        BookingResponse response = bookingQueryService.getMyBookingDetail(bookingId, user.getId());
        return ResponseEntity.ok(response);
    }
}
