package com.project.cozystay.accommodation.controller;

import com.project.cozystay.accommodation.dto.*;
import com.project.cozystay.accommodation.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<List<AccommodationResponseDTO>> getAccommodations() {
        List<AccommodationResponseDTO> response =  accommodationService.getAllAccommodations();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> createAccommodation(@RequestBody AccommodationRequestDTO request) {
        AccommodationResponseDTO response = accommodationService.createAccommodation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/details/{accommodationId}")
    public ResponseEntity<AccommodationDetailResponseDTO> addAccommodationDetail(@PathVariable Long accommodationId, @RequestBody AccommodationDetailRequestDTO request) {
        AccommodationDetailResponseDTO response = accommodationService.addAccommodationDetail(accommodationId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accommodationId}")
    public ResponseEntity<AccommodationFullResponseDTO> getAccommodationDetail(@PathVariable Long accommodationId) {
        AccommodationFullResponseDTO response = accommodationService.getAccommodationDetail(accommodationId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accommodationId}/publish")
    public ResponseEntity<Void> changePublish(@PathVariable Long accommodationId) {
        accommodationService.publish(accommodationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/images/{accommodationId}")
    public ResponseEntity<AccommodationImageResponseDTO> addImage(@PathVariable Long accommodationId, @RequestBody List<AccommodationImageRequestDTO> request){
        AccommodationImageResponseDTO response = accommodationService.addImage(accommodationId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/amenities/{accommodationId}")
    public ResponseEntity<AccommodationAmenityResponseDTO> addAmenities(@PathVariable Long accommodationId, @RequestBody List<AccommodationAmenityRequestDTO> request){
        AccommodationAmenityResponseDTO response = accommodationService.addAmenities(accommodationId, request);
        return ResponseEntity.ok(response);
    }


    }

