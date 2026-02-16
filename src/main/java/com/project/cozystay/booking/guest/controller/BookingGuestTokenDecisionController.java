package com.project.cozystay.booking.guest.controller;

import com.project.cozystay.booking.guest.service.BookingGuestTokenDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking-guests/invitations")
public class BookingGuestTokenDecisionController {

    private final BookingGuestTokenDecisionService decisionService;

    @PostMapping("/{token}/accept")
    public void accept(@PathVariable String token) {
        decisionService.acceptByToken(token);
    }

    @PostMapping("/{token}/decline")
    public void decline(@PathVariable String token) {
        decisionService.declineByToken(token);
    }

}
