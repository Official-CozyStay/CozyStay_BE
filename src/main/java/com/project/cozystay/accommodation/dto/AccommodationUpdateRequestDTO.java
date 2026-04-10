package com.project.cozystay.accommodation.dto;


import com.project.cozystay.accommodation.domain.AccommodationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationUpdateRequestDTO {
    private String title;
    private String description;
    private AccommodationType accommodationType;
    private String address;

    private String city;

    private String district;

    private String state;
    private String country;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer maxGuests;
    private BigDecimal pricePerNight;
    private BigDecimal cleaningFee;
    private Boolean instantBooking;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;

}
