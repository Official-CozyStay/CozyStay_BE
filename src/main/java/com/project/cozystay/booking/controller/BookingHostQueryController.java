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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Booking Query (Host)", description = "호스트의 예약 내역 조회 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings/host")
public class BookingHostQueryController {

    private final BookingHostQueryService bookingHostQueryService;

    @Operation(summary = "호스트 예약 목록 조회", description = "호스트가 자신의 숙소에 들어온 모든 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<HostBookingListItemResponse>> getHostBookings(
            @AuthenticationPrincipal CustomOAuth2User user){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        Long hostId = user.getId();
        return ResponseEntity.ok(bookingHostQueryService.getHostBookings(hostId));
    }

    @Operation(summary = "호스트 예약 상세 조회", description = "호스트가 특정 예약의 상세 정보를 조회합니다.")
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
