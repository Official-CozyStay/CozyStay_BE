package com.project.cozystay.booking.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.booking.domain.AvailabilityCalendar;
import com.project.cozystay.booking.dto.AvailabilityResponse;
import com.project.cozystay.booking.dto.AvailabilityDayResponse;
import com.project.cozystay.booking.repository.AvailabilityCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // 날짜 -> AvailabilityCalendar 매핑
        Map<LocalDate, AvailabilityCalendar> calendarMap = calenderList.stream()
                .collect(Collectors.toMap(
                        AvailabilityCalendar::getDate,
                        Function.identity()
                ));

        // from ~ to-1 까지의 모든 날짜에 대해 DTO 생성
        List<AvailabilityDayResponse> dayResponses
                = from.datesUntil(to)
                .map(date->{
                    AvailabilityCalendar cal = calendarMap.get(date);

                    // DB에 설정 값이 있는 특별한 날 => 가격이 다른 날, 예약이 불가능한 날
                    if(cal != null){
                        BigDecimal price = cal.getCustomPrice() != null
                                ? cal.getCustomPrice()
                                : defaultPricePerNight;

                        return AvailabilityDayResponse.builder()
                                .date(cal.getDate())
                                .available(cal.isAvailable())
                                .pricePerNight(price)
                                .minNights(cal.getMinNights())
                                .build();
                    }

                    // DB에 설정이 없는 기본날
                    return AvailabilityDayResponse.builder()
                            .date(date)
                            .available(true)
                            .pricePerNight(defaultPricePerNight)
                            .minNights(1)
                            .build();
                }).toList();

        // 최종 응답 DTO
        return AvailabilityResponse.builder()
                .accommodationId(accommodationId)
                .from(from)
                .to(to)
                .days(dayResponses)
                .build();

    }
}


