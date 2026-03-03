package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.dto.BookingGuestListResponse;
import com.project.cozystay.booking.guest.service.BookingGuestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Booking Guest")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingGuestQueryController {

    private final BookingGuestQueryService bookingGuestQueryService;

    @Operation(summary = "예약별 동반 게스트 목록 조회", description = "특정 예약에 등록된 모든 동반 게스트 목록을 조회합니다.")
    @GetMapping("/{bookingId}/guests")
    public List<BookingGuestListResponse> getGuests(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User user
            ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestQueryService.getGuests(bookingId, user.getId());
    }


}
