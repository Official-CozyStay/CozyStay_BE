package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.dto.BookingGuestListResponse;
import com.project.cozystay.booking.guest.exception.BookingGuestInvitationNotAllowedException;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingGuestQueryService {

    private final BookingRepository bookingRepository;
    private final BookingGuestRepository bookingGuestRepository;

    @Transactional(readOnly = true)
    public List<BookingGuestListResponse> getGuests(Long bookingId, Long requesterUserId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new BookingNotFoundException("예약을 찾을 수 없습니다."));

        // 예약자 본인만 조회 가능
        if(!booking.getGuestId().equals(requesterUserId)){
            throw new BookingGuestInvitationNotAllowedException("예약자 본인만 게스트 목록을 조회할 수 있습니다.");
        }

        List<BookingGuest> guests = bookingGuestRepository.findAllByBooking_IdOrderByInvitedAtAsc(bookingId);

        return guests.stream()
                .map(bg -> new BookingGuestListResponse(
                        bg.getId(),
                        bg.getGuestUserId(),
                        bg.getGuestName(),
                        bg.getGuestEmail(),
                        bg.getGuestPhone(),
                        bg.getInvitationStatus(),
                        bg.getInvitedAt(),
                        bg.getRespondedAt()
                ))
                .toList();

    }
}
