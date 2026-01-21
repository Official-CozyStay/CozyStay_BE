package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, Long> {
    List<AvailabilityCalendar>findByAccommodationIdAndDateBetween(
            Long accommodationId,
            LocalDate start,
            LocalDate end
    );

    List<AvailabilityCalendar>findAllByAccommodationIdAndDateIn(Long accommodationId, List<LocalDate> dates);

    Optional<AvailabilityCalendar>findByAccommodationIdAndDate(Long accommodationId, LocalDate date);

    void deleteAllByAccommodationIdAndDateIn(Long accommodationId, List<LocalDate> dates);

}
