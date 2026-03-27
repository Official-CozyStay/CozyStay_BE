package com.project.cozystay.booking.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.booking.domain.AvailabilityCalendar;
import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.dto.AvailabilityDayUpdateRequest;
import com.project.cozystay.booking.dto.AvailabilityUpdateRequest;
import com.project.cozystay.booking.exception.AccommodationNotFoundException;
import com.project.cozystay.booking.exception.BookingConflictException;
import com.project.cozystay.booking.repository.AvailabilityCalendarRepository;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingAvailabilityCommandService {

    private final AccommodationRepository accommodationRepository;
    private final AvailabilityCalendarRepository availabilityCalendarRepository;
    private final BookingRepository bookingRepository;

    private static final boolean DEFAULT_AVAILABLE = true;
    private static final int DEFAULT_MIN_NIGHTS = 1;

    @Transactional
    public void updateAvailability(Long accommodationId, Long hostId, AvailabilityUpdateRequest request){

        // 요청 날짜 정리
        List<AvailabilityDayUpdateRequest> days = request.getDays();

        // 숙소 조회
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(()-> new AccommodationNotFoundException(accommodationId));

        //권한 체크
        if(!accommodation.getHostId().equals(hostId)){
            throw new AccommodationNotFoundException(accommodationId);
        }

        // 요청 날짜 목록(중복 제거)
        List<LocalDate> dates = days.stream()
                .map(AvailabilityDayUpdateRequest::getDate)
                .distinct()
                .toList();

        // 기존 예외 데이터 한번에 조회
        Map<LocalDate, AvailabilityCalendar> existingMap =
                availabilityCalendarRepository.findAllByAccommodationIdAndDateIn(accommodationId, dates)
                        .stream()
                        .collect(Collectors.toMap(AvailabilityCalendar::getDate, it->it));

        // 충돌 체크 : isAvailable=false로 막으려는 날짜가 예약과 겹치면 409
        List<LocalDate> blockDates = days.stream()
                .filter(d -> Boolean.FALSE.equals(d.getIsAvailable()))
                .map(AvailabilityDayUpdateRequest::getDate)
                .distinct()
                .toList();

        if(!blockDates.isEmpty()){
            LocalDate startInclusive = Collections.min(blockDates);
            LocalDate endExclusive = Collections.max(blockDates).plusDays(1);

            boolean hasOverlap = bookingRepository.existsActiveBookingOverlapping(
                    accommodationId,
                    List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                    startInclusive,
                    endExclusive
            );

            if (hasOverlap) {
                throw new BookingConflictException(accommodationId, startInclusive, endExclusive);
            }

        }

        // Upsert / Delete 처리
        List<AvailabilityCalendar> toUpsert = new ArrayList<>();
        List<LocalDate> datesToDelete = new ArrayList<>();

        // 같은 date가 여러 번 오면 마지막 값으로 덮어쓰기(중복 방지)
        Map<LocalDate, AvailabilityDayUpdateRequest> normalized = new LinkedHashMap<>();
        for(AvailabilityDayUpdateRequest d : days){
            normalized.put(d.getDate(), d);
        }

        for (AvailabilityDayUpdateRequest d : normalized.values()) {
            LocalDate date = d.getDate();

            boolean available = (d.getIsAvailable() == null) ? DEFAULT_AVAILABLE : d.getIsAvailable();
            BigDecimal customPrice = d.getCustomPrice();
            int minNights = (d.getMinNights() == null) ? DEFAULT_MIN_NIGHTS : d.getMinNights();

            boolean isDefault = (available == DEFAULT_AVAILABLE)
                    && (customPrice == null)
                    && (minNights == DEFAULT_MIN_NIGHTS);

            AvailabilityCalendar existing = existingMap.get(date);

            if (isDefault) {
                // 기본값이면 row 저장할 필요 없음. 기존 row가 있다면 삭제.
                if (existing != null) {
                    datesToDelete.add(date);
                }
                continue;
            }

            if (existing == null) {
                AvailabilityCalendar created = AvailabilityCalendar.builder()
                        .accommodationId(accommodationId)
                        .date(date)
                        .available(available)
                        .customPrice(customPrice)
                        .minNights(minNights)
                        .build();
                toUpsert.add(created);
            } else {
                existing.update(available, customPrice, minNights);
                toUpsert.add(existing);
            }
        }

        // 삭제 먼저 (같은 date에 대해 upsert와 충돌 방지)
        if (!datesToDelete.isEmpty()) {
            availabilityCalendarRepository.deleteAllByAccommodationIdAndDateIn(accommodationId, datesToDelete);
        }

        if (!toUpsert.isEmpty()) {
            availabilityCalendarRepository.saveAll(toUpsert);
        }

    }

}
