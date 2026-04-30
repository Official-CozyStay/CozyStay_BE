package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.service.BookingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Booking Query", description = "게스트의 예약 내역 조회 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingQueryController {

    private final BookingQueryService bookingQueryService;

    // 내 예약 목록
    @Operation(summary = "내 예약 목록 조회", description = "사용자의 예약 목록을 status 기준으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(required = false)BookingStatus status
            ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }
        List<BookingResponse> response = bookingQueryService.getMyBookings(user.getId(), status);
        return ResponseEntity.ok(response);
    }

    // 내 예약 상세
    @Operation(summary = "내 예약 상세 조회", description = "로그인한 사용자의 특정 예약 상세 정보를 조회합니다.")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getMyBookingDetail(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }
        BookingResponse response = bookingQueryService.getMyBookingDetail(bookingId, user.getId());
        return ResponseEntity.ok(response);
    }
}
