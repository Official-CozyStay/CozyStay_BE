package com.project.cozystay.booking.domain;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.booking.exception.BookingAlreadyCancelledException;
import com.project.cozystay.booking.exception.BookingCancellationNotAllowedException;
import com.project.cozystay.booking.exception.BookingDecisionNotAllowedException;
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
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="booking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

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

    private static final String DEFAULT_CURRENCY = "KRW";

    // 엔티티가 처음 DB에 저장될 때 자동으로 값 세팅
    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if(this.currency == null){
            this.currency = DEFAULT_CURRENCY;
        }
        if(this.status==null){
            this.status = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    // 예약 취소
    public void cancel(){
        if(this.status == BookingStatus.CANCELLED){
            throw new BookingAlreadyCancelledException(this.id);
        }
        if(this.status == BookingStatus.COMPLETED){
            throw new BookingCancellationNotAllowedException(this.id, this.status);
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    // 호스트 전용 상태 전이
    // 수락
    public void confirmByHost(){
        if(this.status != BookingStatus.PENDING){
            throw new BookingDecisionNotAllowedException(this.id, this.status);
        }
        this.status = BookingStatus.CONFIRMED;
    }
    // 거절
    public void rejectByHost(){
        if(this.status != BookingStatus.PENDING){
            throw new BookingDecisionNotAllowedException(this.id, this.status);
        }
        this.status = BookingStatus.REJECTED;
    }

}
