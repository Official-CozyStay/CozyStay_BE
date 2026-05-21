package com.project.cozystay.booking.guest.service;

import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import com.project.cozystay.booking.guest.dto.BookingGuestConnectionResponse;
import com.project.cozystay.booking.guest.repository.BookingGuestRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingGuestConnectionQueryService {

    private final BookingGuestRepository bookingGuestRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BookingGuestConnectionResponse> getConnections(Long userId){

        // 내가 초대한 동반자 중, 초대를 수락한 사람들 조회
        List<BookingGuest> invitedByMe = bookingGuestRepository.findGuestsInvitedByMe(
                userId, InvitationStatus.ACCEPTED
        );

        List<BookingGuest> invitedMe = bookingGuestRepository.findInvitationsForMe(
                userId,
                InvitationStatus.ACCEPTED
        );

        // 같은 사람과 여러 번 여행했어도 인연 목록에는 한번만 보여주기 위해 Set 사용
        LinkedHashSet<Long> connectionUserIds = new LinkedHashSet<>();

        // 내가 초대한 동반자 중 초대를 수락한 사람들의 회원 ID 수집
        for(BookingGuest bookingGuest : invitedByMe){
            Long connectionUserId = bookingGuest.getGuestUserId();

            if(connectionUserId != null){
                connectionUserIds.add(connectionUserId);
            }
        }

        // 내가 동반자로 초대받고 수락한 예약의 예약자 ID 수집
        for(BookingGuest bookingGuest : invitedMe){
            Long connectionUserId = bookingGuest.getBooking().getGuestId();

            if(connectionUserId != null){
                connectionUserIds.add(connectionUserId);
            }
        }

        if(connectionUserIds.isEmpty()){
            return List.of();
        }

        // 사용자 정보를 ID 목록 기준으로 한 번에 조회해서 N+1 쿼리를 방지한다.
        Map<Long, User> usersById = userRepository.findAllById(connectionUserIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return connectionUserIds.stream()
                .map(usersById::get)
                .filter(user -> user != null)
                .map(user -> new BookingGuestConnectionResponse(
                        user.getId(),
                        user.getNickName(),
                        user.getProfileImageUrl()
                ))
                .toList();
    }


}
