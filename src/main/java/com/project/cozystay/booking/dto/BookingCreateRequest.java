package com.project.cozystay.booking.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingCreateRequest {

    private Long accommodationId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
}
