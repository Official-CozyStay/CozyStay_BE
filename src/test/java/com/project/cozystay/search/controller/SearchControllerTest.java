package com.project.cozystay.search.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void search_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/search"))
                .andExpect(status().isOk());
    }

    @Test
    void search_withCity_shouldReturnFilteredResults() throws Exception {
        // This test assumes you have some test data in your database.
        // For example, an accommodation in "Seoul".
        mockMvc.perform(get("/api/v1/search")
                        .param("city", "Seoul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void search_withPriceRange_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("minPrice", "50000")
                        .param("maxPrice", "150000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void search_withBeds_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("numberOfBeds", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void search_withDateRange_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("checkInDate", "2025-01-01")
                        .param("checkOutDate", "2025-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }

    @Test
    void search_withAllFilters_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("city", "Seoul")
                        .param("minPrice", "50000")
                        .param("maxPrice", "200000")
                        .param("numberOfBeds", "2")
                        .param("checkInDate", "2025-02-01")
                        .param("checkOutDate", "2025-02-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray());
    }
}
