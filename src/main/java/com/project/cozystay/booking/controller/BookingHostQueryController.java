package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.dto.HostBookingDetailResponse;
import com.project.cozystay.booking.dto.HostBookingListItemResponse;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.service.BookingHostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings/host")
public class BookingHostQueryController {

    private final BookingHostQueryService bookingHostQueryService;

    @GetMapping
    public ResponseEntity<List<HostBookingListItemResponse>> getHostBookings(
            @AuthenticationPrincipal CustomOAuth2User user){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        Long hostId = user.getId();
        return ResponseEntity.ok(bookingHostQueryService.getHostBookings(hostId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<HostBookingDetailResponse> getHostBookingDetail(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        Long hostId = user.getId();
        return ResponseEntity.ok(bookingHostQueryService.getHostBookingDetail(bookingId, hostId));
    }
}
