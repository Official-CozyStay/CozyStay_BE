package com.project.cozystay.review.service;

import com.project.cozystay.review.domain.UserReview;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.dto.UserReviewCreateRequest;
import com.project.cozystay.review.dto.UserReviewResponse;
import com.project.cozystay.review.repository.UserReviewRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final UserRepository userRepository;

    // 사용자 리뷰 생성
    @Transactional
    public UserReviewResponse createUserReview(Long hostId, UserReviewCreateRequest request) {

        if(userReviewRepository.existsByTargetGuest_IdAndReviewerHost_Id(request.targetGuestId(), hostId)){
            throw new IllegalArgumentException("이미 작성한 리뷰입니다.");
        }

        User reviewerHost = userRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("호스트가 존재하지 않습니다."));

        User targetGuest = userRepository.findById(request.targetGuestId())
                .orElseThrow(() -> new IllegalArgumentException("게스트가 존재하지 않습니다."));

        //TODO 이 호스트가 이 게스트와 실제로 예약 관계가 있는지 검증 로직 추가
        UserReview review = request.toEntity(reviewerHost, targetGuest);

        userReviewRepository.save(review);

        reviewerHost.increaseReviewCount(); // 리뷰 카운트 +1

        return UserReviewResponse.from(review);
    }

    // 사용자 리뷰 조회
    public List<UserReviewResponse> getUserReviews(Long targetUserId){

        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + targetUserId);
        }

        List<UserReview> userReviewList = userReviewRepository.findByTargetGuest_Id(targetUserId);

        return userReviewList.stream()
                .map(UserReviewResponse::from)
                .toList();
    }

    // 사용자 리뷰 수정
    //TODO 사용자가 답글을 달기 전까지만 수정 가능하도록하는 로직 추가 필요
    @Transactional
    public ReviewResponse updateUserReview(Long reviewId, UserReviewCreateRequest request){

        UserReview userReview = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        userReview.update(request);

        return new ReviewResponse("리뷰를 수정하였습니다");
    }

    // 사용자 리뷰 삭제
    @Transactional
    public ReviewResponse deleteUserReview(Long targetGuestId, Long reviewerId){

        UserReview userReview = userReviewRepository.findByTargetGuest_IdAndReviewerHost_Id(targetGuestId, reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        userReviewRepository.delete(userReview);

        return new ReviewResponse("리뷰를 삭제하였습니다");
    }


    // TODO 특정 호스트가 작성한 사용자 리뷰들 조회
    // TODO 특정 호스트가 작성한 특정 사용자 리뷰 조회
}
