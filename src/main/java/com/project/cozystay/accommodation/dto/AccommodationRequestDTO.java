package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestDTO {

    @NotBlank(message = "숙소 제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "숙소 타입은 필수입니다.")
    private AccommodationType accommodationType;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "도시는 필수입니다.")
    private String city;     // 시/군 (예: 성남시, 가평군)

    @NotBlank(message = "구/군은 필수입니다.")
    private String district; // 구 (예: 분당구)

    private String state;

    @NotBlank(message = "국가는 필수입니다.")
    private String country;

    private String postalCode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @NotNull(message = "최대 인원수는 필수입니다.")
    @Min(value = 1, message = "최대 인원수는 1명 이상이어야 합니다.")
    private Integer maxGuests;

    @NotNull(message = "1박당 가격은 필수입니다.")
    @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다.")
    private BigDecimal pricePerNight;

    private BigDecimal cleaningFee;

    private Boolean instantBooking;

    private String checkInTime;

    private String checkOutTime;

    public Accommodation toEntity(Long hostId) {
        LocalTime parsedCheckIn = (this.checkInTime != null && !this.checkInTime.isBlank())
                ? LocalTime.parse(this.checkInTime)
                : LocalTime.of(15, 0); // 기본 체크인 15:00

        LocalTime parsedCheckOut = (this.checkOutTime != null && !this.checkOutTime.isBlank())
                ? LocalTime.parse(this.checkOutTime)
                : LocalTime.of(11, 0); // 기본 체크아웃 11:00

        return Accommodation.builder()
                .hostId(hostId)
                .title(this.title)
                .description(this.description)
                .accommodationType(this.accommodationType)
                .address(this.address)
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
                .checkInTime(parsedCheckIn)
                .checkOutTime(parsedCheckOut)
                .build();
    }
}
