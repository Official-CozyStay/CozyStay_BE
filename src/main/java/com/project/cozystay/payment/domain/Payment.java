package com.project.cozystay.payment.domain;

import com.project.cozystay.booking.domain.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
@Table(
      name = "payments",
      uniqueConstraints ={
              @UniqueConstraint(name = "uk_payments_booking", columnNames = "booking_id")
      }
)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Long payerId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // 실제 PG 연동 시 채워질 값 (지금은 Mock)
    private String paymentKey;

    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Payment(Booking booking, Long payerId, int amount){
        this.booking = booking;
        this.payerId = payerId;
        this.amount = amount;
        this.status = PaymentStatus.READY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private static Payment create(Booking booking, Long payerId, int amount){
        return new Payment(booking, payerId, amount);
    }

    private void markSuccess(String paymentKey){
        if(this.status == PaymentStatus.CANCELLED) throw new IllegalStateException("이미 환불된 결제입니다.");
        this.status = PaymentStatus.SUCCESS;
        this.paymentKey = paymentKey;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed(){
        if(this.status == PaymentStatus.SUCCESS) throw new IllegalStateException("이미 성공한 결제입니다.");
        if(this.status == PaymentStatus.CANCELLED) throw new IllegalStateException("이미 환불된 결제입니다.");
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void refund(){
        if(this.status != PaymentStatus.SUCCESS){
            throw new IllegalStateException("성공한 결제만 환불할 수 있습니다. current=" + this.status);
        }
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = this.cancelledAt;
    }
}
