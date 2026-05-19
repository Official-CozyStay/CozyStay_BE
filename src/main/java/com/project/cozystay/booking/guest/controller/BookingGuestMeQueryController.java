package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.dto.BookingGuestConnectionResponse;
import com.project.cozystay.booking.guest.dto.MyInvitationResponse;
import com.project.cozystay.booking.guest.service.BookingGuestConnectionQueryService;
import com.project.cozystay.booking.guest.service.BookingGuestMeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Booking Guest",  description = "동반 게스트 초대 목록 조회 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/booking-guests")
public class BookingGuestMeQueryController {

    private final BookingGuestMeQueryService bookingGuestMeQueryService;
    private final BookingGuestConnectionQueryService bookingGuestConnectionQueryService;

    @Operation(summary = "내 동반 게스트 초대 목록 조회", description = "내가 받은 모든 동반 게스트 초대 내역을 조회합니다.")
    @GetMapping("/me")
    public Page<MyInvitationResponse> getMyInvitations(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(required = false)InvitationStatus status,
            Pageable pageable
            ) {
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestMeQueryService.getMyInvitations(user.getId(), status, pageable);
    }

    @Operation(
            summary = "내 인연 목록 조회",
            description = "수락된 동반 게스트 관계를 기준으로 내 인연 목록을 조회합니다."
    )
    @GetMapping("/connections")
    public List<BookingGuestConnectionResponse> getConnections(
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestConnectionQueryService.getConnections(user.getId());
    }

    @Operation(
            summary = "내 동반자 예약 목록 조회",
            description = "내가 동반자로 초대받고 수락한 예약 목록을 조회합니다."
    )
    @GetMapping("/me/bookings")
    public List<BookingResponse> getMyCompanionBookings(
            @AuthenticationPrincipal CustomOAuth2User user
    ){
        if(user == null){
            throw new AuthenticationRequiredException();
        }

        return bookingGuestMeQueryService.getMyCompanionBookings(user.getId());
    }
}
