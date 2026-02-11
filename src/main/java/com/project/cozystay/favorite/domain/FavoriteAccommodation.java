package com.project.cozystay.favorite.domain;


import com.project.cozystay.accommodation.domain.Accommodation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_accommodations")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteAccommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_accommodation_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_id", nullable = false)
    private Favorite favorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    public static FavoriteAccommodation of(Favorite favorite, Accommodation accommodation) {
        return FavoriteAccommodation.builder()
                .favorite(favorite)
                .accommodation(accommodation)
                .build();
    }
}
