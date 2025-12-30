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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<AccommodationSearchResponse> search(
            @ModelAttribute AccommodationSearchRequest request) {
        AccommodationSearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
}
