package com.project.cozystay.booking.controller;

import com.project.cozystay.booking.dto.AvailabilityResponse;
import com.project.cozystay.booking.service.BookingAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class BookingAvailabilityController {

    private final BookingAvailabilityService bookingAvailabilityService;

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
}
