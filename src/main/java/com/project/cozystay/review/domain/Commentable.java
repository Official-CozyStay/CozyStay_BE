package com.project.cozystay.review.domain;

import com.project.cozystay.comment.domain.Comment;

public interface Commentable {
    void addComment(Comment comment);
}
