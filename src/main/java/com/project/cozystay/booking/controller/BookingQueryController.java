package com.project.cozystay.booking.controller;

import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.service.BookingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @RequestParam Long guestId, // TODO : JWT에서 꺼내도록 바꾸기
            @RequestParam(required = false)BookingStatus status
            ){
        List<BookingResponse> response = bookingQueryService.getMyBookings(guestId, status);
        return ResponseEntity.ok(response);
    }

    // 내 예약 상세
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getMyBookingDetail(
            @PathVariable Long bookingId,
            @RequestParam Long guestId //TODO : JWT에서 꺼내도록 바꾸기
    ){
        BookingResponse response = bookingQueryService.getMyBookingDetail(bookingId, guestId);
        return ResponseEntity.ok(response);
    }
}
