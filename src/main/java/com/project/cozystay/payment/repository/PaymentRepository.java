package com.project.cozystay.payment.repository;

import com.project.cozystay.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking_Id(Long bookingId);
    boolean existsByBooking_Id(Long bookingId);

    @Query("""
            select p
            from Payment p
            join fetch p.booking b
            where p.id = :paymentId 
            """)
    Optional<Payment>findByIdWithBooking(@Param("paymentId") Long paymentId);

    @Query("""
select p
from Payment p
join fetch p.booking b
where b.id = :bookingId
""")
    Optional<Payment> findByBookingIdWithBooking(@Param("bookingId")Long bookingId);
}
