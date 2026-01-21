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
@Table(name = "reviewComment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User reviewer;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    private Comment(User reviewer, String content) {
        this.reviewer = reviewer;
        this.content = content;
    }

    public static Comment of(User reviewer, String content) {
        return Comment.builder()
                .reviewer(reviewer)
                .content(content)
                .build();
    }

    public void update(String content) {
        this.content = content;
    }
}
