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

        BigDecimal latitude,

        BigDecimal longitude,

        @Min(value = 1, message = "최대 인원수는 1명 이상이어야 합니다.")
        Integer maxGuests,

        @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다.")
        BigDecimal pricePerNight,

        @DecimalMin(value = "0.0", message = "청소비는 0원 이상이어야 합니다.")
        BigDecimal cleaningFee,

        Boolean instantBooking,

        LocalTime checkInTime,

        LocalTime checkOutTime
) {
}
