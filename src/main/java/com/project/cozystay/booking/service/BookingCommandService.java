package com.project.cozystay.booking.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.booking.domain.AvailabilityCalendar;
import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.dto.BookingCreateRequest;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.repository.AvailabilityCalendarRepository;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingCommandService {

    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final AvailabilityCalendarRepository availabilityCalendarRepository;

    public BookingResponse createBooking(BookingCreateRequest request) {

        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();

        if(checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("checkIn은 checkOut보다 이전 날짜여야 합니다.");
        }

        // 숙소 정보 조회
        Accommodation accommodation = accommodationRepository.findById(request.getAccommodationId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 숙소입니다. id =" + request.getAccommodationId()));

        // 인원 수 검증
        if(request.getNumberOfGuests() <= 0 ||
        request.getNumberOfGuests() > accommodation.getMaxGuests()){
            throw new IllegalArgumentException("허용 인원 범위를 벗어났습니다.");
        }

        // 이미 예약된 건이 있는지 (날짜 겹침) 체크
        boolean hasOverlap = bookingRepository
                .existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
                        request.getAccommodationId(),
                        checkOut,
                        checkIn
                );

        if(hasOverlap){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 겹치는 예약이 존재합니다.");
        }

        // AvailabilityCalendar 기반으로 예약 가능 여부 + 가격 계산 준비
        LocalDate endInclusive = checkOut.minusDays(1);

        List<AvailabilityCalendar> calendars =
                availabilityCalendarRepository.findByAccommodationIdAndDateBetween(
                        request.getAccommodationId(), checkIn, endInclusive
                );

        Map<LocalDate, AvailabilityCalendar> calendarMap = calendars.stream()
                .collect(Collectors.toMap(
                        AvailabilityCalendar::getDate, Function.identity()
                ));

        BigDecimal basePrice = accommodation.getPricePerNight();
        if(basePrice == null) basePrice = BigDecimal.ZERO;

        BigDecimal totalRoomPrice = BigDecimal.ZERO;

        // 하루씩 돌면서 예약 불가일이면 예외, 가격 계산
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date = date.plusDays(1)) {
            AvailabilityCalendar cal = calendarMap.get(date);

            // AvailabilityCalendar에 있고, isAvailable == false 면 예약불가
            if(cal != null && !cal.isAvailable()){
                throw new IllegalStateException("해당 날짜는 예약이 불가합니다: " + date);
            }

            BigDecimal dayPrice = basePrice;
            if(cal != null && cal.getCustomPrice() != null){
                dayPrice = cal.getCustomPrice();
            }

            totalRoomPrice = totalRoomPrice.add(dayPrice);
        }

        // 청소비/서비스 수수료계산
        BigDecimal cleaningFee = accommodation.getCleaningFee() != null
                ? accommodation.getCleaningFee()
                : BigDecimal.ZERO;

        BigDecimal serviceFeePct = accommodation.getServiceFeePercentage() != null
                ? accommodation.getServiceFeePercentage()
                : BigDecimal.ZERO;

        BigDecimal serviceFee = totalRoomPrice.multiply(serviceFeePct).divide(BigDecimal.valueOf(100));

        BigDecimal totalPrice = totalRoomPrice.add(cleaningFee).add(serviceFee);
        totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

        // Booking  엔티티 생성
        Booking booking = Booking.builder()
                .accommodationId(request.getAccommodationId())
                .guestId(request.getGuestId()) // TODO : 나중에 JWT 가져오기
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfGuests(request.getNumberOfGuests())
                .totalPrice(totalPrice)
                .pricePerNightSnapshot(basePrice)
                .cleaningFeeSnapshot(cleaningFee)
                .serviceFeeSnapshot(serviceFee)
                .build();

        Booking saved = bookingRepository.save(booking);

        // DTO로 변환해서 반환
        return BookingResponse.builder()
                .bookingId(saved.getId())
                .accommodationId(saved.getAccommodationId())
                .guestId(saved.getGuestId())
                .checkInDate(saved.getCheckInDate())
                .checkOutDate(saved.getCheckOutDate())
                .numberOfGuests(saved.getNumberOfGuests())
                .totalPrice(saved.getTotalPrice())
                .bookingStatus(saved.getStatus().name())
                .build();

    }

}
