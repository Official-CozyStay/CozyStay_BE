package com.project.cozystay.booking.dto;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HostBookingListItemResponse {

    private Long bookingId;
    private Long accommodationId;
    private String accommodationTitle;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;

    private BookingStatus status;
    private BigDecimal totalPrice;

    private LocalDateTime createdAt;

    public static HostBookingListItemResponse from(Booking booking) {
        return new HostBookingListItemResponse(
                booking.getId(),
                booking.getAccommodation().getId(),
                booking.getAccommodation().getTitle(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfGuests(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCreatedAt()
        );
    }

}
