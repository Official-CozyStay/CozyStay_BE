package com.project.cozystay.booking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name="bookings",
        indexes = {
                @Index(
                        name = "idx_bookings_accommodation_dates",
                        columnList = "accommodation_id, check_in_date, check_out_date"
                ),
                @Index(
                        name = "idx_bookings_guest_created_at",
                        columnList = "guest_id, created_at"
                )
        }
)
@Getter @Setter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="booking_id")
    private Long id;

    @Column(name="accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name= "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name= "booking_status", nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "price_per_night_snapshot", precision = 10, scale = 2)
    private BigDecimal pricePerNightSnapshot;

    @Column(name = "cleaning_fee_snapshot", precision = 10, scale = 2)
    private BigDecimal cleaningFeeSnapshot;

    @Column(name = "service_fee_snapshot", precision = 10, scale = 2)
    private BigDecimal serviceFeeSnapshot;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // 엔티티가 처음 DB에 저장될 때 자동으로 값 세팅
    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if(this.currency == null){
            this.currency = "KRW";
        }
        if(this.status==null){
            this.status = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(){
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}
