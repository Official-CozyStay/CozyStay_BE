package com.project.cozystay.review.repository;

import com.project.cozystay.review.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    List<UserReview> findByTargetGuestId(Long targetGuestId);

    // 해당 예약 ID로 작성된 리뷰 존재 여부
    boolean existsByBooking_Id(Long bookingId);

    // 특정 호스트가 작성한 모든 게스트 리뷰
    List<UserReview> findByReviewerHostId(Long reviewerHostId);

    // 특정 호스트가 작성한 특정 게스트에 대한 리뷰
    Optional<UserReview> findByTargetGuestIdAndReviewerHostId(Long targetGuestId, Long reviewerHostId);

    boolean existsByTargetGuestIdAndReviewerHostId(Long targetGuestId, Long reviewerHostId);

}
