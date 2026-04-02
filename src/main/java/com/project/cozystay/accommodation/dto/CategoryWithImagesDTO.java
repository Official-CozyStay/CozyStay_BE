package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.domain.AccommodationImageCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryWithImagesDTO {

    private Long categoryId;
    private String categoryName;
    private Integer displayOrder;
    private List<AccommodationImageDTO> images;

    public static List<CategoryWithImagesDTO> fromAccommodation(Accommodation accommodation) {
        Map<AccommodationImageCategory, List<AccommodationImage>> grouped = accommodation.getImages().stream()
                .collect(Collectors.toMap(
                        AccommodationImage::getCategory,
                        img -> {
                            List<AccommodationImage> list = new ArrayList<>();
                            list.add(img);
                            return list;
                        },
                        (left, right) -> {
                            left.addAll(right);
                            return left;
                        }
                ));

        Comparator<AccommodationImageCategory> categoryOrder = Comparator
                .nullsLast(
                        Comparator.comparing(
                                AccommodationImageCategory::getDisplayOrder,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ).thenComparing(
                                AccommodationImageCategory::getId,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                );

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(categoryOrder))
                .map(entry -> {
                    AccommodationImageCategory cat = entry.getKey();
                    List<AccommodationImage> imgs = entry.getValue().stream()
                            .sorted(
                                    Comparator.comparing(
                                            AccommodationImage::getDisplayOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())
                                    ).thenComparing(
                                            AccommodationImage::getId,
                                            Comparator.nullsLast(Comparator.naturalOrder())
                                    )
                            )
                            .toList();

                    return CategoryWithImagesDTO.builder()
                            .categoryId(cat != null ? cat.getId() : null)
                            .categoryName(cat != null ? cat.getName() : "미분류")
                            .displayOrder(cat != null && cat.getDisplayOrder() != null
                                    ? cat.getDisplayOrder()
                                    : Integer.MAX_VALUE)
                            .images(imgs.stream().map(CategoryWithImagesDTO::toImageDTO).toList())
                            .build();
                })
                .toList();
    }

    private static AccommodationImageDTO toImageDTO(AccommodationImage i) {
        return AccommodationImageDTO.builder()
                .imageId(i.getId())
                .imageUrl(i.getImageUrl())
                .displayOrder(i.getDisplayOrder())
                .isPrimary(i.isPrimary())
                .build();
    }
}
