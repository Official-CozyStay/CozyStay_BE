package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.booking.guest.service.BookingGuestTokenDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking Guest")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking-guests/invitations")
public class BookingGuestTokenDecisionController {

    private final BookingGuestTokenDecisionService decisionService;

    @Operation(summary = "초대 토큰으로 수락", description = "초대 링크(토큰)를 통해 동반 게스트 초대를 수락합니다.")
    @PostMapping("/{token}/accept")
    public void accept(@PathVariable String token) {
        decisionService.acceptByToken(token);
    }

    @Operation(summary = "초대 토큰으로 거절", description = "초대 링크(토큰)를 통해 동반 게스트 초대를 거절합니다.")
    @PostMapping("/{token}/decline")
    public void decline(@PathVariable String token) {
        decisionService.declineByToken(token);
    }

}
