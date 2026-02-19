package com.project.cozystay.review.repository;

import com.project.cozystay.review.domain.AccommodationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, Long> {

    @Query("SELECT r FROM AccommodationReview r LEFT JOIN FETCH r.comment WHERE r.accommodation.id = :accId")
    List<AccommodationReview> findByAccommodation_Id(@Param("accId") Long accId);

    // 해당 예약 ID로 작성된 리뷰 존재 여부
    boolean existsByBooking_Id(Long bookingId);

    // 특정 게스트가 작성한 리뷰들
    List<AccommodationReview> findByGuestId(Long guestId);

    // 특정 게스트가 작성한 특정 숙소 리뷰
    @Query("""
    SELECT DISTINCT ar FROM AccommodationReview ar
    WHERE ar.guest.id = :guestId
    AND ar.accommodation.id = :accId
    """)
    Optional<AccommodationReview> findByGuestAndAccommodation(@Param("guestId") Long guestId, @Param("accId") Long accId);

    @Query("""
            SELECT ar FROM AccommodationReview ar
            WHERE ar.comment.Id = :commentId
            """)
    Optional<AccommodationReview> findByComment_CommentId(@Param("commentId") Long commentId);


}
