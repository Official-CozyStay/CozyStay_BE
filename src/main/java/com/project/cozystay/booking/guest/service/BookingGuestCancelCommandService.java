package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.exception.BookingGuestCancelNotAllowedException;
import com.project.cozystay.booking.guest.exception.BookingGuestInvitationNotAllowedException;
import com.project.cozystay.booking.guest.exception.BookingGuestNotFoundException;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@RequiredArgsConstructor
@Service
public class BookingGuestCancelCommandService {

    private static final EnumSet<BookingStatus> NOT_CANCELLABLE_BOOKING_STATUSES =
            EnumSet.of(BookingStatus.CANCELLED, BookingStatus.REJECTED, BookingStatus.COMPLETED);

    private final BookingRepository bookingRepository;
    private final BookingGuestRepository bookingGuestRepository;

    @Transactional
    public void cancelInvitation(Long bookingId, Long bookingGuestId, Long requesterUserId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("예약을 찾을 수 없습니다."));

        // 예약자 본인 체크
        if(!booking.getGuestId().equals(requesterUserId)){
            throw new BookingGuestInvitationNotAllowedException("예약자 본인만 동반자 초대를 취소할 수 있습니다.");
        }

        // 예약 상태 체크
        if(NOT_CANCELLABLE_BOOKING_STATUSES.contains(booking.getStatus())){
            throw new BookingGuestCancelNotAllowedException("현재 예약 상태에서는 동반자 초대를 취소할 수 없습니다.");
        }

        // bookingId에 속한 bookingGuest인지 같이 검증
        BookingGuest bookingGuest = bookingGuestRepository.findByIdAndBooking_Id(bookingGuestId, bookingId)
                .orElseThrow(()-> new BookingGuestNotFoundException("동반자 초대를 찾을 수 없습니다."));

        // 초대 상태 확인 : PENDING만 취소 가능
        if(bookingGuest.getInvitationStatus() != InvitationStatus.PENDING){
            throw new BookingGuestCancelNotAllowedException("대기 상태인 초대만 취소할 수 있습니다.");
        }

        bookingGuestRepository.delete(bookingGuest);


    }

}
