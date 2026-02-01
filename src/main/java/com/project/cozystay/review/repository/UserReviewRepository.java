package com.project.cozystay.review.repository;

import com.project.cozystay.review.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    @Query("SELECT r FROM UserReview r LEFT JOIN FETCH r.comment WHERE r.targetGuest.id = :targetGuestId")
    List<UserReview> findByTargetGuestId(@Param("targetGuestId") Long targetGuestId);

    // 해당 예약 ID로 작성된 리뷰 존재 여부
    boolean existsByBooking_Id(Long bookingId);

    // 특정 호스트가 작성한 모든 게스트 리뷰
    @Query("SELECT r FROM UserReview r LEFT JOIN FETCH r.comment WHERE r.reviewerHost.id = :reviewerHostId")
    List<UserReview> findByReviewerHostId(@Param("reviewerHostId") Long reviewerHostId);

    // 특정 호스트가 작성한 특정 게스트에 대한 리뷰
    Optional<UserReview> findByTargetGuestIdAndReviewerHostId(Long targetGuestId, Long reviewerHostId);

    boolean existsByTargetGuestIdAndReviewerHostId(Long targetGuestId, Long reviewerHostId);

    @Query("""
            SELECT ur FROM UserReview ur
            WHERE ur.comment.Id = :commentId
            """)
    Optional<UserReview> findByComment_CommentId(@Param("commentId") Long commentId);
}
