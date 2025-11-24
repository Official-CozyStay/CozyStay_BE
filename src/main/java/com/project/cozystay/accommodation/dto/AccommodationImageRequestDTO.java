package com.project.cozystay.accommodation.dto;


import com.project.cozystay.accommodation.domain.AccommodationImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageRequestDTO {
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isPrimary;

    public AccommodationImage toEntity() {
        return AccommodationImage.builder()
                .imageUrl(this.imageUrl)
                .displayOrder(this.displayOrder)
                .primary(this.isPrimary != null ? this.isPrimary : false)
                .build();
    }
}
