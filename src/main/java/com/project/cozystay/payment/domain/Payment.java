package com.project.cozystay.payment.domain;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.common.BaseTimeEntity;
import com.project.cozystay.payment.exception.PaymentInvalidStateException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
@Table(
      name = "payments",
      uniqueConstraints ={
              @UniqueConstraint(name = "uk_payments_booking", columnNames = "booking_id"),
              @UniqueConstraint(name = "uk_payments_payment_key", columnNames = "payment_key")
      }
)
public class Payment extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus status;

    // 실제 PG 연동 시 채워질 값 (지금은 Mock)
    @Column(name= "payment_key", length = 255, unique = true)
    private String paymentKey;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    private Payment(Booking booking, Long payerId, BigDecimal amount, PaymentMethod paymentMethod){
        this.booking = booking;
        this.payerId = payerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.READY;
    }

    public static Payment create(Booking booking, Long payerId, BigDecimal amount, PaymentMethod paymentMethod){
        return new Payment(booking, payerId, amount, paymentMethod);
    }

    public void markSuccess(String paymentKey){
        if(this.status != PaymentStatus.READY){
            throw new PaymentInvalidStateException("결제 성공 처리 불가: status=" + this.status);
        }
        this.status = PaymentStatus.SUCCESS;
        this.paymentKey = paymentKey;
        this.paidAt = LocalDateTime.now();
    }

    public void markFailed(){
        if(this.status != PaymentStatus.READY){
            throw new PaymentInvalidStateException("결제 실패 처리 불가: status=" + this.status);
        }
        this.status = PaymentStatus.FAILED;
    }

    // 환불 & 결제 실패 시 결제 취소 처리
    public void refund(){
        if(this.status == PaymentStatus.CANCELLED) return;

        if(this.status != PaymentStatus.SUCCESS
                && this.status != PaymentStatus.READY
                && this.status != PaymentStatus.FAILED){
            throw new PaymentInvalidStateException("환불/결제 취소 불가: status=" + this.status);
        }

        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void retry(BigDecimal amount, PaymentMethod method){
        if(this.status != PaymentStatus.FAILED && this.status != PaymentStatus.CANCELLED){
            throw new PaymentInvalidStateException("결제 재시도 불가: status=" + this.status);
        }

        this.amount = amount;
        this.paymentMethod = method;

        // 이전 결제 시도 값 초기화
        this.paymentKey = null;
        this.paidAt = null;
        this.cancelledAt = null;

        // 다시 결제 시작
        this.status = PaymentStatus.READY;
    }
}
