package com.project.cozystay.search.domain;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@Document(indexName = "accommodations")
public class AccommodationDocument {

    @Id
    private Long id; // MySQL의 Accommodation ID

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String address;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String province;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String city;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String district;

    @Field(type = FieldType.Double)
    private Double pricePerNight;

    @Field(type = FieldType.Keyword)
    private String mainImageUrl; // 썸네일 이미지 단건

    @Builder
    public AccommodationDocument(Long id, String title, String description, String address,
                                 String province, String city, String district,
                                 Double pricePerNight, String mainImageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.province = province;
        this.city = city;
        this.district = district;
        this.pricePerNight = pricePerNight;
        this.mainImageUrl = mainImageUrl;
    }
}
