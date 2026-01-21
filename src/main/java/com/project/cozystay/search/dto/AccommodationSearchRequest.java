package com.project.cozystay.search.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AccommodationSearchRequest {
    private String city;
    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer numberOfBeds;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
}
