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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BookingGuestConnectionQueryService {

    private final BookingGuestRepository bookingGuestRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BookingGuestConnectionResponse> getConnections(Long userId){

        // 같은 사람과 여러번 여행했어도 인연 목록에서는 한 번만 보여주기 위해 Map 사용
        // LinkedHashMap을 사용하면 먼저 추가된 순서가 유지된다.
        Map<Long, BookingGuestConnectionResponse> connections = new LinkedHashMap<>();

        // 내가 초대한 동반자 중, 초대를 수락한 사람들 조회
        List<BookingGuest> invitedByMe = bookingGuestRepository.findGuestsInvitedByMe(userId, InvitationStatus.ACCEPTED);

        for(BookingGuest bookingGuest : invitedByMe){
            Long connectionUserId = bookingGuest.getGuestUserId();
            addConnection(connections, connectionUserId);
        }

        // 내가 동반자로 초대받고 수락한 예약 조회
        List<BookingGuest> invitedMe = bookingGuestRepository.findInvitationsForMe(userId, InvitationStatus.ACCEPTED);

        for(BookingGuest bookingGuest : invitedMe){
            Long connectionUserId = bookingGuest.getBooking().getGuestId();
            addConnection(connections, connectionUserId);
        }

        return List.copyOf(connections.values());
    }

    private void addConnection(
            Map<Long, BookingGuestConnectionResponse> connections,
            Long connectionUserId
    ){
        // 비회원 초대 데이터가 남아있거나 이미 추가된 사용자라면 건너뛴다.
        if(connectionUserId == null || connections.containsKey(connectionUserId)){
            return;
        }

        User user = userRepository.findById(connectionUserId).orElse(null);

        if(user == null){
            return;
        }

        connections.put(
                user.getId(),
                new BookingGuestConnectionResponse(
                        user.getId(),
                        user.getNickName(),
                        user.getProfileImageUrl()
                )
        );
    }
}
