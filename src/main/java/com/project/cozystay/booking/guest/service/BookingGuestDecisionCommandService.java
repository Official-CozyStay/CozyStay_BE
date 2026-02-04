package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.exception.BookingGuestNotFoundException;
import com.project.cozystay.booking.guest.exception.BookingGuestResponseForbiddenException;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookingGuestDecisionCommandService {

    private final BookingGuestRepository bookingGuestRepository;

    @Transactional
    public void accept (Long bookingGuestId, Long responderUserId){
        BookingGuest bookingGuest = bookingGuestRepository.findById(bookingGuestId)
                .orElseThrow(()-> new BookingGuestNotFoundException("동반자 초대를 찾을 수 없습니다."));

        // 초대 받은 사람만 응답 가능
        if(bookingGuest.getGuestUserId() == null || !bookingGuest.getGuestUserId().equals(responderUserId)){
            throw new BookingGuestResponseForbiddenException("초대받은 사용자만 수락할 수 있습니다.");
        }

        bookingGuest.accept();
    }

    @Transactional
    public void decline (Long bookingGuestId, Long responderUserId){
        BookingGuest bookingGuest = bookingGuestRepository.findById(bookingGuestId)
                .orElseThrow(()-> new BookingGuestNotFoundException("동반자 초대를 찾을 수 없습니다."));

        if (bookingGuest.getGuestUserId() == null || !bookingGuest.getGuestUserId().equals(responderUserId)) {
            throw new BookingGuestResponseForbiddenException("초대받은 사용자만 거절할 수 있습니다.");
        }

        bookingGuest.decline();
    }

}
