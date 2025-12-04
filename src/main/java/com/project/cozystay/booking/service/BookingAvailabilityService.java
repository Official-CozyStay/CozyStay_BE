package com.project.cozystay.booking.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.booking.domain.AvailabilityCalendar;
import com.project.cozystay.booking.dto.AvailabilityResponse;
import com.project.cozystay.booking.dto.AvailabiltiyDayResponse;
import com.project.cozystay.booking.repository.AvailabilityCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingAvailabilityService {

    private final AvailabilityCalendarRepository availabilityCalendarRepository;
    private final AccommodationRepository accommodationRepository;

    public AvailabilityResponse getAvailability(
            Long accommodationId,
            LocalDate from,
            LocalDate to
            ) {
        // 날짜 유효성 검증 : from < to
        if(from == null || to == null || !from.isBefore(to)){
            throw new IllegalArgumentException("from 날짜는 to 날짜보다 이전이어야 합니다.");
        }

        // 숙소 기본 정보 가져오기 (기본 1박 가격 등)
        Accommodation accommodation = accommodationRepository
                .findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소 입니다. id=" + accommodationId));

        BigDecimal defaultPricePerNight = accommodation.getPricePerNight();

        // 조회 범위 : [from, to) : 2/1~2/5일로 요청시 2/1~2/4까지 4박의 정보만 필요함, 종료 날짜를 포함하지 않아야함
        LocalDate endInclusive = to.minusDays(1);

        List<AvailabilityCalendar> calenderList = availabilityCalendarRepository.findByAccommodationIdAndDateBetween(
                accommodationId, from, endInclusive
        );

        // 엔티티 -> DTO매핑
        List<AvailabiltiyDayResponse> dayResponses = calenderList.stream()
                .map(cal-> AvailabiltiyDayResponse.builder()
                        .date(cal.getDate())
                        .available(cal.isAvailable())
                        .pricePerNight(cal.getCustomPrice() != null ? cal.getCustomPrice() : defaultPricePerNight)
                        .minNights(cal.getMinNights())
                        .build()
                ).toList();

        return AvailabilityResponse.builder()
                .accommodationId(accommodationId)
                .from(from)
                .to(to)
                .days(dayResponses)
                .build();

    }


}


