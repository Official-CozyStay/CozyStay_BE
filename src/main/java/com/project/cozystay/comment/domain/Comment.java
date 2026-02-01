package com.project.cozystay.comment.domain;

import com.project.cozystay.common.BaseTimeEntity;
import com.project.cozystay.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    @Builder
    private Comment(User author, String content, ReviewType reviewType) {
        this.author = author;
        this.content = content;
        this.reviewType = reviewType;
    }

    public static Comment of(User author, String content, ReviewType reviewType) {
        return Comment.builder()
                .author(author)
                .content(content)
                .reviewType(reviewType)
                .build();
    }

    public void update(String content) {
        this.content = content;
    }
}
