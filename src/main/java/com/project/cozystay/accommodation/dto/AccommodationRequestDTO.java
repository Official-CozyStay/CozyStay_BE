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

    private String title;

    private String description;

    private AccommodationType accommodationType;

    private String address;

    private String province; // 도/광역시 (예: 경기도, 서울특별시)

    private String city;     // 시/군 (예: 성남시, 가평군)

    private String district; // 구 (예: 분당구)

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

    public Accommodation toEntity(Long hostId) {
        return Accommodation.builder()
                .hostId(hostId)
                .title(this.title)
                .description(this.description)
                .accommodationType(this.accommodationType)
                .address(this.address)
                .province(this.province)
                .city(this.city)
                .district(this.district)
                .state(this.state)
                .country(this.country)
                .postalCode(this.postalCode)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .maxGuests(this.maxGuests)
                .pricePerNight(this.pricePerNight)
                .cleaningFee(this.cleaningFee != null ? this.cleaningFee : BigDecimal.ZERO)
                .instantBooking(this.instantBooking != null ? this.instantBooking : false)
                .checkInTime(this.checkInTime)
                .checkOutTime(this.checkOutTime)
                .status(AccommodationStatus.DRAFT)
                .build();
    }
}
