package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, Long> {
    List<AvailabilityCalendar>findByAccommodationIdAndDateBetween(
            Long accommodationId,
            LocalDate start,
            LocalDate end
    );
}
