package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.AccommodationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalTime;

public record AccommodationUpdateRequestDTO(

        @NotBlank(message = "숙소 제목은 필수입니다.")
        String title,

        String description,

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

        @jakarta.validation.constraints.DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @jakarta.validation.constraints.DecimalMax(value = "90.0", message = "위도는 90.0 이하이어야 합니다.")
        BigDecimal latitude,

        @jakarta.validation.constraints.DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @jakarta.validation.constraints.DecimalMax(value = "180.0", message = "경도는 180.0 이하이어야 합니다.")
        BigDecimal longitude,

        @Min(value = 1, message = "최대 인원수는 1명 이상이어야 합니다.")
        Integer maxGuests,

        @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다.")
        BigDecimal pricePerNight,

        @DecimalMin(value = "0.0", message = "청소비는 0원 이상이어야 합니다.")
        BigDecimal cleaningFee,

        Boolean instantBooking,

        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime checkInTime,

        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime checkOutTime
) {
}
