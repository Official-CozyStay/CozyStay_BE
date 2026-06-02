package com.project.cozystay.review.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.comment.dto.CommentResponseDTO;
import com.project.cozystay.review.domain.UserReview;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.dto.UserReviewCreateRequest;
import com.project.cozystay.review.dto.UserReviewResponse;
import com.project.cozystay.review.exception.ReviewAlreadyExistsException;
import com.project.cozystay.review.exception.ReviewNotFoundException;
import com.project.cozystay.review.exception.ReviewUpdateNotAllowedException;
import com.project.cozystay.review.repository.UserReviewRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.exception.UserNotFoundException;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    // 게스트 리뷰 생성
    @Transactional
    public UserReviewResponse createUserReview(Long hostId, UserReviewCreateRequest request) {

        if(userReviewRepository.existsByTargetGuestIdAndReviewerHostId(request.targetGuestId(), hostId)){
            throw new ReviewAlreadyExistsException("이미 해당 게스트에 대해 작성하신 리뷰가 존재합니다.");
        }

        User reviewerHost = userRepository.findById(hostId)
                .orElseThrow(UserNotFoundException::new);

        User targetGuest = userRepository.findById(request.targetGuestId())
                .orElseThrow(UserNotFoundException::new);

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(request.bookingId()));

        // 중복 리뷰 생성 방지
        if (userReviewRepository.existsByBooking_Id(request.bookingId())) {
            throw new ReviewAlreadyExistsException(request.bookingId());
        }

        // 리뷰 작성자와 호스트가 동일한지 확인
        if( !Objects.equals(booking.getAccommodation().getHostId(), hostId)) {
            throw new AccessDeniedException("리뷰를 작성할 권한이 없습니다.");
        }

        UserReview review = UserReview.of(booking, reviewerHost, targetGuest, request.rating(), request.reviewComment());

        userReviewRepository.save(review);

        reviewerHost.increaseReviewCount(); // 리뷰 카운트 +1

        return new UserReviewResponse(
                review.getTargetGuest().getId(),
                review.getBooking().getId(),
                review.getReviewerHost().getNickName(),
                review.getReviewerHost().getProfileImageUrl(),
                review.getRating(),
                review.getReviewComment(),
                review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
        );
    }

    // 게스트 리뷰 조회
    public List<UserReviewResponse> getUserReviews(Long targetUserId){

        if (!userRepository.existsById(targetUserId)) {
            throw new UserNotFoundException();
        }

        List<UserReview> userReviewList = userReviewRepository.findByTargetGuestId(targetUserId);

        return userReviewList.stream()
                .map(review -> new UserReviewResponse(
                        review.getTargetGuest().getId(),
                        review.getBooking().getId(),
                        review.getReviewerHost().getNickName(),
                        review.getReviewerHost().getProfileImageUrl(),
                        review.getRating(),
                        review.getReviewComment(),
                        review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
                ))
                .collect(Collectors.toList());
    }

    // 게스트 리뷰 수정
    @Transactional
    public ReviewResponse updateUserReview(Long reviewId, UserReviewCreateRequest request){

        UserReview userReview = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if(userReview.getComment() != null){
            throw new ReviewUpdateNotAllowedException("답글이 달린 리뷰는 수정할 수 없습니다.");
        }
        userReview.update(request);

        // TODO 응답 형식 변경 고려
        return new ReviewResponse("리뷰를 수정하였습니다");
    }

    // 게스트 리뷰 삭제
    @Transactional
    public ReviewResponse deleteUserReview(Long reviewId){

        UserReview userReview = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        userReviewRepository.delete(userReview);

        return new ReviewResponse("리뷰를 삭제하였습니다");
    }

    // 특정 호스트가 작성한 모든 게스트 리뷰 조회
    public List<UserReviewResponse> getUserReviewListByHost(Long reviewerHostId){

        List<UserReview> reviewList = userReviewRepository.findByReviewerHostId(reviewerHostId);

        return reviewList.stream()
                .map(review -> new UserReviewResponse(
                        review.getTargetGuest().getId(),
                        review.getBooking().getId(),
                        review.getReviewerHost().getNickName(),
                        review.getReviewerHost().getProfileImageUrl(),
                        review.getRating(),
                        review.getReviewComment(),
                        review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
                ))
                .collect(Collectors.toList());
    }

    // 특정 호스트가 작성한 특정 게스트 리뷰 조회
    public UserReviewResponse getUserReviewByHost(Long targetGuestId, Long reviewerHostId){

        UserReview review = userReviewRepository.findByTargetGuestIdAndReviewerHostId(targetGuestId, reviewerHostId)
                .orElseThrow(() -> new ReviewNotFoundException("해당 사용자에 대해서 작성하신 리뷰를 찾을 수 없습니다. (대상 게스트 ID: " + targetGuestId + ")"));

        return new UserReviewResponse(
                review.getTargetGuest().getId(),
                review.getBooking().getId(),
                review.getReviewerHost().getNickName(),
                review.getReviewerHost().getProfileImageUrl(),
                review.getRating(),
                review.getReviewComment(),
                review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
        );
    }
}
