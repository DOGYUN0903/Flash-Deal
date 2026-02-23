package com.prj.flashdeal.domain.review.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.review.entity.Review;

public record ReviewResponse(
    Long reviewId,
    String memberName,
    Integer rating,
    String content,
    LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getMember().getName(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt()
        );
    }
}
