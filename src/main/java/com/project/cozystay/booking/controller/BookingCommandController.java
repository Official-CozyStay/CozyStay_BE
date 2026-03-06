package com.project.cozystay.booking.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.booking.dto.BookingCreateRequest;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.exception.AuthenticationRequiredException;
import com.project.cozystay.booking.service.BookingCancelCommandService;
import com.project.cozystay.booking.service.BookingCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Booking", description = "예약 요청/취소 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingCommandController {

    private final BookingCommandService bookingCommandService;
    private final BookingCancelCommandService bookingCancelCommandService;

    @Operation(summary = "예약 생성", description = "게스트가 숙소를 예약합니다.")
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

    @Operation(summary = "예약 취소", description = "게스트가 예약을 취소합니다.")
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        if(principal == null){
            throw new AuthenticationRequiredException();
        }

        Long guestId = principal.getUser().getId();
        bookingCancelCommandService.cancel(bookingId, guestId);

        return ResponseEntity.noContent().build();
    }
}
