package com.project.cozystay.search.repository;

import com.project.cozystay.accommodation.domain.AccommodationStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.project.cozystay.search.dto.AccommodationSearchRequest; // Added missing import

import static com.project.cozystay.accommodation.domain.QAccommodation.accommodation;
import static com.project.cozystay.accommodation.domain.QAccommodationImage.accommodationImage;
import static com.project.cozystay.booking.domain.QBooking.booking;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class AccommodationJPASearchRepositoryImpl implements AccommodationJPASearchRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Tuple> search(AccommodationSearchRequest request) {
        return queryFactory
                .select(accommodation, accommodationImage.imageUrl)
                .from(accommodation)
                .leftJoin(accommodation.images, accommodationImage).on(accommodationImage.primary.isTrue())
                .where(
                        accommodation.status.eq(AccommodationStatus.ACTIVE), // 활성화된 숙소만
                        provinceEq(request.getProvince()),
                        cityEq(request.getCity()),
                        districtEq(request.getDistrict()),
                        titleContains(request.getTitle()),
                        priceBetween(request.getMinPrice(), request.getMaxPrice()),
                        isAvailable(request.getCheckInDate(), request.getCheckOutDate())
                )
                .offset((long) request.getPage() * request.getSize())
                .limit(request.getSize())
                .orderBy(accommodation.id.desc())
                .fetch();
    }

    private BooleanExpression provinceEq(String province) {
        return hasText(province) ? accommodation.province.eq(province) : null;
    }

    private BooleanExpression cityEq(String city) {
        return hasText(city) ? accommodation.city.eq(city) : null;
    }

    private BooleanExpression districtEq(String district) {
        return hasText(district) ? accommodation.district.eq(district) : null;
    }

    private BooleanExpression titleContains(String title) {
        return hasText(title) ? accommodation.title.contains(title) : null;
    }

    private BooleanExpression priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return accommodation.pricePerNight.loe(maxPrice);
        }
        if (maxPrice == null) {
            return accommodation.pricePerNight.goe(minPrice);
        }
        return accommodation.pricePerNight.between(minPrice, maxPrice);
    }

    private BooleanExpression isAvailable(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return null;
        }

        // NOT IN 대신 NOT EXISTS 사용
        return JPAExpressions.selectOne()
                .from(booking)
                .where(
                        booking.accommodation.id.eq(accommodation.id),
                        booking.checkOutDate.after(checkIn),
                        booking.checkInDate.before(checkOut)
                )
                .notExists();
    }
}
