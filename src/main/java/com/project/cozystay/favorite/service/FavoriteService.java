package com.project.cozystay.favorite.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.favorite.domain.Favorite;
import com.project.cozystay.favorite.domain.FavoriteAccommodation;
import com.project.cozystay.favorite.dto.FavoriteCreateRequestDTO;
import com.project.cozystay.favorite.dto.FavoriteDetailResponseDTO;
import com.project.cozystay.favorite.dto.FavoriteResponseDTO;
import com.project.cozystay.favorite.dto.FavoriteUpdateRequestDTO;
import com.project.cozystay.favorite.repository.FavoriteAccommodationRepository;
import com.project.cozystay.favorite.repository.FavoriteRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteAccommodationRepository favoriteAccommodationRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createFavorite(Long userId, FavoriteCreateRequestDTO request) {

        User user = userRepository.getReferenceById(userId);

        Favorite favorite = request.toEntity(user);

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void addAccommodation(Long userId, Long favoriteId, Long accommodationId){
        Favorite favorite = getOwnedFavorite(userId, favoriteId);

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        //추후 AccommodationStatus.ACTIVE 활용하게 될 경우 추가
//        Accommodation accommodation = accommodationRepository.findById(accommodationId)
//                .filter(acc -> acc.getStatus() == AccommodationStatus.ACTIVE)
//                .orElseThrow(() -> new IllegalArgumentException("활성화된 숙소를 찾을 수 없습니다."));

        FavoriteAccommodation favoriteAccommodation = FavoriteAccommodation.of(favorite, accommodation);

        favoriteAccommodationRepository.save(favoriteAccommodation);
    }

    @Transactional
    public void deleteAccommodation(Long userId, Long favoriteId, Long accommodationId){
        getOwnedFavorite(userId, favoriteId);

        FavoriteAccommodation favoriteAccommodation = favoriteAccommodationRepository.findByFavorite_IdAndAccommodation_Id(favoriteId, accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 숙소가 즐겨찾기에 없습니다."));

        favoriteAccommodationRepository.delete(favoriteAccommodation);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponseDTO> getFavorites(Long userId){

        List<Favorite> favorites = favoriteRepository.findAllByUser_Id(userId);

        return favorites.stream().map(FavoriteResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public FavoriteDetailResponseDTO getFavoriteDetail(Long userId, Long favoriteId){
        Favorite favorite = favoriteRepository.findDetailByIdAndUserId(favoriteId, userId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기 목록을 찾을 수 없습니다."));

        return FavoriteDetailResponseDTO.from(favorite);
    }

    @Transactional
    public void updateFavorite(Long userId, Long favoriteId, FavoriteUpdateRequestDTO request){
        Favorite favorite = getOwnedFavorite(userId, favoriteId);

        favorite.update(request);
    }

    @Transactional
    public void deleteFavorite(Long userId, Long favoriteId){
        Favorite favorite = getOwnedFavorite(userId, favoriteId);

        favoriteRepository.delete(favorite);
    }

    /**
     * 유저가 소유한 즐겨찾기 목록을 조회하는 공통 메서드
     */
    private Favorite getOwnedFavorite(Long userId, Long favoriteId){
        return favoriteRepository.findByIdAndUser_Id(favoriteId, userId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기 목록을 찾을 수 없습니다."));
    }

}
