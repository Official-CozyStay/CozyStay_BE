package com.project.cozystay.search.controller;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.domain.AccommodationStatus;
import com.project.cozystay.accommodation.domain.AccommodationType;
import com.project.cozystay.accommodation.repository.AccommodationImageRepository;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AccommodationImageRepository accommodationImageRepository;

    @Test
    void 기본_이미지가_포함된_숙소를_반환해야_한다() throws Exception {
        // given
        Accommodation accommodation = Accommodation.builder()
                .hostId(1L)
                .title("Test Accommodation in Seoul")
                .province("서울특별시")
                .city("강남구")
                .district("신사동")
                .country("South Korea")
                .address("Test Address")
                .accommodationType(AccommodationType.ENTIRE_PLACE)
                .pricePerNight(new BigDecimal("100.00"))
                .status(AccommodationStatus.ACTIVE)
                .maxGuests(2)
                .build();
        accommodationRepository.save(accommodation);

        AccommodationImage primaryImage = AccommodationImage.builder()
                .accommodation(accommodation)
                .imageUrl("http://example.com/primary_image.jpg")
                .primary(true)
                .build();
        accommodationImageRepository.save(primaryImage);

        AccommodationImage anotherImage = AccommodationImage.builder()
                .accommodation(accommodation)
                .imageUrl("http://example.com/another_image.jpg")
                .primary(false)
                .build();
        accommodationImageRepository.save(anotherImage);

        // when & then
        mockMvc.perform(get("/api/v1/search")
                        .param("province", "서울특별시")
                        .param("city", "강남구")
                        .param("district", "신사동"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations.length()", is(1)))
                .andExpect(jsonPath("$.accommodations[0].title", is("Test Accommodation in Seoul")))
                .andExpect(jsonPath("$.accommodations[0].province", is("서울특별시")))
                .andExpect(jsonPath("$.accommodations[0].city", is("강남구")))
                .andExpect(jsonPath("$.accommodations[0].district", is("신사동")))
                .andExpect(jsonPath("$.accommodations[0].mainImageUrl", is("http://example.com/primary_image.jpg")))
                .andDo(print());
    }
    
    @Test
    void 검색_성공시_OK를_반환해야_한다() throws Exception {
        mockMvc.perform(get("/api/v1/search"))
                .andExpect(status().isOk());
    }

    @Test
    void 도시로_검색시_필터링된_결과를_반환해야_한다() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("city", "Gangnam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void 가격_범위로_검색시_필터링된_결과를_반환해야_한다() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("minPrice", "50000")
                        .param("maxPrice", "150000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void 침대_수로_검색시_필터링된_결과를_반환해야_한다() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("numberOfBeds", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void 날짜_범위로_검색시_필터링된_결과를_반환해야_한다() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("checkInDate", "2025-01-01")
                        .param("checkOutDate", "2025-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void 모든_필터로_검색시_필터링된_결과를_반환해야_한다() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("province", "서울특별시")
                        .param("city", "강남구")
                        .param("minPrice", "50000")
                        .param("maxPrice", "200000")
                        .param("numberOfBeds", "2")
                        .param("checkInDate", "2025-02-01")
                        .param("checkOutDate", "2025-02-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }
}
