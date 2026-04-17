package com.project.cozystay.search.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AccommodationSearchRequest {
    private String state; // 도/광역시 (예: 경기도, 서울특별시)

    private String city;     // 시/군 (예: 성남시, 가평군)

    private String district; // 구 (예: 분당구)

    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer numberOfBeds;

    // 페이징 파라미터 추가 (기본값 설정)
    private int page = 0;
    private int size = 20;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
}
