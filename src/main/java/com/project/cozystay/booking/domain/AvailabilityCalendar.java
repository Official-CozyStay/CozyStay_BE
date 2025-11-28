package com.project.cozystay.booking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "availability_calendar",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_accommodation_date",
                        columnNames = {"accommodation_id", "date"}
                )
        }
)
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AvailabilityCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long id;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @Column(name = "custom_price", precision = 10, scale = 2)
    private BigDecimal customPrice;

    @Column(name = "min_nights", nullable = false)
    private int minNights;

}
