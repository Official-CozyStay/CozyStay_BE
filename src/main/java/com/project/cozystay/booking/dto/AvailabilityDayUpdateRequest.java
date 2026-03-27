package com.project.cozystay.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDayUpdateRequest {

    @NotNull(message = "date는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Boolean isAvailable;

    // null이면 기본 가격 사용. 값이 있으면 0보다 커야 함
    @DecimalMin(value = "0.01", message = "customPrice는 0보다 커야합니다.")
    private BigDecimal customPrice;

    // null이면 DEFAULT_MIN_NIGHTS처리. 값이 있으면 1 이상
    @Min(value = 1, message = "minNights는 1 이상이어야 합니다.")
    private Integer minNights;
}
