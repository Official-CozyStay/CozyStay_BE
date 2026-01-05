package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.dto.BookingCreateRequest;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.service.BookingCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingCommandController {

    private final BookingCommandService bookingCommandService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User principal
            ) {
        if(principal == null){
            throw new AuthenticationRequiredException();
        }

        Long guestId = principal.getUser().getId();

        BookingResponse response = bookingCommandService.createBooking(request, guestId);

        return ResponseEntity.created(URI.create("/api/bookings/" + response.getBookingId())).body(response);
    }
}
