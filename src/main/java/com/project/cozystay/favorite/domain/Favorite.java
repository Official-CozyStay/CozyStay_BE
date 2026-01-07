package com.project.cozystay.favorite.domain;

import com.project.cozystay.favorite.dto.FavoriteUpdateRequestDTO;
import com.project.cozystay.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "favorites")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean isPrivate = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "favorite", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FavoriteAccommodation> favoriteAccommodations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void update(FavoriteUpdateRequestDTO request) {
        if (request.getName() != null) {this.name = request.getName();}
        if (request.getDescription() != null) {this.description = request.getDescription();}
        if (request.getIsPrivate() != null) {this.isPrivate = request.getIsPrivate();}
    }
}
