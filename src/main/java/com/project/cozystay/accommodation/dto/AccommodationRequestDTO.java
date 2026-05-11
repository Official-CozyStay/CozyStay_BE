package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalTime;

public record AccommodationRequestDTO(
    @NotBlank(message = "숙소 제목은 필수입니다.")
    String title,

    String description,

    @NotNull(message = "숙소 타입은 필수입니다.")
    AccommodationType accommodationType,

    @NotBlank(message = "주소는 필수입니다.")
    String address,

    @NotBlank(message = "도시는 필수입니다.")
    String city,

    @NotBlank(message = "구/군은 필수입니다.")
    String district,

    String state,

    @NotBlank(message = "국가는 필수입니다.")
    String country,

    String postalCode,

    BigDecimal latitude,

    BigDecimal longitude,

    @NotNull(message = "최대 인원수는 필수입니다.")
    @Min(value = 1, message = "최대 인원수는 1명 이상이어야 합니다.")
    Integer maxGuests,

    @NotNull(message = "1박당 가격은 필수입니다.")
    @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다.")
    BigDecimal pricePerNight,

    BigDecimal cleaningFee,

    Boolean instantBooking,

    String checkInTime,

    String checkOutTime
) {
    public Accommodation toEntity(Long hostId) {
        LocalTime parsedCheckIn = (checkInTime != null && !checkInTime.isBlank())
                ? LocalTime.parse(checkInTime)
                : LocalTime.of(15, 0);

        LocalTime parsedCheckOut = (checkOutTime != null && !checkOutTime.isBlank())
                ? LocalTime.parse(checkOutTime)
                : LocalTime.of(11, 0);

        return Accommodation.builder()
                .hostId(hostId)
                .title(title)
                .description(description)
                .accommodationType(accommodationType)
                .address(address)
                .city(city)
                .district(district)
                .state(state)
                .country(country)
                .postalCode(postalCode)
                .latitude(latitude)
                .longitude(longitude)
                .maxGuests(maxGuests)
                .pricePerNight(pricePerNight)
                .cleaningFee(cleaningFee != null ? cleaningFee : BigDecimal.ZERO)
                .instantBooking(instantBooking != null ? instantBooking : false)
                .checkInTime(parsedCheckIn)
                .checkOutTime(parsedCheckOut)
                .build();
    }
}
