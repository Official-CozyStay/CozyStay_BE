package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from AvailabilityCalendar a
        where a.accommodationId = :accommodationId
          and a.date in :dates
    """)
    void deleteAllByAccommodationIdAndDateIn(Long accommodationId, List<LocalDate> dates);
}
