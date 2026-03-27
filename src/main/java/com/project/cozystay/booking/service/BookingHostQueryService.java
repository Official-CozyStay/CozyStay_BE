package com.project.cozystay.booking.service;

import com.project.cozystay.booking.dto.HostBookingDetailResponse;
import com.project.cozystay.booking.dto.HostBookingListItemResponse;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingHostQueryService {

    private final BookingRepository bookingRepository;

    public List<HostBookingListItemResponse> getHostBookings(Long hostId) {
        return bookingRepository.findAllByHostIdOrderByCreatedAtDesc(hostId)
                .stream()
                .map(HostBookingListItemResponse::from)
                .toList();
    }

    public HostBookingDetailResponse getHostBookingDetail(Long bookingId, Long hostId) {
        return bookingRepository.findByIdAndHostId(bookingId, hostId)
                .map(HostBookingDetailResponse::from)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

}
