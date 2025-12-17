package com.project.cozystay.booking.controller;

import com.project.cozystay.booking.dto.BookingCreateRequest;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.service.BookingCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingCommandController {

    private final BookingCommandService bookingCommandService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingCreateRequest request) {
        // TODO : 나중에 여기서 guestId는 JWT에서 꺼내서 세팅하도록 변경
        BookingResponse response = bookingCommandService.createBooking(request);
        return ResponseEntity.created(URI.create("/api/bookings/" + response.getBookingId())).body(response);
    }
}
