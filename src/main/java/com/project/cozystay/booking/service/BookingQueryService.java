package com.project.cozystay.booking.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.dto.BookingResponse;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingQueryService {

    private final BookingRepository bookingRepository;

    public List<BookingResponse> getMyBookings(Long guestId, BookingStatus status){
        List<Booking> bookings;
        if(status == null){
            bookings = bookingRepository.findByGuestIdOrderByCreatedAtDesc(guestId);
        }
        else{
            bookings = bookingRepository.findByGuestIdAndStatusOrderByCreatedAtDesc(guestId, status);
        }

        return bookings.stream()
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse getMyBookingDetail(Long bookingId, Long guestId){
        Booking booking = bookingRepository.findByIdAndGuestId(bookingId, guestId)
                .orElseThrow(()-> new BookingNotFoundException("예약이 존재하지 않거나 접근 권한이 없습니다."));

        return toResponse(booking);
    }

    private BookingResponse toResponse(Booking b){
        return BookingResponse.builder()
                .bookingId(b.getId())
                .accommodationId(b.getAccommodationId())
                .guestId(b.getGuestId())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .numberOfGuests(b.getNumberOfGuests())
                .totalPrice(b.getTotalPrice())
                .bookingStatus(b.getStatus().name())
                .build();
    }
}
