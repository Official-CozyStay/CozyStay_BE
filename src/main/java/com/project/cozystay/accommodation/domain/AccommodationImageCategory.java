package com.project.cozystay.accommodation.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accommodation_image_categories")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class AccommodationImageCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "display_Order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    public static AccommodationImageCategory create(Accommodation accommodation, String name, Integer displayOrder) {
        return AccommodationImageCategory.builder()
                .accommodation(accommodation)
                .name(name)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .build();
    }
}
