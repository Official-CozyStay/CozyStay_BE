package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.AccommodationType;

import java.math.BigDecimal;
import java.time.LocalTime;

public record AccommodationRequestDTO (

    String title,
    String description,
    AccommodationType accommodationType,
    String address,
    String city,
    String district,
    String state,
    String country,
    String postalCode,
    BigDecimal latitude,
    BigDecimal longitude,
    Integer maxGuests,
    BigDecimal pricePerNight,
    BigDecimal cleaningFee,
    Boolean instantBooking,
    LocalTime checkInTime,
    LocalTime checkOutTime
){
}
