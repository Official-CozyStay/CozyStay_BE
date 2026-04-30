package com.project.cozystay.accommodation.domain;


import com.project.cozystay.accommodation.dto.AccommodationAmenityRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amenity_id")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String icon;

    @Column(length = 50)
    private String category;

    public static Amenity create(AccommodationAmenityRequestDTO request) {
        return Amenity.builder()
                .name(request.name())
                .icon(request.icon())
                .category(request.category())
                .build();
    }
}
