package com.project.cozystay.payment.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.domain.PaymentMethod;
import com.project.cozystay.payment.exception.PaymentAlreadyExistsException;
import com.project.cozystay.payment.exception.PaymentInvalidStateException;
import com.project.cozystay.payment.exception.PaymentNotFoundException;
import com.project.cozystay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    // 결제 생성
    @Transactional
    public Payment createPayment(Long bookingId, Long payerId, PaymentMethod method) {
        Booking booking = bookingRepository.findByIdWithAccommodation(bookingId)
                .orElseThrow(()->new BookingNotFoundException("예약을 찾을 수 없습니다."));

        if(!booking.getGuestId().equals(payerId)){
            throw new PaymentInvalidStateException("본인의 예약만 결제할 수 있습니다.");
        }

        if(booking.getStatus() != BookingStatus.PENDING){
            throw new PaymentInvalidStateException("PENDING 예약만 결제 할 수 있습니다. status=" + booking.getStatus());
        }

        if (paymentRepository.existsByBooking_Id(bookingId)) {
            throw new PaymentAlreadyExistsException("이미 결제가 생성된 예약입니다.");
        }

        BigDecimal amount = calculateAmount(booking);

        Payment payment = Payment.create(booking, payerId, amount, method);
        return paymentRepository.save(payment);
    }

    private BigDecimal calculateAmount(Booking booking) {
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if(nights <= 0){
            throw new PaymentInvalidStateException("숙박 일수가 올바르지 않습니다. checkIn=" + checkIn + ", checkOut=" + checkOut);
        }

        BigDecimal pricePerNight = booking.getAccommodation().getPricePerNight();

        if(pricePerNight == null || pricePerNight.signum() <= 0){
            throw new PaymentInvalidStateException("숙소 가격이 올바르지 않습니다.");
        }

        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }

    // Mock 결제 성공 처리
    @Transactional
    public Payment confirmPayment(Long paymentId, Long payerId, String paymentKey){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("결제를 찾을 수 없습니다."));

        if(!payment.getPayerId().equals(payerId)){
            throw new PaymentInvalidStateException("결제자만 결제 확정 처리를 할 수 있습니다.");
        }

        payment.markSuccess(paymentKey);
        return payment;
    }

    // Mock 결제 실패 처리
    @Transactional
    public Payment failPayment(Long paymentId, Long payerId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("결제를 찾을 수 없습니다."));

        if(!payment.getPayerId().equals(payerId)){
            throw new PaymentInvalidStateException("결제자만 결제 실패 처리를 할 수 있습니다.");
        }

        payment.markFailed();
        payment.getBooking().cancel(); // 결제 실패 시 예약 취소

        return payment;
    }

    // 환불 처리
    @Transactional
    public void refundByBooking(Long bookingId){
        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(()-> new PaymentNotFoundException("예약 결제를 찾을 수 없습니다. bookingId=" + bookingId));

        payment.refund();
    }
}
