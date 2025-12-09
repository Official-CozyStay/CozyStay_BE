package com.project.cozystay.review.repository;

import com.project.cozystay.review.domain.AccommodationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, Long> {

    // 숙소에 달린 리뷰
    List<AccommodationReview> findByAccommodation_AccommodationId(Long accId);

    // 특정 게스트가 작성한 리뷰들
    List<AccommodationReview> findByGuestId(Long guestId);

    // 특정 게스트가 작성한 특정 숙소 리뷰
    @Query("""
    SELECT DISTINCT ar FROM AccommodationReview ar
    WHERE ar.guest.id = :guestId
    AND ar.accommodation.accommodationId = :accId
    """)
    Optional<AccommodationReview> findByGuestAndAccommodation(@Param("guestId") Long guestId, @Param("accId") Long accId);
}
