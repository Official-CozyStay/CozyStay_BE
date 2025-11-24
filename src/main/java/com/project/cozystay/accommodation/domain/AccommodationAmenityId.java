package com.project.cozystay.accommodation.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationAmenityId implements Serializable {

    private Long accommodationId;
    private Integer amenityId;

}
