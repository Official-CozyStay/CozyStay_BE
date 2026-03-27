package com.project.cozystay.search.controller;

import com.project.cozystay.search.dto.AccommodationSearchRequest;
import com.project.cozystay.search.dto.AccommodationSearchResponse;
import com.project.cozystay.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "숙소 검색 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "숙소 검색", description = "위치, 날짜, 인원 등 다양한 조건으로 숙소를 검색합니다.")
    @GetMapping
    public ResponseEntity<AccommodationSearchResponse> search(
            @ModelAttribute AccommodationSearchRequest request) {
        AccommodationSearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
}
