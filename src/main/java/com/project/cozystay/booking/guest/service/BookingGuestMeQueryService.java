package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.dto.MyInvitationResponse;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookingGuestMeQueryService {

    private final BookingGuestRepository bookingGuestRepository;

    @Transactional(readOnly = true)
    public Page<MyInvitationResponse> getMyInvitations(Long userId, InvitationStatus status, Pageable pageable){

        Page<BookingGuest> page = (status == null)
        ? bookingGuestRepository.findAllByGuestUserIdWithBooking(userId, pageable)
        : bookingGuestRepository.findAllByGuestUserIdAndInvitationStatusWithBooking(userId, status, pageable);

        return page.map(bg -> {
            Booking booking = bg.getBooking();
            return new MyInvitationResponse(
                    bg.getId(),
                    booking.getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    bg.getInvitationStatus(),
                    bg.getInvitedAt(),
                    bg.getRespondedAt()
            );
        });
    }
}
