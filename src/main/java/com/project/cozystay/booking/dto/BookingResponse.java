package com.project.cozystay.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {

    private Long bookingId;
    private Long accommodationId;
    private Long guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private BigDecimal totalPrice;
    private String bookingStatus;

}
