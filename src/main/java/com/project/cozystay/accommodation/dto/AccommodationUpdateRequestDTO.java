package com.project.cozystay.accommodation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.cozystay.accommodation.domain.AccommodationType;
import jakarta.validation.constraints.DecimalMax;
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

        @NotBlank(message = "시/도는 필수입니다.")
        String state,

        @NotBlank(message = "국가는 필수입니다.")
        String country,

        String postalCode,

        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하이어야 합니다.")
        BigDecimal latitude,

        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하이어야 합니다.")
        BigDecimal longitude,

        @Min(value = 1, message = "최대 인원수는 1명 이상이어야 합니다.")
        Integer maxGuests,

        @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다.")
        BigDecimal pricePerNight,

        @DecimalMin(value = "0.0", message = "청소비는 0원 이상이어야 합니다.")
        BigDecimal cleaningFee,

        Boolean instantBooking,

        @JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime checkInTime,

        @JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime checkOutTime
) {
}
