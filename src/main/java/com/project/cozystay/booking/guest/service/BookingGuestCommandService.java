package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.dto.BookingGuestCreateRequest;
import com.project.cozystay.booking.guest.dto.BookingGuestCreateResponse;
import com.project.cozystay.booking.guest.exception.BookingGuestDuplicateInvitationException;
import com.project.cozystay.booking.guest.exception.BookingGuestInvitationNotAllowedException;
import com.project.cozystay.booking.guest.exception.BookingGuestLimitExceededException;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@RequiredArgsConstructor
@Service
public class BookingGuestCommandService {

    private static final EnumSet<BookingStatus> NOT_INVITABLE_STATUSES =
            EnumSet.of(BookingStatus.CANCELLED, BookingStatus.REJECTED, BookingStatus.COMPLETED);

    private final BookingRepository bookingRepository;
    private final BookingGuestRepository bookingGuestRepository;

    @Transactional
    public BookingGuestCreateResponse invite(Long bookingId, Long inviterUserId, BookingGuestCreateRequest request) {
        Booking booking = bookingRepository.findByIdAndGuestIdForUpdate(bookingId, inviterUserId)
                .orElseThrow(() -> new BookingNotFoundException("예약을 찾을 수 없습니다."));

        // 예약자 본인 체크
        if(!booking.getGuestId().equals(inviterUserId)) {
            throw new BookingGuestInvitationNotAllowedException("예약자 본인만 동반자를 초대할 수 있습니다.");
        }

        // 상태 체크
        if(NOT_INVITABLE_STATUSES.contains(booking.getStatus())) {
            throw new BookingGuestInvitationNotAllowedException("현재 예약 상태에서는 동반자를 초대할 수 없습니다.");
        }

        // 중복 초대 방지 (같은 booking에 같은 이메일)
        if(bookingGuestRepository.existsByBooking_IdAndGuestEmail(bookingId, request.getGuestEmail())){
            throw new BookingGuestDuplicateInvitationException("이미 초대된 이메일입니다.");
        }

        // 정원 제한
        // booking.guestCount = 총 인원수, 동반자 최대 = guestCount-1
        int totalGuests = booking.getNumberOfGuests();
        long currentInvited = bookingGuestRepository.countByBooking_IdAndInvitationStatusNot(bookingId, InvitationStatus.DECLINED);
        long maxCompanions = Math.max(0, totalGuests - 1);

        if(currentInvited >= maxCompanions) {
            throw new BookingGuestLimitExceededException("동반자 초대 가능 인원을 초과했습니다.");
        }

        BookingGuest bookingGuest = BookingGuest.invite(
                booking,
                request.getGuestUserId(),
                request.getGuestName(),
                request.getGuestEmail(),
                request.getGuestPhone()
        );

        BookingGuest saved = bookingGuestRepository.save(bookingGuest);

        return new BookingGuestCreateResponse(saved.getId(), saved.getInvitationStatus(), saved.getInvitedAt());
    }
}
