package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationStatus;
import com.project.cozystay.accommodation.domain.AccommodationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestDTO {
    private Long hostId;

    private String title;

    private String description;

    private AccommodationType accommodationType;

    private String address;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer maxGuests;

    private BigDecimal pricePerNight;

    private BigDecimal cleaningFee;

    private BigDecimal serviceFeePercentage;

    private Boolean instantBooking;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    public Accommodation toEntity() {
        return Accommodation.builder()
                .hostId(this.hostId)
                .title(this.title)
                .description(this.description)
                .accommodationType(this.accommodationType)
                .address(this.address)
                .city(this.city)
                .state(this.state)
                .country(this.country)
                .postalCode(this.postalCode)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .maxGuests(this.maxGuests)
                .pricePerNight(this.pricePerNight)
                .cleaningFee(this.cleaningFee != null ? this.cleaningFee : BigDecimal.ZERO)
                .serviceFeePercentage(this.serviceFeePercentage != null ? this.serviceFeePercentage : BigDecimal.ZERO)
                .instantBooking(this.instantBooking != null ? this.instantBooking : false)
                .checkInTime(this.checkInTime)
                .checkOutTime(this.checkOutTime)
                .status(AccommodationStatus.DRAFT)
                .build();
    }
}
