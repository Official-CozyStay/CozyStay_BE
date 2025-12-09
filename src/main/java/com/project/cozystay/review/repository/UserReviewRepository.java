package com.project.cozystay.review.repository;

import com.project.cozystay.review.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    List<UserReview> findByTargetGuestId(Long targetGuestId);

    // 특정 호스트가 남긴 모든 리뷰
    List<UserReview> findByReviewerHostId(Long reviewerHostId);

    Optional<UserReview> findByTargetGuestIdAndReviewerHostId(Long targetGuestId, Long reviewerHostId);

    boolean existsByTargetGuestIdAndReviewerHostId(Long targetGuestId, Long reviewerHostId);

}
