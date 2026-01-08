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
public class HostBookingDetailResponse {

    private Long bookingId;

    private Long accommodationId;
    private String accommodationTitle;

    private Long guestId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;

    private BookingStatus status;

    private BigDecimal totalPrice;

    private BigDecimal pricePerNightSnapshot;
    private BigDecimal cleaningFeeSnapshot;
    private BigDecimal serviceFeeSnapshot;
    private String currency;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;

    public static HostBookingDetailResponse from(Booking booking) {
        return new HostBookingDetailResponse(
                booking.getId(),
                booking.getAccommodation().getId(),
                booking.getAccommodation().getTitle(),
                booking.getGuestId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfGuests(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getPricePerNightSnapshot(),
                booking.getCleaningFeeSnapshot(),
                booking.getServiceFeeSnapshot(),
                booking.getCurrency(),
                booking.getCreatedAt(),
                booking.getUpdatedAt(),
                booking.getCancelledAt()
        );
    }

}
