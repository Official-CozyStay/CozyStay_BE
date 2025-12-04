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
@RequestMapping("/api/properties")
public class BookingAvailabilityController {

    private final BookingAvailabilityService bookingAvailabilityService;

    @GetMapping("/{propertyId}/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ){
        //propertyId = accommodationId
        AvailabilityResponse response = bookingAvailabilityService.getAvailability(propertyId, from, to);
        return ResponseEntity.ok(response);
    }
}
