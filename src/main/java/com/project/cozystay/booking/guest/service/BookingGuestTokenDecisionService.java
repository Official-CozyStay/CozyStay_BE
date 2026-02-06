package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.exception.BookingGuestNotFoundException;
import com.project.cozystay.booking.guest.exception.BookingGuestResponseNotAllowedException;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingGuestTokenDecisionService {

    private final BookingGuestRepository bookingGuestRepository;

    @Transactional
    public void acceptByToken(String token){
        BookingGuest bg = bookingGuestRepository.findByInvitationToken(token)
                .orElseThrow(()-> new BookingGuestNotFoundException("유효하지 않은 초대입니다."));

        validateTokenUsable(bg);
        bg.accept();
    }

    @Transactional
    public void declineByToken(String token){
        BookingGuest bg = bookingGuestRepository.findByInvitationToken(token)
                .orElseThrow(()->new BookingGuestNotFoundException("유효하지 않은 초대입니다."));

        validateTokenUsable(bg);
        bg.decline();
    }

    private void validateTokenUsable(BookingGuest bg){
        //회원 초대는 토큰 응답 금지
        if(bg.getGuestUserId() != null){
            throw new BookingGuestResponseNotAllowedException("가입자 초대는 토큰으로 응답할 수 없습니다.");
        }

        if(bg.getInvitationStatus() != InvitationStatus.PENDING){
            throw new BookingGuestResponseNotAllowedException("이미 응답한 초대입니다.");
        }

        if(bg.getTokenExpiresAt() != null && bg.getTokenExpiresAt().isBefore(LocalDateTime.now())){
            throw new BookingGuestResponseNotAllowedException("초대 링크가 만료되었습니다.");
        }
    }
}
