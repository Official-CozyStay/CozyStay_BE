package com.project.cozystay.review.repository;

import com.project.cozystay.review.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    List<UserReview> findByTargetGuest_Id(Long targetGuestId);

    // 특정 호스트가 남긴 모든 리뷰
    List<UserReview> findByReviewerHost_id(Long reviewerHostId);

    Optional<UserReview> findByTargetGuest_IdAndReviewerHost_Id(Long targetGuestId, Long reviewerHostId);

    boolean existsByTargetGuest_IdAndReviewerHost_Id(Long targetGuestId, Long reviewerHostId);

}
