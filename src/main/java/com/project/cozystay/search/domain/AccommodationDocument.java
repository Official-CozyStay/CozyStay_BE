package com.project.cozystay.search.domain;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

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

    @Field(type = FieldType.Keyword)
    private String state;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String district;

    private double pricePerNight;
    
    private String mainImageUrl;

    @Builder
    public AccommodationDocument(Long id, String title, String description, String address,
                                 String state, String city, String district,
                                 double pricePerNight, String mainImageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.state = state;
        this.city = city;
        this.district = district;
        this.pricePerNight = pricePerNight;
        this.mainImageUrl = mainImageUrl;
    }
}
