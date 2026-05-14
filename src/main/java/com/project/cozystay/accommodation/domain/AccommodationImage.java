package com.project.cozystay.accommodation.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "accommodation_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class AccommodationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AccommodationImageCategory category;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "is_primary")
    @Builder.Default
    private boolean  primary = false;

    private Integer displayOrder;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    public void assignAccommodation(Accommodation accommodation){
        this.accommodation = accommodation;
    }

    public void assignCategory(AccommodationImageCategory category) {this.category = category;}

    public static AccommodationImage create(
            String imageUrl,
            Integer displayOrder,
            Boolean primary
    ) {
        return AccommodationImage.builder()
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .primary(primary != null ? primary : false)
                .build();
    }
}
