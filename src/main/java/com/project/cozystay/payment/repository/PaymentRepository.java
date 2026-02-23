package com.project.cozystay.payment.repository;

import com.project.cozystay.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking_Id(Long bookingId);
    boolean existsByBooking_Id(Long bookingId);
}
